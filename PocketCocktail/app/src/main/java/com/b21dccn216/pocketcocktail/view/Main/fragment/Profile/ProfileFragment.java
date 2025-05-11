package com.b21dccn216.pocketcocktail.view.Main.fragment.Profile;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentProfileBinding;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.User;
import com.bumptech.glide.Glide;


public class ProfileFragment extends BaseFragment<ProfileContract.View, ProfileContract.Presenter>
    implements ProfileContract.View {
    private FragmentProfileBinding binding;
    private User currentUser;
    private boolean isEditting = false;

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    protected ProfileContract.Presenter createPresenter() {
        return new ProfilePresenter();
    }

    @Override
    protected ProfileContract.View getViewImpl() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
        currentUser = SessionManager.getInstance().getUser();

        setViewCurrentUser();

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.signOut();
                requireActivity().finish();
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditting = true;
                setVisibilityEditing(isEditting);
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditting = false;
                setVisibilityEditing(isEditting);
                currentUser = SessionManager.getInstance().getUser();
                setViewCurrentUser();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.saveUserInformation(currentUser);
            }
        });

        return binding.getRoot();
    }


    public void setVisibilityEditing(Boolean isEditting){
        binding.edtEmail.setEnabled(isEditting);
        binding.edtFullName.setEnabled(isEditting);
        binding.confirmField.setVisibility(isEditting ? View.VISIBLE : View.GONE);
    }

    public void setViewCurrentUser(){
        binding.userFullName.setText(currentUser.getName());
        binding.edtFullName.setText(currentUser.getName());
        binding.edtEmail.setText(currentUser.getEmail());
        Glide
                .with(requireActivity())
                .load(currentUser.getImage())
                .placeholder(R.drawable.user)
                .error(R.drawable.baseline_error_outline_24)
                .into(binding.profileImage);

    }

    @Override
    public void updateInfoSuccess() {
        setViewCurrentUser();
        isEditting = false;
        setVisibilityEditing(isEditting);
    }

    @Override
    public void updateInfoFail(String message) {
        currentUser = SessionManager.getInstance().getUser();
        setViewCurrentUser();
        isEditting = false;
        setVisibilityEditing(isEditting);

    }
}