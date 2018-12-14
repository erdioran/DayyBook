package com.erdioran.dayybook.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erdioran.dayybook.R;

import java.util.List;

import butterknife.BindViews;

public class FragmentSignUp extends Fragment {

    @BindViews(value = {R.id.email_input_edit,
            R.id.password_input_edit,
            R.id.confirm_password_edit})
    protected List<TextInputEditText> views;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        return view;
    }
}
