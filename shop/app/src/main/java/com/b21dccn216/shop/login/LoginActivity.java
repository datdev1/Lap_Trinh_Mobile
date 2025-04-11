package com.b21dccn216.shop.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.b21dccn216.shop.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;


    //login with google account
    private GoogleSignInOptions googleSignInOption;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setViewModel(loginViewModel);
        binding.setLifecycleOwner(this);

        firebaseAuth = FirebaseAuth.getInstance();
        googleSignInOption = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption);

        binding.loginWithGoogle.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
//            startActivityForResult(intent, RC_SIGN_IN);
            startGoogleSignInForResult.launch(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch (ApiException e){
                Log.e("Google Sign-In", "FAILE", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Log.d("Google Sign-In", "Sign-in successful: " + user.getEmail());
                    } else {
                        Log.e("Google Sign-In", "Authentication failed", task.getException());
                    }
                });
    }

    /**
     * @since 16-11-2022
     * @author Phong-Kaster
     * LOGIN WITH GOOGLE ACCOUNT
     * START SIGN UP ACTIVITY FOR RESULT
     */
    private final ActivityResultLauncher<Intent> startGoogleSignInForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int statusResult = result.getResultCode();
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

                if( statusResult == RESULT_OK)
                {
                    /*Step 1 - get email & password to server to authentication | sign up*/
                    assert account != null;
                    String email = account.getEmail();
                    String password = account.getId();
                    Log.d("Google Sign-In", "Sign-in successful: " + email);
                    Log.d("Google Sign-In", "Sign-in successful: " + password);

                    /*Step 2 - login*/
//                    viewModel.loginWithGoogle(email, password);
                }
                else
                {
                    Log.e("Google Sign-In", "Authentication failed");
                }
            }
        );/*end startGoogleSignInForResult*/


}