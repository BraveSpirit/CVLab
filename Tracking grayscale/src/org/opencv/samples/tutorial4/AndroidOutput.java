package org.opencv.samples.tutorial4;

import org.opencv.core.*;

import android.util.Log;

public class AndroidOutput {
	
	
	public AndroidOutput()
	{
		
	}
	
	public void printInLog(Mat m, String tag)
	{
		Log.i(tag, m.toString());
	}
	
	public void printInLogMat(Mat m, String tag)
	{
		String msg = new String();
		double[] buf;
		msg = Integer.toString(m.cols())+"x"+Integer.toString(m.rows())+":";
		try{
		for(int i = 0; i < m.rows(); i++)
			for(int j = 0; j < m.cols(); j++)
			{
				buf = m.get(i, j);
				msg+=Integer.toString(i) + "," + Integer.toString(j) + "=(";
				for(int k = 0; k < buf.length; k++)
				{
					msg+=Double.toString(buf[k]);
					if(k!=buf.length-1)
						msg+=",";
				}
				msg+=")";
				Log.i(tag, msg);					
				msg = "";
			}
		}
		catch(Exception e)
		{
			
		}
		Log.i(tag, msg);
	}
	

}
