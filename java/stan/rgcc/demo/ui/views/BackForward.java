package stan.rgcc.demo.ui.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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
    private int centerY;
    private int leftX;
    private int rightX;
    private int outerSize;
    private int side;

    private int innerColor;
    private int outerColor;
    private int rippleColor;
    private int betweenPadding;
    private int circleSize;
    private int leftIconSize;
    private int leftIconTint;
    private Drawable leftDrawable;
    private int rightIconSize;
    private int rightIconTint;
    private Drawable rightDrawable;

    private float density;
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint shadowPaint = new Paint();
    private Paint outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private AnimatorSet rippleAnimation;
    private AccelerateDecelerateInterpolator interpolator;

    private final Animator.AnimatorListener rippleProxy = new Animator.AnimatorListener()
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
    };

    private ChangeSideListener listener;

    public BackForward(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        circlePaint.setStyle(Paint.Style.FILL);
        outerPaint.setStyle(Paint.Style.FILL);
        ripplePaint.setStyle(Paint.Style.FILL);
        TypedArray backForwardTypedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BackForward,
                0, 0);
        try
        {
            setOuterColor(backForwardTypedArray.getColor(R.styleable.BackForward_outer_color, 0));
            setInnerColor(backForwardTypedArray.getColor(R.styleable.BackForward_inner_color, 0));
            setRippleColor(backForwardTypedArray.getColor(R.styleable.BackForward_ripple_color, 0));
            setCircleSize(backForwardTypedArray.getDimensionPixelSize(R.styleable.BackForward_inner_size, px(56)));
            setBackIcon(backForwardTypedArray.getDrawable(R.styleable.BackForward_back_icon));
            setBetweenPadding(backForwardTypedArray.getDimensionPixelSize(R.styleable.BackForward_between_padding, px(56)));
            setBackIconTint(backForwardTypedArray.getColor(R.styleable.BackForward_back_icon_tint, 0));
            setBackIconSize(backForwardTypedArray.getDimensionPixelSize(R.styleable.BackForward_back_icon_size, px(48)));
            setForwardIcon(backForwardTypedArray.getDrawable(R.styleable.BackForward_forward_icon));
            setForwardIconTint(backForwardTypedArray.getColor(R.styleable.BackForward_forward_icon_tint, 0));
            setForwardIconSize(backForwardTypedArray.getDimensionPixelSize(R.styleable.BackForward_forward_icon_size, px(48)));
        }
        finally
        {
            backForwardTypedArray.recycle();
        }
        shadowPaint.setStyle(Paint.Style.FILL);
        rippleAnimation = new AnimatorSet();
        interpolator = new AccelerateDecelerateInterpolator();
        side = Sides.NOTHING;
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });
    }

    public void setRippleColor(int color)
    {
        rippleColor = color;
        recalculate();
    }
    public void setInnerColor(int color)
    {
        innerColor = color;
        recalculate();
    }
    public void setOuterColor(int color)
    {
        outerColor = color;
        recalculate();
    }
    public void setBetweenPadding(int padding)
    {
        betweenPadding = padding;
        recalculate();
    }
    public void setForwardIconTint(int color)
    {
        rightIconTint = color;
        recalculate();
    }
    public void setBackIconTint(int color)
    {
        leftIconTint = color;
        recalculate();
    }
    public void setForwardIconSize(int forwardIconSize)
    {
        rightIconSize = forwardIconSize;
        recalculate();
    }
    public void setForwardIcon(Drawable drawable)
    {
        rightDrawable = drawable;
        recalculate();
    }
    public void setBackIconSize(int backIconSize)
    {
        leftIconSize = backIconSize;
        recalculate();
    }
    public void setBackIcon(Drawable drawable)
    {
        leftDrawable = drawable;
        recalculate();
    }
    public void setCircleSize(int cs)
    {
        circleSize = cs;
        recalculate();
    }

    public void setRippleCircleSize(int value)
    {
        rippleCircleSize = value;
        invalidate();
    }
    public void setRippleX(float value)
    {
        rippleX = value;
//        Log.e(getClass().getName(), "rippleX " + rippleX);
        invalidateRipple();
    }
    public void setRippleY(float value)
    {
        rippleY = value;
//        Log.e(getClass().getName(), "rippleY " + rippleY);
        invalidateRipple();
    }
    private void invalidateRipple()
    {
        float sideX;
        switch(side)
        {
            case Sides.BACK:
                sideX = leftX;
                break;
            case Sides.FORWARD:
                sideX = rightX;
                break;
            default:
                return;
        }
        rippleCircleSize = (int)(circleSize*(1-(Math.sqrt((sideX-rippleX)*(sideX-rippleX)+(centerY-rippleY)*(centerY-rippleY))/rippleCircleStartSize)));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
//        int width = circleSize*6;
        int width = outerSize*4 + betweenPadding;
//        int height = heightMeasureSpec;
        int height = outerSize*2;
        setMeasuredDimension(width, height);
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
//                Log.e(getClass().getName(), "onTouchEvent " + x + " " + y + " MotionEvent ACTION_DOWN");
                if(y > centerY - circleSize && y < centerY + circleSize)
                {
                    if(x > leftX - circleSize && x < leftX + circleSize)
                    {
                        if(Math.sqrt((leftX-x)*(leftX-x)+(centerY-y)*(centerY-y)) < circleSize)
                        {
                            side = Sides.BACK;
                            touchDown(x, y);
                        }
                        side = Sides.BACK;
                        touchDown(x, y);
                    }
                    else if(x > rightX - circleSize && x < rightX + circleSize)
                    {
                        if(Math.sqrt((rightX-x)*(rightX-x)+(centerY-y)*(centerY-y)) < circleSize)
                        {
                            side = Sides.FORWARD;
                            touchDown(x, y);
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
//                Log.e(getClass().getName(), "onTouchEvent " + x + " " + y + " MotionEvent ACTION_MOVE");
                switch(side)
                {
                    case Sides.BACK:
                        if(Math.sqrt((leftX-x)*(leftX-x)+(centerY-y)*(centerY-y)) > circleSize)
                        {
                            side = Sides.NOTHING;
                            rippleOut();
                        }
                        break;
                    case Sides.FORWARD:
                        if(Math.sqrt((rightX-x)*(rightX-x)+(centerY-y)*(centerY-y)) > circleSize)
                        {
                            side = Sides.NOTHING;
                            rippleOut();
                        }
                        break;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            {
//                Log.e(getClass().getName(), "onTouchEvent " + x + " " + y + " MotionEvent ACTION_UP");
                switch(side)
                {
                    case Sides.BACK:
                    case Sides.FORWARD:
                        touchUp(x, y);
                        break;
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
    }
    private void touchDown(float x, float y)
    {
        float sideX;
        switch(side)
        {
            case Sides.BACK:
                sideX = leftX;
                break;
            case Sides.FORWARD:
                sideX = rightX;
                break;
            default:
                return;
        }
        Log.e(getClass().getName(), "touchDown " + x + " " + y + " side " + side + " sideX " + sideX);
        rippleCircleStartSize = (int)Math.sqrt((sideX-x)*(sideX-x)+(centerY-y)*(centerY-y));
//        Log.e(getClass().getName(), "rippleCircleStartSize " + rippleCircleStartSize);
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        rippleAnimation = new AnimatorSet();
        drawRipple = true;
        rippleAnimation.setInterpolator(interpolator);
        rippleAnimation.play(ObjectAnimator.ofFloat(this, "rippleX", x, sideX))
                       .with(ObjectAnimator.ofFloat(this, "rippleY", y, centerY));
        rippleAnimation.setDuration(600);
        rippleAnimation.start();
    }
    private void touchUp(float x, float y)
    {
        Log.e(getClass().getName(), "touchUp " + x + " " + y + " side " + side);
        if(rippleAnimation.isStarted())
        {
            rippleAnimation.removeAllListeners();
            rippleAnimation.cancel();
            rippleAnimation = new AnimatorSet();
            drawRipple = true;
            rippleAnimation.setInterpolator(interpolator);
            float sideX = -1;
            switch(side)
            {
                case Sides.BACK:
                    sideX = leftX;
                    if(listener != null)
                    {
                        listener.changeSide(Sides.BACK);
                    }
                    break;
                case Sides.FORWARD:
                    if(listener != null)
                    {
                        listener.changeSide(Sides.FORWARD);
                    }
                    sideX = rightX;
                    break;
                default:
                    return;
            }
            rippleAnimation.play(ObjectAnimator.ofFloat(this, "rippleX", rippleX, sideX))
                           .with(ObjectAnimator.ofFloat(this, "rippleY", rippleY, centerY));
            double duration = 200;
            duration *= circleSize-rippleCircleSize;
            duration /= circleSize;
            rippleAnimation.setDuration((long)duration);
            rippleAnimation.start();
            rippleAnimation.addListener(rippleProxy);
        }
        else
        {
            switch(side)
            {
                case Sides.BACK:
                case Sides.FORWARD:
                    if(listener != null)
                    {
                        listener.changeSide(side);
                    }
                    break;
            }
            rippleOut();
        }
    }
    private void rippleOut()
    {
        side = Sides.NOTHING;
        rippleAnimation.removeAllListeners();
        rippleAnimation.cancel();
        double duration = 200;
        duration *= rippleCircleSize;
        duration /= circleSize;
        if(duration <= 0)
        {
            rippleCircleSize = 0;
            drawRipple = false;
            return;
        }
        rippleAnimation = new AnimatorSet();
        rippleAnimation.play(ObjectAnimator.ofInt(this, "rippleCircleSize", rippleCircleSize, 0));
        rippleAnimation.setDuration((long)duration);
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
        canvas.drawCircle(leftX, centerY, circleSize, circlePaint);
        canvas.drawCircle(rightX, centerY, circleSize, circlePaint);
        if(leftDrawable != null)
        {
            leftDrawable.draw(canvas);
        }
        if(rightDrawable != null)
        {
            rightDrawable.draw(canvas);
        }
        if(drawRipple)
        {
            canvas.drawCircle(rippleX, rippleY, rippleCircleSize, ripplePaint);
        }
    }

    private void recalculate()
    {
        outerSize = (int)(circleSize*1.2);
        leftX = outerSize;
        rightX = leftX + outerSize + betweenPadding + outerSize;
        centerY = outerSize;
        if(leftDrawable != null)
        {
            leftDrawable.setBounds(leftX - leftIconSize/2, centerY - leftIconSize/2, leftX + leftIconSize/2, centerY+leftIconSize/2);
            leftDrawable.mutate();
            leftDrawable.setColorFilter(leftIconTint, PorterDuff.Mode.SRC_ATOP);
        }
        if(rightDrawable != null)
        {
            rightDrawable.setBounds(rightX - rightIconSize/2, centerY - rightIconSize/2, rightX + rightIconSize/2, centerY+rightIconSize/2);
            rightDrawable.mutate();
            rightDrawable.setColorFilter(rightIconTint, PorterDuff.Mode.SRC_ATOP);
        }
        outerPaint.setColor(outerColor);
        circlePaint.setColor(innerColor);
        ripplePaint.setColor(rippleColor);
//        Log.e(getClass().getName(), "recalculate"
//                + "\ncircleSize " + circleSize
//                + "\nouterSize " + outerSize
//                + "\nbetweenPadding " + betweenPadding
//        );
    }

    public void setListener(ChangeSideListener l)
    {
        listener = l;
    }

    public interface ChangeSideListener
    {
        void changeSide(int newSide);
    }
    public interface Sides
    {
        int BACK = 0;
        int FORWARD = 1;
        int NOTHING = -1;
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