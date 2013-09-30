package com.activity.mobilearn;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class ChartRaderView extends View {
	private Paint mPaint;
    private Path mPath;
    private Path mPathCenter;
    private String type;
    private float chartData[];
    
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
            Canvas.CLIP_SAVE_FLAG |
            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
            Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    public ChartRaderView(Context context, float data[], String type) {
        super(context);
		setFocusable(true);
		
		this.type = type;
		float max = 0;
		if(this.type.equals("main")) {
			max = 180;
		} else if (this.type.equals("statistics")) {
			max = 180;
		}
		chartData = data;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
        mPath = makeFollowPath(chartData, max);
        mPathCenter = makeFollowPathCenter();        
    }

    @Override 
    protected void onDraw(Canvas canvas) {
    	
    	int i;
		String chartDataString[] = new String[5];
		for(i=0;i<chartData.length;i++){
			chartDataString[i] = String.valueOf(Math.round(chartData[i]));
			while(chartDataString[i].length() < 3){
				chartDataString[i] = "0" + chartDataString[i];
			}
		}
    	
    	if(this.type.equals("main")) {
    		mPaint.setColor(Color.WHITE);
    		mPaint.setTextSize(30);
    		canvas.drawText("초기", 228, 68, mPaint);
    		canvas.drawText(chartDataString[1], 230, 98, mPaint);
    		canvas.drawText("감각", 42, 250, mPaint);
    		canvas.drawText(chartDataString[2], 44, 280, mPaint);
    		canvas.drawText("단기", 415, 250, mPaint);
    		canvas.drawText(chartDataString[3], 417, 280, mPaint);
    		canvas.drawText("장기", 228, 463, mPaint);
    		canvas.drawText(chartDataString[4], 230, 433, mPaint);
			canvas.translate(30, 30);
			canvas.saveLayerAlpha(0, 0, 450, 450, 0x88, LAYER_FLAGS);
			canvas.translate(5, 5);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(5);
			canvas.drawCircle(220, 220, 220, mPaint);
			canvas.translate(40, 40);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.rgb(209, 209, 209));
			canvas.drawPath(mPath, mPaint);
			canvas.translate(180, 180);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.WHITE);
			canvas.drawPath(mPathCenter, mPaint);
			canvas.restore();
    	} else if (this.type.equals("statistics")) {
    		int size = 440;
        	
    		mPaint.setColor(Color.WHITE);
    		mPaint.setTextSize(30);
    		canvas.drawText("초기", 339, 60, mPaint);
    		canvas.drawText(chartDataString[1], 341, 90, mPaint);
    		canvas.drawText("감각", 150, 243, mPaint);
    		canvas.drawText(chartDataString[2], 152, 273, mPaint);
    		canvas.drawText("단기", 530, 243, mPaint);
    		canvas.drawText(chartDataString[3], 532, 273, mPaint);
    		canvas.drawText("장기", 339, 460, mPaint);
    		canvas.drawText(chartDataString[4], 341, 430, mPaint);
    		canvas.translate(143, 25);
			canvas.saveLayerAlpha(0, 0, size+10, size+10, 0x88, LAYER_FLAGS);
			canvas.translate(5, 5);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(5);			
			canvas.drawCircle(size/2, size/2, size/2, mPaint);
			canvas.translate(40, 40);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.rgb(209, 209, 209));
			canvas.drawPath(mPath, mPaint);
			canvas.translate(180, 180);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.WHITE);
			canvas.drawPath(mPathCenter, mPaint);
			canvas.restore();
    	}
    }

    private static Path makeFollowPathCenter() {
        Path p = new Path();
        p.moveTo(0, -20);
        p.lineTo(20, 0);
        p.lineTo(0, 20);
        p.lineTo(-20, 0);
        p.lineTo(0, -20);
        return p;
    }
    
    private static Path makeFollowPath(float[] chartData, float max) {
    	
    	float standard = chartData[0];
    	
    	float left = Math.round(chartData[2] / standard * max * 0.7) + Math.round(max*0.3);
    	float right = Math.round(chartData[3] / standard * max * 0.7) + Math.round(max*0.3);
    	float top = Math.round(chartData[1] / standard * max * 0.7) + Math.round(max*0.3);
    	float bottom = Math.round(chartData[4] / standard * max * 0.7) + Math.round(max*0.3);
        Path p = new Path();
        p.moveTo(max, max-top);
        p.lineTo(right+max, max);
        p.lineTo(max, bottom+max);
        p.lineTo(max-left, max);
        p.lineTo(max, max-top);
        return p;
    }
}
