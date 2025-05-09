package com.b21dccn216.pocketcocktail.base;

public interface BaseContract {

    interface View {
        void showError(String message);
    }

    interface Presenter<V extends BaseContract.View> {
        void attachView(V view);
        void detachView();
        void onCreate();
        void onStart();
        void onResume();
        void onPause();
        void onStop();
        void onDestroy();
    }
}


