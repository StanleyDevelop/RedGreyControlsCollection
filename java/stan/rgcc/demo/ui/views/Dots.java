package stan.rgcc.demo.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import stan.rgcc.demo.R;

public class Dots
        extends View
{
    private Drawable dot_icon;
    private int dot_size;
    private int dot_text_color;
    private int dot_text_size;

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int textHeight;
    private int textWidth;
    private final float density;
    private List<Dot> dots = Collections.emptyList();
    private final Listener emptyListener = new Listener()
    {
        public void newDot(int xPercent, int yPercent)
        {
        }
        public void selectDot(Dot dot)
        {
        }
    };
    private Listener listener = emptyListener;

    public Dots(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        density = context.getResources()
                         .getDisplayMetrics().density;
        TypedArray typedArray = context.getTheme()
                                       .obtainStyledAttributes(attrs, R.styleable.Dots, 0, 0);
        try
        {
            setDotIcon(typedArray.getDrawable(R.styleable.Dots_dot_icon));
            setDotSize(typedArray.getDimensionPixelSize(R.styleable.Dots_dot_size, px(44)));
            setTextColor(typedArray.getColor(R.styleable.Dots_dot_text_color, Color.BLACK));
            setTextSize(typedArray.getDimensionPixelSize(R.styleable.Dots_dot_text_size, px(20)));
        }
        finally
        {
            typedArray.recycle();
        }
        setOnClickListener(new OnClickListener()
        {
            public void onClick(View view)
            {
            }
        });
    }

    public boolean onTouchEvent(MotionEvent ev)
    {
//        Log.e(getClass().getName(), "onTouchEvent " + ev);
        float x = ev.getX();
        float y = ev.getY();
        switch(ev.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                touch(x, y);
                break;
            }
        }
        return super.onTouchEvent(ev);
    }
    private void touch(float x, float y)
    {
        if(dots.isEmpty())
        {
            listener.newDot((int)(x*100/getWidth()), (int)(y*100/getHeight()));
            return;
        }
        Dot nearestDot = dots.get(0);
        double nearestLength = Math.sqrt((Math.abs(getWidth()*nearestDot.getXPercent()/100) - Math.abs(x))*(Math.abs(getWidth()*nearestDot.getXPercent()/100) - Math.abs(x)) + (Math.abs(getHeight()*nearestDot.getYPercent()/100) - Math.abs(y))*(Math.abs(getHeight()*nearestDot.getYPercent()/100) - Math.abs(y)));
        for(Dot dot : dots)
        {
            int xCenter = getWidth()*dot.getXPercent()/100;
            int yCenter = getHeight()*dot.getYPercent()/100;
            double l = Math.sqrt((Math.abs(xCenter) - Math.abs(x))*(Math.abs(xCenter) - Math.abs(x)) + (Math.abs(yCenter) - Math.abs(y))*(Math.abs(yCenter) - Math.abs(y)));
            if(nearestLength > l)
            {
                nearestDot = dot;
                nearestLength = l;
            }
        }
        if(nearestLength > px(44))
        {
            listener.newDot((int)(x*100/getWidth()), (int)(y*100/getHeight()));
        }
        else
        {
            listener.selectDot(nearestDot);
        }
    }

    protected void onDraw(Canvas canvas)
    {
//        Log.e(getClass().getName(), "draw");
//        Log.e(getClass().getName(), "w " + getWidth());
//        canvas.drawRect(mark_radius, 0, getWidth(), getHeight(), backgroundPaint);
        for(int i=0; i<dots.size(); i++)
        {
            int xCenter = getWidth()*dots.get(i).getXPercent()/100;
            int yCenter = getHeight()*dots.get(i).getYPercent()/100;
            dot_icon.setBounds(xCenter - dot_size/2, yCenter - dot_size/2, xCenter + dot_size/2, yCenter + dot_size/2);
            dot_icon.draw(canvas);
            canvas.drawText(Integer.toString(i+1), xCenter - (i > 9 ? textWidth : textWidth/2), yCenter + textHeight/2, textPaint);
        }
    }

    public void setDotSize(int ds)
    {
        dot_size = ds;
        recalculate();
    }
    public void setDotIcon(Drawable di)
    {
        if(di != null)
        {
            dot_icon = di.getConstantState().newDrawable();
        }
        else
        {
            dot_icon = null;
        }
        recalculate();
    }
    public void setTextColor(int tc)
    {
        dot_text_color = tc;
        recalculate();
    }
    public void setTextSize(int ts)
    {
        dot_text_size = ts;
        recalculate();
    }
    private void recalculate()
    {
        textPaint.setTextSize(dot_text_size);
        Rect bounds = new Rect();
        textPaint.getTextBounds("1", 0, 1, bounds);
        textHeight = bounds.height();
        textWidth = bounds.width();
        textWidth *= 1.975;
        textPaint.setColor(dot_text_color);
    }

    public void setDots(List<Dot> ds)
    {
        dots = ds == null ? Collections.<Dot>emptyList() : ds;
        invalidate();
    }
    public void setDots(Dot firstDot, Dot secondDot, Dot... ds)
    {
        List<Dot> tmp = new ArrayList<>(ds.length + 2);
        tmp.add(firstDot);
        tmp.add(secondDot);
        tmp.addAll(Arrays.asList(ds));
        setDots(tmp);
    }
    public void setDots(Dot dot)
    {
        dots = new ArrayList<>(1);
        dots.add(dot);
        invalidate();
    }
    public void setListener(Listener l)
    {
        listener = l;
    }

    private int px(float dp)
    {
        if(dp < 0)
        {
            return 0;
        }
        return (int)Math.ceil(density * dp);
    }

    static public class Dot
    {
        private final int id;
        private final int xPercent;
        private final int yPercent;

        public Dot(int id, int xPercent, int yPercent)
        {
            this.id = id;
            this.xPercent = xPercent;
            this.yPercent = yPercent;
        }

        public int getId()
        {
            return id;
        }
        public int getXPercent()
        {
            return xPercent;
        }
        public int getYPercent()
        {
            return yPercent;
        }
    }

    public interface Listener
    {
        void newDot(int xPercent, int yPercent);
        void selectDot(Dot dot);
    }
}