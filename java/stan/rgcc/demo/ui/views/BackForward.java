package stan.rgcc.demo.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

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
    private int rightX;
    private int outerSize;
    private int shadowCircleSize;
    private boolean drawShadow;

    private int circleSize;

    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint shadowPaint = new Paint();
    private Paint outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private AnimatorSet rippleAnimation;
    private AnimatorSet circleAnimation;

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
//        shadowPaint.setStyle(Paint.Style.FILL);
//        shadowPaint.setColor(getResources().getColor(R.color.black));
//        shadowPaint.setARGB(255, 51, 153, 255);
        setLayerType(LAYER_TYPE_SOFTWARE, shadowPaint);
        rippleAnimation = new AnimatorSet();
        circleAnimation = new AnimatorSet();
        post(new Runnable()
        {
            @Override
            public void run()
            {
                leftY = getHeight()/2;
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

    public void setLeftY(int value)
    {
        leftY = value;
        Log.e(getClass().getName(), "leftY " + leftY);
        int centerY = getHeight()/2;
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
        int centerY = getHeight()/2;
        float l = Math.abs(leftX-rippleX);
        float h = Math.abs(centerY-rippleY);
        double tmp = circleSize*(1-(Math.sqrt(l*l+h*h)/rippleCircleStartSize));
        rippleCircleSize = (int)tmp;
        Log.e(getClass().getName(), "rippleCircleSize " + rippleCircleSize + " tmp " + tmp);
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
        circleAnimation.removeAllListeners();
        circleAnimation.cancel();
        circleAnimation = new AnimatorSet();
        int centerY = getHeight()/2;
        circleAnimation.play(ObjectAnimator.ofInt(this, "leftY", centerY, centerY + (int)(circleSize*0.1)));
        circleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        circleAnimation.setDuration(250);
        drawShadow = true;
        circleAnimation.start();
    }
    private void touchUp(float x, float y)
    {
        if(circleAnimation.isStarted())
        {
            circleAnimation.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {

                }
                @Override
                public void onAnimationEnd(Animator animator)
                {
                    circleBack();
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
        }
        else
        {
            circleBack();
        }
        Log.e(getClass().getName(), "touchUp " + x + " " + y);
        int centerY = getHeight()/2;
        float l = Math.abs(leftX-x);
        l*=l;
        float h = Math.abs(leftY-y);
        h*=h;
        rippleCircleStartSize = (int)Math.sqrt(l+h);
        Log.e(getClass().getName(), "rippleCircleStartSize " + rippleCircleStartSize);
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        rippleAnimation = new AnimatorSet();
        drawRipple = true;
        rippleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
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
    private void circleBack()
    {
        circleAnimation.removeAllListeners();
        circleAnimation.cancel();
        circleAnimation = new AnimatorSet();
        circleAnimation.play(ObjectAnimator.ofInt(this, "leftY", leftY, getHeight()/2));
        circleAnimation.setInterpolator(new LinearInterpolator());
        circleAnimation.setDuration(250);
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
        int y = getHeight()/2;
        canvas.drawCircle(leftX, y, outerSize, outerPaint);
        canvas.drawCircle(rightX, y, outerSize, outerPaint);
        canvas.drawRect(leftX, y-outerSize, rightX, y+outerSize, outerPaint);
        if(drawShadow)
        {
            canvas.drawCircle(leftX, leftY, shadowCircleSize, shadowPaint);
        }
        canvas.drawCircle(leftX, y, circleSize, circlePaint);
        canvas.drawCircle(rightX, y, circleSize, circlePaint);
        if(drawRipple)
        {
            canvas.drawCircle(rippleX, rippleY, rippleCircleSize, ripplePaint);
        }
    }

    private void recalculate()
    {
    }
}