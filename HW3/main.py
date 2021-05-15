

class Vector:
    x: float
    y: float
    z: float

    def __init__(self, x, y, z):
        self.x = float(x)
        self.y = float(y)
        self.z = float(z)

    def __sub__(self, v2):
        return Vector(self.x - v2.x, self.y - v2.y, self.z - v2.z)

    def __mul__(self, a: float):
        return Vector(self.x * a, self.y * a, self.z * a)

    def __add__(self, v2):
        return Vector(self.x + v2.x, self.y + v2.y, self.z + v2.z)

    def __str__(self):
        return f"v {self.x} {self.y} {self.z}\n"


def load_frames(input_file):
    frames, timestamps = [], []
    with open(f"3rd Homework - data/{input_file}") as f:
        for line in f:
            model, timestamp = line.split()
            frames.append(model)
            timestamps.append(int(timestamp))
    return frames, timestamps


def load_vertices(frame):
    frame_v = []
    with open(f"3rd Homework - data/{frame}") as f:
        for line in f:
            if line.startswith("v "):
                _, x, y, z = line.split()
                frame_v.append(Vector(x, y, z))
            else:
                continue
    return frame_v


def catmull_rom(k_i_minus1, k_i, k_i_plus1, k_i_plus2, num_of_steps):
    tau = 0.2
    step_size = 1 / num_of_steps
    vertices_results = []
    for i in range(len(k_i_minus1)):
        d = k_i[i]
        c = (k_i_plus1[i] - k_i_minus1[i]) * tau
        b = k_i_minus1[i] * (2 * tau) + k_i[i] * (tau - 3) + k_i_plus1[i] * (3 - 2 * tau) - k_i_plus2[i] * tau
        a = k_i_minus1[i] * -tau + k_i[i] * (2 - tau) + k_i_plus1[i] * (tau - 2) + k_i_plus2[i] * tau

        vertex_interpolations = []
        for i in range(0, num_of_steps):
            u = i * step_size
            b_i = a * u**3 + b * u**2 + c * u + d
            vertex_interpolations.append(b_i)

        vertices_results.append(vertex_interpolations)

    return vertices_results


def work(frames, timestamps, framerate):
    frames_vertices = list(map(lambda f: load_vertices(f), frames))

    for i in range(len(frames_vertices) - 1):
        vertices2, vertices3 = frames_vertices[i], frames_vertices[i+1]
        if i == 0:
            vertices1, vertices4 = frames_vertices[i], frames_vertices[i+2]
        elif i == len(frames_vertices) - 2:
            vertices1, vertices4 = frames_vertices[i-1], frames_vertices[i+1]
        else:
            vertices1, vertices4 = frames_vertices[i-1], frames_vertices[i+2]

        number_of_steps = int((timestamps[i + 1] - timestamps[i]) / 1000 * framerate)
        result = catmull_rom(vertices1, vertices2, vertices3, vertices4, number_of_steps)
        for j in range(number_of_steps):
            with open(f"results_data/results2/frame_{i+1}_{j+1}.obj", "w") as f_new, open("3rd Homework - data/frame_01.obj") as f:
                counter = 0
                for line in f:
                    if line.startswith("v "):
                        try:
                            f_new.write(str(result[counter][j]))
                        except Exception as e:
                            print(e)
                        counter += 1
                    else:
                        f_new.write(line)
            f.close()
            f_new.close()


if __name__ == "__main__":
    frame_rate = 60
    scenario = "input_02.txt"
    main_frames, main_timestamps = load_frames(scenario)
    work(main_frames, main_timestamps, frame_rate)
