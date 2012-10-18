function g = Polynomial(t, f, fp, a, b, c, d)

g = t*((a*t + b)^2 + fp^2*(c*t + d)^2)^2 - (a*d - b*c)*(1 + f^2*t^2)*(a*t + b)*(c*t + d);