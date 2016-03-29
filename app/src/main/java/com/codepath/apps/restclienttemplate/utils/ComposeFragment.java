package com.codepath.apps.restclienttemplate.utils;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ComposeFragment extends DialogFragment {

    @Bind(R.id.txt_message)
    EditText mCompose;
    @Bind(R.id.btn_tweet)
    Button mTweet;
    @Bind(R.id.txt_counter)
    TextView mCounter;
    @Bind(R.id.fragment_bottom_layout)
    RelativeLayout bottomLayout;
    @Bind(R.id.img_profile)
    ImageView mProfileImg;
    @Bind(R.id.btn_close)
    ImageButton mCloseBtn;

    long replyId = -1;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance() {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public interface ComposeDialogListener {
        void onFinishEditDialog(String inputText, long replyId);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompose();
            }
        });
        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(mCompose);
            }
        });
        mTweet.setClickable(false);



        mCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCounter.setText(String.valueOf((140 - s.length())));
                if (s.length() > 140)
                    mCounter.setTextColor(Color.RED);
                else mCounter.setTextColor(Color.GRAY);
                if (isInRange(s) && !TextUtils.isEmpty(s)) {
                    mTweet.setClickable(true);
                    mTweet.setBackgroundResource(R.drawable.btn_enable);
                } else {
                    mTweet.setClickable(false);
                    mTweet.setBackgroundResource(R.drawable.btn_disable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void onCompose() {
        ComposeDialogListener listener = (ComposeDialogListener) getActivity();
        listener.onFinishEditDialog(mCompose.getText().toString(), replyId);
        dismiss();
    }

    private void showKeyboard(final View view) {
        view.requestFocus();
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                mCompose.setSelection(mCompose.getText().length());
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
        makeFullScreen();
        showKeyboard(mCompose);
    }

    private void makeFullScreen() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    private Boolean isInRange(CharSequence s) {
        return s.length() > 0 && s.length() <= 140;
    }
}
