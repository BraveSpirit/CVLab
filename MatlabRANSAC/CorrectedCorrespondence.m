function [x, x_prime] = CorrectedCorrespondence(x, xp, F)

% inhomogeneous coordinates
% translate to origin
xT = mean(x,2);
xpT = mean(xp,2);

T = [1 0 -xT(1); 0 1 -xT(2); 0 0 1];
Tp = [1 0 -xpT(1); 0 1 -xpT(2); 0 0 1];

F = inv(Tp')*F*inv(T);

% compute epipoles
[U,S,V] = svd(F);
e = V(:,end);
e = e/norm(e);
[U,S,V] = svd(F');
ep = V(:,end);
ep = ep/norm(ep);
% Form rotation matrices
R = [e(1) e(2) 0; -e(2) e(1) 0; 0 0 1];
R_prime = [ep(1) ep(2) 0; -ep(2) ep(1) 0; 0 0 1];

F = R_prime*F*R';

f = e(3);
fp = ep(3);
a = F(2,2);
b = F(2,3);
c = F(3,2);
d = F(3,3);

s = fsolve(@(t)Polynomial(t,f,fp,a,b,c,d), 0)





