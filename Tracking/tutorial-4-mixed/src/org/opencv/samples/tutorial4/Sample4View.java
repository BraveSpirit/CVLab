package org.opencv.samples.tutorial4;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.calib3d.*;
import org.opencv.core.TermCriteria;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
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
    private Mat mFund;
    private Mat mHom;

    private int mViewMode;
	private Bitmap mBitmap;
	private static final Size wSize = new Size(3,3);
	
	private static final TermCriteria tCriteria = new TermCriteria();
	private static final double[] params = {20,0.3};
		
	
    private Mat descriptors = new Mat();
    private Mat descriptorsOld = new Mat();
    private List<DMatch> lMatches;
    private List<KeyPoint> lPtsToDraw;
    private DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.BRIEF);
    private DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);
	private FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.FAST);
	private MatOfKeyPoint mKeyPts = new MatOfKeyPoint();
	private MatOfKeyPoint mKeyPtsOld = new MatOfKeyPoint();
	private MatOfKeyPoint mPtsToDraw;
	
	private MatOfByte status = new MatOfByte();
	private MatOfFloat error = new MatOfFloat();
	
    private List<KeyPoint> lKeyPts = new ArrayList<KeyPoint>();
	
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

        int length = data.length;
        
        final int viewMode = mViewMode;

        switch (viewMode) {
        case VIEW_MODE_RGB:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            break;
        case VIEW_MODE_FEATURES_KLT:
        	//try{
	        	mRgb = new Mat();
	        	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
	        	Imgproc.cvtColor(mRgba, mRgb, Imgproc.COLOR_RGBA2RGB);
	        
		    	    /*
				     * Native code usage start
				     */
				    
		        KeyPoint[] returnedKeyPts = FindFeatures(mRgba.nativeObj, 50);
			        
	            mKeyPts.fromArray(returnedKeyPts);
			        
				    /*
				     * Native code usage end
				     */
		        if(mRgbOld != null){	
		        		
				    error = new MatOfFloat();
				    
				    lPtsToDraw = new ArrayList<KeyPoint>();
				    
				    MatOfDMatch mMatches = new MatOfDMatch();
				    lKeyPts = mKeyPts.toList();    
				    
				    mPtsToDraw = new MatOfKeyPoint();
				
				    
				    descriptorExtractor.compute(mRgb, mKeyPts, descriptors);
				    //descriptorExtractor.compute(mRgbOld, mKeyPtsOld, descriptorsOld);
				    descriptorMatcher.match(descriptors, descriptorsOld, mMatches);
				    
				    
				    lMatches = mMatches.toList();
				    for(int i = 0; i < lMatches.size(); i++)
				    {
				    	DMatch buf = lMatches.get(i);
				    	lPtsToDraw.add(lKeyPts.get(buf.queryIdx));
				    }
				    
				    mPtsToDraw.fromList(lPtsToDraw);
				    
				    //Video.calcOpticalFlowPyrLK(mRgbOld, mRgb, m2fOld, m2f, status, error, wSize, 5, tCriteria, Video.OPTFLOW_USE_INITIAL_FLOW, 0.1 );
				    //mFund = Calib3d.findFundamentalMat(m2f, m2fOld, Calib3d.FM_8POINT, 1.0, 0.99, status);
				    //mHom = Calib3d.findHomography(m2fOld, m2f);
				    
				    //Video.calcOpticalFlowPyrLK(mRgbOld, mRgb, m2fOld, m2f, status, error);
				    Mat buf = new Mat();
				    //Imgproc.cvtColor(mRgba, buf, Imgproc.COLOR_RGBA2RGB);
				    if(!mPtsToDraw.empty()){
				    	Features2d.drawKeypoints(mRgb, mPtsToDraw, buf);
				    	Imgproc.cvtColor(buf, mRgba, Imgproc.COLOR_RGB2RGBA);
				    }
	        	}
	        	else
	        	{
				    descriptorExtractor.compute(mRgb, mKeyPts, descriptors);
	        	}
	        		
	        	mRgbOld = mRgb;
	        	mKeyPtsOld = mKeyPts;
	        	descriptorsOld = descriptors;
        	/*
        	}
        	catch(Exception ex)
        	{
        		
        	}
		    */
		    
		    
            break;
        case VIEW_MODE_FEATURES:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            //FindFeatures(mGraySubmat.getNativeObjAddr(), mRgba.getNativeObjAddr());
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

    public native KeyPoint[] FindFeatures(long matAddrRgba, int pts);

    public void setViewMode(int viewMode) {
		mViewMode = viewMode;
    }
}
