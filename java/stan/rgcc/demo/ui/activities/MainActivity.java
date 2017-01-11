package stan.rgcc.demo.ui.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.fragments.BackForwardsFragment;
import stan.rgcc.demo.ui.fragments.StepsFragment;
import stan.rgcc.demo.ui.fragments.SwitchsFragment;
import stan.rgcc.demo.ui.fragments.TabStepsFragment;

public class MainActivity
        extends FragmentActivity
{
    private final Fragment switchsFragment = new SwitchsFragment();
    private final Fragment bckfwdFragment = new BackForwardsFragment();
    private final Fragment stepsFragment = new StepsFragment();
    private final Fragment tabStepsFragment = new TabStepsFragment();
    private Fragment currentFragment;

    private final View.OnClickListener clickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch(view.getId())
            {
                case R.id.switchs:
                    currentFragment = switchsFragment;
                    replaceFragment();
                    break;
                case R.id.bckfwds:
                    currentFragment = bckfwdFragment;
                    replaceFragment();
                    break;
                case R.id.steps:
                    currentFragment = stepsFragment;
                    replaceFragment();
                    break;
                case R.id.tabsteps:
                    currentFragment = tabStepsFragment;
                    replaceFragment();
                    break;
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
        setClickListener(findViewById(R.id.switchs), findViewById(R.id.bckfwds), findViewById(R.id.steps), findViewById(R.id.tabsteps));
    }

    private void init()
    {
        currentFragment = tabStepsFragment;
        replaceFragment();
    }

    private void replaceFragment()
    {
        getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame, currentFragment)
                            .commit();
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