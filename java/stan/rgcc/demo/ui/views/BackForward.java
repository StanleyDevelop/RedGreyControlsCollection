package stan.rgcc.demo.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import stan.rgcc.demo.R;

public class BackForward
        extends View
{
    private float rippleX;
    private float rippleY;
    private int rippleCircleSize;
    private int rippleCircleStartSize;
    private boolean drawRipple;
    private int leftX;
    private int leftY;
    private int centerY;
    private int rightX;
    private int outerSize;
    private int dSize;
    private float shadowX;
    private float shadowY;
    private int shadowCircleSize;
    private boolean drawShadow;

    private int circleSize;

    private final Animator.AnimatorListener hideShadow = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animator)
        {
        }
        @Override
        public void onAnimationEnd(Animator animator)
        {
            drawShadow = false;
        }
        @Override
        public void onAnimationCancel(Animator animator)
        {
        }
        @Override
        public void onAnimationRepeat(Animator animator)
        {
        }
    };
    private final Animator.AnimatorListener reopenShadow = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animator)
        {
        }
        @Override
        public void onAnimationEnd(Animator animator)
        {
            animateShowShadow();
        }
        @Override
        public void onAnimationCancel(Animator animator)
        {
        }
        @Override
        public void onAnimationRepeat(Animator animator)
        {
        }
    };
    private final Animator.AnimatorListener hideShadowProxy = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animator)
        {
        }
        @Override
        public void onAnimationEnd(Animator animator)
        {
            animateHideShadow();
        }
        @Override
        public void onAnimationCancel(Animator animator)
        {
        }
        @Override
        public void onAnimationRepeat(Animator animator)
        {
        }
    };

    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint shadowPaint = new Paint();
    private Paint outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private AnimatorSet rippleAnimation;
    private AnimatorSet circleAnimation;
    private AccelerateDecelerateInterpolator interpolator;

    public BackForward(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray backForwardTypedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BackForward,
                0, 0);
        TypedArray circlableTypedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Circlable,
                0, 0);
        try
        {
            setCircleSize(circlableTypedArray.getDimensionPixelSize(R.styleable.Circlable_circle_size, 0));
        }
        finally
        {
            backForwardTypedArray.recycle();
            circlableTypedArray.recycle();
        }
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(getResources().getColor(R.color.red));
        outerPaint.setStyle(Paint.Style.FILL);
        outerPaint.setColor(getResources().getColor(R.color.graylight));
        ripplePaint.setStyle(Paint.Style.FILL);
        ripplePaint.setColor(getResources().getColor(R.color.white_trans));
        shadowPaint.setStyle(Paint.Style.FILL);
        rippleAnimation = new AnimatorSet();
        circleAnimation = new AnimatorSet();
        interpolator = new AccelerateDecelerateInterpolator();
        post(new Runnable()
        {
            @Override
            public void run()
            {
                leftY = getHeight()/2;
                centerY = getHeight()/2;
                Log.e(getClass().getName(), "h " + leftY);
                setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                    }
                });
            }
        });
    }

    public void setCircleSize(int cs)
    {
        circleSize = cs;
        Log.e(getClass().getName(), "circleSize " + circleSize);
        outerSize = (int)(cs*1.2);
        Log.e(getClass().getName(), "outerSize " + outerSize);
        leftX = outerSize;
        rightX = leftX + outerSize + (int)(cs*0.1) + outerSize;
        recalculate();
    }

    public void setShadowCircleSize(int value)
    {
        shadowCircleSize = value;
        Log.e(getClass().getName(), "shadowCircleSize " + shadowCircleSize);
        invalidate();
    }
    public void setLeftY(int value)
    {
        leftY = value;
        Log.e(getClass().getName(), "leftY " + leftY);
        shadowCircleSize = (int)(circleSize*0.9) - (leftY-(centerY+(int)(circleSize*0.1)));
        shadowPaint.setShadowLayer((int)((leftY - centerY)*1.8),0,0, getResources().getColor(R.color.black));
        invalidate();
    }
    public void setRippleCircleSize(int value)
    {
        rippleCircleSize = value;
        invalidate();
    }
    public void setRippleX(float value)
    {
        rippleX = value;
        Log.e(getClass().getName(), "rippleX " + rippleX);
        invalidateRipple();
    }
    public void setRippleY(float value)
    {
        rippleY = value;
        Log.e(getClass().getName(), "rippleY " + rippleY);
        invalidateRipple();
    }
    private void invalidateRipple()
    {
        rippleCircleSize = (int)(circleSize*(1-(Math.sqrt(Math.abs(leftX-rippleX)*Math.abs(leftX-rippleX)+Math.abs(centerY-rippleY)*Math.abs(centerY-rippleY))/rippleCircleStartSize)));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = circleSize*6;
        int height = heightMeasureSpec;
        setMeasuredDimension(width, height);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int masked = ev.getActionMasked();
        switch(masked)
        {
            case MotionEvent.ACTION_DOWN:
            {
                touchDown(ev.getX(), ev.getY());
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                touchUp(ev.getX(), ev.getY());
                break;
            }
        }
        return super.onTouchEvent(ev);
    }
    private void touchDown(float x, float y)
    {
        Log.e(getClass().getName(), "touchDown " + x + " " + y);
        animateShowShadow();
    }
    private void animateShowShadow()
    {
        circleAnimation.removeAllListeners();
        circleAnimation.cancel();
        circleAnimation = new AnimatorSet();
        shadowX = leftX;
        shadowY = leftY + (outerSize - circleSize)/2;
        circleAnimation.play(ObjectAnimator.ofInt(this, "shadowCircleSize", shadowCircleSize, outerSize));
        circleAnimation.setInterpolator(interpolator);
        circleAnimation.setDuration(300);
        drawShadow = true;
        circleAnimation.start();
    }
    private void animateHideShadow()
    {
        circleAnimation.removeAllListeners();
        circleAnimation.cancel();
        circleAnimation = new AnimatorSet();
        circleAnimation.play(ObjectAnimator.ofInt(this, "shadowCircleSize", shadowCircleSize, dSize));
        circleAnimation.setInterpolator(interpolator);
        circleAnimation.setDuration(300);
        circleAnimation.addListener(hideShadow);
        circleAnimation.start();
    }
    private void touchUp(float x, float y)
    {
        if(circleAnimation.isStarted())
        {
            circleAnimation.addListener(hideShadowProxy);
        }
        else
        {
            animateHideShadow();
        }
        Log.e(getClass().getName(), "touchUp " + x + " " + y);
        rippleCircleStartSize = (int)Math.sqrt(Math.abs(leftX-x)*Math.abs(leftX-x)+Math.abs(leftY-y)*Math.abs(leftY-y));
        Log.e(getClass().getName(), "rippleCircleStartSize " + rippleCircleStartSize);
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        rippleAnimation = new AnimatorSet();
        drawRipple = true;
        rippleAnimation.setInterpolator(interpolator);
        rippleAnimation.play(ObjectAnimator.ofFloat(this, "rippleX", x, leftX)).with(ObjectAnimator.ofFloat(this, "rippleY", y, centerY));
        rippleAnimation.setDuration(300);
        rippleAnimation.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }
            @Override
            public void onAnimationEnd(Animator animator)
            {
                rippleOut();
            }
            @Override
            public void onAnimationCancel(Animator animator)
            {

            }
            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });
        rippleAnimation.start();
    }
    private void buttonDown()
    {
        circleAnimation.removeAllListeners();
        circleAnimation.cancel();
        circleAnimation = new AnimatorSet();
        circleAnimation.play(ObjectAnimator.ofInt(this, "shadowCircleSize", shadowCircleSize, dSize));
        circleAnimation.setInterpolator(interpolator);
        circleAnimation.setDuration(300);
        circleAnimation.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }
            @Override
            public void onAnimationEnd(Animator animator)
            {
                drawShadow = false;
            }
            @Override
            public void onAnimationCancel(Animator animator)
            {

            }
            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });
        circleAnimation.start();
    }
    private void rippleOut()
    {
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        rippleAnimation = new AnimatorSet();
        rippleAnimation.play(ObjectAnimator.ofInt(this, "rippleCircleSize", rippleCircleSize, 0));
        rippleAnimation.setDuration(200);
        rippleAnimation.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }
            @Override
            public void onAnimationEnd(Animator animator)
            {
                drawRipple = false;
            }
            @Override
            public void onAnimationCancel(Animator animator)
            {

            }
            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });
        rippleAnimation.start();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawCircle(leftX, centerY, outerSize, outerPaint);
        canvas.drawCircle(rightX, centerY, outerSize, outerPaint);
        canvas.drawRect(leftX, centerY-outerSize, rightX, centerY+outerSize, outerPaint);
        if(drawShadow)
        {
            drawShadow(canvas);
        }
        canvas.drawCircle(leftX, centerY, circleSize, circlePaint);
        canvas.drawCircle(rightX, centerY, circleSize, circlePaint);
        if(drawRipple)
        {
            canvas.drawCircle(rippleX, rippleY, rippleCircleSize, ripplePaint);
        }
    }

    private void drawShadow(Canvas canvas)
    {
        for(int i=shadowCircleSize; i>=dSize; i--)
        {
            float alpha = i * 255;
            Log.e(getClass().getName(), "alpha " + alpha);
            shadowPaint.setColor(Color.argb((int)(255 - alpha/shadowCircleSize)/5,0,0,0));
            canvas.drawCircle(shadowX, shadowY, i, shadowPaint);
        }
    }

    private void recalculate()
    {
        dSize = circleSize-(outerSize-circleSize);
    }
}