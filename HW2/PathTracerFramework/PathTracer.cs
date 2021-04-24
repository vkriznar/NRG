using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static PathTracer.Samplers;

namespace PathTracer
{
  class PathTracer
  {
    public Spectrum Li(Ray r, Scene s)
    {
            var L = Spectrum.ZeroSpectrum;
            var beta = Spectrum.Create(1);
            int nBounces = 0;

            while(nBounces < 20)
            {
                (var mint, var si) = s.Intersect(r);
                // We've missed all objects
                if (mint == null) { break; }

                // We've hit light, multiply emission with beta parameter and break
                if (si.Obj is Light)
                {
                    // Direct light hit
                    if (nBounces == 0)
                        L = si.Le(si.Wo) * beta;
                    break;
                }

                // Reuse path
                var sampleLight = Light.UniformSampleOneLight(si, s);
                if (beta.Max() > 1)
                    beta = Spectrum.Create(1);
                L = L.AddTo(beta * sampleLight);

                var shp = si.Obj as Shape;
                (var f, var wi, var pdf, var specular)  = shp.BSDF.Sample_f(si.Wo, si);
                var thetaI = Math.Tan(Math.Sqrt(wi.x * wi.x + wi.y * wi.y) / wi.z);
                beta = beta * f * Math.Abs(Math.Cos(thetaI)) / pdf;

                r = si.SpawnRay(wi);

                // Russian Roulette
                if (nBounces > 3)
                {
                    var q = 1 - beta.Max();
                    if (ThreadSafeRandom.NextDouble() < q) { break; }
                    beta = beta / (1 - q);
                }
                
                nBounces++;
            }


            return L;
    }

  }
}
