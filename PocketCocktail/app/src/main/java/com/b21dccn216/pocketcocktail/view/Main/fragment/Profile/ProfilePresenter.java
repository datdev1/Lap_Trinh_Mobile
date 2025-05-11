package com.b21dccn216.pocketcocktail.view.Main.fragment.Profile;

import androidx.fragment.app.Fragment;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.User;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    public void saveUserInformation(User currentUser) {
        if(validateUserInformation(currentUser)){
            userDAO.updateUser(currentUser,
                    avoid -> {
                        SessionManager.getInstance().setUser(currentUser);
                        view.updateInfoSuccess();
                    },
                    e -> {

                        view.updateInfoFail(e.getMessage());
                    });
        }
    }

    private boolean validateUserInformation(User user) {
        if(user == null) {
            showAlertDialog("User is invalid", "User is invalid");
            return false;
        }
        if(user.getName() == null || user.getName().isEmpty() || user.getName().length() < 6){
            showAlertDialog(
                    "Full name is invalid",
                    "Please ensure name field is not empty and more than 6 digit");
            return false;
        }
        String email = user.getEmail();

        if(email == null || email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlertDialog(
                    "Email is invalid",
                    "Ensure email is in correct format");
            return false;
        }
        return true;
    }

    private void showAlertDialog(String title, String message){
        DialogHelper.showAlertDialog(((Fragment)view).getActivity(),
                title,
                message);
    }
}
