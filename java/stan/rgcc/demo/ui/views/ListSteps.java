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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import stan.rgcc.demo.R;

public class ListSteps
        extends View
{
    private Drawable[] steps = new Drawable[0];
    private Drawable current_drawable;
    private int current_drawable_size;
    private int background_fill;
    private int step_radius;
    private int step_back_radius;
    private int mark_radius;
    private int step_margin;
    private int access_fill_color;
    private int current_fill;
    private int denied_fill_color;
    private int access_icon_color;
    private int denied_icon_color;
    private int line_color;
    private int lastAccessStep;
    private int currentStep;
    private Interpolator interpolator;
    private Interpolator drawableInterpolator;
    private int animateTime;

    private int stepSize;
    private int drawableSize;
    private int drawableY;
    private int minWidth;
    private int allHeight;
    private int centerX;
    private int currentY;
    private int currentCenterY;
    private int lineWidth;
    private int lastAccessStepY;
    private int oldStep;

    private float density;
    private boolean dispatch;

    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint accessFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint deniedFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private AnimatorSet stepsAnimation = new AnimatorSet();
    private AnimatorSet drawableAnimation = new AnimatorSet();

    private ChangeStepListener listener;

    public ListSteps(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        density = context.getResources()
                         .getDisplayMetrics().density;
        animateTime = 300;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ListSteps, 0, 0);
        try
        {
            setCurrentDrawable(typedArray.getDrawable(R.styleable.ListSteps_current_drawable));
            setCurrentDrawableSize(typedArray.getDimensionPixelSize(R.styleable.ListSteps_current_drawable_size, px(24)));
            setBackgroundFill(typedArray.getColor(R.styleable.ListSteps_background_fill, Color.WHITE));
            setLineColor(typedArray.getColor(R.styleable.ListSteps_line_color, Color.GRAY));
            setCurrentColor(typedArray.getColor(R.styleable.ListSteps_current_fill, Color.RED));
            setAccessColor(typedArray.getColor(R.styleable.ListSteps_access_fill_color, Color.RED));
            setAccessIconColor(typedArray.getColor(R.styleable.ListSteps_access_icon_color, Color.GRAY));
            setDeniedColor(typedArray.getColor(R.styleable.ListSteps_denied_fill_color, Color.BLACK));
            setDeniedIconColor(typedArray.getColor(R.styleable.ListSteps_denied_icon_color, Color.BLACK));
            setStepSize(typedArray.getDimensionPixelSize(R.styleable.ListSteps_step_radius, px(24)));
            setStepBackSize(typedArray.getDimensionPixelSize(R.styleable.ListSteps_step_back_radius, px(24)));
            setMarkSize(typedArray.getDimensionPixelSize(R.styleable.ListSteps_mark_radius, px(6)));
            setStepMargin(typedArray.getDimensionPixelSize(R.styleable.ListSteps_step_margin, px(12)));
        }
        finally
        {
            typedArray.recycle();
        }
        drawableInterpolator = new AccelerateDecelerateInterpolator();
//        interpolator = new BounceInterpolator();
        interpolator = new AnticipateOvershootInterpolator(1.5f);
//        interpolator = new OvershootInterpolator(1.5f);
//        interpolator = new AccelerateDecelerateInterpolator();
        currentStep = -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = minWidth;
        int height = allHeight;

        if(widthMode == MeasureSpec.EXACTLY)
        {
            if(widthSize > width)
            {
                width = widthSize;
            }
        }
        centerX = width-(width-mark_radius)/2;
        setMeasuredDimension(width, height);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        if(steps.length == 0)
        {
            return;
        }
        canvas.drawRect(mark_radius, 0, getWidth(), getHeight(), backgroundPaint);
        canvas.drawRect(mark_radius - (lineWidth/2), 0, mark_radius + (lineWidth/2), getHeight(), linePaint);
        canvas.drawRect(mark_radius - (lineWidth/2), 0, mark_radius + (lineWidth/2), currentCenterY, currentPaint);
        canvas.drawCircle(mark_radius, currentCenterY, mark_radius, currentPaint);
        int y = 0;
        for(int i=0; i<steps.length; i++)
        {
//            canvas.drawRect(mark_radius, y, getWidth(), y+1, currentPaint);
            y += step_margin;
            if(currentStep == i)
            {
                canvas.drawCircle(centerX, y+step_radius, step_radius, accessFillPaint);
                steps[i].setColorFilter(current_fill, PorterDuff.Mode.SRC_IN);
            }
            else if(i > lastAccessStep)
            {
                canvas.drawCircle(centerX, y+step_radius, step_radius, deniedFillPaint);
                steps[i].setColorFilter(denied_icon_color, PorterDuff.Mode.SRC_IN);
            }
            else
            {
                canvas.drawCircle(centerX, y+step_radius, step_radius, accessFillPaint);
                steps[i].setColorFilter(access_icon_color, PorterDuff.Mode.SRC_IN);
            }
            if(y > drawableY && y<drawableY+stepSize && current_drawable != null)
            {
                current_drawable.setBounds(centerX-drawableSize/2, (drawableY+step_margin+step_radius)-drawableSize/2, centerX+drawableSize/2, (drawableY+step_margin+step_radius)+drawableSize/2);
                current_drawable.draw(canvas);
            }
            steps[i].setBounds(centerX-step_radius, y, centerX+step_radius, y+step_radius*2);
            steps[i].draw(canvas);
            y += step_radius*2;
            y += step_margin;
        }
//        canvas.drawRect(mark_radius, y, getWidth(), y+1, currentPaint);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
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
        int masked = ev.getActionMasked();
        float x = ev.getX();
        float y = ev.getY();
        switch(masked)
        {
            case MotionEvent.ACTION_DOWN:
            {
                if(x < 0 || x > getWidth() || y < 0 || y > lastAccessStepY + lastAccessStepY+stepSize || (y > currentY && y < currentY + stepSize))
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
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            {
                setOldStep();
                dispatch = false;
                break;
            }
            case MotionEvent.ACTION_UP:
            {
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
    private void touch(float x, float y)
    {
        int h = 0;
        for(int i=0; i<=lastAccessStep; i++)
        {
            h += stepSize;
            if(y < h)
            {
                if(i != currentStep)
                {
//                    animateRipple(x, y, i, animateTime, null);
                }
                setStep(i);
                return;
            }
        }
    }

    public void setCurrentDrawable(Drawable cd)
    {
        if(cd != null)
        {
            current_drawable = cd.getConstantState().newDrawable();
        }
        else
        {
            current_drawable = null;
        }
        recalculate();
    }
    public void setCurrentDrawableSize(int size)
    {
        current_drawable_size = size;
        recalculate();
    }
    public void setBackgroundFill(int color)
    {
        background_fill = color;
        recalculate();
    }
    public void setLineColor(int color)
    {
        line_color = color;
        recalculate();
    }
    public void setCurrentColor(int color)
    {
        current_fill = color;
        recalculate();
    }
    public void setAccessColor(int color)
    {
        access_fill_color = color;
        recalculate();
    }
    public void setAccessIconColor(int color)
    {
        access_icon_color = color;
        recalculate();
    }
    public void setDeniedColor(int color)
    {
        denied_fill_color = color;
        recalculate();
    }
    public void setDeniedIconColor(int color)
    {
        denied_icon_color = color;
        recalculate();
    }
    public void setStepSize(int size)
    {
        step_radius = size;
        recalculate();
    }
    public void setStepBackSize(int size)
    {
        step_back_radius = size;
        recalculate();
    }
    public void setMarkSize(int size)
    {
        mark_radius = size;
        recalculate();
    }
    public void setStepMargin(int size)
    {
        step_margin = size;
        recalculate();
    }
    public void setSteps(Drawable... ss)
    {
        if(ss == null || ss.length == 0)
        {
            return;
        }
        steps = ss;
        lastAccessStep = steps.length-1;
        recalculate();
    }
    public void setStep(int step)
    {
        if(step == currentStep)
        {
            return;
        }
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
    public void setOldStep()
    {
        currentStep = oldStep;
        animateSteps(currentStep, 200);
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
    public void setAnimateTime(int at)
    {
        animateTime = at;
    }
    public void setCurrentCenterY(int value)
    {
        currentCenterY = value;
        invalidate();
    }
    public void setDrawableSize(int value)
    {
        drawableSize = value;
        invalidate();
    }
    private void animateDrawable(final int duration, boolean show)
    {
        drawableAnimation.removeAllListeners();
        drawableAnimation.cancel();
        drawableAnimation = new AnimatorSet();
        drawableAnimation.setInterpolator(drawableInterpolator);
//        drawableAnimation.setInterpolator(interpolator);
        if(show)
        {
            drawableAnimation.play(ObjectAnimator.ofInt(this, "drawableSize", current_drawable_size/2, current_drawable_size));
        }
        else
        {
            drawableAnimation.play(ObjectAnimator.ofInt(this, "drawableSize", current_drawable_size, current_drawable_size/2));
            drawableAnimation.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {
                }
                @Override
                public void onAnimationEnd(Animator animator)
                {
                    drawableY = currentY;
                    animateDrawable(duration, true);
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
        drawableAnimation.setDuration(duration);
        drawableAnimation.start();
    }
    private void animateSteps(int step, int duration)
    {
        stepsAnimation.removeAllListeners();
        stepsAnimation.cancel();
        stepsAnimation = new AnimatorSet();
        stepsAnimation.setInterpolator(interpolator);
        int h = 0;
        for(int i=0; i<step; i++)
        {
            h += stepSize;
        }
        stepsAnimation.play(ObjectAnimator.ofInt(this, "currentCenterY", currentCenterY, h + stepSize/2));
        stepsAnimation.setDuration(duration);
        stepsAnimation.start();
        animateDrawable(duration/2, false);
    }

    private void recalculate()
    {
        lineWidth = px(2);
        backgroundPaint.setColor(background_fill);
        linePaint.setColor(line_color);
        currentPaint.setColor(current_fill);
        accessFillPaint.setColor(access_fill_color);
        deniedFillPaint.setColor(denied_fill_color);
        drawableSize = current_drawable_size;
        stepSize = step_margin*2 + step_radius*2;
        minWidth = mark_radius*2 + step_radius*2;
        allHeight = 0;
        lastAccessStepY = 0;
        for(int i=0; i<steps.length; i++)
        {
            if(i == lastAccessStep)
            {
                lastAccessStepY = allHeight;
            }
            allHeight += stepSize;
        }
        currentY = 0;
        if(steps.length > 0)
        {
            int y=0;
            for(int i=0; i<currentStep; i++)
            {
                y += stepSize;
            }
            currentY = y;
            currentCenterY = y + stepSize/2;
        }
        invalidate();
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