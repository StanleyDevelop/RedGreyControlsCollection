package stan.rgcc.demo.ui.fragments;

import android.view.View;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.ListSteps;

public class ListStepsFragment
    extends UtilFragment
{
    private ListSteps list_steps;

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
                    case R.id.step1:
                        break;
                }
            }
        };
    }

    @Override
    protected int setContentView()
    {
        return R.layout.list_steps_fragment;
    }

    @Override
    protected void initViews(View v)
    {
        list_steps = findView(R.id.list_steps);
        setClickListener(findView(R.id.step1));
    }

    @Override
    protected void init()
    {
        list_steps.setSteps(
                getResources().getDrawable(R.drawable.ic_android_white_48dp)
                ,getResources().getDrawable(R.drawable.man)
                ,getResources().getDrawable(R.drawable.ic_directions_car_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_extension_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_highlight_off_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_lightbulb_outline_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_extension_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_lightbulb_outline_white_48dp)
                ,getResources().getDrawable(R.drawable.man)
                ,getResources().getDrawable(R.drawable.ic_lightbulb_outline_white_48dp)
                ,getResources().getDrawable(R.drawable.man)
                ,getResources().getDrawable(R.drawable.ic_lightbulb_outline_white_48dp)
                ,getResources().getDrawable(R.drawable.man)
                ,getResources().getDrawable(R.drawable.ic_extension_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_extension_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_highlight_off_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_location_on_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_directions_car_white_48dp)
                ,getResources().getDrawable(R.drawable.ic_extension_white_48dp)
        );
        list_steps.setLastAccessStep(13);
        list_steps.setStep(10);
    }
}