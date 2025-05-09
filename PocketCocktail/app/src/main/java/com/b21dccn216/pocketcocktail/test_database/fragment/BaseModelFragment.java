package com.b21dccn216.pocketcocktail.test_database.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseModelFragment extends Fragment {
    protected View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        initViews();
        return rootView;
    }

    protected abstract int getLayoutId();
    protected abstract void initViews();
    protected abstract void loadData();
    protected abstract void clearInputs();
    protected abstract void fillInputs(Object item);
    protected abstract void saveItem();
    protected abstract void updateItem();
    protected abstract void deleteItem();

    protected void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
} 