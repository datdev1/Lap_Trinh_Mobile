package com.b21dccn216.pocketcocktail.view.Main.fragment.Profile;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.b21dccn216.pocketcocktail.R;
import com.b21dccn216.pocketcocktail.base.BaseFragment;
import com.b21dccn216.pocketcocktail.databinding.FragmentProfileBinding;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.User;
import com.bumptech.glide.Glide;


public class ProfileFragment extends BaseFragment<ProfileContract.View, ProfileContract.Presenter>
    implements ProfileContract.View {
    private FragmentProfileBinding binding;
    private User editingUser;
    private boolean isEditting = false;
    private Uri selectedImageUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(binding.profileImage);
                }
            });

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
        editingUser = presenter.getCurrentUser();

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
                editingUser = presenter.getCurrentUser();
                setViewCurrentUser();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO:: SET DIALOG LOADING
                editingUser.setEmail(binding.edtEmail.getText().toString());
                editingUser.setName(binding.edtFullName.getText().toString());
                if(selectedImageUri != null){
                    presenter.saveUserWithImage(editingUser, selectedImageUri);
                }else{
                    presenter.saveUserInformation(editingUser);
                }

            }
        });

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
                isEditting = true;
                setVisibilityEditing(isEditting);
            }
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            ChangePasswordDialog dialog = new ChangePasswordDialog(requireActivity(),
                    new ChangePasswordDialog.ChangePasswordDialogCallback() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onSave(String oldPassword, String newPassword, String confirmPassword) {
                            presenter.changePassword(oldPassword, newPassword, confirmPassword);
                        }
                    });
            dialog.show();

        });

        return binding.getRoot();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    public void setVisibilityEditing(Boolean isEditting){
        binding.edtEmail.setEnabled(isEditting);
        binding.edtFullName.setEnabled(isEditting);
        binding.confirmField.setVisibility(isEditting ? View.VISIBLE : View.GONE);
    }

    public void setViewCurrentUser(){
        binding.userFullName.setText(editingUser.getName());
        binding.edtFullName.setText(editingUser.getName());
        binding.edtEmail.setText(editingUser.getEmail());
        Glide
                .with(requireActivity())
                .load(editingUser.getImage())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(binding.profileImage);

    }

    @Override
    public void updateInfoSuccess() {
        // TODO:: SET DIALOG FINISH LOADING
        editingUser = presenter.getCurrentUser();
        setViewCurrentUser();
        isEditting = false;
        setVisibilityEditing(isEditting);
        Toast.makeText(requireActivity(), "Update success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateInfoFail(String message) {
        // TODO:: SET DIALOG FINISH LOADING
        editingUser = presenter.getCurrentUser();
        setViewCurrentUser();
        isEditting = false;
        setVisibilityEditing(isEditting);
        Toast.makeText(requireActivity(), "Update Fail", Toast.LENGTH_SHORT).show();

    }
}