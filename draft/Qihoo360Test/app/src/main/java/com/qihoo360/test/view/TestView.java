package com.qihoo360.test.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.qihoo360.test.RecogRate;

import java.util.List;

/**
 * Created by mashuai-s on 2017/7/10.
 */

public class TestView extends View {
    private String TAG = TestView.class.getSimpleName();
    private Paint mRectPaint;
    private Paint mTextPaint;
    private Paint mArcPaint;
    private Paint mLinePaint;
    private RectF mRectF;
    private int width;
    private List<RecogRate> lstSource;

    public TestView(Context context) {
        this(context, null);
        initView();
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setColor(Color.YELLOW);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(0);

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(70);
        mArcPaint.setStyle(Paint.Style.STROKE);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(dip2px(getContext(), 4));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "onSizeChanged w->" + w + " oldw->" + oldw);
        width = w;
        mRectF = new RectF((float) (w * 0.2), (float) (w * 0.2), (float) (w * 0.8), (float) (w * 0.8));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getScreenW(getContext());
        int height = width + lstSource.size() * dip2px(getContext(), 50);

        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int widthResult = 0;
        if (modeWidth == MeasureSpec.EXACTLY) {
            widthResult = 1;
        } else if (modeWidth == MeasureSpec.AT_MOST) {
            widthResult = 2;
        } else if (modeWidth == MeasureSpec.UNSPECIFIED) {
            widthResult = 3;
        }

        int heightResult = 0;
        if (modeHeight == MeasureSpec.EXACTLY) {
            heightResult = 1;
        } else if (modeHeight == MeasureSpec.AT_MOST) {
            heightResult = 2;
        } else if (modeHeight == MeasureSpec.UNSPECIFIED) {
            heightResult = 3;
        }

        Log.e(TAG, "-> onMeasure() sizeWidth=" + sizeWidth + " sizeHeight=" + sizeHeight);
        Log.e(TAG, "-> onMeasure() widthResult=" + widthResult + " heightResult=" + heightResult);
        setMeasuredDimension(width, height);
    }

    private int getScreenW(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        return metric.widthPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawRect(mRectF, mRectPaint);

        //draw text
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTextSize(dip2px(getContext(), 40));
        float numTextWidth = mTextPaint.measureText("88");
        int x = (int) (width / 2 - numTextWidth / 2);
        int y = (int) (mRectF.top + mRectF.height() / 2);
        canvas.drawText("88", x, y, mTextPaint);

        mTextPaint.setTextSize(dip2px(getContext(), 12));
        numTextWidth = mTextPaint.measureText("近30天识别总量");
        x = (int) (width / 2 - numTextWidth / 2);
        y += dip2px(getContext(), 20);
        canvas.drawText("近30天识别总量", x, y, mTextPaint);

        float startArc = 0;
        float startX = mRectF.left / 2;
        float startY = mRectF.bottom + dip2px(getContext(), 50);

        for (RecogRate item : lstSource) {
            float angle = 360 * item.rate / 100;
            mArcPaint.setColor(getContext().getResources().getColor(item.colorId));
            canvas.drawArc(mRectF, startArc, angle, false, mArcPaint);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), item.iconId);
            canvas.drawBitmap(bitmap, startX, startY, null);

            mTextPaint.setTextSize(dip2px(getContext(), 14));
            mTextPaint.setColor(getContext().getResources().getColor(item.colorId));
            String text = item.number + "个 占/" + item.rate + "%";
            float textX = mRectF.left;
            float textY = startY + dip2px(getContext(), 12);
            canvas.drawText(text, textX, textY, mTextPaint);

            mLinePaint.setColor(getContext().getResources().getColor(item.colorId));
            float lineX = textX;
            float lineY = textY + dip2px(getContext(), 10);
            float stopX = lineX + mRectF.width() * item.rate / 100 * 2;
            float stopY = lineY;

            RectF oval = new RectF(textX, lineY, stopX, stopY + (dip2px(getContext(), 5)));// 设置个新的长方形
            canvas.drawRoundRect(oval, 6, 6, mLinePaint);//第二个参数是x半径，第三个参数是y半径

            startY += dip2px(getContext(), 50);
            startArc += angle;
        }
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setDataSource(List<RecogRate> items) {
        lstSource = items;
        invalidate();
    }
}