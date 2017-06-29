package stan.rgcc.demo.ui.fragments;

import android.view.View;

import java.util.Arrays;
import java.util.List;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.QueueDots;

public class QueueDotsFragment
    extends UtilFragment
{
    private QueueDots queue;

    protected View.OnClickListener setClickListener()
    {
        return new View.OnClickListener()
        {
            public void onClick(View view)
            {
                switch(view.getId())
                {
                    case R.id.previous:
                        queue.previous();
                        break;
                    case R.id.next:
                        queue.next();
                        break;
                    case R.id.reload:
                        queue.setDots(dots());
                        break;
                }
            }
        };
    }
    protected int setContentView()
    {
        return R.layout.queue_dots_fragment;
    }
    protected void initViews(View v)
    {
        queue = findView(R.id.queue);
        setClickListener(findView(R.id.previous), findView(R.id.next), findView(R.id.reload));
    }
    protected void init()
    {
        queue.setDots(dots());
        queue.setListener(new QueueDots.Listener()
        {
            public void first()
            {
                toast("first");
            }
            public void nextDot(QueueDots.Dot dot)
            {
                log("next dot " + dot.getId() + " " + dot.getXPercent() + " " + dot.getYPercent());
//                toast("next dot " + dot.getXPercent() + " " + dot.getYPercent());
            }
            public void last()
            {
                toast("last");
            }
        });
    }

    private List<QueueDots.Dot> dots()
    {
        return Arrays.asList(new QueueDots.Dot(1, 25, 33, 1),
                new QueueDots.Dot(2, 50, 33, 2),
                new QueueDots.Dot(3, 75, 33, 3),
                new QueueDots.Dot(4, 25, 66, 4),
                new QueueDots.Dot(5, 50, 66, 5),
                new QueueDots.Dot(6, 75, 66, 6));
    }
}