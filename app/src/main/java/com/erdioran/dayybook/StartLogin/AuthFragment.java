package com.erdioran.dayybook.StartLogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;


import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;


import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import butterknife.ButterKnife;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.erdioran.dayybook.MainActivity;
import com.erdioran.dayybook.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.transitionseverywhere.*;

import butterknife.OnClick;
import butterknife.BindView;

public abstract class AuthFragment extends Fragment {

    private FirebaseAuth auth;


    private VerticalTextView buttonSignUp;
    private TextInputEditText email_input_edit, password_input_edit,confirm_password_edit;

    protected Callback callback;

    @BindView(R.id.caption)
    protected VerticalTextView caption;

    @BindView(R.id.root)
    protected ViewGroup parent;

    protected boolean lock;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(authLayout(),container,false);
        ButterKnife.bind(this,root);
        return root;



    }




















    public void setCallback(@NonNull Callback callback) {
        this.callback = callback;
    }

    @LayoutRes
    public abstract int authLayout();
    public abstract void fold();
    public abstract void clearFocus();

    @OnClick(R.id.root)
    public void unfold(){
        if(!lock) {
            caption.setVerticalText(false);
            caption.requestLayout();
            Rotate transition = new Rotate();
            transition.setStartAngle(-90f);
            transition.setEndAngle(0f);
            transition.addTarget(caption);
            TransitionSet set=new TransitionSet();
            set.setDuration(getResources().getInteger(R.integer.duration));
            ChangeBounds changeBounds=new ChangeBounds();
            set.addTransition(changeBounds);
            set.addTransition(transition);
            TextSizeTransition sizeTransition=new TextSizeTransition();
            sizeTransition.addTarget(caption);
            set.addTransition(sizeTransition);
            set.setOrdering(TransitionSet.ORDERING_TOGETHER);
            caption.post(()->{
                TransitionManager.beginDelayedTransition(parent, set);
                caption.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.unfolded_size));
                caption.setTextColor(ContextCompat.getColor(getContext(),R.color.color_label));
                caption.setTranslationX(0);
                ConstraintLayout.LayoutParams params = getParams();
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                params.verticalBias = 0.78f;
                caption.setLayoutParams(params);
            });
            callback.show(this);
            lock=true;
        }
    }

    protected ConstraintLayout.LayoutParams getParams(){
        return ConstraintLayout.LayoutParams.class.cast(caption.getLayoutParams());
    }

    interface Callback {
        void show(AuthFragment fragment);
        void scale(boolean hasFocus);
    }
}
