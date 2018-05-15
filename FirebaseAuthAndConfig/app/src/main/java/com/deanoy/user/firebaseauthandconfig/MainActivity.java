package com.deanoy.user.firebaseauthandconfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Arrays;

import Models.UserDetails;

public class MainActivity extends Activity {
    private static int RC_SIGN_IN = 1;
    private static String TAG = "MainActivity";
    private static final String EMAIL = "email";
    private static final String EMAIL_DATA = "email_data";
    private static final String PASSWORD_DATA = "password_data";
    private static final String ENABLE_ANONYMOUS_SIGNIN = "enable_anonymous_signin";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    // UI
    private EditText metEmail;
    private EditText metPassword;
    private Button mbtnSkip;
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton mGoogleSignInButton;
    private CallbackManager mFacebookCallbackManager;
    private LoginButton mFacebookLoginButton;

    private boolean mIsAnonymousSignInEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirebaseServices();
        bindUI();

        Log.e(TAG, "onCreate <<");
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart >>");

        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        displayUserDetails(currentUser);
        setUI();

        Log.e(TAG, "onStart <<");
    }

    // On Clicks
    public void onSignUpClick(View v) {
        Log.e(TAG, "onSignUpClick >>");

        startDisplaySignUpScreen();

        Log.e(TAG, "onSignUpClick <<");
    }

    public void onSignInClick(View v) {
        Log.e(TAG, "onSignInClick >>");

        if(isValidInputCredentials())
        {
            mAuth.signInWithEmailAndPassword(metEmail.getText().toString(), metPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                displayUserDetails(user);
                            }
                            else
                            {
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        Log.e(TAG, "onSignInClick <<");
    }

    public void onForgotPasswordClick(View v) {
        Intent forgotPasswordIntent = new Intent(this, PasswordResetActivity.class);
        forgotPasswordIntent.putExtra(EMAIL_DATA, metEmail.getText().toString());
        startActivity(forgotPasswordIntent);
    }

    public void onSkipClick(View v) {
        Log.e(TAG, "onSignInClick >>");
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            displayUserDetails(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Log.e(TAG, "onSignInClick <<");

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult >>");
        Log.e(TAG, "Result code: " + resultCode + ". Request code: " + requestCode);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        Log.e(TAG, "onActivityResult <<");
    }

    // UI
    private void bindUI() {
        mbtnSkip = findViewById(R.id.btnSkipMainActivity);
        //Email/Password auth
        metEmail = findViewById(R.id.etEmailMainActivity);
        metPassword = findViewById(R.id.etPasswordMainActivity);

        // Google/Facebook auth
        initGoogleAuth();
        initFacebookAuth();
    }

    private void setUI() {
        if(mIsAnonymousSignInEnabled) {
            mbtnSkip.setVisibility(View.VISIBLE);
        } else {
            mbtnSkip.setVisibility(View.INVISIBLE);
        }
    }

    private void initGoogleAuth() {
        //Google auth
        mGoogleSignInButton = findViewById(R.id.btnGoogleSignInMainActivity);
        mGoogleSignInButton .setSize(SignInButton.SIZE_STANDARD);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onGoogleSignInClick >>");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                Log.e(TAG, "onGoogleSignInClick <<");
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
    }

    private void initFacebookAuth() {
        mFacebookLoginButton = findViewById(R.id.btnFacebookLoginMainActivity);
        // Set facebook permissions here
        mFacebookLoginButton.setReadPermissions(Arrays.asList(EMAIL));
        // Callback registration
        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
            }
        });
    }

    private void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    // Helper functions
    private void initFirebaseServices() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFacebookCallbackManager = CallbackManager.Factory.create();
        initConfig();
    }

    private void initConfig() {
        Log.e(TAG, "initConfig >>");
        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.default_remote_config);
        fetchConfig();
        Log.e(TAG, "initConfig <<");
    }

    private void fetchConfig() {
         mIsAnonymousSignInEnabled = mFirebaseRemoteConfig.getBoolean(ENABLE_ANONYMOUS_SIGNIN);
        Log.e(TAG, "fetchConfig >> is anonymous signin enabled: " + mIsAnonymousSignInEnabled);

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            Log.e(TAG, "fetchConfig >> remote config in debug mode");
            cacheExpiration = 0;
        }

        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e(TAG, "FirebaseRemoteConfig:fetch:Complete >>");

                        if (task.isSuccessful()) {

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetching remote config failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        assignFetchedConfig();
                        setUI();
                        Log.e(TAG, "FirebaseRemoteConfig:fetch:Complete <<");
                    }
                });
    }

    private void assignFetchedConfig() {
        mIsAnonymousSignInEnabled = mFirebaseRemoteConfig.getBoolean(ENABLE_ANONYMOUS_SIGNIN);
        Log.e(TAG, "assignFetchedConfig >> is anonymous sign in enabled: " + mIsAnonymousSignInEnabled);
    }

    private void displayUserDetails(FirebaseUser currentUser) {
        if(currentUser != null)
        {
            Log.e(TAG, "User is online. Displaying home screen");
            startDisplayHomeScreen();
        }
    }

    private void startDisplayHomeScreen() {
        Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);

        startActivity(i);
        if(mAuth.getCurrentUser() != null) {
            finish();
        }

    }

    private void startDisplaySignUpScreen()
    {
        Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);

        // Transfer email/password data to sign up activity
        signUpIntent.putExtra(EMAIL_DATA, metEmail.getText().toString());
        signUpIntent.putExtra(PASSWORD_DATA, metPassword.getText().toString());

        startActivity(signUpIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
            // Signed in successfully, show authenticated UI.
            Log.e(TAG, "signInResult:successAccount");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.updateEmail(account.getEmail().toString());
                            displayUserDetails(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            Toast.makeText(MainActivity.this, "Facebook authentication success",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e(TAG,"email" +  user.getEmail());
                            displayUserDetails(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            LoginManager.getInstance().logOut(); // Can't log into firebase, log out of facebook
                            Toast.makeText(MainActivity.this, "Facebook authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidInputCredentials() {
        boolean result = true;

        if(metEmail.getText().toString().isEmpty())
        {
            displayMessage("You should enter your email in the email field");
            result = false;
        }

        if(result && metPassword.getText().toString().isEmpty())
        {
            displayMessage("You should enter your password in the password field");
            result = false;
        }

        return result;
    }
}