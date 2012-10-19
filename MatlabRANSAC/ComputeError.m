function d = ComputeError(x, x_prime, H)
% Compute reprojection error for all points

numOfPoints = size(x,2);

x1 = H\x_prime;
x2 = H*x;

for i = 1:numOfPoints
    x1(:,i) = x1(:,i)/x1(3,i);
    x2(:,i) = x2(:,i)/x2(3,i);
end

d = diag((x_prime(1:2,:)-x2(1:2,:))'*(x_prime(1:2,:)-x2(1:2,:))) +...
diag((x(1:2,:)-x1(1:2,:))'*(x(1:2,:)-x1(1:2,:)));
