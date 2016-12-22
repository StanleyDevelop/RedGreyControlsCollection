package stan.rgcc.demo.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.Switch;

public class MainActivity
        extends FragmentActivity
{
    private TextView switch_white_text;
    private Switch switcher_white;
    private TextView switch_blue_text;
    private Switch switcher_blue;

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
        switch_blue_text = findView(R.id.switch_blue_text);
        switcher_blue = findView(R.id.switcher_blue);
        setClickListener(switcher_white, switcher_blue);
    }
    private void init()
    {
        switcher_white.setListener(new Switch.ChangeSideListener()
        {
            @Override
            public void changeSide(Switch.Side newSide)
            {
                Log.e(getClass().getName(), "white side " + newSide);
                switch_white_text.setText("side " + newSide);
            }
        });
        switcher_blue.setListener(new Switch.ChangeSideListener()
        {
            @Override
            public void changeSide(Switch.Side newSide)
            {
                Log.e(getClass().getName(), "blue side " + newSide);
                switch_blue_text.setText("side " + newSide);
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