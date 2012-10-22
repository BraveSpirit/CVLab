package org.opencv.samples.tutorial4;

import org.opencv.core.*;


public class MatrixFunctions {
	
	private Mat mMat1 = new Mat();
	private Mat mMat2 = new Mat();
	
	private final double resDot = 9.0;
	private final double[] resCross = {-1.4, -4, 1};
	private final double[] resMul = {0.0, 0.2, 8.8};
	
	public MatrixFunctions()
	{
		double[] buf = {1.0,0.2,2.2};
		double[] buf2 = {0.0, 1.0, 4};
		mMat1.put(0, 0, buf);
		mMat2.put(0, 0, buf2);
	}
	
	public void checkDot()
	{
		assert(resDot==mMat1.dot(mMat2));
	}
	
	public void checkCross()
	{
		MatOfDouble mResCross = new MatOfDouble();
		mResCross.fromArray(resCross);
		assert(mMat1.cross(mMat2).equals(mResCross));
	}
	
	public void checkMul()
	{
		MatOfDouble mResMul = new MatOfDouble();
		mResMul.fromArray(resMul);
		assert(mMat1.mul(mMat2).equals(resMul));
	}
	
	public void checkAll()
	{
		checkDot();
		checkCross();
		checkMul();
	}
}
