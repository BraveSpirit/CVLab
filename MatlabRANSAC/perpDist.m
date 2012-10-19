function d = perpDist(F,x1,x2)

% x has to be 3xn
% d contains the distances, 1xn

if (size(x1,1) == 2)
    x1 = [x1;ones(1,size(x1,2))];
    x2 = [x2;ones(1,size(x2,2))];
end
l1 = F*x1;
l2 = F'*x2;

d1 = diag(abs(l1'*x2))./norm(l1(1:2),1);

d2 = diag(abs(l2'*x1))./norm(l1(1:2),1);

d = d1 + d2;

end