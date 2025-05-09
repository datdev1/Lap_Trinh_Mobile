package com.b21dccn216.pocketcocktail.view.Login.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentSignUpBinding;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.view.Login.LoginContract;
import com.b21dccn216.pocketcocktail.view.Login.LoginPresenter;
import com.b21dccn216.pocketcocktail.view.Login.model.User;
import com.b21dccn216.pocketcocktail.view.Main.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends BaseFragment<LoginContract.View, LoginContract.Presenter>
    implements LoginContract.View{
    private FragmentSignUpBinding binding;

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
            User user = new User(
                    binding.email.getText().toString(),
                    binding.password.getText().toString(),
                    binding.fullname.getText().toString(),
                    binding.phone.getText().toString()
            );
            if(!validateSignUpInput(user)){
                return;
            }
            presenter.signUpWithEmailAndPassword(user);
        });

        return binding.getRoot();
    }

    private boolean validateSignUpInput(User user){
        if(user.getName() == null || user.getName().isEmpty() || user.getName().length() < 6){
            DialogHelper.showAlertDialog(getActivity(),
                    "Full name is invalid",
                    "Please ensure name field is not empty and more than 6 digit");
            return false;
        }
        if(user.getPhone() == null || user.getPhone().isEmpty() || !Patterns.PHONE.matcher(user.getPhone().trim()).matches()){
            DialogHelper.showAlertDialog(getActivity(),
                    "Phone number is invalid",
                    "Please ensure phone number is not empty and in correct format");
            return false;
        }
        return validateLoginInput(user);
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