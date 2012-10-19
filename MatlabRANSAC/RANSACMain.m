close all, clear all, clc;

IMG_NAME1 = 'images/image_1.jpg';
IMG_NAME2 = 'images/image_2.jpg';

% read in image

% img1 = imresize(img1, 1);
% img2 = imresize(img2, 1);

% convert to gray image
img1 = im2double(imread(IMG_NAME1));
img2 = im2double(imread(IMG_NAME2));
imgBW1 = single(rgb2gray(img1));
imgBW2 = single(rgb2gray(img2));

% figure(1)
% imshow(imgBW1)

[f1,d1] = vl_sift(imgBW1);

% h1 = vl_plotframe(f1) ;
% h2 = vl_plotframe(f1) ;
% set(h1,'color','k','linewidth',3) ;
% set(h2,'color','y','linewidth',2) ;

% figure(2)
% imshow(imgBW2)

[f2,d2] = vl_sift(imgBW2);

% h1 = vl_plotframe(f2) ;
% h2 = vl_plotframe(f2) ;
% set(h1,'color','k','linewidth',3) ;
% set(h2,'color','y','linewidth',2) ;

[matches, scores] = vl_ubcmatch(d1, d2);

bestMatches = find(scores < 30000);
matches = matches(:,bestMatches);

pts1 = f1(1:2, matches(1,:));
pts2 =  f2(1:2, matches(2,:));

pts1 = [pts1;ones(1,size(pts1,2))];
pts2 = [pts2;ones(1,size(pts2,2))];

thresh = 2;
prob = .999;
maxNum = 10000;

[inliers,F] = RANSAC8pt_adaptive(pts1,pts2,thresh,prob,maxNum);

ptsInliers1 = pts1(1:2,inliers);
ptsInliers2 = pts2(1:2,inliers);

showFeatureMatches(img1, ptsInliers1, img2, ptsInliers2, 30, []);
% pause
thresh = 1;
prob = .999;

inliers = RANSAC3pt_adaptive(pts1,pts2,F,thresh,prob, maxNum);
ptsInliers1 = pts1(1:2,inliers);
ptsInliers2 = pts2(1:2,inliers);
showFeatureMatches(img1, ptsInliers1, img2, ptsInliers2, 100, []);

% Compute essential matrix
% [Eh, E, x1n, x2n] = essentialMatrix(trackedPoints1, trackedPoints2);
% [Pcorrect] = decomposeE(Eh, x1n, x2n);
% [XS, err] = linearTriangulation([eye(3),zeros(3,1)], trackedPoints1, Pcorrect, trackedPoints2);
% plot3(XS(1,:),XS(2,:),XS(3,:),'.b','markersize',5);
