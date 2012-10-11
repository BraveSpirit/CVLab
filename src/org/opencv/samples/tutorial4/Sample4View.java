package org.opencv.samples.tutorial4;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

class Sample4View extends SampleViewBase {

    public static final int     VIEW_MODE_RGB     = 0;
    public static final int     VIEW_MODE_FEATURES_KLT = 1;
    public static final int     VIEW_MODE_FEATURES = 2;
    
    private Mat mYuv;
    private Mat mRgba;
    private Mat mGraySubmat;
    private Mat mIntermediateMat;

    private int mViewMode;
	private Bitmap mBitmap;
	
	private Context context;
	
	private FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.FAST);
	//private MatOfKeyPoint keyPts = new MatOfKeyPoint();

    public Sample4View(Context context) {
        super(context);
        this.context = context;
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
    protected Bitmap processFrame(byte[] data, MatOfKeyPoint keyPts, Mat mImg) {
        mYuv.put(0, 0, data);
        
        final int viewMode = mViewMode;

        switch (viewMode) {
        case VIEW_MODE_RGB:
            Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            break;
        case VIEW_MODE_FEATURES_KLT:
        	Imgproc.cvtColor(mYuv, mRgba, Imgproc.COLOR_YUV420sp2RGB, 4);
            //try{
        	featureDetector.detect(mGraySubmat, keyPts);
        	Mat buf = new Mat();
        	Imgproc.cvtColor(mRgba, buf, Imgproc.COLOR_RGBA2RGB);
        	Features2d.drawKeypoints(buf, keyPts, buf);
        	mImg = buf;
        	Imgproc.cvtColor(buf, mRgba, Imgproc.COLOR_RGB2RGBA);       	
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

    public native void FindFeatures(long matAddrGr, long matAddrRgba);

    public void setViewMode(int viewMode) {
		mViewMode = viewMode;
    }
}