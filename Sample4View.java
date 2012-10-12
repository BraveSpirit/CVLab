package org.opencv.samples.tutorial4;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

class Sample4View extends SampleViewBase {

    public static final int     VIEW_MODE_RGB     = 0;
    public static final int     VIEW_MODE_FEATURES_KLT = 1;
    public static final int     VIEW_MODE_FEATURES = 2;
    
    private Mat mYuv;
    private Mat mRgba;
    private Mat mRgb;
    private Mat mGraySubmat;
    private Mat mIntermediateMat;
    private Mat mRgbaOld;
    private Mat mRgbOld;

    private int mViewMode;
	private Bitmap mBitmap;
	private static final Size wSize = new Size(15,15);
	
	private static final TermCriteria tCriteria = new TermCriteria();
	private static final double[] params = {20,0.3};
		
	private FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.FAST);
	private MatOfKeyPoint mKeyPts = new MatOfKeyPoint();
	private MatOfKeyPoint mKeyPtsOld = new MatOfKeyPoint();
	private MatOfKeyPoint mTrackedKeyPoints = new MatOfKeyPoint();
	
	private MatOfByte status = new MatOfByte();
	private Mat mStatus = new Mat();
	private MatOfFloat error = new MatOfFloat();
	
    public Sample4View(Context context) {
        super(context);
    }
    
	@Override
	protected void onPreviewStarted(int previewWidtd, int previewHeight) {
        // initialize Mats before usage
        mYuv = new Mat(getFrameHeight() + getFrameHeight() / 2, getFrameWidth(), CvType.CV_8UC1);
        mGraySubmat = mYuv.submat(0, getFrameHeight(), 0, getFrameWidth());

        mRgba = new Mat();
        mIntermediateMat = new Mat();
        
        mBitmap = Bitmap.createBitmap(previewWidtd, previewHeight, Bitmap.Config.ARGB_8888);
	}

	@Override
	protected void onPreviewStopped() {
		
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		
        // Explicitly deallocate Mats
        if (mYuv != null)
            mYuv.release();
        if (mRgba != null)
            mRgba.release();
        if (mGraySubmat != null)
            mGraySubmat.release();
        if (mIntermediateMat != null)
            mIntermediateMat.release();

        mYuv = null;
        mRgba = null;
        mGraySubmat = null;
        mIntermediateMat = null;
		
	}

    @Override
    protected Bitmap processFrame(byte[] data) {
        mYuv.put(0, 0, data);

        final int viewMode = mViewMode;

        switch (viewMode) {
        case VIEW_MODE_RGB:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            break;
        case VIEW_MODE_FEATURES_KLT:
        	
        	mRgb = new Mat();
        	
        	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
        	Imgproc.cvtColor(mRgba, mRgb, Imgproc.COLOR_RGBA2RGB);
        	
        	featureDetector.detect(mGraySubmat, mKeyPts);	
        	
        	if(mRgbOld != null){
	            MatOfPoint2f m2fOld = new MatOfPoint2f();
			    MatOfPoint2f m2f	= new MatOfPoint2f(); 
	            //Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
			    
			    
        		KeyPoint[] kPOld = mKeyPtsOld.toArray();
        		for(int i = 0; i < kPOld.length; i++)
        		{
        			m2fOld.fromArray(kPOld[i].pt);
        		}
        		
        		KeyPoint[] kP = mKeyPts.toArray();
        		for(int i = 0; i < kPOld.length; i++)
        		{
        			m2f.fromArray(kP[i].pt);
        		}
			    
			    error = new MatOfFloat();
			    
			    Video.calcOpticalFlowPyrLK(mRgbOld, mRgb, m2fOld, m2f, status, error, wSize, 5, tCriteria, Video.OPTFLOW_USE_INITIAL_FLOW, 0.1);			    
			    		    
			    status.assignTo(mStatus);
			    
			    //byte[] statusVec = status.toArray();
			    //Mat mStatus = status.get(row, col, data);			    
			    
			    //Turn MatOfPoint2f into array of Point
			    Point[] m2fPoints = m2f.toArray();
			   			    
			    //New array of KeyPoint to contain m2fPoints elements
			    KeyPoint[] m2fKeyPoints = new KeyPoint[m2fPoints.length];
			    			    
			    //Convert m2f to KeyPoint[]!!!!
			    // Worst way ever to convert from Point to KeyPoint
			    for (int i = 0; i < m2fPoints.length; i++)
			    {
			    	m2fKeyPoints[i]= new KeyPoint();
			    	m2fKeyPoints[i].pt = m2fPoints[i];
			    }
			    
			    for (int j = 0; j < status.rows();j++){
			    	for (int i = 0; i < status.cols(); i++)
			    	{
			    		//double[] outData = null;
			    		//outData = status.get(i, j);
			    		//if(outData[0] == 1)
			    		if(mStatus.get(i, j)[0] == 1)
			    		{
			    			m2fKeyPoints[i] = new KeyPoint();
			    			m2fKeyPoints[i].pt = m2fPoints[i];
			    			
			    		}
			    	}			    
			    }	
			    
			    if (m2fKeyPoints.length > 0)
			    {
			    	mTrackedKeyPoints.fromArray(m2fKeyPoints);
				    Mat buf = new Mat();
				    Imgproc.cvtColor(mRgba, buf, Imgproc.COLOR_RGBA2RGB);
				    Features2d.drawKeypoints(buf, mTrackedKeyPoints, buf);
				    Imgproc.cvtColor(buf, mRgba, Imgproc.COLOR_RGB2RGBA);
			    }
			    else
			    {
				    Mat buf = new Mat();
				    Imgproc.cvtColor(mRgba, buf, Imgproc.COLOR_RGBA2RGB);
				    Features2d.drawKeypoints(buf, mKeyPtsOld, buf);
				    Imgproc.cvtColor(buf, mRgba, Imgproc.COLOR_RGB2RGBA);
			    }
			    

        	}
        	
        	

		     
        	mRgbOld = mRgb;
        	mKeyPtsOld = mKeyPts;
		    
		    
            break;
        case VIEW_MODE_FEATURES:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            FindFeatures(mGraySubmat.getNativeObjAddr(), mRgba.getNativeObjAddr());
            break;
        }

        Bitmap bmp = mBitmap;

        try {
            Utils.matToBitmap(mRgba, bmp);
        } catch(Exception e) {
            Log.e("org.opencv.samples.puzzle15", "Utils.matToBitmap() throws an exception: " + e.getMessage());
            bmp.recycle();
            bmp = null;
        }

        return bmp;
    }

    private Mat GetPlanarHomography(Mat fundamentalMatrix, Point[] oldPoints, Point[] newPoints){
    	
    	//Convert Point objects to float arrays
    	float[] x1 = new float[3];
    	x1[0] = (float) oldPoints[0].x;
    	x1[1] = (float) oldPoints[0].y;
    	x1[2] = 1;
    	float[] x2 = new float[3];
    	x2[0] = (float) oldPoints[1].x;
    	x2[1] = (float) oldPoints[1].y;
    	x2[2] = 1;
    	float[] x3 = new float[3];
    	x3[0] = (float) oldPoints[2].x;
    	x3[1] = (float) oldPoints[2].y;
    	x3[2] = 1;
    	
    	float[] x1_new = new float[3];
    	x1_new[0] = (float) newPoints[0].x;
    	x1_new[1] = (float) newPoints[0].y;
    	x1_new[2] = 1;
    	float[] x2_new = new float[3];
    	x2_new[0] = (float) newPoints[1].x;
    	x2_new[1] = (float) newPoints[1].y;
    	x2_new[2] = 1;
    	float[] x3_new = new float[3];
    	x3_new[0] = (float) newPoints[2].x;
    	x3_new[1] = (float) newPoints[2].y;
    	x3_new[2] = 1;    	
    	
    	//solution to transpose(F)*e = 0 
    	Mat e = new Mat();    	
    	
    	//create right-hand side of equation
    	Size sizeZeroVec = new Size(3,1);
    	Mat zeroVec = new Mat(sizeZeroVec,0);
    	
    	Core.solve(fundamentalMatrix.t(), zeroVec, e, Core.SVD_FULL_UV);
    	
    	Size sizeCrossMatrixE = new Size(3,3);
    	Mat crossMatrixE = new Mat(sizeCrossMatrixE,0);
    	
    	
    	
    	
    	//Create v (the solution to Mv = b)
    	
    	//b, the right-hand side of the equation
    	float[] b = new float[3];

    	
    	
    	Mat mat = new Mat();
    	return mat;
    }
    
    public native void FindFeatures(long matAddrGr, long matAddrRgba);

    public void setViewMode(int viewMode) {
		mViewMode = viewMode;
    }
}
