package stan.rgcc.demo.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class UtilFragment
    extends Fragment
{
    private View mainView;
    private final View.OnClickListener clickListener = setClickListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(mainView == null)
        {
            mainView = inflater.inflate(setContentView(), container, false);
            initViews(mainView);
            init();
        }
        return mainView;
    }

    abstract protected View.OnClickListener setClickListener();
    abstract protected int setContentView();
    abstract protected void initViews(View v);
    abstract protected void init();

    protected <VIEW extends View> VIEW findView(int id)
    {
        return (VIEW)mainView.findViewById(id);
    }
    protected void setClickListener(View... views)
    {
        for(View v : views)
        {
            if(v != null)
            {
                v.setOnClickListener(clickListener);
            }
        }
    }
    final protected void log(String message)
    {
        Log.e(getClass().getName(), message);
    }
}