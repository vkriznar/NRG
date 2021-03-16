public enum OctreeParts {
    BottomLeftFront(0),
    BottomRightFront(1),
    BottomLeftBack(2),
    BottomRightBack(3),
    TopLeftFront(4),
    TopRightFront(5),
    TopLeftBack(6),
    TopRightBack(7);

    int id;

    OctreeParts(int id){
        this.id = id;
    }

    public static OctreeParts get(int id) throws Exception {
        for (OctreeParts loc : OctreeParts.values()) {
            if (loc.id == id) return loc;
        }

        throw new Exception("No OctLocation with num: " + id);
    }
}
