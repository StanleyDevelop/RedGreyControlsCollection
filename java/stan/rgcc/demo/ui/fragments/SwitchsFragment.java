package stan.rgcc.demo.ui.fragments;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.Switch;

public class SwitchsFragment
    extends UtilFragment
{
    private TextView switch_white_text;
    private Switch switcher_white;
    private Switch switcher_blue;

    @Override
    protected View.OnClickListener setClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                switch(view.getId())
                {
                }
            }
        };
    }

    @Override
    protected int setContentView()
    {
        return R.layout.switchs_fragment;
    }

    @Override
    protected void initViews(View v)
    {
        switch_white_text = findView(R.id.switch_white_text);
        switcher_white = findView(R.id.switcher_white);
        switcher_blue = findView(R.id.switcher_blue);
    }

    @Override
    protected void init()
    {
        switcher_white.setListener(new Switch.ChangeSideListener()
        {
            @Override
            public void changeSide(int newSide)
            {
                Log.e(getClass().getName(), "white side " + newSide);
                switch_white_text.setText("side " + newSide);
            }
        });
        switcher_blue.setListener(new Switch.ChangeSideListener()
        {
            @Override
            public void changeSide(int newSide)
            {
                Log.e(getClass().getName(), "blue side " + newSide);
            }
        });
    }
}