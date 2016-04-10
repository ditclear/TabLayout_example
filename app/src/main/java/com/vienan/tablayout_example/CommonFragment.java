package com.vienan.tablayout_example;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by vienan on 16/4/10.
 */
public class CommonFragment extends BaseFragment {

    private static final String FRAGMENT_INDEX = "fragment_index";
    private int mCurIndex = -1;

    private TextView mFragmentView;
    /** 标志位，标志已经初始化完成 */
    private boolean isPrepared;
    /** 是否已被加载过一次，第二次就不再去请求数据了 */
    private boolean mHasLoadedOnce;

    public static CommonFragment newInstance(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_INDEX, index);
        CommonFragment fragment = new CommonFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView= (TextView) inflater.inflate(R.layout.fragment_common,container,false);
            if (getArguments() != null) mCurIndex = getArguments().getInt(FRAGMENT_INDEX,1);
            isPrepared = true;
            lazyLoad();

        }
        //共用一个视图，需要先移除以前的view
        ViewGroup parent = (ViewGroup) mFragmentView.getParent();
        if (parent != null) parent.removeView(mFragmentView);
        return mFragmentView;

    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared||!isVisible || mHasLoadedOnce) {
            //已加载的fragment不需要重新加载
            return;
        }
        showLoadingUI().compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String >() {
                    @Override
                    public void onCompleted() {
                        mLoadingView.dismiss();
                        mHasLoadedOnce = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mFragmentView.setText("Fragment # 加载失败...");
                    }

                    @Override
                    public void onNext(String s) {
                        mFragmentView.setText("Fragment #" + s);

                    }
                });
    }

    private Observable showLoadingUI() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    showLoadingView();
                    Thread.sleep(2500);
                    subscriber.onNext( ""+mCurIndex);
                    subscriber.onCompleted();

                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }
}
