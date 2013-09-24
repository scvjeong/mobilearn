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
    
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
            Canvas.CLIP_SAVE_FLAG |
            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
            Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    public ChartRaderView(Context context, float chartData[]) {
        super(context);
		setFocusable(true);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
        mPath = makeFollowPath(chartData);
    }

    @Override 
    protected void onDraw(Canvas canvas) {
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
    }

    private static Path makeFollowPath(float[] chartData) {
    	float max = 200;
    	float standard = chartData[0];
    	
    	float left = Math.round((chartData[1] / standard * max));
    	float right = Math.round(chartData[2] / standard * max);
    	float top = Math.round(chartData[3] / standard * max);
    	float bottom = Math.round(chartData[4] / standard * max);
    	Log.e("chart","top : " + top);
        Path p = new Path();
        p.moveTo(max, max-top);
        p.lineTo(right+max, max);
        p.lineTo(max, bottom+max);
        p.lineTo(max-left, max);
        p.lineTo(max, max-top);
        return p;
    }
}
