package com.erdioran.dayybook.StartLogin;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;

import com.erdioran.dayybook.MainActivity;
import com.erdioran.dayybook.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionInflater;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LogInFragment extends AuthFragment implements View.OnClickListener {
    @BindViews(value = {R.id.second, R.id.last})
    protected List<ImageView> sharedElements;
    @BindViews(value = {R.id.first})
    protected List<LoginButton> firsElement;
    @BindViews(value = {R.id.email_input_edit, R.id.password_input_edit})
    protected List<TextInputEditText> views;


    private String userName, userPassword;
    private ImageView imgProfile;
    private TextInputEditText email_input_edit, password_input_edit;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private VerticalTextView buttonLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAG = "MainActivity";


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            caption.setText(getString(R.string.log_in_label));
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_log_in));
            for (TextInputEditText editText : views) {
                if (editText.getId() == R.id.password_input_edit) {
                    final TextInputLayout inputLayout = ButterKnife.findById(view, R.id.password_input);
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    inputLayout.setTypeface(boldTypeface);
                    editText.addTextChangedListener(new TextWatcherAdapter() {
                        @Override
                        public void afterTextChanged(Editable editable) {
                            inputLayout.setPasswordVisibilityToggleEnabled(editable.length() > 0);
                        }
                    });
                }
                editText.setOnFocusChangeListener((temp, hasFocus) -> {
                    if (!hasFocus) {
                        boolean isEnabled = editText.getText().length() > 0;
                        editText.setSelected(isEnabled);
                    }
                });
            }
        }


        email_input_edit = (TextInputEditText) getView().findViewById(R.id.email_input_edit);
        password_input_edit = (TextInputEditText) getView().findViewById(R.id.password_input_edit);
        buttonLogin = (VerticalTextView) getView().findViewById(R.id.caption);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser(); // authenticated user

        if (firebaseUser != null) { // check user session

            Intent i = new Intent(getActivity(), MainActivity.class);
            startActivity(i);
            getActivity().finish();
        }


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = email_input_edit.getText().toString();
                userPassword = password_input_edit.getText().toString();
                if (userName.isEmpty() || userPassword.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Lütfen gerekli alanları doldurunuz!", Toast.LENGTH_SHORT).show();

                } else {

                    loginFunc();
                }
            }
        });


        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    "com.erdioran.dayybook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);


        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) getView().findViewById(R.id.first);

        loginButton.setReadPermissions("email", "public_profile");//user_status, publish_actions..
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), "Sign In canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(), "Something bad happened", Toast.LENGTH_SHORT).show();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (AccessToken.getCurrentAccessToken() != null)
                    Toast.makeText(getActivity(), AccessToken.getCurrentAccessToken().getExpires().toString(), Toast.LENGTH_SHORT).show();
                if (user != null) {
                    String email = user.getEmail();
                    String userName = user.getDisplayName();
                    email_input_edit.setText(email);
                    password_input_edit.setText(userName);
                    Picasso.with(getActivity()).load(user.getPhotoUrl()).into(imgProfile);
                    loginButton.setVisibility(View.GONE);

                } else {
                    Log.d("TG", "SIGNED OUT");
                    //             txtEmail.setText("");
                    //          txtUser.setText("");
                    //        imgProfile.setImageBitmap(null);
                    loginButton.setVisibility(View.VISIBLE);

                }
            }
        };


    }


    private void loginFunc() {

        mAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                            getActivity().finish();

                        } else {
                            // hata
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void handleFacebookAccessToken(AccessToken accessToken) {

        Log.d(TAG, "ZZZZZZZ: " + accessToken.getToken());
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        Intent intent1 = new Intent(getActivity(), MainActivity.class);
        startActivity(intent1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

    @Override
    public void onClick(View v) {
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
    }


    @Override
    public int authLayout() {
        return R.layout.fragment_login;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void fold() {
        lock = false;
        Rotate transition = new Rotate();
        transition.setEndAngle(-90f);
        transition.addTarget(caption);
        TransitionSet set = new TransitionSet();
        set.setDuration(getResources().getInteger(R.integer.duration));
        ChangeBounds changeBounds = new ChangeBounds();
        set.addTransition(changeBounds);
        set.addTransition(transition);
        TextSizeTransition sizeTransition = new TextSizeTransition();
        sizeTransition.addTarget(caption);
        set.addTransition(sizeTransition);
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        final float padding = getResources().getDimension(R.dimen.folded_label_padding) / 2;
        set.addListener(new Transition.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                caption.setTranslationX(-padding);
                caption.setRotation(0);
                caption.setVerticalText(true);
                caption.requestLayout();

            }
        });
        TransitionManager.beginDelayedTransition(parent, set);
        caption.setTextSize(TypedValue.COMPLEX_UNIT_PX, caption.getTextSize() / 2);
        caption.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams params = getParams();
        params.leftToLeft = ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias = 0.5f;
        caption.setLayoutParams(params);
        caption.setTranslationX(caption.getWidth() / 8 - padding);
    }

    @Override
    public void clearFocus() {
        for (View view : views) view.clearFocus();
    }

}
