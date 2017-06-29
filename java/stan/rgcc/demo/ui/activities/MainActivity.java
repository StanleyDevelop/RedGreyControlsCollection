package stan.rgcc.demo.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.fragments.BackForwardsFragment;
import stan.rgcc.demo.ui.fragments.DotsFragment;
import stan.rgcc.demo.ui.fragments.ListStepsFragment;
import stan.rgcc.demo.ui.fragments.QueueDotsFragment;
import stan.rgcc.demo.ui.fragments.StateBadgeFragment;
import stan.rgcc.demo.ui.fragments.StepsFragment;
import stan.rgcc.demo.ui.fragments.SwitchsFragment;
import stan.rgcc.demo.ui.fragments.TabStepsFragment;

public class MainActivity
        extends Activity
{
    private final Fragment switchsFragment = new SwitchsFragment();
    private final Fragment bckfwdFragment = new BackForwardsFragment();
    private final Fragment stepsFragment = new StepsFragment();
    private final Fragment tabStepsFragment = new TabStepsFragment();
    private final Fragment statebadgeFragment = new StateBadgeFragment();
    private final Fragment listStepsFragment = new ListStepsFragment();
    private final Fragment dotsFragment = new DotsFragment();
    private final Fragment queueFragment = new QueueDotsFragment();
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
                case R.id.statebadge:
                    currentFragment = statebadgeFragment;
                    replaceFragment();
                    break;
                case R.id.liststeps:
                    currentFragment = listStepsFragment;
                    replaceFragment();
                    break;
                case R.id.dots:
                    currentFragment = dotsFragment;
                    replaceFragment();
                    break;
                case R.id.queue:
                    currentFragment = queueFragment;
                    replaceFragment();
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initViews();
        init();
    }
    private void initViews()
    {
        setClickListener(findViewById(R.id.switchs)
                ,findViewById(R.id.bckfwds)
                ,findViewById(R.id.steps)
                ,findViewById(R.id.tabsteps)
                ,findViewById(R.id.statebadge)
                ,findViewById(R.id.liststeps)
                ,findViewById(R.id.dots)
                ,findViewById(R.id.queue)
        );
    }
    private void init()
    {
        currentFragment = queueFragment;
        replaceFragment();
    }

    private void replaceFragment()
    {
        getFragmentManager().beginTransaction()
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