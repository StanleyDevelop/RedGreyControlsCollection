package stan.rgcc.demo.ui.fragments;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import stan.rgcc.demo.R;
import stan.rgcc.demo.ui.views.BackForward;

public class BackForwardsFragment
        extends UtilFragment
{
    private TextView bckfwd_text;
    private BackForward bckfwd;

    @Override
    protected View.OnClickListener setClickListener()
    {
        return null;
    }

    @Override
    protected int setContentView()
    {
        return R.layout.back_forwards_fragment;
    }

    @Override
    protected void initViews(View v)
    {
        bckfwd_text = findView(R.id.bckfwd_text);
        bckfwd = findView(R.id.bckfwd);
    }

    @Override
    protected void init()
    {
        bckfwd.setListener(new BackForward.ChangeSideListener()
        {
            @Override
            public void changeSide(int newSide)
            {
                Log.e(getClass().getName(), "BackForward side " + newSide);
                bckfwd_text.setText("side " + newSide);
            }
        });
    }
}