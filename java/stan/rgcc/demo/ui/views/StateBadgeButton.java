package stan.rgcc.demo.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
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

public class StateBadgeButton
    extends View
{
    private int colorOn;
    private int colorOff;
    private int state;
    private int iconSize;
    private int textSize;
    private String text;
    private Drawable icon;
    private int badgeColor;
    private int badgeTextColor;
    private int badgeSize;
    private int badgeTextSize;
    private int badgeCount;
    private boolean drawBadge;

    private float density;
    private Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint badgeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private AnimatorSet rippleAnimation = new AnimatorSet();
    private AnimatorSet badgeCountAnimation = new AnimatorSet();
    private Interpolator interpolator;
    private int minHeight;
    private int minWidth;
    private int textHeight;
    private int textWidth;
    private int centerX;
    private int centerY;
    private int currentColor;
    private int badgeTextHeight;
    private float badgeTextWidth;
    private int badgeX;
    private int badgeY;
    private boolean drawRipple;
    private final int maxRippleAlpha = 128;
    private int rippleCircleSize;
    private int maxRippleCircleSize;
    private float rippleX;
    private float rippleY;
    private boolean dispatch;
    private int newBadgeCount;
    private final Animator.AnimatorListener rippleSizeProxy = new Animator.AnimatorListener()
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
    private final Animator.AnimatorListener rippleTransparentProxy = new Animator.AnimatorListener()
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
            ripplePaint.setAlpha(maxRippleAlpha);
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
    private final Animator.AnimatorListener badgeCountHideProxy = new Animator.AnimatorListener()
    {
        @Override
        public void onAnimationStart(Animator animator)
        {
        }
        @Override
        public void onAnimationEnd(Animator animator)
        {
            badgeCount = newBadgeCount;
            recalculate();
            animateBadgeCountShow();
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

    public StateBadgeButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        text = "";
        density = context.getResources().getDisplayMetrics().density;
        buttonPaint.setStyle(Paint.Style.FILL);
        drawBadge = false;
        drawRipple = false;
        interpolator = new AccelerateDecelerateInterpolator();
        TypedArray stateBadgeButtonTypedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StateBadgeButton,
                0, 0);
        try
        {
            setColorOn(stateBadgeButtonTypedArray.getColor(R.styleable.StateBadgeButton_color_on, 0));
            setColorOff(stateBadgeButtonTypedArray.getColor(R.styleable.StateBadgeButton_color_off, 0));
            setState(stateBadgeButtonTypedArray.getInteger(R.styleable.StateBadgeButton_button_state, 0));
            setIcon(stateBadgeButtonTypedArray.getDrawable(R.styleable.StateBadgeButton_button_icon));
            setIconSize(stateBadgeButtonTypedArray.getDimensionPixelSize(R.styleable.StateBadgeButton_button_icon_size, px(16)));
            setTextSize(stateBadgeButtonTypedArray.getDimensionPixelSize(R.styleable.StateBadgeButton_button_text_size, px(8)));
            setText(stateBadgeButtonTypedArray.getString(R.styleable.StateBadgeButton_button_text));
            setBadgeColor(stateBadgeButtonTypedArray.getColor(R.styleable.StateBadgeButton_badge_color, 0));
            setBadgeTextColor(stateBadgeButtonTypedArray.getColor(R.styleable.StateBadgeButton_badge_text_color, 0));
            setBadgeSize(stateBadgeButtonTypedArray.getDimensionPixelSize(R.styleable.StateBadgeButton_badge_radius, px(16)));
            setBadgeTextSize(stateBadgeButtonTypedArray.getDimensionPixelSize(R.styleable.StateBadgeButton_badge_text_size, px(8)));
        }
        finally
        {
            stateBadgeButtonTypedArray.recycle();
        }
    }

    public void setBadgeCountSize(int value)
    {
        badgeTextPaint.setTextSize(value);
        recalculateBadgeText();
        invalidate();
    }
    public void setRippleAlpha(int value)
    {
        ripplePaint.setAlpha(value);
        invalidate();
    }
    public void setRippleCircleSize(int value)
    {
        rippleCircleSize = value;
        invalidate();
    }
    public void setDrawBadge(boolean d)
    {
        drawBadge = d;
        recalculate();
    }
    public void setBadgeCount(int c)
    {
        if(c == badgeCount)
        {
            return;
        }
        newBadgeCount = c;
        animateBadgeCountHide();
    }
    public void setBadgeTextSize(int size)
    {
        badgeTextSize = size;
        recalculate();
    }
    public void setBadgeTextColor(int color)
    {
        badgeTextColor = color;
        recalculate();
    }
    public void setBadgeSize(int size)
    {
        badgeSize = size;
        recalculate();
    }
    public void setBadgeColor(int color)
    {
        badgeColor = color;
        recalculate();
    }
    public void setIcon(Drawable i)
    {
        icon = i;
        if(icon != null)
        {
            icon.mutate();
        }
        recalculate();
    }
    public void setText(String t)
    {
        if(t == null)
        {
            return;
        }
        text = t;
        recalculate();
    }
    public void setTextSize(int size)
    {
        textSize = size;
        recalculate();
    }
    public void setIconSize(int size)
    {
        iconSize = size;
        recalculate();
    }
    public void setState(int s)
    {
        if(s != States.OFF && s != States.ON)
        {
            return;
        }
        if(s == state)
        {
            return;
        }
        state = s;
        recalculate();
    }
    public void setColorOff(int color)
    {
        colorOff = color;
        recalculate();
    }
    public void setColorOn(int color)
    {
        colorOn = color;
        recalculate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int width = minWidth;
        int height = minHeight;
        if(widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        }
        if(textWidth > width)
        {
            text += "...";
            while(textWidth > width)
            {
                text = text.substring(0, text.length()-4)+"...";
                textWidth = (int)buttonPaint.measureText(text);
            }
            recalculate();
        }
        width = minWidth;
        if(widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(heightMode == MeasureSpec.EXACTLY)
        {
            if(heightSize > height)
            {
                height = heightSize;
            }
        }
        width += getPaddingRight();
        width += getPaddingLeft();
        height += getPaddingTop();
        height += getPaddingBottom();
        centerX = width/2;
        centerY = height/2;
        badgeX = width-badgeSize-getPaddingRight();
        badgeY = badgeSize+getPaddingTop();
        maxRippleCircleSize = (int)Math.sqrt(width*width + height*height);
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
        int masked = ev.getActionMasked();
        float x = ev.getX();
        float y = ev.getY();
        switch(masked)
        {
            case MotionEvent.ACTION_DOWN:
            {
                if(state == States.OFF)
                {
                    rippleX = x;
                    rippleY = y;
                    animateRipple(900, null);
                    dispatch = true;
                    return true;
                }
                else
                {
                    dispatch = false;
                    return false;
                }
            }
            case MotionEvent.ACTION_MOVE:
            {
//                Log.e(getClass().getName(), "ACTION_MOVE x " + x +" y "+ y);
                if(y<0 || y>getHeight() || x<0 || x>getWidth())
                {
                    dispatch = false;
                    rippleOut();
                }
//                rippleX = x;
//                rippleY = y;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            {
                rippleOut();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                performClick();
                rippleOut();
                break;
            }
        }
        return super.onTouchEvent(ev);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        int y = centerY - minHeight/2;
//        canvas.drawLine(0, y, getWidth(), y, buttonPaint);
        if(icon != null)
        {
            icon.setBounds(centerX - iconSize/2, y, centerX + iconSize/2, y+iconSize);
            icon.draw(canvas);
            y += iconSize;
        }
        canvas.drawText(text, centerX - textWidth/2, y + textHeight, buttonPaint);
        if(drawRipple)
        {
            canvas.drawCircle(rippleX, rippleY, rippleCircleSize, ripplePaint);
        }
        if(drawBadge)
        {
            canvas.drawCircle(badgeX, badgeY, badgeSize, badgePaint);
            canvas.drawText(Integer.toString(badgeCount), badgeX-badgeTextWidth/2, badgeY+badgeTextHeight/2, badgeTextPaint);
//            canvas.drawLine(badgeX-badgeTextWidth/2, 0, badgeX-badgeTextWidth/2, getHeight(), buttonPaint);
//            canvas.drawLine(badgeX+badgeTextWidth/2, 0, badgeX+badgeTextWidth/2, getHeight(), buttonPaint);
//            canvas.drawLine(badgeX, 0, badgeX, getHeight(), buttonPaint);
//            canvas.drawLine(0, badgeY, getWidth(), badgeY, buttonPaint);
        }
//        canvas.drawLine(0, y, getWidth(), y, buttonPaint);
//        canvas.drawLine(0, centerY + minHeight/2, getWidth(), centerY + minHeight/2, buttonPaint);
    }
    private void animateBadgeCountHide()
    {
        Log.e(getClass().getName(), "animateBadgeCountHide");
        badgeCountAnimation.removeAllListeners();
        badgeCountAnimation.cancel();
        badgeCountAnimation = new AnimatorSet();
        badgeCountAnimation.setInterpolator(interpolator);
        badgeCountAnimation.play(ObjectAnimator.ofInt(this, "badgeCountSize", badgeTextSize, 0));
        badgeCountAnimation.addListener(badgeCountHideProxy);
        badgeCountAnimation.setDuration(100);
        badgeCountAnimation.start();
    }
    private void animateBadgeCountShow()
    {
        Log.e(getClass().getName(), "animateBadgeCountShow");
        badgeCountAnimation.removeAllListeners();
        badgeCountAnimation.cancel();
        badgeCountAnimation = new AnimatorSet();
        badgeCountAnimation.setInterpolator(interpolator);
        badgeCountAnimation.play(ObjectAnimator.ofInt(this, "badgeCountSize", 0, badgeTextSize));
        badgeCountAnimation.setDuration(100);
        badgeCountAnimation.start();
    }
    private void animateRipple(int time, Animator.AnimatorListener listener)
    {
        Log.e(getClass().getName(), "animateRipple");
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        rippleAnimation = new AnimatorSet();
        ripplePaint.setAlpha(maxRippleAlpha);
        drawRipple = true;
        rippleAnimation.setInterpolator(interpolator);
        rippleAnimation.play(ObjectAnimator.ofInt(this, "rippleCircleSize", maxRippleCircleSize/5, maxRippleCircleSize));
        rippleAnimation.setDuration(time);
        if(listener != null)
        {
            rippleAnimation.addListener(listener);
        }
        rippleAnimation.start();
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
        rippleAnimation.addListener(rippleSizeProxy);
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
        rippleAnimation.addListener(rippleTransparentProxy);
        rippleAnimation.start();
    }

    private void recalculate()
    {
        ripplePaint.setColor(colorOn);
        ripplePaint.setAlpha(maxRippleAlpha);
        badgePaint.setColor(badgeColor);
        badgeTextPaint.setColor(badgeTextColor);
        badgeTextPaint.setTextSize(badgeTextSize);
        recalculateBadgeText();
//        badgeTextWidth *= 2;
//        badgeTextWidth /= 3;
//        badgeTextWidth *= 0.985;
        currentColor = state == States.ON ? colorOn : colorOff;
        buttonPaint.setColor(currentColor);
        buttonPaint.setTextSize(textSize);
        Rect bounds = new Rect();
        buttonPaint.getTextBounds("1", 0, 1, bounds);
        textHeight = bounds.height();
        minHeight = textHeight;
        textWidth = (int)buttonPaint.measureText(text);
        if(icon != null)
        {
            icon.setColorFilter(currentColor, PorterDuff.Mode.SRC_IN);
            minHeight += iconSize;
            minWidth = iconSize > textWidth ? iconSize : textWidth;
        }
        invalidate();
        Log.e(getClass().getName(), "recalculate"
                + "\nbadgeTextWidth " + badgeTextWidth
        );
    }
    private void recalculateBadgeText()
    {
        Rect badgeBounds = new Rect();
        badgeTextPaint.getTextBounds("1", 0, 1, badgeBounds);
        badgeTextHeight = badgeBounds.height();
        badgeTextWidth = badgeBounds.width();
        badgeTextWidth *= 1.975;
        if(badgeCount > 9)
        {
            badgeTextWidth += badgeTextWidth;
        }
        else if(badgeCount < 0)
        {
            badgeTextWidth += badgeTextWidth/2;
            if(badgeCount < -9)
            {
                badgeTextWidth += badgeTextWidth/2;
            }
        }
    }

    public interface States
    {
        int ON = 1;
        int OFF = 0;
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