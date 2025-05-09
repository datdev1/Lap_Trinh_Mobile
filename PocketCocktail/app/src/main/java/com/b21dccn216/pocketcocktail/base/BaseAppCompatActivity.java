package com.b21dccn216.pocketcocktail.base;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseAppCompatActivity<V extends  BaseContract.View, P extends BaseContract.Presenter> extends AppCompatActivity implements BaseContract.View{

    protected P presenter;

    protected abstract P createPresenter();
    protected abstract V getView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.attachView(getView());
        presenter.onCreate();
    }



    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this,"Error " + message, Toast.LENGTH_SHORT).show();

    }
}
