package com.b21dccn216.pocketcocktail.view.Login;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.b21dccn216.pocketcocktail.base.BasePresenter;
import com.b21dccn216.pocketcocktail.dao.UserDAO;
import com.b21dccn216.pocketcocktail.helper.DialogHelper;
import com.b21dccn216.pocketcocktail.helper.SessionManager;
import com.b21dccn216.pocketcocktail.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPresenter extends BasePresenter<LoginContract.View>  implements LoginContract.Presenter{

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private UserDAO userDAO = new UserDAO();

    public LoginPresenter() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // initialize resource
    }

    @Override
    public void loginByEmailAndPassword(User user){
        view.showLoading(true);
        // validate user input
        if(!validateLoginInput(user)){
            view.showLoading(false);
            return;
        }
        mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
            .addOnCompleteListener(((Fragment)view).getActivity(),task -> {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    userDAO.getUserByUuidAuthen(firebaseUser.getUid(),
                            querySnapshot -> {
                                if(querySnapshot.getDocuments().isEmpty()){
                                    view.authFail("User not found");
                                    return;
                                }
                                User u = querySnapshot.getDocuments().get(0).toObject(User.class);
                                if(u == null){
                                    view.authFail("User not found");
                                    return;
                                }
                                SessionManager.getInstance().setUser(u);
                                view.authSuccess();
                            }, e -> {
                                view.authFail(e.getMessage());
                            });
                }else{
                    view.authFail("Login failed: " + task.getException().getMessage());
                }
        });
    }
        
        private void showAlertDialog(String title, String message){
            DialogHelper.showAlertDialog(((Fragment)view).getActivity(),
                    title,
                    message);
        }
        
        private boolean validateSignUpInput(User user, String confirmPassword){
            if(user.getName() == null || user.getName().isEmpty() || user.getName().length() < 6){
                showAlertDialog(
                        "Full name is invalid",
                        "Please ensure name field is not empty and more than 6 digit");
                return false;
            }
            if(confirmPassword == null || confirmPassword.isEmpty() || !confirmPassword.equals(user.getPassword())){
                showAlertDialog("Confirm password is invalid","Please ensure confirm password is correct");
                Log.e("datdev1","confirmPassword.isEmpty(): " + confirmPassword.isEmpty());
                Log.e("datdev1","!confirmPassword.equals(user.getPassword(): " + !confirmPassword.equals(user.getPassword()));
                Log.e("datdev1","confirmPassword: " + confirmPassword);
                Log.e("datdev1","user.getPassword(): " + user.getPassword());
                return false;
            }
            return validateLoginInput(user);
        }
    
        private boolean validateLoginInput(User user) {
            if(user == null) {
                showAlertDialog("User is invalid", "User is invalid");
                return false;
            }
            String email = user.getEmail(),
                    password = user.getPassword();
    
            if(email == null || email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showAlertDialog(
                        "Email is invalid",
                        "Ensure email is in correct format");
                return false;
            }
    
            if (password == null || password.trim().isEmpty() || password.length() < 6) {
                showAlertDialog(
                        "Password is invalid",
                        "Ensure password is more than 6 digit");
                return false;
            }
            return true;
        }
    
        @Override
        public void signUpWithEmailAndPassword(User user, String confirmPassword) {
            view.showLoading(true);
            // validate user input
            if(!validateSignUpInput(user, confirmPassword)) {
                view.showLoading(false);
                return;
            }
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            // signup success
                            // TODO:: Xử lý đối tượng user, lưu thông tin phone, fullname và firebase
                            user.setSaveUuidFromAuthen(mAuth.getUid());
                            user.setRole("User");
                            userDAO.addUser(user,
                                    aVoid -> {
                                        SessionManager.getInstance().setUser(user);
                                        view.authSuccess();
                                    },
                                    e -> view.authFail(e.getMessage())
                            );
                        }else{
                            view.authFail("Sign-up failed: " + task.getException().getMessage());
                        }
                    });
    }
}
