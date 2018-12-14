package com.erdioran.dayybook.StartLogin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.erdioran.dayybook.Fragments.AddFragment;
import com.erdioran.dayybook.Fragments.FragmentLogin;
import com.erdioran.dayybook.Fragments.FragmentSignUp;
import com.erdioran.dayybook.Fragments.HomeFragment;
import com.erdioran.dayybook.Fragments.SettingsFragment;
import com.erdioran.dayybook.R;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

public class ActivityLogin extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @BindViews(value = {R.id.logo})
    protected List<ImageView> sharedElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        BottomNavigationView navigationLogin=findViewById(R.id.bottomNavigationLogin);
        navigationLogin.setOnNavigationItemSelectedListener(this);

        //BACKGROUND
        final ImageView background=ButterKnife.findById(this,R.id.scrolling_background);
        int[] screenSize=screenSize();
        Glide.with(this)
                .load(R.drawable.busy)
                .asBitmap()
                .override(screenSize[0]*2,screenSize[1])
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new ImageViewTarget<Bitmap>(background) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        background.setImageBitmap(resource);
                        background.post(()->{
                            //we need to scroll to the very left edge of the image
                            //fire the scale animation
                            background.scrollTo(-background.getWidth()/2,0);
                            ObjectAnimator xAnimator=ObjectAnimator.ofFloat(background, View.SCALE_X,4f,background.getScaleX());
                            ObjectAnimator yAnimator=ObjectAnimator.ofFloat(background,View.SCALE_Y,4f,background.getScaleY());
                            AnimatorSet set=new AnimatorSet();
                            set.playTogether(xAnimator,yAnimator);
                            set.setDuration(getResources().getInteger(R.integer.duration));
                            set.start();
                        });
                    }
                });




        loadFragment(new FragmentLogin());
    }

    private int[] screenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new int[]{size.x,size.y};
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_login:
                fragment = new FragmentLogin();
                break;
            case R.id.navigation_signUp:
                fragment = new FragmentSignUp();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public void onBackPressed() {
    }
}
