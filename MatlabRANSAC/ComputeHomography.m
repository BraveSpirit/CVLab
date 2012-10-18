function H = ComputeHomography(x, x_prime, F)

% Compute epipole F^T*e= 0
[U,S,V] = svd(F');
e_prime = V(:,end);

crossMatrix = [0 -e_prime(3) e_prime(2);
               e_prime(3) 0 -e_prime(1);
               -e_prime(2) e_prime(1) 0];

A = crossMatrix*F;

b = zeros(3,1);

for i = 1:3
    b(i) = dot(cross(x_prime(:,i),A*x(:,i)),cross(x_prime(:,i),e_prime))/...
        dot(cross(x_prime(:,i),e_prime),cross(x_prime(:,i),e_prime));
end

M = x';
v = M\b;

H = A - e_prime*v';