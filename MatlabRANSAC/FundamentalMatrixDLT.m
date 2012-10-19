function F = FundamentalMatrixDLT(x1,x2)

% computes fundamental matrix from at least 8 corresponding points
% using the DLT algortihm with normalized points
    
% normalize points
[x1, T1] = normalisePoints2D(x1);
[x2, T2] = normalisePoints2D(x2);

% algebraic constraints
A = [x2(1,:)'.*x1(1,:)'   x2(1,:)'.*x1(2,:)'  x2(1,:)' ...
     x2(2,:)'.*x1(1,:)'   x2(2,:)'.*x1(2,:)'  x2(2,:)' ...
     x1(1,:)'             x1(2,:)'            ones(size(x1,2),1) ];       

[U S V] = svd(A);

% the entries of the fundamental matrix correspond to the last column of V
F = reshape(V(:,9),3,3)';

% enforce rank 2 of F
[U,S,V] = svd(F);
F = U*diag([S(1,1) S(2,2) 0])*V';

% denormalise
F = T2'*F*T1;

end