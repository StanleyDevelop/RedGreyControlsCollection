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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
    private int innerCircleSize;
    private int maxInnerCircleSize;

    private Side side;

    private AnimatorSet currentAnimation;

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ChangeSideListener listener;

    public Switch(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        textPaint.setStyle(Paint.Style.FILL);
        circlePaint.setStyle(Paint.Style.STROKE);
        innerCirclePaint.setStyle(Paint.Style.FILL);
        TypedArray switchTypedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Switch,
                0, 0);
        TypedArray circlableTypedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Circlable,
                0, 0);
        try
        {
            setLeftText(switchTypedArray.getString(R.styleable.Switch_left_text));
            setRightText(switchTypedArray.getString(R.styleable.Switch_right_text));
            setTextSize(switchTypedArray.getDimensionPixelSize(R.styleable.Switch_text_size, 0));
            setTextColor(switchTypedArray.getColor(R.styleable.Switch_text_color, Color.BLACK));
            setInnerCircleColor(switchTypedArray.getColor(R.styleable.Switch_inner_circle_color, Color.BLACK));
            setCircleColor(circlableTypedArray.getColor(R.styleable.Circlable_circle_color, Color.BLACK));
            setCircleSize(circlableTypedArray.getDimensionPixelSize(R.styleable.Circlable_circle_size, 0));
        }
        finally
        {
            switchTypedArray.recycle();
            circlableTypedArray.recycle();
        }
        recalculate();
        post(new Runnable()
        {
            @Override
            public void run()
            {
                setSide(Side.LEFT);
            }
        });
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        int h = getHeight();
        int textY = h - (h-textHeight)/2;
//        int textY = h - (h-textSize)/2;
        canvas.drawCircle(leftX, h/2, circleSize, circlePaint);
        canvas.drawCircle(innerCircleX, h/2, innerCircleSize, innerCirclePaint);
        canvas.drawText(leftText, leftTextX, textY, textPaint);
        canvas.drawCircle(rightX, h/2, circleSize, circlePaint);
        canvas.drawText(rightText, rightTextX, textY, textPaint);

//        canvas.drawRect(leftTextX, textY - textHeight, leftTextX +leftTextWidth, textY, textPaint);

//        canvas.drawLine(circleSize, h, circleSize + 1, h, circlePaint);
//        canvas.drawLine(leftTextX, h, leftTextX + 1, h, circlePaint);
//        canvas.drawLine(leftTextX + leftTextWidth, h, leftTextX + leftTextWidth + 1, h, circlePaint);
//        canvas.drawLine(leftTextX, h, leftTextX + 1, h, circlePaint);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = rightTextX+rightTextWidth - (leftX-circleSize);
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
                break;
            }
        }
        return super.onTouchEvent(ev);
//        return true;
    }
    private void touch(float x, float y)
    {
//        Log.e(getClass().getName(), "touch " + x + " " + y);
        if(x < getWidth()/2)
        {
            setSide(Side.LEFT);
        }
        else
        {
            setSide(Side.RIGHT);
        }
    }

    public void setInnerCircleSize(int value)
    {
        innerCircleSize = value;
//        Log.e(getClass().getName(), "innerCircleSize " + innerCircleSize);
        invalidate();
    }
    public void setSide(Side s)
    {
        if(side == s)
        {
            return;
        }
        side = s;
        if(listener != null)
        {
            listener.changeSide(side);
        }
        if(side == Side.LEFT)
        {
            final int ics = innerCircleSize;
            moveSwitch(ics, 0, 150, new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {
                    innerCircleX = rightX;
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
                            moveSwitch(0, maxInnerCircleSize, 150, null);
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
            });
        }
        else if(side == Side.RIGHT)
        {
            final int ics = innerCircleSize;
            moveSwitch(ics, 0, 150, new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animator)
                {
                    innerCircleX = leftX;
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
                            moveSwitch(0, maxInnerCircleSize, 150, null);
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
            });
        }
    }
    private void cancelCurrentAnimation()
    {
        if(currentAnimation != null)
        {
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
        if(listener != null)
        {
            currentAnimation.addListener(listener);
        }
        currentAnimation.start();
    }

    public Side getSide()
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
        leftTextWidth = (int)textPaint.measureText(leftText);
        rightTextWidth = (int)textPaint.measureText(rightText);
        textPaint.setTextSize(textSize);
//        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        Rect bounds = new Rect();
        textPaint.getTextBounds(leftText+rightText, 0, (leftText+rightText).length(), bounds);
        textHeight = bounds.height();
//        textHeight = (int)(fontMetrics.bottom-fontMetrics.top);
//        textHeight = (int)(fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);
//        Log.e(getClass().getName(), "textHeight " + textHeight + " textSize " + textSize + " fontMetrics " + (fontMetrics.bottom-fontMetrics.top));
        float tmp = circleSize;
        tmp /= 100;
        innerCircleSize = (int)(tmp*80);
        maxInnerCircleSize = innerCircleSize;
        leftX = circleSize;
        leftTextX = leftX + circleSize*2;
        leftWidth = leftTextX + leftTextWidth;
        rightX = leftWidth + circleSize*2;
        rightTextX = rightX + circleSize*2;
        if(side == Side.LEFT)
        {
            innerCircleX = leftX;
        }
        else if(side == Side.RIGHT)
        {
            innerCircleX = rightX;
        }
        textPaint.setColor(textColor);
        circlePaint.setColor(circleColor);
        innerCirclePaint.setColor(innerCircleColor);
    }

    public void setListener(ChangeSideListener listener)
    {
        this.listener = listener;
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

    public interface ChangeSideListener
    {
        void changeSide(Side newSide);
    }
    public enum Side
    {
        LEFT,
        RIGHT,
        NOTHING,
    }
}