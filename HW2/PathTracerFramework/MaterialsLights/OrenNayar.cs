using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PathTracer
{
    class OrenNayar : BxDF
    {
        private Spectrum kd;
        private double sigma;
        public OrenNayar(Spectrum r)
        {
            kd = r;
            sigma = 0.7; // Change this parameter for different effects. If you set it to 0 it will become Lambertian surface
        }

        public override Spectrum f(Vector3 wo, Vector3 wi)
        {
            if (!Utils.SameHemisphere(wo, wi))
                return Spectrum.ZeroSpectrum;

            var fiI = Math.Atan(wi.y / wi.x);
            var thetaI = Math.Atan(Math.Sqrt(wi.x * wi.x + wi.y * wi.y));
            var fiO = Math.Atan(wo.y / wo.x);
            var thetaO = Math.Atan(Math.Sqrt(wo.x * wo.x + wo.y * wo.y));

            var a = 1 - sigma * sigma / (2 * (sigma * sigma + 0.33));
            var b = 0.45 * sigma * sigma / (sigma * sigma + 0.09);

            var fr = kd * Utils.PiInv * (a + b * Math.Max(0, Math.Cos(fiI - fiO)) * Math.Sin(Math.Max(thetaI, thetaO)) * Math.Tan(Math.Min(thetaI, thetaO)));
            return fr;
        }

        public override (Spectrum, Vector3, double) Sample_f(Vector3 wo)
        {
            var wi = Samplers.CosineSampleHemisphere();
            if (wo.z < 0)
                wi.z *= -1;
            double pdf = Pdf(wo, wi);
            return (f(wo, wi), wi, pdf);
        }

        public override double Pdf(Vector3 wo, Vector3 wi)
        {
            if (!Utils.SameHemisphere(wo, wi))
                return 0;

            return Math.Abs(wi.z) * Utils.PiInv; // wi.z == cosTheta
        }
    }
}
