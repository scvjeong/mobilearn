package com.activity.mobilearn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class ChartLineView extends View {
    private Paint mPaint;
    private Path mPath;
    private Path bPath;
    private String type;
    private static final int interval = 90;
    
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
            Canvas.CLIP_SAVE_FLAG |
            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
            Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    public ChartLineView(Context context, float chartData[], String type) {
        super(context);
		setFocusable(true);
		
		this.type = type;
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		
		bPath = makeBarPath(chartData);						
        mPath = makeFollowPath(chartData);

    }

    @Override 
    protected void onDraw(Canvas canvas) {
    	if(this.type.equals("main")) {
    		canvas.translate(70, 50);
    		canvas.saveLayerAlpha(0, 0, 650, 600, 0x88, LAYER_FLAGS);
    		canvas.translate(5, 5);
    		mPaint.setStyle(Paint.Style.STROKE);
    		mPaint.setStrokeWidth(5);
    		mPaint.setColor(Color.WHITE);
    		canvas.drawPath(bPath, mPaint);
    		canvas.translate(20, 0);
    		mPaint.setStyle(Paint.Style.FILL);
    		mPaint.setColor(Color.rgb(118,122,173));
    		canvas.drawPath(mPath, mPaint);
    		
    		canvas.restore();
    	} else if (this.type.equals("statistics")) {
    		canvas.translate(70, 50);
    		canvas.saveLayerAlpha(0, 0, 650, 600, 0x88, LAYER_FLAGS);
    		canvas.translate(5, 5);
    		mPaint.setStyle(Paint.Style.STROKE);
    		mPaint.setStrokeWidth(5);
    		mPaint.setColor(Color.WHITE);
    		canvas.drawPath(bPath, mPaint);
    		canvas.translate(20, 0);
    		mPaint.setStyle(Paint.Style.FILL);
    		mPaint.setColor(Color.rgb(118,122,173));
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
        p.lineTo(interval*1, m);
        p.lineTo(interval*2, t1);
        p.lineTo(interval*3, w);
        p.lineTo(interval*4, t2);
        p.lineTo(interval*5, f);
        p.lineTo(interval*6, s2);
        p.lineTo(interval*6, max);
        p.lineTo(0, max);
        p.lineTo(0, s1);
        return p;
    }
}
