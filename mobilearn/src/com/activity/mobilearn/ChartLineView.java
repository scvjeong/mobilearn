package com.activity.mobilearn;

import com.lib.mobilearn.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class ChartLineView extends View {
    private Paint mPaint;
    private Path mPath;
    private Path bPath;
    private String type;
    private float chartData[];
    private static final int interval = 90;
    
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
            Canvas.CLIP_SAVE_FLAG |
            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
            Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    public ChartLineView(Context context, float data[], String type) {
        super(context);
		setFocusable(true);
		
		this.type = type;
		chartData = data;
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		
		bPath = makeBarPath(chartData);						
        mPath = makeFollowPath(chartData);

    }

    @Override 
    protected void onDraw(Canvas canvas) {
    	if(this.type.equals("main")) {
    		canvas.translate(70, 50);
    		
    		mPaint.setColor(Color.WHITE);
    		mPaint.setTextSize(30);
    		String week[] = {"S","M","T","W","T","F","S"};
    		Utils utils = new Utils();
    		int i, idx, wFlag = utils.getWeekForNumber(android.text.format.DateFormat.format("EEE", System.currentTimeMillis()).toString());
    		for(i=0; i<7; i++){
    			idx = (wFlag+1)+i;
    			if(idx>6)
    				idx = idx - 7;
    			canvas.drawText(week[idx], 15+(interval*i), 285, mPaint);
    		}
    		
    		String persent = Math.round(chartData[7]/180*100) + "%";
    		mPaint.setTextSize(65);
    		canvas.drawText(persent, 310, 26, mPaint);
    		mPaint.setTextSize(30);
    		canvas.drawText("TODAY'S", 440, 0, mPaint);
    		canvas.drawText("SCORE", 440, 30, mPaint);
    		
    		canvas.saveLayerAlpha(0, 0, 650, 600, 0x88, LAYER_FLAGS);
    		canvas.translate(0, 30);
    		mPaint.setStyle(Paint.Style.STROKE);
    		mPaint.setStrokeWidth(8);
    		mPaint.setColor(Color.WHITE);
    		canvas.drawPath(bPath, mPaint);
    		canvas.translate(20, 0);
    		mPaint.setStyle(Paint.Style.FILL);
    		mPaint.setColor(Color.rgb(118,122,173));
    		canvas.drawPath(mPath, mPaint);
    		
    		canvas.restore();
    	} else if (this.type.equals("statistics")) {
    		
    		canvas.translate(70, 70);
    		
    		mPaint.setColor(Color.WHITE);
    		mPaint.setTextSize(30);
    		String week[] = {"S","M","T","W","T","F","S"};
    		Utils utils = new Utils();
    		int i, idx, wFlag = utils.getWeekForNumber(android.text.format.DateFormat.format("EEE", System.currentTimeMillis()).toString());
    		for(i=0; i<7; i++){
    			idx = (wFlag+1)+i;
    			if(idx>6)
    				idx = idx - 7;
    			canvas.drawText(week[idx], 15+(interval*i), 310, mPaint);
    		}
    		
    		String persent = Math.round(chartData[7]/200*100) + "%";
    		mPaint.setTextSize(65);
    		canvas.drawText(persent, 310, 46, mPaint);
    		mPaint.setTextSize(30);
    		canvas.drawText("TODAY'S", 440, 20, mPaint);
    		canvas.drawText("SCORE", 440, 50, mPaint);
    		
    		canvas.saveLayerAlpha(0, 0, 650, 600, 0x88, LAYER_FLAGS);
    		canvas.translate(5, 5);
    		mPaint.setStyle(Paint.Style.STROKE);
    		mPaint.setStrokeWidth(5);
    		canvas.drawPath(bPath, mPaint);
    		canvas.translate(20, 0);
    		mPaint.setStyle(Paint.Style.FILL);
    		mPaint.setColor(Color.rgb(209, 209, 209));
    		canvas.drawPath(mPath, mPaint);
    	}
    }
    
    private static Path makeBarPath(float chartData[]) {
    	
    	float max = chartData[0];        	
        Path p = new Path();
        p.moveTo(0, 0);
        p.lineTo(0, max+20);
        p.lineTo(interval*6+40, max+20);

        return p;
    }

    private static Path makeFollowPath(float chartData[]) {
   	
    	float max = chartData[0];
    	float s1 = Math.round(max - chartData[1]);
    	float m = Math.round(max - chartData[2]);
    	float t1 = Math.round(max - chartData[3]);
    	float w = Math.round(max - chartData[4]);
    	float t2 = Math.round(max - chartData[5]);
    	float f = Math.round(max - chartData[6]);
    	float s2 = Math.round(max - chartData[7]);
    	
        Path p = new Path();
        p.moveTo(0, s1);
        for(int i=1; i<7; i++) {
	        p.lineTo(interval*i, Math.round(max - chartData[i+1]));
        }
        p.lineTo(interval*6, max);
        p.lineTo(0, max);
        p.lineTo(0, s1);
        return p;
    }
}
