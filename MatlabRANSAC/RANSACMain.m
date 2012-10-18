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



bestMatches = find(scores < 30000);
matches = matches(:,bestMatches);
scores = scores(bestMatches);
% get the top 15
[sorted,id] = sort(scores);

top15 = id(1:15);

trackedPoints1 = f1(1:2, matches(1,:));
trackedPoints2 =  f2(1:2, matches(2,:));

trackedPoints1 = [trackedPoints1;ones(1,size(trackedPoints1,2))];
trackedPoints2 = [trackedPoints2;ones(1,size(trackedPoints2,2))];

showFeatureMatches(img1, trackedPoints1(1:2, top15), img2, trackedPoints2(1:2, top15), 30, []);
pause
% Select points by clicking
selectedIndeces = 1:1:size(trackedPoints1,2); % Select all points

% Compute fundamental matrix
% [Fh, F] = fundamentalMatrix(trackedPoints1(:,selectedIndeces), trackedPoints2(:,selectedIndeces));
[Fh, F] = fundamentalMatrix(trackedPoints1(:, top15),trackedPoints2(:, top15));

% Compute essential matrix
% [Eh, E, x1n, x2n] = essentialMatrix(trackedPoints1, trackedPoints2);
% [Pcorrect] = decomposeE(Eh, x1n, x2n);
% [XS, err] = linearTriangulation([eye(3),zeros(3,1)], trackedPoints1, Pcorrect, trackedPoints2);
% plot3(XS(1,:),XS(2,:),XS(3,:),'.b','markersize',5);

selectedIndeces = [];
showFeatureMatches(img1, trackedPoints1(1:2, :), img2, trackedPoints2(1:2, :), 10, selectedIndeces);

iterations = 100;
threshold = 1200;

bestInliers = [];
bestH = [];
mostInliers = 0;

for j = 1:iterations

    selectedIndeces = randperm(size(trackedPoints1,2),3);    
    showFeatureMatches(img1, f1(1:2, matches(1,:)), img2, f2(1:2, matches(2,:)), 10, selectedIndeces);
        
    notSelectedIndeces = 1:1:size(trackedPoints1,2);
    notSelectedIndeces(selectedIndeces) = [];

    x = trackedPoints1(:,selectedIndeces);
    x_prime = trackedPoints2(:,selectedIndeces);
%     CorrectedCorrespondence(x, x_prime, F);
    
    H = ComputeHomography(x, x_prime, Fh);

    inliers = [];
    
    for i = 1:length(notSelectedIndeces)

        % select a new point
        tempIndex = randperm(size(notSelectedIndeces,2),1);
        newPointIndex = notSelectedIndeces(tempIndex);
        notSelectedIndeces(tempIndex) = [];

        error = ComputeError([x,trackedPoints1(:,newPointIndex)],[x_prime,trackedPoints2(:,newPointIndex)], H);

        if (error < threshold)
            inliers = [inliers, newPointIndex];
        end
    end
    
    if (length(inliers) > mostInliers)
        bestH = H;
        bestInliers = inliers;
        mostInliers = length(inliers);
    end
end

showFeaturePoints(img1, trackedPoints1(1:2, :), img2, trackedPoints2(1:2, :), 15, bestInliers)
