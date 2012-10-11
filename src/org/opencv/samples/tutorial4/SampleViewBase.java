package org.opencv.samples.tutorial4;

import java.io.IOException;
import java.util.List;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.opencv.video.Video;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.features2d.*;

@TargetApi(11)
public abstract class SampleViewBase extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final String TAG = "Sample::SurfaceView";

    private Camera              mCamera;
    private SurfaceHolder       mHolder;
    private int                 mFrameWidth;
    private int                 mFrameHeight;
    private byte[]              mFrame;
    private boolean             mThreadRun;
    private byte[]              mBuffer;
    private MatOfKeyPoint 		keyPts;
    private MatOfKeyPoint		keyPtsOld;
    private Mat					mImg;
    private Mat					mImgOld;
    // optical flow variables block
    private MatOfByte			status; 		// status vector
    private MatOfFloat			err;			// error vector
    
    public SampleViewBase(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    public int getFrameWidth() {
        return mFrameWidth;
    }

    public int getFrameHeight() {
        return mFrameHeight;
    }

    public void setPreview() throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mCamera.setPreviewTexture( new SurfaceTexture(10) );
        else
        	mCamera.setPreviewDisplay(null);
	}
    
    public boolean openCamera() {
        Log.i(TAG, "openCamera");
        releaseCamera();
        mCamera = Camera.open();
        if(mCamera == null) {
        	Log.e(TAG, "Can't open camera!");
        	return false;
        }

        mCamera.setPreviewCallbackWithBuffer(new PreviewCallback() {
            public void onPreviewFrame(byte[] data, Camera camera) {
                synchronized (SampleViewBase.this) {
                    System.arraycopy(data, 0, mFrame, 0, data.length);
                    SampleViewBase.this.notify(); 
                }
                camera.addCallbackBuffer(mBuffer);
            }
        });
        return true;
    }
    
    public void releaseCamera() {
        Log.i(TAG, "releaseCamera");
        mThreadRun = false;
        synchronized (this) {
	        if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            }
        }
        onPreviewStopped();
    }
    
    public void setupCamera(int width, int height) {
        Log.i(TAG, "setupCamera");
        synchronized (this) {
            if (mCamera != null) {
                Camera.Parameters params = mCamera.getParameters();
                List<Camera.Size> sizes = params.getSupportedPreviewSizes();
                mFrameWidth = width;
                mFrameHeight = height;

                // selecting optimal camera preview size
                {
                    int  minDiff = Integer.MAX_VALUE;
                    for (Camera.Size size : sizes) {
                        if (Math.abs(size.height - height) < minDiff) {
                            mFrameWidth = size.width;
                            mFrameHeight = size.height;
                            minDiff = Math.abs(size.height - height);
                        }
                    }
                }

                params.setPreviewSize(getFrameWidth(), getFrameHeight());
                
                List<String> FocusModes = params.getSupportedFocusModes();
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                {
                	params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }            
                
                mCamera.setParameters(params);
                
                /* Now allocate the buffer */
                params = mCamera.getParameters();
                int size = params.getPreviewSize().width * params.getPreviewSize().height;
                size  = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
                mBuffer = new byte[size];
                /* The buffer where the current frame will be copied */
                mFrame = new byte [size];
                mCamera.addCallbackBuffer(mBuffer);

    			try {
    				setPreview();
    			} catch (IOException e) {
    				Log.e(TAG, "mCamera.setPreviewDisplay/setPreviewTexture fails: " + e);
    			}

                /* Notify that the preview is about to be started and deliver preview size */
                onPreviewStarted(params.getPreviewSize().width, params.getPreviewSize().height);

                /* Now we can start a preview */
                mCamera.startPreview();
            }
        }
    }
    
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        setupCamera(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        (new Thread(this)).start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        releaseCamera();
    }



    /* The bitmap returned by this method shall be owned by the child and released in onPreviewStopped() */
    protected abstract Bitmap processFrame(byte[] data, MatOfKeyPoint keyPts, Mat mImg);

    /**
     * This method is called when the preview process is being started. It is called before the first frame delivered and processFrame is called
     * It is called with the width and height parameters of the preview process. It can be used to prepare the data needed during the frame processing.
     * @param previewWidth - the width of the preview frames that will be delivered via processFrame
     * @param previewHeight - the height of the preview frames that will be delivered via processFrame
     */
    protected abstract void onPreviewStarted(int previewWidtd, int previewHeight);

    /**
     * This method is called when preview is stopped. When this method is called the preview stopped and all the processing of frames already completed.
     * If the Bitmap object returned via processFrame is cached - it is a good time to recycle it.
     * Any other resources used during the preview can be released.
     */
    protected abstract void onPreviewStopped();

    public void run() {
        mThreadRun = true;
        Log.i(TAG, "Starting processing thread");
        while (mThreadRun) {
            Bitmap bmp = null;

            synchronized (this) {
                try {
                    this.wait();
                    mImgOld = mImg;
                    this.openCamera();
                    bmp = processFrame(mFrame, keyPts, mImg);
                    try{
	                    if(keyPtsOld != null)
	                    {
	                    	//Video.buildOpticalFlowPyramid(img, pyramid, winSize, maxLevel)
	                    	Size wSize = new Size(3,3);
	                    	
	                    	/* 
	                    	 * setting termination criteria: iter, eps
	                    	 */
	                    	TermCriteria tCriteria = new TermCriteria();
	                    	double[] params = {20,0.3};
	                    	tCriteria.set(params);
	                    	
	                    	//Video.calcOpticalFlowPyrLK(prevImg, nextImg, prevPts, nextPts, status, err, winSize, maxLevel)
	                    	Video.calcOpticalFlowPyrLK(mImgOld, mImg, new MatOfPoint2f(keyPtsOld), new MatOfPoint2f(keyPts), status, err, wSize, 5, tCriteria, Video.OPTFLOW_LK_GET_MIN_EIGENVALS, 0.1);
	                    
	                    }
                    }
                    catch(Exception e)
                    {
                    
                    }
                    	//bmp = processFrame(mFrame);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            keyPtsOld = keyPts;
            		
            if (bmp != null) {
                Canvas canvas = mHolder.lockCanvas();
                if (canvas != null) {
                    canvas.drawBitmap(bmp, (canvas.getWidth() - getFrameWidth()) / 2, (canvas.getHeight() - getFrameHeight()) / 2, null);
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}