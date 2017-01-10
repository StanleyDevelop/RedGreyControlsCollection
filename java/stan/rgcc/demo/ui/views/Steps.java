package stan.rgcc.demo.ui.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import stan.rgcc.demo.R;

public class Steps
        extends View
{
    private int stepsCount;
    private int circleSize;
    private int textSize;
    private int betweenPadding;
    private int textColor;
    private int circleColor;
    private int currentCircleColor;
    private int orientation;

    private int centerX;
    private int centerY;
    private int textHeight;
    private int textWidth;
    private int startAnimateX;
    private int startAnimateY;
    private int currentStepX;
    private int currentStepY;
    private int currentCircleSize;
    private int newStepX;
    private int newStepY;

    private float density;
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint currentCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int currentStep;
    private int oldStep;
    private AnimatorSet stepsAnimation;
    private final AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

    private ChangeStepListener listener;

    public Steps(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        stepsAnimation = new AnimatorSet();
        TypedArray stepsTypedArray = context.getTheme()
                                            .obtainStyledAttributes(attrs, R.styleable.Steps, 0, 0);
        try
        {
            setStepsOrientation(stepsTypedArray.getInt(R.styleable.Steps_steps_orientation, 0));
            setStepsCount(stepsTypedArray.getInt(R.styleable.Steps_steps_count, 2));
            int cStep = stepsTypedArray.getInt(R.styleable.Steps_current_step, 0);
            if(cStep < 0)
            {
                cStep = 0;
            }
            currentStep = cStep;
            setCircleSize(stepsTypedArray.getDimensionPixelSize(R.styleable.Steps_step_circle_size, px(12)));
            setTextSize(stepsTypedArray.getDimensionPixelSize(R.styleable.Steps_step_text_size, px(6)));
            setBetweenPadding(stepsTypedArray.getDimensionPixelSize(R.styleable.Steps_between_steps_padding, px(6)));
            setTextColor(stepsTypedArray.getColor(R.styleable.Steps_step_text_color, Color.BLACK));
            setCircleColor(stepsTypedArray.getColor(R.styleable.Steps_step_circle_color, Color.BLACK));
            setCurrentCircleColor(stepsTypedArray.getColor(R.styleable.Steps_current_step_circle_color, Color.BLACK));
        }
        finally
        {
            stepsTypedArray.recycle();
        }
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });
    }

    private void setStepsOrientation(int o)
    {
        if(o != 0 && o != 1)
        {
            o = 0;
        }
        orientation = o;
    }

    private void setCurrentCircleColor(int color)
    {
        currentCircleColor = color;
        recalculate();
    }
    private void setCircleColor(int color)
    {
        circleColor = color;
        recalculate();
    }
    private void setTextColor(int color)
    {
        textColor = color;
        recalculate();
    }
    private void setCurrentStep(int cStep)
    {
        if(cStep == currentStep)
        {
            return;
        }
        if(cStep < 0)
        {
            cStep = 0;
        }
        else if(cStep > stepsCount-1)
        {
            cStep = stepsCount-1;
        }
        if(orientation == 0)
        {
            animateSteps(cStep);
        }
        else
        {
            animateStepsVertical(cStep);
        }
        currentStep = cStep;
//        recalculate();
    }
    public void setCurrentStepX(int value)
    {
//        Log.e(getClass().getName(), "setCurrentStepX " + value);
        currentStepX = value;
        currentCircleSize = circleSize/2;
        double len = Math.abs(startAnimateX - newStepX);
        double move = Math.abs(startAnimateX - currentStepX) - len/2;
        currentCircleSize += circleSize*Math.abs(move/(len/2))/2;
        Log.e(getClass().getName(), "currentCircleSize " + currentCircleSize);
        invalidate();
    }
    public void setCurrentStepY(int value)
    {
        currentStepY = value;
        currentCircleSize = circleSize/2;
        double len = Math.abs(startAnimateY - newStepY);
        double move = Math.abs(startAnimateY - currentStepY) - len/2;
        currentCircleSize += circleSize*Math.abs(move/(len/2))/2;
        Log.e(getClass().getName(), "currentCircleSize " + currentCircleSize);
        invalidate();
    }
    private void animateSteps(int newStep)
    {
        Log.e(getClass().getName(), "animateSteps newStep " + newStep);
        stepsAnimation.removeAllListeners();
        stepsAnimation.cancel();
        stepsAnimation = new AnimatorSet();
        stepsAnimation.setInterpolator(interpolator);
        startAnimateX = currentStepX;
        newStepX = (circleSize*2+betweenPadding)*newStep + circleSize;
        stepsAnimation.play(ObjectAnimator.ofInt(this, "currentStepX", startAnimateX, newStepX));
        stepsAnimation.setDuration(300);
        stepsAnimation.start();
    }
    private void animateStepsVertical(int newStep)
    {
        stepsAnimation.removeAllListeners();
        stepsAnimation.cancel();
        stepsAnimation = new AnimatorSet();
        stepsAnimation.setInterpolator(interpolator);
        startAnimateY = currentStepY;
        newStepY = (circleSize*2+betweenPadding)*newStep + circleSize;
        stepsAnimation.play(ObjectAnimator.ofInt(this, "currentStepY", startAnimateY, newStepY));
        stepsAnimation.setDuration(300);
        stepsAnimation.start();
    }

    private void setBetweenPadding(int padding)
    {
        betweenPadding = padding;
        recalculate();
    }

    private void setTextSize(int size)
    {
        textSize = size;
        recalculate();
    }

    public void setCircleSize(int size)
    {
        circleSize = size;
        recalculate();
    }

    public void setStepsCount(int count)
    {
        if(count > 2)
        {
            stepsCount = count;
        }
        else
        {
            stepsCount = 2;
        }
        recalculate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        Log.e(getClass().getName(), "onTouchEvent " + ev);
        int masked = ev.getActionMasked();
        float x = ev.getX();
        float y = ev.getY();
        switch(masked)
        {
            case MotionEvent.ACTION_DOWN:
            {
                oldStep = currentStep;
                touch(x, y);
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                touch(x, y);
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                if(listener != null && oldStep != currentStep)
                {
                    listener.changeStep(currentStep);
                    oldStep = currentStep;
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void touch(float x, float y)
    {
        if(orientation == 0)
        {
            for(int i=0; i<stepsCount; i++)
            {
                if(x > (circleSize*2+betweenPadding)*i && x < (circleSize*2+betweenPadding)*i+circleSize*2)
                {
                    setCurrentStep(i);
                    return;
                }
            }
        }
        else
        {
            for(int i=0; i<stepsCount; i++)
            {
                if(y > (circleSize*2+betweenPadding)*i && y < (circleSize*2+betweenPadding)*i+circleSize*2)
                {
                    setCurrentStep(i);
                    return;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = circleSize*2*stepsCount + betweenPadding*(stepsCount-1);
        int height = circleSize*2;
        if(orientation == 0)
        {
            setMeasuredDimension(width, height);
        }
        else
        {
            setMeasuredDimension(height, width);
        }
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        if(orientation == 0)
        {
            drawCircles(canvas);
            canvas.drawCircle(currentStepX, centerY, currentCircleSize, currentCirclePaint);
            drawTexts(canvas);
        }
        else
        {
            drawCirclesVertical(canvas);
            canvas.drawCircle(centerX, currentStepY, currentCircleSize, currentCirclePaint);
            drawTextsVertical(canvas);
        }
    }

    private void drawTextsVertical(Canvas canvas)
    {
        canvas.drawText(Integer.toString(1), centerX - textWidth/2,  circleSize + textHeight/2, textPaint);
        for(int i=1; i<stepsCount-1; i++)
        {
            canvas.drawText(Integer.toString(i+1), centerX - textWidth/2, i*circleSize*2 + circleSize + i*betweenPadding + textHeight/2, textPaint);
        }
        canvas.drawText(Integer.toString(stepsCount), centerX - textWidth/2, (stepsCount-1)*circleSize*2 + circleSize + (stepsCount-1)*betweenPadding + textHeight/2, textPaint);
    }

    private void drawCirclesVertical(Canvas canvas)
    {
        canvas.drawCircle(centerX, circleSize, circleSize, circlePaint);
        for(int i=1; i<stepsCount-1; i++)
        {
            canvas.drawCircle(centerX, i*circleSize*2 + circleSize + i*betweenPadding, circleSize, circlePaint);
        }
        canvas.drawCircle(centerX, (stepsCount-1)*circleSize*2 + circleSize + (stepsCount-1)*betweenPadding, circleSize, circlePaint);
    }

    private void drawCircles(Canvas canvas)
    {
        canvas.drawCircle(circleSize, centerY, circleSize, circlePaint);
        for(int i=1; i<stepsCount-1; i++)
        {
            canvas.drawCircle(i*circleSize*2 + circleSize + i*betweenPadding, centerY, circleSize, circlePaint);
        }
        canvas.drawCircle((stepsCount-1)*circleSize*2 + circleSize + (stepsCount-1)*betweenPadding, centerY, circleSize, circlePaint);
    }
    private void drawTexts(Canvas canvas)
    {
        canvas.drawText(Integer.toString(1), circleSize - textWidth/2, centerY + textHeight/2, textPaint);
        for(int i=1; i<stepsCount-1; i++)
        {
            canvas.drawText(Integer.toString(i+1), i*circleSize*2 + circleSize + i*betweenPadding - textWidth/2, centerY + textHeight/2, textPaint);
        }
        canvas.drawText(Integer.toString(stepsCount), (stepsCount-1)*circleSize*2 + circleSize + (stepsCount-1)*betweenPadding - textWidth/2, centerY + textHeight/2, textPaint);
    }

    private void recalculate()
    {
        Log.e(getClass().getName(), "recalculate"
                + "\ncurrentStep " + currentStep
                + "\nbetweenPadding " + betweenPadding
        );
        centerX = circleSize;
        centerY = circleSize;
        textPaint.setTextSize(textSize);
        Rect bounds = new Rect();
        textPaint.getTextBounds("1", 0, 1, bounds);
        textHeight = bounds.height();
//        textHeight *= 2;
//        textHeight /= 3;
        textWidth = bounds.width();
        textWidth*=2;
        currentStepX = (circleSize*2+betweenPadding)*currentStep + circleSize;
        currentStepY = (circleSize*2+betweenPadding)*currentStep + circleSize;
        currentCircleSize = circleSize;
        textPaint.setColor(textColor);
        circlePaint.setColor(circleColor);
        currentCirclePaint.setColor(currentCircleColor);
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