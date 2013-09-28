package com.activity.mobilearn;

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
    private String type;
    
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
            Canvas.CLIP_SAVE_FLAG |
            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
            Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    public ChartRaderView(Context context, float chartData[], String type) {
        super(context);
		setFocusable(true);
		
		this.type = type;
		float max = 0;
		if(this.type.equals("main")) {
			max = 200;
		} else if (this.type.equals("statistics")) {
			max = 240;
		}
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
        mPath = makeFollowPath(chartData, max);
        
    }

    @Override 
    protected void onDraw(Canvas canvas) {
    	
    	if(this.type.equals("main")) {
			canvas.translate(40, 10);
			canvas.saveLayerAlpha(0, 0, 430, 430, 0x88, LAYER_FLAGS);
			canvas.translate(5, 5);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(5);
			mPaint.setColor(Color.WHITE);
			canvas.drawCircle(210, 210, 210, mPaint);
			canvas.translate(10, 10);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.rgb(209, 209, 209));
			canvas.drawPath(mPath, mPaint);
			canvas.restore();
    	} else if (this.type.equals("statistics")) {
    		canvas.translate(110, 25);
			canvas.saveLayerAlpha(0, 0, 510, 510, 0x88, LAYER_FLAGS);
			canvas.translate(5, 5);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(5);
			mPaint.setColor(Color.WHITE);
			canvas.drawCircle(250, 250, 250, mPaint);
			canvas.translate(10, 10);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.rgb(209, 209, 209));
			canvas.drawPath(mPath, mPaint);
			canvas.restore();
    	}
    }

    private static Path makeFollowPath(float[] chartData, float max) {
    	
    	float standard = chartData[0];
    	
    	float left = Math.round((chartData[1] / standard * max));
    	float right = Math.round(chartData[2] / standard * max);
    	float top = Math.round(chartData[3] / standard * max);
    	float bottom = Math.round(chartData[4] / standard * max);
        Path p = new Path();
        p.moveTo(max, max-top);
        p.lineTo(right+max, max);
        p.lineTo(max, bottom+max);
        p.lineTo(max-left, max);
        p.lineTo(max, max-top);
        return p;
    }
}
