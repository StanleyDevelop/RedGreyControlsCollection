package stan.rgcc.demo.ui.fragments;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.Dots;

public class DotsFragment
    extends UtilFragment
{
    private Dots dots;
    private Switch select_enabled;
    private Switch try_add_enabled;
    private Switch show_numbers;

    private List<Dots.Dot> dotList;

    protected View.OnClickListener setClickListener()
    {
        return new View.OnClickListener()
        {
            public void onClick(View view)
            {
                switch(view.getId())
                {
                    case R.id.clear:
                        dotList.clear();
                        dots.clearDots();
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
        select_enabled = findView(R.id.select_enabled);
        try_add_enabled = findView(R.id.try_add_enabled);
        show_numbers = findView(R.id.show_numbers);
        setClickListener(findView(R.id.clear));
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
                toast("select dot " + dot.getXPercent() + " " + dot.getYPercent());
            }
        });
        select_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                dots.setSelectEnabled(isChecked);
            }
        });
        try_add_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                dots.setTryAddEnabled(isChecked);
            }
        });
        show_numbers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                dots.showNumbers(isChecked);
            }
        });
    }
}