function plotPlane(normal,fig)

%# a plane is a*x+b*y+c*z+d=0
%# [a,b,c] is the normal. Thus, we have to calculate
%# d and we're set
d = 1; 

%# create x,y
[xx,yy]=ndgrid(-5:10:5,-5:10:5);

%# calculate corresponding z
z = (-normal(1)*xx - normal(2)*yy - d)/normal(3);

%# plot the surface
figure(fig)
surf(xx,yy,z)
end