package stan.rgcc.demo.ui.fragments;

import android.view.View;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.StateBadgeButton;

public class StateBadgeFragment
    extends UtilFragment
{
    private StateBadgeButton statebadge;
    private StateBadgeButton car;

    private int count;

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
                    case R.id.badgeup:
                        badgeup();
                        break;
                    case R.id.badgeclean:
                        count=-11;
                        statebadge.setBadgeCount(count);
                        break;
                    case R.id.buttonoff:
                        statebadge.setState(StateBadgeButton.States.OFF);
                        car.setState(StateBadgeButton.States.OFF);
                        break;
                    case R.id.statebadge:
                        statebadge.setState(StateBadgeButton.States.ON);
                        break;
                    case R.id.car:
                        car.setState(StateBadgeButton.States.ON);
                        break;
                }
            }
        };
    }
    private void badgeup()
    {
        count++;
        if(count > 99)
        {
            count = 99;
            return;
        }
        statebadge.setBadgeCount(count);
    }

    @Override
    protected int setContentView()
    {
        return R.layout.statebadgefragment;
    }

    @Override
    protected void initViews(View v)
    {
        statebadge = findView(R.id.statebadge);
        car = findView(R.id.car);
        setClickListener(findView(R.id.badgeup)
                ,findView(R.id.badgeclean)
                ,findView(R.id.buttonoff)
                ,statebadge
                ,car
        );
    }

    @Override
    protected void init()
    {
        count = 0;
        statebadge.setDrawBadge(true);
    }
}