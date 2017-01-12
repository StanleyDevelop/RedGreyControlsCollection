package stan.rgcc.demo.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import stan.rgcc.demo.R;

public class Switch
        extends View
{
    private int textColor;
    private int circleColor;
    private int innerCircleColor;
    private String leftText = "";
    private int textHeight;
    private int leftTextWidth;
    private int leftWidth;
    private int leftX;
    private int innerCircleX;
    private int leftTextX;
    private String rightText = "";
    private int rightTextWidth;
    private int rightX;
    private int rightTextX;
    private int textSize;
    private int circleSize;
    private int circleStrokeWidth;
    private int innerCircleSize;
    private int maxInnerCircleSize;
    private int animateDuration;
    private Interpolator interpolator;

    private int side;
    private int oldSide;

    private AnimatorSet currentAnimation;

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Animator.AnimatorListener leftToRightProxy = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animator)
        {
        }
        @Override
        public void onAnimationEnd(Animator animator)
        {
            post(new Runnable()
            {
                @Override
                public void run()
                {
                    innerCircleX = rightX;
                    moveSwitch(0, maxInnerCircleSize, animateDuration, null);
                }
            });
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
    private final Animator.AnimatorListener rightToLeftProxy = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animator)
        {
        }
        @Override
        public void onAnimationEnd(Animator animator)
        {
            post(new Runnable()
            {
                @Override
                public void run()
                {
                    innerCircleX = leftX;
                    moveSwitch(0, maxInnerCircleSize, animateDuration, null);
                }
            });
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

    private ChangeSideListener listener;

    public Switch(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        textPaint.setStyle(Paint.Style.FILL);
        circlePaint.setStyle(Paint.Style.STROKE);
        circleStrokeWidth = 2;
        innerCirclePaint.setStyle(Paint.Style.FILL);
        TypedArray switchTypedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Switch,
                0, 0);
        try
        {
            setLeftText(switchTypedArray.getString(R.styleable.Switch_left_text));
            setRightText(switchTypedArray.getString(R.styleable.Switch_right_text));
            setTextSize(switchTypedArray.getDimensionPixelSize(R.styleable.Switch_text_size, 0));
            setTextColor(switchTypedArray.getColor(R.styleable.Switch_text_color, Color.BLACK));
            setInnerCircleColor(switchTypedArray.getColor(R.styleable.Switch_inner_circle_color, Color.BLACK));
            setCircleColor(switchTypedArray.getColor(R.styleable.Switch_outer_circle_color, Color.BLACK));
            setCircleSize(switchTypedArray.getDimensionPixelSize(R.styleable.Switch_inner_circle_radius, 0));
            setAnimateDuration(switchTypedArray.getInt(R.styleable.Switch_animate_duration, 50));
        }
        finally
        {
            switchTypedArray.recycle();
        }
        interpolator = new AccelerateDecelerateInterpolator();
        side = Sides.NOTHING;
        oldSide = Sides.NOTHING;
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
        recalculate();
    }

    private void setAnimateDuration(int time)
    {
        if(time < 50)
        {
            time = 50;
        }
        else if(time > 300)
        {
            time = 300;
        }
        animateDuration = time;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int h = getHeight();
        int textY = h - (h-textHeight)/2;
        canvas.drawCircle(leftX, h/2, circleSize, circlePaint);
        if(side == Sides.LEFT || side == Sides.RIGHT)
        {
            canvas.drawCircle(innerCircleX, h/2, innerCircleSize, innerCirclePaint);
        }
        canvas.drawText(leftText, leftTextX, textY, textPaint);
        canvas.drawCircle(rightX, h/2, circleSize, circlePaint);
        canvas.drawText(rightText, rightTextX, textY, textPaint);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = rightTextX+rightTextWidth - (leftX-circleSize) + circleStrokeWidth*2;
        int height = circleSize*2 + circleStrokeWidth*2;
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
                touch(ev.getX(), ev.getY());
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                touch(ev.getX(), ev.getY());
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                if(listener != null && side != oldSide)
                {
                    oldSide = side;
                    listener.changeSide(side);
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
//        return true;
    }
    private void touch(float x, float y)
    {
//        Log.e(getClass().getName(), "touch " + x + " " + y);
        if(x < leftWidth + circleSize/2)
        {
            setSide(Sides.LEFT);
        }
        else
        {
            setSide(Sides.RIGHT);
        }
    }

    public void setInnerCircleSize(int value)
    {
        innerCircleSize = value;
//        Log.e(getClass().getName(), "innerCircleSize " + innerCircleSize);
        invalidate();
    }
    public void setSide(int newSide)
    {
        if(newSide != Sides.LEFT && newSide != Sides.RIGHT && newSide != Sides.NOTHING)
        {
            return;
        }
        if(side == newSide)
        {
            return;
        }
        if(side == Sides.NOTHING)
        {
            side = newSide;
            switch(newSide)
            {
                case Sides.LEFT:
                    innerCircleX = leftX;
                    break;
                case Sides.RIGHT:
                    innerCircleX = rightX;
                    break;
            }
            moveSwitch(0, maxInnerCircleSize, animateDuration, null);
        }
        else if(newSide == Sides.LEFT)
        {
            side = newSide;
            innerCircleX = rightX;
            moveSwitch(innerCircleSize, 0, animateDuration, rightToLeftProxy);
        }
        else if(newSide == Sides.RIGHT)
        {
            side = newSide;
            innerCircleX = leftX;
            moveSwitch(innerCircleSize, 0, animateDuration, leftToRightProxy);
        }
    }
    private void cancelCurrentAnimation()
    {
        if(currentAnimation != null)
        {
            currentAnimation.removeAllListeners();
            currentAnimation.cancel();
            currentAnimation = null;
        }
    }
    public void moveSwitch(int start, int end, int duration, Animator.AnimatorListener listener)
    {
        cancelCurrentAnimation();
        currentAnimation = new AnimatorSet();
        currentAnimation.play(ObjectAnimator.ofInt(this, "innerCircleSize", start, end));
        currentAnimation.setDuration(duration);
        currentAnimation.setInterpolator(interpolator);
        if(listener != null)
        {
            currentAnimation.addListener(listener);
        }
        currentAnimation.start();
    }

    public int getSide()
    {
        return side;
    }

    public void setLeftText(String lt)
    {
        leftText = lt == null ? "" : lt;
        recalculate();
    }

    public void setRightText(String rt)
    {
        rightText = rt == null ? "" : rt;
        recalculate();
    }

    public void setTextSize(int ts)
    {
        textSize = ts;
        recalculate();
    }

    public void setCircleSize(int cs)
    {
        circleSize = cs;
        recalculate();
    }

    private void recalculate()
    {
        circlePaint.setStrokeWidth(circleStrokeWidth);
        leftTextWidth = (int)textPaint.measureText(leftText);
        rightTextWidth = (int)textPaint.measureText(rightText);
        textPaint.setTextSize(textSize);
        Rect bounds = new Rect();
//        textPaint.getTextBounds(leftText+rightText, 0, (leftText+rightText).length(), bounds);
        textPaint.getTextBounds("1", 0, 1, bounds);
        textHeight = bounds.height();
//        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//        textHeight = (int)(fontMetrics.bottom-fontMetrics.top);
//        textHeight = (int)(fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);
//        Log.e(getClass().getName(), "textHeight " + textHeight + " textSize " + textSize + " fontMetrics " + (fontMetrics.bottom-fontMetrics.top));
        float tmp = circleSize;
        tmp /= 100;
        innerCircleSize = (int)(tmp*80);
        maxInnerCircleSize = innerCircleSize;
        leftX = circleSize + circleStrokeWidth;
        leftTextX = leftX + circleSize*2;
        leftWidth = leftTextX + leftTextWidth;
        rightX = leftWidth + circleSize*2 + circleStrokeWidth;
        rightTextX = rightX + circleSize*2;
        if(side == Sides.LEFT)
        {
            innerCircleX = leftX;
        }
        else if(side == Sides.RIGHT)
        {
            innerCircleX = rightX;
        }
        textPaint.setColor(textColor);
        circlePaint.setColor(circleColor);
        innerCirclePaint.setColor(innerCircleColor);
    }

    public void setListener(ChangeSideListener l)
    {
        listener = l;
    }

    public void setTextColor(int tc)
    {
        textColor = tc;
        recalculate();
    }
    public void setCircleColor(int cc)
    {
        circleColor = cc;
        recalculate();
    }
    public void setInnerCircleColor(int cc)
    {
        innerCircleColor = cc;
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

    public interface ChangeSideListener
    {
        void changeSide(int newSide);
    }
    public interface Sides
    {
        int LEFT = 0;
        int RIGHT = 1;
        int NOTHING = -1;
    }
}