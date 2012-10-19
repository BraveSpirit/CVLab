function [inliers, bestF] = RANSAC8pt_adaptive(pts1,pts2,thresh,prob,maxNum)

% maxNum: terminate after maxNum steps if a success rate of prob could
% not be achieved

numOfPoints = size(pts1,2);
maxNInliers = 0;
inlierRatio = 0;

inliers = [];
bestF = zeros(3,3);

for i = 1:maxNum
    
    if (mod(i,100) == 0)
        i
    end
    
    % select 8 random point correspondences
    selectedIndeces = randperm(size(pts1,2),8);
    selPoints1 = pts1(:,selectedIndeces);
    selPoints2 = pts2(:,selectedIndeces);
    
    % compute fundamental matrix
    F = FundamentalMatrixDLT(selPoints1,selPoints2);
     
    d = perpDist(F, pts1, pts2);
        
    if (sum(d < thresh) > maxNInliers)
        maxNInliers = sum(d < thresh);
        inliers = find(d < thresh);
        inlierRatio = maxNInliers/numOfPoints;
        bestF = F;
    end
    
    if (prob < 1 - (1 - inlierRatio^8)^i)
        break
    end
    
    
    % REMOVE MODEL CORRESPONDENCES!!! 
    
    
end    
    

end