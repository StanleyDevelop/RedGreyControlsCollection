package stan.rgcc.demo.ui.fragments;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.Steps;
import stan.rgcc.demo.ui.views.TextSteps;

public class StepsFragment
        extends UtilFragment
{
    private TextView steps_white_text;
    private Steps steps_white;
    private TextSteps text_steps;

    @Override
    protected View.OnClickListener setClickListener()
    {
        return null;
    }

    @Override
    protected int setContentView()
    {
        return R.layout.steps_fragment;
    }

    @Override
    protected void initViews(View v)
    {
        steps_white_text = findView(R.id.steps_white_text);
        steps_white = findView(R.id.steps_white);
        text_steps = findView(R.id.text_steps);
    }

    @Override
    protected void init()
    {
        steps_white.setListener(new Steps.ChangeStepListener()
        {
            @Override
            public void changeStep(int newStep)
            {
                Log.e(getClass().getName(), "Steps newStep " + newStep);
                steps_white_text.setText("newStep " + newStep);
            }
        });
        text_steps.setSteps("first", "second", "third", "other");
    }
}