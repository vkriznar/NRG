using System;
using MathNet.Numerics.Integration;

namespace PathTracer
{
  class Sphere : Shape
  {
    public double Radius { get; set; }
    public Sphere(double radius, Transform objectToWorld)
    {
      Radius = radius;
      ObjectToWorld = objectToWorld;
    }

    public override (double?, SurfaceInteraction) Intersect(Ray ray)
    {
        Ray r = WorldToObject.Apply(ray);

        // Compute quadratic sphere coefficients
        var a = Vector3.Dot(r.d, r.d);
        var b = 2 * Vector3.Dot(r.o, r.d);
        var c = Vector3.Dot(r.o, r.o) - Radius * Radius;

        // Initialize _double_ ray coordinate values
        (bool hit, double t0, double t1) = Utils.Quadratic(a, b, c);
        if (!hit)
            return (null, null);


        // Solve quadratic equation for _t_ values
        if (t1 <= Renderer.Epsilon)
            return (null, null);
        double tShapeHit = t0;
        if (tShapeHit <= Renderer.Epsilon)
            tShapeHit = t1;

        // Compute sphere hit position and phi
        Vector3 pHit = r.Point(tShapeHit);
        pHit *= Radius / (pHit - Vector3.ZeroVector).Length();
        if (pHit.x == 0 && pHit.y == 0)
            pHit.x = 1e-5f * this.Radius;
        var phi = Math.Atan2(pHit.y, pHit.x);
        if (phi < 0)
            phi += 2 * Math.PI;

        double theta = Math.Acos(Utils.Clamp(pHit.z / Radius, -1, 1));
        Vector3 dpdu = new Vector3(-pHit.y, pHit.x, 0);

        double zRadius = Math.Sqrt(pHit.x * pHit.x + pHit.y * pHit.y);
        double cosPhi = pHit.x / zRadius;
        double sinPhi = pHit.y / zRadius;

        Vector3 P = Utils.SphericalDirection(sinPhi, cosPhi, theta);
        Vector3 N = P - ObjectToWorld.ApplyPoint(Vector3.ZeroVector);

        // Return shape hit and surface interaction
        var si = new SurfaceInteraction(pHit, N, -ray.d, dpdu, this);
        return (tShapeHit, ObjectToWorld.Apply(si));
    }

    public override (SurfaceInteraction, double) Sample() {
        // Sphere sampling
        Vector3 v = Samplers.UniformSampleSphere();
        Vector3 pObj = new Vector3(Radius* v.x, Radius* v.y, Radius* v.z);
        Vector3 dpdu = new Vector3(-pObj.y, pObj.x, 0);
        Vector3 n = new Vector3(pObj.x, pObj.y, pObj.z);
        double pdf = 1 / Area();

        return (ObjectToWorld.Apply(new SurfaceInteraction(pObj, n, Vector3.ZeroVector, dpdu, this)), pdf);
    }

    public override double Area() { return 4 * Math.PI * Radius * Radius; }

    public override double Pdf(SurfaceInteraction si, Vector3 wi)
    {
        Vector3 pCenter = ObjectToWorld.ApplyPoint(Vector3.ZeroVector);
        var sinThetaMax2 = Radius * Radius / (si.Point - pCenter).Length();
        var cosThetaMax = Math.Sqrt(Math.Max(0, 1 - sinThetaMax2));
        return 1 / (2 * Math.PI * (1 - cosThetaMax));
    }

  }
}
