package com.activity.mobilearn;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class TimeTableView extends View {
	private Paint mPaint;
    private Path mPath;

    
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG |
            Canvas.CLIP_SAVE_FLAG |
            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
            Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    public TimeTableView(Context context) {
        super(context);
		setFocusable(true);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
    }

    @Override 
    protected void onDraw(Canvas canvas) {
    	int size = 150;

		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.rgb(172, 172, 172));
		mPaint.setTextSize(30);
		canvas.drawText("S", 240, 180, mPaint);
		canvas.drawText("E", 240, 235, mPaint);
		mPaint.setTextSize(50);
		canvas.drawText("18:00", 270, 180, mPaint);
		canvas.drawText("24:00", 270, 240, mPaint);
		
    	mPaint.setColor(Color.rgb(127, 50, 59));
		canvas.translate(170, 50);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(90);
		canvas.drawCircle(size, size, size, mPaint);

		canvas.saveLayerAlpha(-50, -50, 150, 500, 0x88, LAYER_FLAGS);
		mPaint.setColor(Color.rgb(10, 10, 10));
		canvas.drawCircle(size, size, size, mPaint);
		

		canvas.restore();

    }
}
