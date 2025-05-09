package com.b21dccn216.pocketcocktail.view.Login.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.view.Login.LoginContract;
import com.b21dccn216.pocketcocktail.view.Login.LoginPresenter;
import com.b21dccn216.pocketcocktail.view.Login.model.User;
import com.b21dccn216.pocketcocktail.view.Main.HomeActivity;
import com.b21dccn216.pocketcocktail.databinding.FragmentLoginBinding;


public class LoginFragment extends BaseFragment<LoginContract.View, LoginContract.Presenter>
        implements LoginContract.View
{
    private FragmentLoginBinding binding;

    public LoginFragment() {
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
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        setUpButton();


        return binding.getRoot();
    }

    private void setUpButton(){
        binding.btnLogin.setOnClickListener( v -> {
            User user = new User();
            user.setEmail(binding.edtEmail.getText().toString());
            user.setPassword(binding.edtPassword.getText().toString());
            if(!validateLoginInput(user)){
                return;
            }
            presenter.loginByEmailAndPassword(user);
        });

        binding.showPassword.setOnClickListener(v -> {
            int inputType = binding.edtPassword.getInputType();

            if ((inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                // Hide password
                binding.showPassword.setImageResource(R.drawable.eye_crossed);
                binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                // Show password
                binding.showPassword.setImageResource(R.drawable.eye);
                binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            // Move cursor to the end
            binding.edtPassword.setSelection(binding.edtPassword.getText().length());
        });

    }


    private boolean validateLoginInput(User user) {
        if(user == null) {
            DialogHelper.showAlertDialog(getActivity(),
                    "User is invalid", "User is invalid");
            return false;
        }
        String email = user.getEmail(),
                password = user.getPassword();

        if(email == null || email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            DialogHelper.showAlertDialog(getActivity(),
                    "Email is invalid",
                    "Ensure email is in correct format");
            return false;
        }

        if (password == null || password.trim().isEmpty() || password.length() < 6) {
            DialogHelper.showAlertDialog(getActivity(),
                    "Password is invalid",
                    "Ensure password is more than 6 digit");
            return false;
        }
        return true;
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