function [normalzedPts, T] = normalisePoints2D(pts)

% pts is 2xn or 3xn
% returns normalized 2d points in homogenous coordinates

if size(pts,1) == 2
    pts = [pts;ones(1,size(pts,2))];
end

normalzedPts = ones(size(pts));

% set homogenous coordinates to 1
pts(1,:) = pts(1,:)./pts(3,:);
pts(2,:) = pts(2,:)./pts(3,:);
pts(3,:) = 1;

% centroid of finite points
c = mean(pts(1:2,:),2);
% shift origin to centroid.
normalzedPts(1,:) = pts(1,:)-c(1);
normalzedPts(2,:) = pts(2,:)-c(2);

dist = sqrt(normalzedPts(1,:).^2 + normalzedPts(2,:).^2);
meandist = mean(dist);

s = sqrt(2)/meandist;

T = [s 0 -s*c(1)
     0 s -s*c(2)
     0 0 1];

normalzedPts = T*pts;

end