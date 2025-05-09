package com.b21dccn216.pocketcocktail.base;

public abstract class BasePresenter<V extends BaseContract.View>  implements BaseContract.Presenter<V>{

    protected V view;

    @Override
    public void attachView(BaseContract.View view) {
        this.view = (V) view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }
}
