package com.b21dccn216.pocketcocktail.helper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.DialogCustomBinding;

public class HelperDialog extends Dialog {
    private DialogCustomBinding binding;
    private DialogType type;
    private String title;
    private String message;


    public HelperDialog(@NonNull Context context, DialogType type, String title, String message) {
        super(context);
        this.type = type;
        this.title = title;
        this.message = message;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogCustomBinding.inflate(LayoutInflater.from(getContext()));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(binding.getRoot());

        binding.tvTitle.setText(title);
        binding.tvMessage.setText(message);

        switch (type){
            case SUCCESS:
                binding.imgStatus.setImageResource(R.drawable.check);
                binding.imgStatus.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(getContext(), R.color.primary)
                ));
                break;
            case ERROR:
                binding.imgStatus.setImageResource(R.drawable.baseline_warning_24);
                binding.imgStatus.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(getContext(), R.color.error)
                ));
                break;
            default:
                binding.imgStatus.setImageResource(R.drawable.baseline_favorite_border_24);
                binding.imgStatus.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(getContext(), R.color.warning)
                ));
                break;
        }

    }

    public enum DialogType{
        LOADING,
        ERROR,
        SUCCESS,
        WARNING,
        INFO
    }

}
