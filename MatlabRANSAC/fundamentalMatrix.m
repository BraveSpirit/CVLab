% Compute the fundamental matrix using the eight point algorithm
% Input 
% 	x1s, x2s 	Point correspondences
%
% Output
% 	Fh 			Fundamental matrix with the det F = 0 constraint
% 	F 			Initial fundamental matrix obtained from the eight point algorithm

function [Fh, F] = fundamentalMatrix(x1s, x2s)

    numOfPoints = size(x1s,2);
    
%     normalize the points
    [nx1s, T1] = normalizePoints2d(x1s);
    [nx2s, T2] = normalizePoints2d(x2s);
    
    A = zeros(numOfPoints, 9);
    
    for iPoint = 1:numOfPoints
        A(iPoint,:) = [nx2s(1,iPoint)*nx1s(:,iPoint)' nx2s(2,iPoint)*nx1s(:,iPoint)' nx1s(:,iPoint)'];
    end

    [U,S,V] = svd(A);
    
    F = V(:,end);
    F = [F(1:3)';
         F(4:6)';
         F(7:9)'];
   
%     Scale back
    F = T2'*F*T1; 
     
    [U,S,V] = svd(F);
    
    S(end,end) = 0;
    Fh = U*S*V';
end