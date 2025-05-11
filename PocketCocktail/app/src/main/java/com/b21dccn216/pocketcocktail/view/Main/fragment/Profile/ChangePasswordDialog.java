package com.b21dccn216.pocketcocktail.view.Main.fragment.Profile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.databinding.DialogChangePasswordBinding;

public class ChangePasswordDialog extends Dialog{
    DialogChangePasswordBinding binding;
    private ChangePasswordDialogCallback callBack;

    public ChangePasswordDialog(@NonNull Context context, ChangePasswordDialogCallback callBack) {
        super(context);
        this.callBack = callBack;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogChangePasswordBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(binding.getRoot());

        binding.save.setOnClickListener(v -> {
            String oldPass = binding.edtPassword.getText().toString(),
                    newPass = binding.edtNewPassword.getText().toString(),
                    confirmPass = binding.edtConfirmPassword.getText().toString();
            if(oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()){
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(newPass.length() < 6){
                Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!newPass.equals(confirmPass)){
                Toast.makeText(getContext(), "Confirm password not match", Toast.LENGTH_SHORT).show();
                return;
            }

            callBack.onSave(oldPass, newPass, confirmPass);
            dismiss();
        });

        binding.cancel.setOnClickListener(v -> {
            callBack.onCancel();
            dismiss();
        });

        binding.btnShowPassword.setOnClickListener(v -> {
            int inputType = binding.edtPassword.getInputType();

            if ((inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Hide password
                binding.btnShowPassword.setImageResource(R.drawable.eye_crossed);
                binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                // Show password
                binding.btnShowPassword.setImageResource(R.drawable.eye);
                binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            // Move cursor to the end
            binding.edtPassword.setSelection(binding.edtPassword.getText().length());
        });
        binding.btnShowConfirmPassword.setOnClickListener(v -> {
            int inputType = binding.edtConfirmPassword.getInputType();

            if ((inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Hide password
                binding.btnShowConfirmPassword.setImageResource(R.drawable.eye_crossed);
                binding.edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                // Show password
                binding.btnShowConfirmPassword.setImageResource(R.drawable.eye);
                binding.edtConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            // Move cursor to the end
            binding.edtConfirmPassword.setSelection(binding.edtConfirmPassword.getText().length());
        });
        binding.btnShowNewPassword.setOnClickListener(v -> {
            int inputType = binding.edtNewPassword.getInputType();

            if ((inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Hide password
                binding.btnShowNewPassword.setImageResource(R.drawable.eye_crossed);
                binding.edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                // Show password
                binding.btnShowNewPassword.setImageResource(R.drawable.eye);
                binding.edtNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            // Move cursor to the end
            binding.edtNewPassword.setSelection(binding.edtNewPassword.getText().length());
        });
        
        
    }

    public interface ChangePasswordDialogCallback{
         void onCancel();
         void onSave(String oldPassword, String newPassword, String confirmPassword);
    }
}
