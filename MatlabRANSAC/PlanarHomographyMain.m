
close all, clear all, clc;

IMG_NAME1 = 'images/planar1.jpg';
IMG_NAME2 = 'images/planar2.jpg';

% read in image
img1 = im2double(imread(IMG_NAME1));
img2 = im2double(imread(IMG_NAME2));

img1 = imresize(img1, 1);
img2 = imresize(img2, 1);

% convert to gray image
imgBW1 = single(rgb2gray(img1));
imgBW2 = single(rgb2gray(img2));

figure(1)
imshow(imgBW1)

[f1,d1] = vl_sift(imgBW1);

h1 = vl_plotframe(f1) ;
h2 = vl_plotframe(f1) ;
set(h1,'color','k','linewidth',3) ;
set(h2,'color','y','linewidth',2) ;

figure(2)
imshow(imgBW2)

[f2,d2] = vl_sift(imgBW2);

h1 = vl_plotframe(f2) ;
h2 = vl_plotframe(f2) ;
set(h1,'color','k','linewidth',3) ;
set(h2,'color','y','linewidth',2) ;

[matches, scores] = vl_ubcmatch(d1, d2);

bestMatches = find(scores < 20000);
matches = matches(:,bestMatches);

trackedPoints1 = f1(1:2, matches(1,:));
trackedPoints2 =  f2(1:2, matches(2,:));

trackedPoints1 = [trackedPoints1;ones(1,size(trackedPoints1,2))];
trackedPoints2 = [trackedPoints2;ones(1,size(trackedPoints2,2))];

% Select points by clicking
% selectedIndeces = [];
selectedIndeces = 1:1:size(trackedPoints1,2);
% showFeatureMatches(img1, trackedPoints1(1:2, :), img2, trackedPoints2(1:2, :), 20, selectedIndeces);

% Select points for fundamental matrix
% for i = 1:10
%     [x,y] = ginput(1);
%     selectedIndex = dsearchn(trackedPoints1(1:2,:)',[x,y])';
%     if (sum(selectedIndex == selectedIndeces) > 0)
%         [x,y] = ginput(1);
%         selectedIndex = dsearchn(trackedPoints1(1:2,:)',[x,y])';
%     end
%     selectedIndeces = [selectedIndeces,selectedIndex];
%     showFeatureMatches(img1, f1(1:2, matches(1,:)), img2, f2(1:2, matches(2,:)), 20, selectedIndeces);
% end

% Compute fundamental matrix
[Fh, F] = fundamentalMatrix(trackedPoints1(:,selectedIndeces), trackedPoints2(:,selectedIndeces));

% Compute essential matrix
% [Eh, E, x1n, x2n] = essentialMatrix(trackedPoints1, trackedPoints2);
% [Pcorrect] = decomposeE(Eh, x1n, x2n);
% [XS, err] = linearTriangulation([eye(3),zeros(3,1)], trackedPoints1, Pcorrect, trackedPoints2);
% plot3(XS(1,:),XS(2,:),XS(3,:),'.b','markersize',5);

selectedIndeces = [];
showFeatureMatches(img1, trackedPoints1(1:2, :), img2, trackedPoints2(1:2, :), 10, selectedIndeces);

% Select points for H matrix
for i = 1:3
    [x,y] = ginput(1);
    selectedIndex = dsearchn(trackedPoints1(1:2,:)',[x,y])';
    if (sum(selectedIndex == selectedIndeces) > 0)
        [x,y] = ginput(1);
        selectedIndex = dsearchn(trackedPoints1(1:2,:)',[x,y])';
    end
    selectedIndeces = [selectedIndeces,selectedIndex];
    showFeatureMatches(img1, f1(1:2, matches(1,:)), img2, f2(1:2, matches(2,:)), 10, selectedIndeces);
end
notSelectedIndeces = 1:1:size(trackedPoints1,2);
notSelectedIndeces(selectedIndeces) = [];

x = trackedPoints1(:,selectedIndeces);
x_prime = trackedPoints2(:,selectedIndeces);

% Compute epipole F^T*e= 0
[U,S,V] = svd(Fh');
e_prime = V(:,end);

crossMatrix = [0 -e_prime(3) e_prime(2);
               e_prime(3) 0 -e_prime(1);
               -e_prime(2) e_prime(1) 0];
           
A = crossMatrix*Fh;

b = zeros(3,1);

for i = 1:3
    b(i) = dot(cross(x_prime(:,i),A*x(:,i)),cross(x_prime(:,i),e_prime))/dot(cross(x_prime(:,i),e_prime),cross(x_prime(:,i),e_prime));
end

M = x';
v = M\b;

H = A - e_prime*v';

ComputeError(x, x_prime, H);

PlotReprojection(x, x_prime, H, img1, img2);
% H'*F + F'*H

errorVec = zeros(1,size(trackedPoints1,2)-3);

for i = 1:length(notSelectedIndeces)
    figure(10)
    subplot(2,1,1)
    showFeatureMatches(img1, f1(1:2, matches(1,:)), img2, f2(1:2, matches(2,:)), 10, selectedIndeces);
    subplot(2,1,2),hold off
    plot(errorVec)
    drawnow
    
    % select points manually
    [x,y] = ginput(1);
    selectedIndex = dsearchn(trackedPoints1(1:2,:)',[x,y])';
    if (sum(selectedIndex == selectedIndeces) > 0)
        [x,y] = ginput(1);
        selectedIndex = dsearchn(trackedPoints1(1:2,:)',[x,y])';
    end
    selectedIndeces = [selectedIndeces,selectedIndex];    
    
    x = trackedPoints1(:,selectedIndeces);
    x_prime = trackedPoints2(:,selectedIndeces);
    
    hold off
    showFeatureMatches(img1, f1(1:2, matches(1,:)), img2, f2(1:2, matches(2,:)), 10, selectedIndeces);
    errorVec(i) = ComputeError(x, x_prime, H)/length(selectedIndeces);   
end








