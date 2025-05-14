package com.b21dccn216.pocketcocktail.helper;

import static android.view.View.VISIBLE;

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

    private OnDialogButtonClickListener onDialogButtonClickListener;


    public HelperDialog(@NonNull Context context, DialogType type, String title, String message,
                        OnDialogButtonClickListener onDialogButtonClickListener) {
        super(context);
        this.type = type;
        this.title = title;
        this.message = message;
        this.onDialogButtonClickListener = onDialogButtonClickListener;
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

        if(onDialogButtonClickListener != null){
            binding.btnNo.setVisibility(VISIBLE);
            binding.btnYes.setVisibility(VISIBLE);
            binding.btnNo.setOnClickListener(v -> onDialogButtonClickListener.onPressNegative());
            binding.btnYes.setOnClickListener(v -> onDialogButtonClickListener.onPressPositive());
        }


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

    public interface OnDialogButtonClickListener{
        void onPressNegative();
        void onPressPositive();
    }
}
