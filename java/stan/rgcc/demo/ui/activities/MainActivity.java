package stan.rgcc.demo.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.BackForward;
import stan.rgcc.demo.ui.views.Steps;
import stan.rgcc.demo.ui.views.Switch;

public class MainActivity
        extends FragmentActivity
{
    private TextView switch_white_text;
    private Switch switcher_white;
    private Switch switcher_blue;
    private TextView bckfwd_text;
    private BackForward bckfwd;
    private TextView steps_white_text;
    private Steps steps_white;

    private final View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch(view.getId())
            {
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initViews();
        init();
    }
    private void initViews()
    {
        switch_white_text = findView(R.id.switch_white_text);
        switcher_white = findView(R.id.switcher_white);
        switcher_blue = findView(R.id.switcher_blue);
        bckfwd_text = findView(R.id.bckfwd_text);
        bckfwd = findView(R.id.bckfwd);
        steps_white_text = findView(R.id.steps_white_text);
        steps_white = findView(R.id.steps_white);
        setClickListener(switcher_white, switcher_blue);
    }
    private void init()
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
        bckfwd.setListener(new BackForward.ChangeSideListener()
        {
            @Override
            public void changeSide(int newSide)
            {
                Log.e(getClass().getName(), "BackForward side " + newSide);
                bckfwd_text.setText("side " + newSide);
            }
        });
        steps_white.setListener(new Steps.ChangeStepListener()
        {
            @Override
            public void changeStep(int newStep)
            {
                Log.e(getClass().getName(), "Steps newStep " + newStep);
                steps_white_text.setText("newStep " + newStep);
            }
        });
    }

    private <VIEW extends View> VIEW findView(int id)
    {
        return (VIEW)findViewById(id);
    }
    private void setClickListener(View... views)
    {
        for(View v : views)
        {
            if(v != null)
            {
                v.setOnClickListener(clickListener);
            }
        }
    }
}