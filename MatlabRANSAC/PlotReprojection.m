function PlotReprojection(x, x_prime, H, img1, img2)

numOfPoints = size(x,2);

reprojectedPoints1 = H\x_prime;
reprojectedPoints2 = H*x;

for i = 1:numOfPoints
    reprojectedPoints1(:,i) = reprojectedPoints1(:,i)/reprojectedPoints1(3,i);
    reprojectedPoints2(:,i) = reprojectedPoints2(:,i)/reprojectedPoints2(3,i);
end

figure
subplot(1,2,1)
imshow(img1),hold on
plot(reprojectedPoints1(1,:),reprojectedPoints1(2,:),'.b','markersize',10)
plot(x(1,:),x(2,:),'.g','markersize',10)
subplot(1,2,2)
imshow(img2),hold on
plot(reprojectedPoints2(1,:),reprojectedPoints2(2,:),'.b','markersize',10)
plot(x_prime(1,:),x_prime(2,:),'.g','markersize',10)