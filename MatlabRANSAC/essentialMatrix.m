% Compute the essential matrix using the eight point algorithm
% Input 
% 	x1s, x2s 	Point correspondences 3xn matrices
%
% Output
% 	Eh 			Essential matrix with the det F = 0 constraint and the constraint that the first two singular values are equal
% 	E 			Initial essential matrix obtained from the eight point algorithm
%

function [Eh, E, x1sn, x2sn] = essentialMatrix(x1s, x2s)

numOfPoints = size(x1s,2);

K = [528.30, 0, 325.54; 0, 529.18, 231.76; 0, 0, 1 ];

x1sn = K\x1s;
x2sn = K\x2s;

% Normalize the points
[x1sn_norm, T1] = normalizePoints2d(x1sn);
[x2sn_norm, T2] = normalizePoints2d(x2sn);

A = zeros(numOfPoints, 9);

for iPoint = 1:numOfPoints
    A(iPoint,:) = [x2sn_norm(1,iPoint)*x1sn_norm(:,iPoint)' x2sn_norm(2,iPoint)*x1sn_norm(:,iPoint)' x1sn_norm(:,iPoint)'];
end

[U,S,V] = svd(A);

E = V(:,end);

E = [E(1:3)'; E(4:6)'; E(7:9)'];

% Scale back
E = T2'*E*T1; 

[U,S,V] = svd(E);

r = S(1,1);
s = S(2,2);

S = [(r+s)/2, 0, 0;
     0, (r+s)/2, 0;
     0, 0, 0];
 
Eh = U*S*V';

end