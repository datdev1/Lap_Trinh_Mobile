package com.b21dccn216.pocketcocktail.view.Main.fragment.Home;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.DialogPreviewDrinkBinding;
import com.bumptech.glide.Glide;

public class ShowImageDialog extends Dialog {
    private final String url;
    DialogPreviewDrinkBinding binding;


    public ShowImageDialog(@NonNull Context context, String url) {
        super(context);
        this.url = url;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogPreviewDrinkBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(binding.getRoot());

        Glide.with(getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.place_holder_drink)
                .into(binding.imageViewDialog);
    }
}


