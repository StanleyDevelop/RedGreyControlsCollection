package stan.rgcc.demo.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import stan.rgcc.demo.R;

public class TabSteps
        extends View
{
    private String[] steps = new String[0];
    private Drawable rightDrawable;
    private int rightIconSize;
    private int textSize;
    private int accessColor;
    private int currentColor;
    private int deniedColor;
    private int backgroundColor;
    private int lastAccessStep;
    private int currentStep;
    private int animateTime;
    private Interpolator interpolator;

    private int allWidth;
    private int minHeight;
    private int textHeight;
    private int textWidth;
    private int centerY;
    private int tabMargin;
    private int sublineHeight;
    private int lastAccessStepX;
    private int currentX;
    private int currentLength;
    private int currentSublineX;
    private int currentSublineLength;
    private int oldStep;

    private float rippleX;
    private float rippleY;
    private final int maxRippleAlpha = 128;
    private int rippleCircleSize;
    private int maxRippleCircleSize;
    private boolean drawRipple;
    private boolean dispatch;

    private float density;
    private Paint tabPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint tabCurrentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private AnimatorSet rippleAnimation = new AnimatorSet();
    private AnimatorSet stepsAnimation = new AnimatorSet();
//    private final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
    private final Animator.AnimatorListener rippleProxy = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animator)
        {
        }
        @Override
        public void onAnimationEnd(Animator animator)
        {
            rippleTransparent();
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
    private ChangeStepListener listener;

    public TabSteps(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        density = context.getResources()
                         .getDisplayMetrics().density;
        TypedArray tabStepsTypedArray = context.getTheme()
                                               .obtainStyledAttributes(attrs, R.styleable.TabSteps, 0, 0);
        try
        {
            setRightIcon(tabStepsTypedArray.getDrawable(R.styleable.TabSteps_right_icon));
            setRightIconSize(tabStepsTypedArray.getDimensionPixelSize(R.styleable.TabSteps_right_icon_size, px(8)));
            setTextSize(tabStepsTypedArray.getDimensionPixelSize(R.styleable.TabSteps_tab_text_size, px(8)));
            setCurrentColor(tabStepsTypedArray.getColor(R.styleable.TabSteps_current_color, Color.RED));
            setAccessColor(tabStepsTypedArray.getColor(R.styleable.TabSteps_access_color, Color.BLACK));
            setDeniedColor(tabStepsTypedArray.getColor(R.styleable.TabSteps_denied_color, Color.GRAY));
            setBackgroundTabColor(tabStepsTypedArray.getColor(R.styleable.TabSteps_background_color, Color.WHITE));
            setAnimateTime(tabStepsTypedArray.getInt(R.styleable.TabSteps_animate_time, 50));
        }
        finally
        {
            tabStepsTypedArray.recycle();
        }
        interpolator = new AccelerateDecelerateInterpolator();
//        interpolator = new AnticipateOvershootInterpolator();
//        interpolator = new AccelerateInterpolator();
//        interpolator = new BounceInterpolator();
//        interpolator = new DecelerateInterpolator();
        currentStep = -1;
//        setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//            }
//        });
    }

    public void setInterpolator(Interpolator i)
    {
        if(i != null)
        {
            interpolator = i;
            recalculate();
        }
    }
    public void setBackgroundTabColor(int color)
    {
        backgroundColor = color;
        recalculate();
    }
    public void setAnimateTime(int time)
    {
        if(time < 50)
        {
            time = 50;
        }
        else if(time > 600)
        {
            time = 600;
        }
        animateTime = time;
    }
    public void setCurrentColor(int color)
    {
        currentColor = color;
        recalculate();
    }
    public void setAccessColor(int color)
    {
        accessColor = color;
        recalculate();
    }
    public void setDeniedColor(int color)
    {
        deniedColor = color;
        recalculate();
    }
    public void setRightIconSize(int size)
    {
        rightIconSize = size;
        recalculate();
    }
    public void setTextSize(int size)
    {
        textSize = size;
        recalculate();
    }
    public void setRightIcon(Drawable drawable)
    {
        rightDrawable = drawable;
        recalculate();
    }
    public void setSteps(String... ss)
    {
        if(ss == null || ss.length == 0)
        {
            return;
        }
        steps = ss;
        lastAccessStep = steps.length-1;
        recalculate();
    }
    public void setLastAccessStep(int step)
    {
        if(step > steps.length -1)
        {
            step = steps.length -1;
        }
        else if(step < 0)
        {
            step = 0;
        }
        lastAccessStep = step;
        recalculate();
    }
    public void setStep(int step)
    {
        if(step == currentStep)
        {
            return;
        }
        drawRipple = false;
        if(step > lastAccessStep)
        {
            step = lastAccessStep;
        }
        else if(step < 0)
        {
            step = 0;
        }
        animateSteps(step, animateTime);
        currentStep = step;
        recalculate();
    }
    private void setCurrentStep(int step)
    {
        if(step == currentStep)
        {
            return;
        }
        animateSteps(step, animateTime);
        currentStep = step;
        recalculate();
    }
    public void setOldStep()
    {
        currentStep = oldStep;
        animateSteps(currentStep, 150);
        recalculate();
    }

    public void setRippleAlpha(int value)
    {
//        Log.e(getClass().getName(), "Alpha " + value);
        ripplePaint.setAlpha(value);
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
        invalidate();
    }
    public void setRippleY(float value)
    {
        rippleY = value;
        invalidate();
    }
    public void setCurrentSublineX(int value)
    {
        currentSublineX = value;
        invalidate();
    }
    public void setCurrentSublineLength(int value)
    {
        currentSublineLength = value;
        invalidate();
    }
    private void animateSteps(int step, int duration)
    {
        stepsAnimation.removeAllListeners();
        stepsAnimation.cancel();
        stepsAnimation = new AnimatorSet();
        stepsAnimation.setInterpolator(interpolator);
        int len = 0;
        for(int i=0; i<step; i++)
        {
            len += stepLength(i);
        }
        stepsAnimation.play(ObjectAnimator.ofInt(this, "currentSublineX", currentSublineX, len))
        .with(ObjectAnimator.ofInt(this, "currentSublineLength", currentSublineLength, stepLength(step)));
        stepsAnimation.setDuration(duration);
        stepsAnimation.start();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if(steps.length == 0)
        {
            return;
        }
        canvas.drawRect(currentX, 0, currentX + currentLength, getHeight(), backgroundPaint);
        if(drawRipple)
        {
            canvas.drawCircle(rippleX, rippleY, rippleCircleSize, ripplePaint);
        }
        tabPaint.setColor(accessColor);
        rightDrawable.setColorFilter(accessColor, PorterDuff.Mode.SRC_IN);
//        canvas.drawLine(0, centerY, allWidth, centerY, tabPaint);
        int iconLength = 0;
        if(rightDrawable != null)
        {
            iconLength += rightIconSize;
        }
        drawTexts(canvas, iconLength);
        drawSubline(canvas, iconLength);
        if(rightDrawable != null)
        {
            drawRightIcons(canvas);
        }
        canvas.drawRect(currentSublineX, getHeight()-sublineHeight, currentSublineX + currentSublineLength, getHeight(), tabCurrentPaint);
    }
    private void drawTexts(Canvas canvas, int iconLength)
    {
        tabPaint.setColor(accessColor);
        int x=0;
        for(int i=0; i<=lastAccessStep; i++)
        {
            int textLength = (int)tabPaint.measureText(steps[i]);
            if(i != currentStep)
            {
                canvas.drawRect(x, 0, x + tabMargin*2 + textLength + tabMargin*2 + iconLength, getHeight(), backgroundPaint);
            }
            if(i == currentStep && currentSublineX == x)
            {
                canvas.drawText(steps[i], x + tabMargin*2, centerY + textHeight/2, tabCurrentPaint);
            }
            else
            {
                canvas.drawText(steps[i], x + tabMargin*2, centerY + textHeight/2, tabPaint);
            }
            x += tabMargin*2 + textLength + tabMargin*2 + iconLength;
        }
        tabPaint.setColor(deniedColor);
        for(int i=lastAccessStep+1; i<steps.length; i++)
        {
            int textLength = (int)tabPaint.measureText(steps[i]);
            canvas.drawRect(x, 0, x + tabMargin*2 + textLength + tabMargin*2 + iconLength, getHeight(), backgroundPaint);
            canvas.drawText(steps[i], x + tabMargin*2, centerY + textHeight/2, tabPaint);
            x += tabMargin*2 + textLength + tabMargin*2 + iconLength;
        }
    }
    private void drawRightIcons(Canvas canvas)
    {
        rightDrawable.setColorFilter(accessColor, PorterDuff.Mode.SRC_IN);
        int x=0;
        for(int i=0; i<=lastAccessStep; i++)
        {
            int textLenght = (int)tabPaint.measureText(steps[i]);
            rightDrawable.setBounds(x + tabMargin*2 + textLenght + tabMargin, centerY - rightIconSize/2, x + textLenght + tabMargin + rightIconSize, centerY+rightIconSize/2);
            if(i == currentStep && currentSublineX == x)
            {
                rightDrawable.setColorFilter(currentColor, PorterDuff.Mode.SRC_IN);
                rightDrawable.draw(canvas);
                rightDrawable.setColorFilter(accessColor, PorterDuff.Mode.SRC_IN);
            }
            else
            {
                rightDrawable.draw(canvas);
            }
            x += tabMargin*2 + textLenght + tabMargin*2 + rightIconSize;
        }
        rightDrawable.setColorFilter(deniedColor, PorterDuff.Mode.SRC_IN);
        for(int i=lastAccessStep+1; i<steps.length; i++)
        {
            int textLenght = (int)tabPaint.measureText(steps[i]);
            rightDrawable.setBounds(x + tabMargin*2 + textLenght + tabMargin, centerY - rightIconSize/2, x + textLenght + tabMargin + rightIconSize, centerY+rightIconSize/2);
            rightDrawable.draw(canvas);
            x += tabMargin*2 + textLenght + tabMargin*2 + rightIconSize;
        }
    }
    private void drawSubline(Canvas canvas, int iconLength)
    {
        int x=0;
        for(int i=0; i<=lastAccessStep; i++)
        {
            x += tabMargin*2 + (int)tabPaint.measureText(steps[i]) + tabMargin*2 + iconLength;
        }
        tabPaint.setColor(accessColor);
        int endAccessX = x;
        canvas.drawRect(0, getHeight()-sublineHeight, endAccessX, getHeight(), tabPaint);
        for(int i=lastAccessStep+1; i<steps.length; i++)
        {
            x += tabMargin*2 + (int)tabPaint.measureText(steps[i]) + tabMargin*2 + iconLength;
        }
        tabPaint.setColor(deniedColor);
        canvas.drawRect(endAccessX, getHeight()-sublineHeight, x, getHeight(), tabPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = allWidth;
        int height = minHeight;

        if(heightMode == MeasureSpec.EXACTLY)
        {
            if(heightSize > height)
            {
                height = heightSize;
            }
        }
//        else if(heightMode == MeasureSpec.AT_MOST)
//        {
//            if(heightSize > height)
//            {
//                height = heightSize;
//            }
//        }
        centerY = height/2;
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
//        Log.e(getClass().getName(), "dispatchTouchEvent " + ev);
        int masked = ev.getActionMasked();
        switch(masked)
        {
            case MotionEvent.ACTION_DOWN:
            {
                return super.dispatchTouchEvent(ev);
            }
        }
        if(dispatch)
        {
            return super.dispatchTouchEvent(ev);
        }
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
//        Log.e(getClass().getName(), "onTouchEvent " + ev);
        int masked = ev.getActionMasked();
        float x = ev.getX();
        float y = ev.getY();
        switch(masked)
        {
            case MotionEvent.ACTION_DOWN:
            {
                if(y < 0 || y > getHeight() || x < 0 || x > lastAccessStepX + stepLength(lastAccessStep) || (x > currentX && x < currentX + currentLength))
                {
                    dispatch = false;
                    break;
                }
                oldStep = currentStep;
                touch(x, y);
                dispatch = true;
                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                if(y < 0 || y > getHeight() || x < 0 || x > lastAccessStepX + stepLength(lastAccessStep))
                {
                    int old = currentStep;
//                    setCurrentStep(oldStep);
                    setOldStep();
                    if(old == oldStep)
                    {
                        rippleOut();
                    }
                    else
                    {
                        drawRipple = false;
                        rippleCircleSize = 0;
//                        animateRipple(currentX + stepLength(currentStep)/2, centerY, currentStep, 150, rippleProxy);
                    }
                    dispatch = false;
                    break;
                }
                touch(x, y);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            {
//                setCurrentStep(oldStep);
                setOldStep();
                drawRipple = false;
                rippleCircleSize = 0;
//                animateRipple(currentX + stepLength(currentStep)/2, centerY, currentStep, 150, rippleProxy);
                dispatch = false;
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                if(dispatch)
                {
                    rippleOut();
                }
                if(oldStep != currentStep)
                {
                    oldStep = currentStep;
                    if(listener != null)
                    {
                        listener.changeStep(currentStep);
                    }
                }
                break;
            }
        }
        return false;
    }
    private void rippleOut()
    {
        Log.e(getClass().getName(), "rippleOut");
        if(maxRippleCircleSize == rippleCircleSize)
        {
            rippleTransparent();
        }
        else
        {
            rippleSize();
        }
    }
    private void rippleSize()
    {
        Log.e(getClass().getName(), "rippleSize");
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        rippleAnimation = new AnimatorSet();
        rippleAnimation.setInterpolator(interpolator);
        rippleAnimation.play(ObjectAnimator.ofInt(this, "rippleCircleSize", rippleCircleSize, maxRippleCircleSize));
        rippleAnimation.setDuration(150);
        rippleAnimation.addListener(rippleProxy);
        rippleAnimation.start();
    }
    private void rippleTransparent()
    {
        Log.e(getClass().getName(), "rippleTransparent");
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        rippleAnimation = new AnimatorSet();
        rippleAnimation.setInterpolator(interpolator);
        rippleAnimation.play(ObjectAnimator.ofInt(this, "rippleAlpha", maxRippleAlpha, 0));
        rippleAnimation.setDuration(150);
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
                rippleCircleSize = 0;
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

    private void touch(float x, float y)
    {
        int len = 0;
        for(int i=0; i<=lastAccessStep; i++)
        {
            len += stepLength(i);
            if(x < len)
            {
                if(i != currentStep)
                {
                    animateRipple(x, y, i, animateTime, null);
                }
                setCurrentStep(i);
                return;
            }
        }
    }

    private void animateRipple(float x, float y, int i, int time, Animator.AnimatorListener listener)
    {
//        ripplePaint.setAlpha(maxRippleAlpha);
//        ripplePaint.setAlpha(12);
        rippleX = x;
        rippleY = y;
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        rippleAnimation = new AnimatorSet();
        drawRipple = true;
        rippleAnimation.setInterpolator(interpolator);
        maxRippleCircleSize = stepLength(i);
        maxRippleCircleSize = (int)Math.sqrt(maxRippleCircleSize*maxRippleCircleSize + getHeight()*getHeight());
        rippleAnimation.play(ObjectAnimator.ofInt(this, "rippleCircleSize", maxRippleCircleSize/5, maxRippleCircleSize));
        rippleAnimation.setDuration(time);
        if(listener != null)
        {
            rippleAnimation.addListener(listener);
        }
        rippleAnimation.start();
    }

    private void recalculate()
    {
        backgroundPaint.setColor(backgroundColor);
        ripplePaint.setColor(currentColor);
        ripplePaint.setAlpha(maxRippleAlpha);
        tabCurrentPaint.setColor(currentColor);
        tabCurrentPaint.setTextSize(textSize);
        tabPaint.setColor(accessColor);
        tabPaint.setTextSize(textSize);
        Rect bounds = new Rect();
        tabPaint.getTextBounds("1", 0, 1, bounds);
        textHeight = bounds.height();
        minHeight = textHeight;
        tabMargin = px(2);
        allWidth = 0;
        int iconLength = 0;
        if(rightDrawable != null)
        {
            iconLength += rightIconSize;
            rightDrawable.mutate();
        }
        for(int i=0; i<steps.length; i++)
        {
            if(i == lastAccessStep)
            {
                lastAccessStepX = allWidth;
            }
            allWidth += (int)tabPaint.measureText(steps[i]) + 4*tabMargin + iconLength;
        }
        int x=0;
        for(int i=0; i<currentStep; i++)
        {
            x += tabMargin*2 + (int)tabPaint.measureText(steps[i]) + tabMargin*2 + iconLength;
        }
        currentX = x;
//        currentSublineX = x;
        currentLength = stepLength(currentStep);
//        currentSublineLength = currentLength;
        sublineHeight = px(4);
//        invalidate();
    }
    private int stepLength(int i)
    {
        if(i < 0 || i > lastAccessStep || steps.length == 0)
        {
            return 0;
        }
        int iconLength = 0;
        if(rightDrawable != null)
        {
            iconLength += rightIconSize;
        }
        return tabMargin*2 + (int)tabPaint.measureText(steps[i]) + tabMargin*2 + iconLength;
    }

    public void setListener(ChangeStepListener l)
    {
        listener = l;
    }

    public interface ChangeStepListener
    {
        void changeStep(int newStep);
    }

    private int px(float dp)
    {
        if(dp < 0)
        {
            return 0;
        }
        return (int)Math.ceil(density * dp);
    }
}