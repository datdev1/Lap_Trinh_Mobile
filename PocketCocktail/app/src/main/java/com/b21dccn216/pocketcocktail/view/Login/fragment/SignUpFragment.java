package com.b21dccn216.pocketcocktail.view.Login.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentSignUpBinding;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.model.User;
import com.b21dccn216.pocketcocktail.view.Login.LoginContract;
import com.b21dccn216.pocketcocktail.view.Login.LoginPresenter;
import com.b21dccn216.pocketcocktail.view.Main.HomeActivity;


public class SignUpFragment extends BaseFragment<LoginContract.View, LoginContract.Presenter>
    implements LoginContract.View{
    private FragmentSignUpBinding binding;

    private User signUpUser;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    protected LoginContract.Presenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected LoginContract.View getViewImpl() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(getLayoutInflater(), container, false);

        binding.signup.setOnClickListener(v-> {
            signUpUser = new User(
                    binding.fullname.getText().toString(),
                    binding.email.getText().toString(),
                    binding.password.getText().toString(),
                    ""
            );
            presenter.signUpWithEmailAndPassword(signUpUser, binding.confirmPassword.getText().toString());
        });

        binding.showPassword.setOnClickListener(v -> {
            int inputType = binding.password.getInputType();
            if ((inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Hide password
                binding.showPassword.setImageResource(R.drawable.eye_crossed);
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                // Show password
                binding.showPassword.setImageResource(R.drawable.eye);
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            // Move cursor to the end
            binding.password.setSelection(binding.password.getText().length());
        });

        binding.showConfirmPassword.setOnClickListener(v -> {
            int inputType = binding.confirmPassword.getInputType();

            if ((inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Hide password
                binding.showConfirmPassword.setImageResource(R.drawable.eye_crossed);
                binding.confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                // Show password
                binding.showConfirmPassword.setImageResource(R.drawable.eye);
                binding.confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            // Move cursor to the end
            binding.confirmPassword.setSelection(binding.confirmPassword.getText().length());
        });

        return binding.getRoot();
    }




    @Override
    public void onResume() {
        super.onResume();
        binding.getRoot().requestLayout();
    }

    @Override
    public void authSuccess() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLoading(boolean isLoading) {

    }

    @Override
    public void authFail(String mess) {
        DialogHelper.showAlertDialog(getActivity(),
                null,
                mess);
    }

}