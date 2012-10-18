% Normalization of 2d-pts
% Inputs: 
%           x1s = 2d points
% Outputs:
%           nxs = normalized points
%           T = normalization matrix
function [nxs, T] = normalizePoints2d(x1s)
    

numOfPoints = size(x1s,2);

%data normalization
%first compute centroid

x1s_centroid = sum(x1s,2)/numOfPoints;

% Translate centroid to origin

x1s_translated = zeros(size(x1s));

x1s_translated(1,:) = x1s(1,:) - x1s_centroid(1);
x1s_translated(2,:) = x1s(2,:) - x1s_centroid(2);

%then, compute scale

x1s_RMS = sum(sqrt(diag(x1s_translated'*x1s_translated)))/numOfPoints;

x1s_scale = sqrt(2)/x1s_RMS;

%create transformation matrix T
T = [x1s_scale, 0, -x1s_centroid(1)*x1s_scale;
    0, x1s_scale, -x1s_centroid(2)*x1s_scale;
    0, 0, 1];

%and normalize the points according to the transformations

nxs = T*x1s;

end