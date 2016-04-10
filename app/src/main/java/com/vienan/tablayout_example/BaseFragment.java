package com.vienan.tablayout_example;

import android.support.v4.app.Fragment;

import com.roger.catloadinglibrary.CatLoadingView;
import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * Created by vienan on 16/4/10.
 */
public abstract class BaseFragment extends RxFragment{

    protected CatLoadingView mLoadingView;
    protected boolean isVisible;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            isVisible=true;
            onVisible();
        }else {
            isVisible=false;
            onInvisible();
        }
    }


    /**
     * visible->lazyLoad
     */
    private void onVisible() {
        lazyLoad();
    }

    private void onInvisible() {

    }

    protected abstract void lazyLoad();

    protected void showLoadingView(){
        mLoadingView=new CatLoadingView();
        mLoadingView.show(getFragmentManager(), "");
    }
}
