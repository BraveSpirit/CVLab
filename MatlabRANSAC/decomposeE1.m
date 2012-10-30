% Decompose the essential matrix
% Return P = [R|t] which relates the two views
% You will need the point correspondences to find the correct solution for P
function [Pcorrect] = decomposeE(Eh, x1s, x2s)

[U,S,V] = svd(Eh);

W = [0 -1 0;
     1 0 0;
     0 0 1];

R1 = U*W*V';
R2 = U*W'*V';

%  Check determinant!
det(R1);
det(R2);

tCross = Eh/R1;

t = [tCross(2,3);tCross(3,1); tCross(1,2)];
t = t/norm(t);

P1 = [R1,t];
P2 = [R1,-t];
P3 = [R2,t];
P4 = [R2,-t];

P = [P1, P2, P3, P4];

for i = 1:4
    P_1 = [eye(3),zeros(3,1)];
    P_2 = P(:,(4*i-3):4*i);
    
    [XS, err] = linearTriangulation(P_1, x1s, P_2, x2s);
    if(~(sum(XS(3,:) < 0) > 0))
        Pcorrect = P_2;
        R = P_2(:,1:3);
        t = P_2(:,end);
        break
    end
end





