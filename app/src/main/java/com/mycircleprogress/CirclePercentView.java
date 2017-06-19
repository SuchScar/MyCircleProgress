package com.mycircleprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


/**
 * 一个电量圆形百分比进度 View
 * <p>
 * Created by ZL on 2015/12/16.
 */
public class CirclePercentView extends View {

    //进度圆的半径
    private float mRadius;

    //进度圆的实际大小（四边是留空的）
    private int mHeight;
    private int mWidth;

    //动画位置百分比进度
    private float mCurPercent;

    //圆心坐标
    private float x;
    private float y;

    //要画的弧度
    private float mEndAngle;

    //进度圆的颜色
    private int mProgressColor;

    //中心百分比文字大小
    private float mCenterTextSize;

    public CirclePercentView(Context context) {
        this(context, null);
    }

    public CirclePercentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePercentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CirclePercentView, defStyleAttr, 0);
        mCurPercent = a.getInteger(R.styleable.CirclePercentView_percent, 0);
        mProgressColor = a.getColor(R.styleable.CirclePercentView_progressColor, 0xffffffff);
        mCenterTextSize = a.getDimensionPixelSize(R.styleable.CirclePercentView_centerTextSize, PxUtils.spToPx(220, context));
        mRadius = a.getDimensionPixelSize(R.styleable.CirclePercentView_radius, PxUtils.dpToPx(100, context));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //获取测量大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            mRadius = widthSize / 2;
            x = widthSize / 2;
            y = heightSize / 2;
            mWidth = widthSize;
            mHeight = heightSize;
        }

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            mWidth = (int) (mRadius * 2);
            mHeight = (int) (mRadius * 2);
            x = mRadius + 70;
            y = mRadius + 70;
        }
        setMeasuredDimension(mWidth + 140, mHeight + 140);//  现在是140边上留点空白区域
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //确定结束角度
        mEndAngle = mCurPercent * 3.6f;

        //进度圆环图画笔
        Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(mProgressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);
        progressPaint.setAntiAlias(true);
        //进度圆环区域矩形
        RectF progressRect = new RectF(70, 70, mWidth + 70, mHeight + 70);

        //进度环刻度白色画笔
        Paint keDuPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        keDuPaint.setColor(mProgressColor);
        keDuPaint.setStyle(Paint.Style.STROKE);
        keDuPaint.setStrokeWidth(10);
        keDuPaint.setAntiAlias(true);
        //进度环刻度矩形区域
        RectF keDuRect = new RectF(56, 56, mWidth + 84, mHeight + 84);

        //渐变黑环形渲染
        RadialGradient radialGradient = new RadialGradient((mWidth + 140) / 2, (mHeight + 140) / 2, mRadius, new int[]{Color.TRANSPARENT, 0x55000000, Color.TRANSPARENT, 0x55000000, Color.TRANSPARENT, 0x55000000, Color.TRANSPARENT, 0x55000000, Color.TRANSPARENT, 0x55000000, Color.TRANSPARENT, 0x55000000, Color.TRANSPARENT, 0x55000000}, null, Shader.TileMode.CLAMP);
        //黑色渐变画笔
        Paint blackRadialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackRadialPaint.setShader(radialGradient);
        blackRadialPaint.setAntiAlias(true);
        //渐变黑环形区域矩形
        RectF blackRadialRect = new RectF(80, 80, mWidth + 60, mHeight + 60);

        //中间黄色遮盖用圆画笔
        Paint yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint.setColor(0xfffcc030);
        yellowPaint.setAntiAlias(true);


        //绘制白色进度换背景
        canvas.drawArc(progressRect, 0, 360, false, progressPaint);
        //绘制白色刻度
        canvas.drawArc(keDuRect, 359, 1, false, keDuPaint);
        canvas.drawArc(keDuRect, 44, 1, false, keDuPaint);
        canvas.drawArc(keDuRect, 89, 1, false, keDuPaint);
        canvas.drawArc(keDuRect, 134, 1, false, keDuPaint);
        canvas.drawArc(keDuRect, 179, 1, false, keDuPaint);
        canvas.drawArc(keDuRect, 224, 1, false, keDuPaint);
        canvas.drawArc(keDuRect, 269, 1, false, keDuPaint);
        canvas.drawArc(keDuRect, 314, 1, false, keDuPaint);

        //进度圆环画笔变色用于绘制当前进度
        progressPaint.setColor(0xff000000);
        canvas.drawArc(progressRect, 270, mEndAngle, false, progressPaint);
        //绘制已覆盖区域的刻度
        keDuPaint.setColor(0xff000000);
        drawKeDu(canvas, keDuRect, mEndAngle, keDuPaint);
        //画当前的标记，用于表示当前的剩余电量
        if (mEndAngle != 0) {
            RectF nowProgressRect = new RectF(90, 90, mWidth + 50, mHeight + 50);
            canvas.drawArc(nowProgressRect, mEndAngle + 269, 1, false, progressPaint);
        }

        //绘制当前阴影部分
        canvas.drawArc(blackRadialRect, 270, mEndAngle, true, blackRadialPaint);

        //绘制黄色圆用于遮盖影音多余部分遮盖
        canvas.drawCircle((mWidth + 140) / 2, (mHeight + 140) / 2, mRadius - 30, yellowPaint);


        //绘制文本
        drawText(canvas);
    }


    private void drawText(Canvas canvas) {
        Paint numPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint powerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        String num = (int) mCurPercent + "";
        String power = "电量";
        String text = "%";
        Rect numRect = new Rect();
        Rect powerRect = new Rect();
        Rect textRect = new Rect();

        //绘制中间百分比数字
        numPaint.setTextSize(mCenterTextSize);
        numPaint.setColor(Color.BLACK);
        numPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/abc.ttf"));
        numPaint.getTextBounds(num, 0, num.length(), numRect);
        float numWidth = numRect.right;
        float numHeight = numRect.height();
        canvas.drawText(num, x - numWidth / 2, y + numHeight / 2, numPaint);

        //绘制电量字样
        powerPaint.setTextSize(PxUtils.spToPx(30, getContext()));
        powerPaint.setColor(Color.BLACK);
        powerPaint.getTextBounds(power, 0, power.length(), powerRect);
        float powerWidth = powerRect.right;
        float powerHeight = powerRect.height();
        canvas.drawText(power, x - powerWidth / 2, 70 + powerHeight / 2 + 100, powerPaint);

        //绘制百分号%字样
        textPaint.setTextSize(PxUtils.spToPx(30, getContext()));
        textPaint.setColor(Color.BLACK);
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        float textWidth = textRect.right;
        float textHeight = textRect.height();
        canvas.drawText(text, x - textWidth / 2, mHeight + 70 + textHeight / 2 - 100, textPaint);

        //绘制四个角落的00,25,50,75
        Paint keDuNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect keDuNumRect = new Rect();
        keDuNumPaint.setColor(Color.BLACK);
        keDuNumPaint.setTextSize(PxUtils.spToPx(12, getContext()));

        //获取两个数字的矩形大小
        keDuNumPaint.getTextBounds("00", 0, "00".length(), keDuNumRect);
        float keDuWidth = keDuNumRect.right;
        float keDuHeight = keDuNumRect.height();
        //开始绘制四个数字
        canvas.drawText("00", x - keDuWidth / 2 - 4, y - mRadius - 40 + keDuHeight / 2, keDuNumPaint);
        canvas.drawText("25", x + mRadius + 40 - keDuWidth / 2 + 4, y + keDuHeight / 2 - 4, keDuNumPaint);
        canvas.drawText("50", x - keDuWidth / 2 + 4, y + mRadius + 40 + keDuHeight / 2, keDuNumPaint);
        canvas.drawText("75", x - mRadius - 40 - keDuWidth / 2 - 4, y + keDuHeight / 2 + 4, keDuNumPaint);
    }

    private void drawKeDu(Canvas canvas, RectF rect, float EndAngle, Paint Paint) {
        if (EndAngle >= 46.8 && EndAngle < 90) {
            canvas.drawArc(rect, 44 + 270, 1, false, Paint);

        } else if (EndAngle >= 90 && EndAngle < 136.8) {
            canvas.drawArc(rect, 44 + 270, 1, false, Paint);
            canvas.drawArc(rect, 89 + 270, 1, false, Paint);

        } else if (EndAngle >= 136.8 && EndAngle < 180) {
            canvas.drawArc(rect, 44 + 270, 1, false, Paint);
            canvas.drawArc(rect, 89 + 270, 1, false, Paint);
            canvas.drawArc(rect, 134 + 270, 1, false, Paint);

        } else if (EndAngle >= 180 && EndAngle < 226.8) {
            canvas.drawArc(rect, 44 + 270, 1, false, Paint);
            canvas.drawArc(rect, 89 + 270, 1, false, Paint);
            canvas.drawArc(rect, 134 + 270, 1, false, Paint);
            canvas.drawArc(rect, 179 + 270, 1, false, Paint);

        } else if (EndAngle >= 226.8 && EndAngle < 270) {
            canvas.drawArc(rect, 44 + 270, 1, false, Paint);
            canvas.drawArc(rect, 89 + 270, 1, false, Paint);
            canvas.drawArc(rect, 134 + 270, 1, false, Paint);
            canvas.drawArc(rect, 179 + 270, 1, false, Paint);
            canvas.drawArc(rect, 224 + 270, 1, false, Paint);

        } else if (EndAngle >= 270 && EndAngle < 316.8) {
            canvas.drawArc(rect, 44 + 270, 1, false, Paint);
            canvas.drawArc(rect, 89 + 270, 1, false, Paint);
            canvas.drawArc(rect, 134 + 270, 1, false, Paint);
            canvas.drawArc(rect, 179 + 270, 1, false, Paint);
            canvas.drawArc(rect, 224 + 270, 1, false, Paint);
            canvas.drawArc(rect, 269 + 270, 1, false, Paint);

        } else if (EndAngle >= 316.8 && EndAngle < 360) {
            canvas.drawArc(rect, 44 + 270, 1, false, Paint);
            canvas.drawArc(rect, 89 + 270, 1, false, Paint);
            canvas.drawArc(rect, 134 + 270, 1, false, Paint);
            canvas.drawArc(rect, 179 + 270, 1, false, Paint);
            canvas.drawArc(rect, 224 + 270, 1, false, Paint);
            canvas.drawArc(rect, 269 + 270, 1, false, Paint);
            canvas.drawArc(rect, 314 + 270, 1, false, Paint);

        } else if (EndAngle == 360) {
            canvas.drawArc(rect, 44 + 270, 1, false, Paint);
            canvas.drawArc(rect, 89 + 270, 1, false, Paint);
            canvas.drawArc(rect, 134 + 270, 1, false, Paint);
            canvas.drawArc(rect, 179 + 270, 1, false, Paint);
            canvas.drawArc(rect, 224 + 270, 1, false, Paint);
            canvas.drawArc(rect, 269 + 270, 1, false, Paint);
            canvas.drawArc(rect, 314 + 270, 1, false, Paint);
            canvas.drawArc(rect, 359 + 270, 1, false, Paint);

        }

    }

    //外部设置百分比数
    public void setPercent(int percent) {
        if (percent > 100) {
            throw new IllegalArgumentException("percent must less than 100!");
        }
        setCurPercent(percent);
    }

    //内部设置百分比 用于动画效果
    private void setCurPercent(int percent) {

        //属性动画
        ValueAnimator anim = ValueAnimator.ofFloat(0, percent);
        anim.setDuration(percent * 15);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurPercent = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int sleepTime = 1;
//                for(int i =0;i<=mPercent;i++){
//                    if(i%20 == 0){
//                        sleepTime+=2;
//                    }
//                    try {
//                        Thread.sleep(sleepTime);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    mCurPercent = i;
//                    CirclePercentView.this.postInvalidate();
//                }
//                }
//
//        }).start();
    }
}
