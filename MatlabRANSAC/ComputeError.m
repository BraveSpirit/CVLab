function error = ComputeError(x, x_prime, H)

numOfPoints = size(x,2);

x1 = H\x_prime;
x2 = H*x;

for i = 1:numOfPoints
    x1(:,i) = x1(:,i)/x1(3,i);
    x2(:,i) = x2(:,i)/x2(3,i);
end

error = sum(diag((x_prime(1:2,:)-x2(1:2,:))*(x_prime(1:2,:)-x2(1:2,:))')) +...
sum(diag((x(1:2,:)-x1(1:2,:))*(x(1:2,:)-x1(1:2,:))'));

error = error/numOfPoints;