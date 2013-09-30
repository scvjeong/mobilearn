package com.lib.mobilearn;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size=1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
	}
	public int getWeekForNumber(String w)
	{
		if(w.equals("��")){
    		return 0;
    	} else if(w.equals("��")){
    		return 1;
    	} else if(w.equals("ȭ")){
    		return 2;    	
    	} else if(w.equals("��")){
    		return 3;
    	} else if(w.equals("��")){
    		return 4;
    	} else if(w.equals("��")){
    		return 5;
    	} else if(w.equals("��")){
    		return 6;
    	}
		return 0;
	}
}
