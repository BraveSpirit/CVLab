% show feature matches between two images
%
% Input:
%   img1        - n x m color image 
%   corner1     - 2 x k matrix, holding keypoint coordinates of first image
%   img2        - n x m color image 
%   corner1     - 2 x k matrix, holding keypoint coordinates of second image
%   fig         - figure id
function showFeatureMatchesSIFT(img1, corner1, img2, corner2, fig, selectedIndeces)
    [sx, sy, sz] = size(img1);
    img = [img1, img2];
    
    corner2 = corner2 + repmat([sy, 0]', [1, size(corner2, 2)]);
    
    figure(fig), imshow(img, []);    
        
    hold on, plot(corner1(1,:), corner1(2,:), '.g','markersize',15);
    hold on, plot(corner2(1,:), corner2(2,:), '.g','markersize',15);    
    hold on, plot([corner1(1,:); corner2(1,:)], [corner1(2,:); corner2(2,:)], 'b','linewidth',2);    

    selectedPoints1 = corner1(1:2,selectedIndeces);
    selectedPoints2 = corner2(1:2,selectedIndeces);
    
    hold on, plot(selectedPoints1(1,:), selectedPoints1(2,:), '.r','markersize',30);
    hold on, plot(selectedPoints2(1,:), selectedPoints2(2,:), '.r','markersize',30);   
    hold on, plot([selectedPoints1(1,:); selectedPoints2(1,:)], [selectedPoints1(2,:); selectedPoints2(2,:)], 'g','linewidth',2);  
end