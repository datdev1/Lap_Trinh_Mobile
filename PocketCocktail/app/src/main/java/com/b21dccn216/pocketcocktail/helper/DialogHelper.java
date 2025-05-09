package com.b21dccn216.pocketcocktail.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.b21dccn216.pocketcocktail.databinding.DialogCustomBinding;

public class DialogHelper {

    // Show a basic dialog with message
    public static void showAlertDialog(Context context, String title, String message) {
        if (context == null) return;
        DialogCustomBinding binding = DialogCustomBinding.inflate(LayoutInflater.from(context));

        binding.tvTitle.setText(title != null ? title : "Message");
        binding.tvMessage.setText(message);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(binding.getRoot())
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }



}
