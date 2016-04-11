##Tablayout_Example
---
tablayout和viewpager结合使用的示例以及如何取消viewpager的预加载(当页面可见时才加载数据),使用了Rxandroid来代替AsyncTask

###ScreenShot：

![gif](https://github.com/vienan/TabLayout_example/blob/master/screenshot.gif)

###Viewpager如何取消预加载

  >主要是重写Fragment的setUserVisibleHint(boolean isVisibleToUser)，根据是否对用户可见来进行操作[MainCode](https://github.com/vienan/TabLayout_example/blob/master/app/src/main/java/com/vienan/tablayout_example/BaseFragment.java)
  
  ```java
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
    
  ```
  >另外还需要对viewpager进行限制：`viewpager.setOffscreenPageLimit(1);`

###使用RxAndroid替代AsyncTask
  ```java
      private Observable showLoadingUI() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    showLoadingView();
                    Thread.sleep(2500);
                    subscriber.onNext( ""+mCurIndex);
                    subscriber.onCompleted();
                  // or do network
                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
            }
         }).subscribeOn(Schedulers.newThread());
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
  ```
  参考资料：[使用RxJava.Observable取代AsyncTask和AsyncTaskLoader](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0331/2672.html)
  
###Thanks to ：

    CatLoadingView (https://github.com/Rogero0o/CatLoadingView)  效果很漂亮
    Rxandroid
    RxLifeCycle
