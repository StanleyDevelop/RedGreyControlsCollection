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
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import stan.rgcc.demo.R;

public class TextSteps
        extends View
{
    private int circleSize;
    private int textSize;
    private int betweenPadding;
    private int textColor;
    private int circleColor;
    private int currentCircleColor;
    private Interpolator interpolator;
    private int animationTime;

    private List<String> steps = Collections.emptyList();
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
    private int maxWidth;

    private float density;
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint currentCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int currentStep;
    private int oldStep;
    private AnimatorSet stepsAnimation;

    private ChangeStepListener listener;

    public TextSteps(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        stepsAnimation = new AnimatorSet();
        TypedArray stepsTypedArray = context.getTheme()
                                            .obtainStyledAttributes(attrs, R.styleable.Steps, 0, 0);
        try
        {
            int cStep = stepsTypedArray.getInt(R.styleable.Steps_current_step, 0);
            if(cStep < 0)
            {
                cStep = 0;
            }
            currentStep = cStep;
            setCircleSize(stepsTypedArray.getDimensionPixelSize(R.styleable.Steps_step_circle_radius, px(12)));
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
//        interpolator = new AccelerateDecelerateInterpolator();
        interpolator = new BounceInterpolator();
        setAnimationTime(300);
        setOnClickListener(new OnClickListener()
        {
            public void onClick(View view)
            {
            }
        });
    }

    public void setAnimationTime(int time)
    {
        if(time < 50)
        {
            time = 50;
        }
        else if(time > 600)
        {
            time = 600;
        }
        animationTime = time*2;
        recalculate();
    }
    public void setInterpolator(Interpolator i)
    {
        if(i != null)
        {
            interpolator = i;
            recalculate();
        }
    }
    public void setCurrentCircleColor(int color)
    {
        currentCircleColor = color;
        recalculate();
    }
    public void setCircleColor(int color)
    {
        circleColor = color;
        recalculate();
    }
    public void setTextColor(int color)
    {
        textColor = color;
        recalculate();
    }
    public void setCurrentStep(int cStep)
    {
        if(cStep == currentStep)
        {
            return;
        }
        if(cStep < 0)
        {
            cStep = 0;
        }
        else if(cStep > steps.size()-1)
        {
            cStep = steps.size()-1;
        }
        animateStepsVertical(cStep);
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
        stepsAnimation.setDuration(animationTime);
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
        stepsAnimation.setDuration(animationTime);
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

    public void setSteps(List<String> ss)
    {
        steps = ss == null || ss.size() < 2 ? Collections.<String>emptyList() : ss;
        recalculate();
    }
    public void setSteps(String firstStep, String secondStep, String... ss)
    {
        steps = new ArrayList<>(ss.length + 2);
        steps.add(firstStep);
        steps.add(secondStep);
        steps.addAll(Arrays.asList(ss));
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
        for(int i=0; i<steps.size(); i++)
        {
            if(y > (circleSize*2+betweenPadding)*i && y < (circleSize*2+betweenPadding)*i+circleSize*2)
            {
                setCurrentStep(i);
                return;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int height = 0;
        if(steps.size() > 1)
        {
            height = circleSize*2*steps.size() + betweenPadding*(steps.size()-1);
        }
        setMeasuredDimension(maxWidth, height);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        if(steps.size() < 2)
        {
            return;
        }
        drawRectsVertical(canvas);
//        canvas.drawCircle(centerX, currentStepY, currentCircleSize, currentCirclePaint);
        canvas.drawCircle(circleSize, currentStepY, currentCircleSize, currentCirclePaint);
        canvas.drawCircle(maxWidth - circleSize, currentStepY, currentCircleSize, currentCirclePaint);
        canvas.drawRect(circleSize, currentStepY - currentCircleSize, maxWidth - circleSize, currentStepY + currentCircleSize, currentCirclePaint);
        drawTextsVertical(canvas);
    }

    private void drawTextsVertical(Canvas canvas)
    {
        int tempWidth = getTextWidth(steps.get(0));
        canvas.drawText(steps.get(0), maxWidth/2 - tempWidth/2,  circleSize + textHeight/2, textPaint);
        for(int i=1; i<steps.size()-1; i++)
        {
            tempWidth = getTextWidth(steps.get(i));
            canvas.drawText(steps.get(i), maxWidth/2 - tempWidth/2, i*circleSize*2 + circleSize + i*betweenPadding + textHeight/2, textPaint);
        }
        tempWidth = getTextWidth(steps.get(steps.size()-1));
        canvas.drawText(steps.get(steps.size()-1), maxWidth/2 - tempWidth/2, (steps.size()-1)*circleSize*2 + circleSize + (steps.size()-1)*betweenPadding + textHeight/2, textPaint);
    }

    private void drawRectsVertical(Canvas canvas)
    {
        canvas.drawCircle(circleSize, circleSize, circleSize, circlePaint);
        canvas.drawCircle(maxWidth - circleSize, circleSize, circleSize, circlePaint);
        canvas.drawRect(circleSize, 0, maxWidth - circleSize, circleSize*2, circlePaint);
        for(int i=1; i<steps.size()-1; i++)
        {
            canvas.drawCircle(circleSize, i*circleSize*2 + circleSize + i*betweenPadding, circleSize, circlePaint);
            canvas.drawCircle(maxWidth - circleSize, i*circleSize*2 + circleSize + i*betweenPadding, circleSize, circlePaint);
            canvas.drawRect(circleSize, i*circleSize*2 + i*betweenPadding, maxWidth - circleSize, i*circleSize*2 + i*betweenPadding + circleSize*2, circlePaint);
        }
//        canvas.drawCircle(centerX, (steps.size()-1)*circleSize*2 + circleSize + (steps.size()-1)*betweenPadding, circleSize, circlePaint);
        canvas.drawCircle(circleSize, (steps.size()-1)*circleSize*2 + circleSize + (steps.size()-1)*betweenPadding, circleSize, circlePaint);
        canvas.drawCircle(maxWidth - circleSize, (steps.size()-1)*circleSize*2 + circleSize + (steps.size()-1)*betweenPadding, circleSize, circlePaint);
        canvas.drawRect(circleSize, (steps.size()-1)*circleSize*2 + (steps.size()-1)*betweenPadding, maxWidth - circleSize, (steps.size()-1)*circleSize*2 + (steps.size()-1)*betweenPadding + circleSize*2, circlePaint);
    }

    private void recalculate()
    {
        if(steps.size() < 2)
        {
            return;
        }
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
        textWidth = bounds.width();
        textWidth*=2;
        currentStepX = (circleSize*2+betweenPadding)*currentStep + circleSize;
        currentStepY = (circleSize*2+betweenPadding)*currentStep + circleSize;
        currentCircleSize = circleSize;
        textPaint.setColor(textColor);
        circlePaint.setColor(circleColor);
        currentCirclePaint.setColor(currentCircleColor);
        calculateWidth();
        invalidate();
    }
    private void calculateWidth()
    {
        Rect firstBounds = new Rect();
        textPaint.getTextBounds(steps.get(0), 0, steps.get(0).length(), firstBounds);
        maxWidth = firstBounds.width();
        for(String step : steps)
        {
            int tmp = getTextWidth(step);
            if(tmp > maxWidth)
            {
                maxWidth = tmp;
            }
        }
        maxWidth += px(20);
        Log.e(getClass().getName(), "calculate width"
                + "\nmaxWidth " + maxWidth
        );
    }
    private int getTextWidth(String text)
    {
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
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