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
import java.util.Comparator;
import java.util.List;

import stan.rgcc.demo.R;

public class QueueDots
        extends View
{
    private int dot_size;
    private int color_not_init;
    private int color_init;
    private int color_current;

    private Paint dotNotInitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint dotInitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint dotCurrentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float density;
    private List<Dot> dots = Collections.emptyList();
    private final Comparator<Dot> dotsComparator = new Comparator<Dot>()
    {
        public int compare(Dot d1, Dot d2)
        {
            return d1.order() > d2.order() ? 1 : d1.order() < d2.order() ? -1 : 0;
        }
    };
    private final Listener emptyListener = new Listener()
    {
        public void first()
        {
        }
        public void nextDot(Dot dot)
        {
        }
        public void last()
        {
        }
    };
    private Listener listener = emptyListener;

    public QueueDots(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        density = context.getResources()
                         .getDisplayMetrics().density;
        TypedArray typedArray = context.getTheme()
                                       .obtainStyledAttributes(attrs, R.styleable.QueueDots, 0, 0);
        try
        {
            setDotSize(typedArray.getDimensionPixelSize(R.styleable.QueueDots_dot_radius, px(54)));
            setColorNotInit(typedArray.getColor(R.styleable.QueueDots_color_not_init, Color.GRAY));
            setColorInit(typedArray.getColor(R.styleable.QueueDots_color_init, Color.GREEN));
            setColorCurrent(typedArray.getColor(R.styleable.QueueDots_color_current, Color.BLUE));
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
            return;
        }
        Dot nearestDot = dots.get(0);
        double nearestLength = Math.sqrt((Math.abs(getWidth()*nearestDot.getXPercent()/100) - Math.abs(x))*(Math.abs(getWidth()*nearestDot.getXPercent()/100) - Math.abs(x)) + (Math.abs(getHeight()*nearestDot.getYPercent()/100) - Math.abs(y))*(Math.abs(getHeight()*nearestDot.getYPercent()/100) - Math.abs(y)));
        for(Dot dot : dots)
        {
            if(nearestDot.getId() == dot.getId())
            {
                continue;
            }
            int xCenter = getWidth()*dot.getXPercent()/100;
            int yCenter = getHeight()*dot.getYPercent()/100;
            double l = Math.sqrt((Math.abs(xCenter) - Math.abs(x))*(Math.abs(xCenter) - Math.abs(x)) + (Math.abs(yCenter) - Math.abs(y))*(Math.abs(yCenter) - Math.abs(y)));
            if(nearestLength > l)
            {
                nearestDot = dot;
                nearestLength = l;
            }
        }
        if(nearestLength < dot_size)
        {
            for(Dot dot : dots)
            {
                if(dot.state == Dot.State.CURRENT)
                {
                    if(dot.getId() == nearestDot.getId())
                    {
                        return;
                    }
                    dot.changeState(Dot.State.INIT);
                    break;
                }
            }
            nearestDot.changeState(Dot.State.CURRENT);
            listener.nextDot(nearestDot);
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas)
    {
//        Log.e(getClass().getName(), "draw");
//        Log.e(getClass().getName(), "w " + getWidth());
//        canvas.drawRect(mark_radius, 0, getWidth(), getHeight(), backgroundPaint);
        for(Dot dot: dots)
        {
            int xCenter = getWidth()*dot.getXPercent()/100;
            int yCenter = getHeight()*dot.getYPercent()/100;
            canvas.drawCircle(xCenter, yCenter, dot_size/2, dot.state == Dot.State.NOT_INIT ? dotNotInitPaint : dot.state == Dot.State.INIT ? dotInitPaint : dotCurrentPaint);
        }
//        for(int i=0; i<dots.size(); i++)
//        {
//            int xCenter = getWidth()*dots.get(i).getXPercent()/100;
//            int yCenter = getHeight()*dots.get(i).getYPercent()/100;
//            dot_icon.setBounds(xCenter - dot_size/2, yCenter - dot_size/2, xCenter + dot_size/2, yCenter + dot_size/2);
//            dot_icon.draw(canvas);
//            if(showNumbers)
//            {
//                canvas.drawText(Integer.toString(i+1), xCenter - (i+1 > 9 ? textWidth : textWidth/2), yCenter + textHeight/2, textPaint);
//            }
//        }
    }

    public void setDotSize(int ds)
    {
        dot_size = ds;
        recalculate();
    }
    public void setColorNotInit(int color)
    {
        color_not_init = color;
        recalculate();
    }
    public void setColorInit(int color)
    {
        color_init = color;
        recalculate();
    }
    public void setColorCurrent(int color)
    {
        color_current = color;
        recalculate();
    }
    private void recalculate()
    {
        dotNotInitPaint.setColor(color_not_init);
        dotInitPaint.setColor(color_init);
        dotCurrentPaint.setColor(color_current);
    }

    public void setDots(List<Dot> ds)
    {
        dots = ds == null ? Collections.<Dot>emptyList() : ds;
        Collections.sort(dots, dotsComparator);
        if(!dots.isEmpty())
        {
            dots.get(0).changeState(Dot.State.CURRENT);
        }
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
        setDots(Collections.singletonList(dot));
    }
    public void clearDots()
    {
        setDots(Collections.<Dot>emptyList());
    }
    public void setListener(Listener l)
    {
        listener = l;
    }

    public void next()
    {
        for(int i=0; i<dots.size(); i++)
        {
            if(dots.get(i).state == Dot.State.CURRENT)
            {
                if(dots.size() > i+1)
                {
                    dots.get(i).changeState(Dot.State.INIT);
                    dots.get(i+1).changeState(Dot.State.CURRENT);
                    listener.nextDot(dots.get(i+1));
                }
                else
                {
                    listener.last();
                }
                invalidate();
                return;
            }
        }
    }
    public void previous()
    {
        for(int i=0; i<dots.size(); i++)
        {
            if(dots.get(i).state == Dot.State.CURRENT)
            {
                if(i > 0)
                {
                    dots.get(i).changeState(Dot.State.INIT);
                    dots.get(i-1).changeState(Dot.State.CURRENT);
                    listener.nextDot(dots.get(i-1));
                }
                else
                {
                    listener.first();
                }
                invalidate();
                return;
            }
        }
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
        private final int order;
        private State state;

        public Dot(int i, int x, int y, int o)
        {
            this(i, x, y, o, State.NOT_INIT);
        }
        public Dot(int i, int x, int y, int o, State s)
        {
            id = i;
            xPercent = x;
            yPercent = y;
            order = o;
            state = s;
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
        public int order()
        {
            return order;
        }
        protected State state()
        {
            return state;
        }
        protected void changeState(State s)
        {
            state = s;
        }

        enum State
        {
            NOT_INIT,
            INIT,
            CURRENT,
        }
    }

    public interface Listener
    {
        void first();
        void nextDot(Dot dot);
        void last();
    }
}