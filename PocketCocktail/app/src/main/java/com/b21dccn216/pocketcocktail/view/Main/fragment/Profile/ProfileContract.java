package com.b21dccn216.pocketcocktail.view.Main.fragment.Profile;

import android.net.Uri;

import com.b21dccn216.pocketcocktail.base.BaseContract;
import com.b21dccn216.pocketcocktail.model.User;

public interface ProfileContract {

    interface View extends BaseContract.View {
        void updateInfoSuccess();

        void updateInfoFail(String message);
        // Define view methods

    }

    interface Presenter extends BaseContract.Presenter<ProfileContract.View> {
        // Define presenter methods
        void refreshScreen();

        void signOut();

        void saveUserInformation(User currentUser);

        void saveUserWithImage(User editingUser, Uri selectedImageUri);

        User getCurrentUser();

        void changePassword(String oldPassword, String newPassword, String confirmPassword);
    }
}
