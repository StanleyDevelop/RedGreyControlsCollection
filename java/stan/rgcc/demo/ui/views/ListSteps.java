package stan.rgcc.demo.ui.views;

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
import android.view.animation.Interpolator;

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

    private int stepSize;
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

    private ChangeStepListener listener;

    public ListSteps(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        density = context.getResources()
                         .getDisplayMetrics().density;
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
        interpolator = new AccelerateDecelerateInterpolator();
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
            steps[i].setBounds(centerX-step_radius, y, centerX+step_radius, y+step_radius*2);
            if(currentStep == i)
            {
                if(current_drawable == null)
                {
                    canvas.drawCircle(centerX, y+step_radius, step_radius, accessFillPaint);
                }
                else
                {
                    current_drawable.setBounds(centerX-current_drawable_size/2, (y+step_radius)-current_drawable_size/2, centerX+current_drawable_size/2, (y+step_radius)+current_drawable_size/2);
                    current_drawable.draw(canvas);
                }
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
        current_drawable = cd;
        if(current_drawable != null)
        {
            current_drawable.mutate();
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
        currentStep = step;
        recalculate();
    }
    public void setOldStep()
    {
        currentStep = oldStep;
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

    private void recalculate()
    {
        lineWidth = px(2);
        backgroundPaint.setColor(background_fill);
        linePaint.setColor(line_color);
        currentPaint.setColor(current_fill);
        accessFillPaint.setColor(access_fill_color);
        deniedFillPaint.setColor(denied_fill_color);
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