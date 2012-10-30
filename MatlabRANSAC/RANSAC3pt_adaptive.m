function [inliers,v, A, e_prime] = RANSAC3pt_adaptive(pts1,pts2,F,thresh,prob,maxNum)

% maxNum: terminate after maxNum steps if a success rate of prob could
% not be achieved

numOfPoints = size(pts1,2);
maxNInliers = 0;
inlierRatio = 0;

inliers = [];

for i = 1:maxNum
    
    if (mod(i,100) == 0)
        i
    end
    
    % select 8 random point correspondences
    selectedIndeces = randperm(size(pts1,2));
    selectedIndeces = selectedIndeces(1:3);
    selPoints1 = pts1(:,selectedIndeces);
    selPoints2 = pts2(:,selectedIndeces);
    
    % compute fundamental matrix
    [H,v, A, e_prime] = ComputeHomography(selPoints1,selPoints2,F);
     
    d = ComputeError(pts1, pts2, H);
        
    if (sum(d < thresh) > maxNInliers)
        maxNInliers = sum(d < thresh);
        inliers = find(d < thresh);
        inlierRatio = maxNInliers/numOfPoints;
    end
    
    if (prob < 1 - (1 - inlierRatio^3)^i)
        break
    end
    
    
    % REMOVE MODEL CORRESPONDENCES!!! 
    
    
end    
    
end