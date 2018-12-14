package com.erdioran.dayybook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.erdioran.dayybook.Helper.NetworkCheck;
import com.erdioran.dayybook.StartLogin.ActivityLogin;
import com.erdioran.dayybook.StartLogin.LoginActivity;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    NetworkInfo info;
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        getSupportActionBar().hide();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
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

        check();
    }



    @SuppressLint("WrongConstant")
    public void check() {
        Timer t = new Timer();
        boolean checkConnection = new NetworkCheck().checkConnection(this);
        if (checkConnection) {
            t.schedule(new splash(), 3000);
        } else {
            Toast.makeText(SplashScreenActivity.this,
                    "Connection Not Found", SPLASH_TIME_OUT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    check();
                }
            }, 2000);
        }
    }

    class splash extends TimerTask {
        @Override
        public void run() {
            Intent i = new Intent(SplashScreenActivity.this, ActivityLogin.class);
            finish();
            startActivity(i);
        }
    }
}

