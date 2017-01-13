package stan.rgcc.demo.ui.fragments;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.TabSteps;

public class TabStepsFragment
    extends UtilFragment
{
    private TextView tab_steps_text;
    private TabSteps tab_steps;
    private TabSteps tab_steps_blue;

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
                        tab_steps.setStep(1);
                        break;
                }
            }
        };
    }

    @Override
    protected int setContentView()
    {
        return R.layout.tab_steps_fragment;
    }

    @Override
    protected void initViews(View v)
    {
        tab_steps_text = findView(R.id.tab_steps_text);
        tab_steps = findView(R.id.tab_steps);
        tab_steps_blue = findView(R.id.tab_steps_blue);
        setClickListener(findView(R.id.step1));
    }

    @Override
    protected void init()
    {
        tab_steps.setSteps("Begin", "Main", "Other", "End");
        tab_steps.setLastAccessStep(2);
        tab_steps.setStep(1);
        tab_steps.setListener(new TabSteps.ChangeStepListener()
        {
            @Override
            public void changeStep(int newStep)
            {
                Log.e(getClass().getName(), "TabSteps step " + newStep);
                tab_steps_text.setText("step " + newStep);
            }
        });
        tab_steps_blue.setSteps("a", "cd", "ghijkl", "mn", "pq", "r", "pq", "r");
        tab_steps_blue.setLastAccessStep(4);
        tab_steps_blue.setStep(0);
    }
}