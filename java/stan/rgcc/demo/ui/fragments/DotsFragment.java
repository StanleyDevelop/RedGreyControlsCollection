package stan.rgcc.demo.ui.fragments;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.Dots;

public class DotsFragment
    extends UtilFragment
{
    private Dots dots;
    private List<Dots.Dot> dotList;

    protected View.OnClickListener setClickListener()
    {
        return new View.OnClickListener()
        {
            public void onClick(View view)
            {
                switch(view.getId())
                {
                    case R.id.step1:
                        break;
                }
            }
        };
    }
    protected int setContentView()
    {
        return R.layout.dots_fragment;
    }
    protected void initViews(View v)
    {
        dots = findView(R.id.dots);
    }
    protected void init()
    {
        dotList = new ArrayList<>();
        dotList.add(new Dots.Dot(1, 75, 25));
        dotList.add(new Dots.Dot(2, 30, 60));
        dots.setDots(dotList);
        dots.setListener(new Dots.Listener()
        {
            public void newDot(int xPercent, int yPercent)
            {
                log("new dot " + xPercent + " " + yPercent);
                dotList.add(new Dots.Dot(new Random().nextInt(), xPercent, yPercent));
                dots.setDots(dotList);
            }
            public void selectDot(Dots.Dot dot)
            {
                log("select dot " + dot.getId() + " " + dot.getXPercent() + " " + dot.getYPercent());
            }
        });
    }
}