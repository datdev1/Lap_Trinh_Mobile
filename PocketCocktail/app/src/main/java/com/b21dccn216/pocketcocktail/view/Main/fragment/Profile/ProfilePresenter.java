package com.b21dccn216.pocketcocktail.view.Main.fragment.Profile;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.HelperDialog;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ProfilePresenter extends BasePresenter<ProfileContract.View>
        implements ProfileContract.Presenter
{
    private FirebaseAuth mAuth;
    private UserDAO userDAO;

    public ProfilePresenter() {
        super();
        mAuth = FirebaseAuth.getInstance();
        userDAO = new UserDAO();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void refreshScreen() {

    }

    @Override
    public void signOut() {
        mAuth.signOut();
        SessionManager.getInstance().clearSession();
    }



    private boolean validateUserInformation(User user) {
        if(user == null) {
            showAlertDialog("User is invalid", "User is invalid",
                    HelperDialog.DialogType.ERROR);
            return false;
        }
        if(user.getName() == null || user.getName().isEmpty() || user.getName().length() < 6){
            showAlertDialog(
                    "Full name is invalid",
                    "Please ensure name field is not empty and more than 6 digit",
                    HelperDialog.DialogType.ERROR);
            return false;
        }
        String email = user.getEmail();

        if(email == null || email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlertDialog(
                    "Email is invalid",
                    "Ensure email is in correct format",
                    HelperDialog.DialogType.ERROR);
            return false;
        }
        return true;
    }

    private void showAlertDialog(String title, String message, HelperDialog.DialogType type){
        if(view == null) return;
        if(((Fragment)view).getActivity() == null) return;
        ((Fragment)view).getActivity().runOnUiThread(() -> {
            DialogHelper.showAlertDialog(((Fragment)view).requireContext(),
                    title,
                    message,
                    type);
        });
    }

    private void changeEmailFirebase(String email, String password){
        FirebaseUser user = mAuth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("datdev1", "User re-authenticated.");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.verifyBeforeUpdateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Show to View
                            showAlertDialog("WARN",
                                    "We has sent verification link to " + email +" address.\n" +
                                    "Pleas confirm to change email address",
                                    HelperDialog.DialogType.WARNING);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void saveUserWithImage(User editingUser, Uri selectedImageUri) {
        if(validateUserInformation(editingUser)) {
            if (!mAuth.getCurrentUser().getEmail().equals(editingUser.getEmail())) {
                changeEmailFirebase(editingUser.getEmail(), editingUser.getPassword());
            }

            try{
                userDAO.updateUserWithImage(((Fragment) view).requireActivity(), editingUser, selectedImageUri,
                        avoid -> {
                            userDAO.getUserByUuidAuthen(mAuth.getCurrentUser().getUid(),
                                    new UserDAO.UserCallback() {
                                        @Override
                                        public void onUserLoaded(User user) {
                                            SessionManager.getInstance().setUser(user);
                                            view.updateInfoSuccess();
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            view.updateInfoFail(e.getMessage());
                                        }
                                    }
                            );
                        },
                        e -> {
                            showAlertDialog("Fail to save",
                                    e.getMessage(), HelperDialog.DialogType.ERROR);
                        });
            }catch (Exception e){
                view.updateInfoFail(e.getMessage());
            }

        }
    }

    @Override
    public void saveUserInformation(User currentUser) {
        if(validateUserInformation(currentUser)){

            if(!mAuth.getCurrentUser().getEmail().equals(currentUser.getEmail())){
                changeEmailFirebase(currentUser.getEmail(), currentUser.getPassword());
            }

            userDAO.updateUser(currentUser,
                    avoid -> {
                        SessionManager.getInstance().setUser(currentUser);
                        view.updateInfoSuccess();
                    },
                    e -> {
                        showAlertDialog("Fail to save",
                                e.getMessage(), HelperDialog.DialogType.ERROR);
                    });
        }
    }

    @Override
    public User getCurrentUser() {
        User u = SessionManager.getInstance().getUser();
        u.setEmail(mAuth.getCurrentUser().getEmail());
        return u;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String confirmPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null) return;
        String email = user.getEmail(); // already logged-in user's email

        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Proceed to update password
                user.updatePassword(newPassword)
                        .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                User dbUser = SessionManager.getInstance().getUser();
                                dbUser.setPassword(newPassword);
                                saveUserInformation(dbUser);
                                showAlertDialog("Success", "Password has been updated successfully.",
                                        HelperDialog.DialogType.SUCCESS);
                            } else {
                                showAlertDialog("Fail", "Check your network",
                                        HelperDialog.DialogType.ERROR);
                            }
                        });
            } else {
                showAlertDialog("Fail", "Check your old password and try again.",
                        HelperDialog.DialogType.ERROR);
            }
        });



    }
}
