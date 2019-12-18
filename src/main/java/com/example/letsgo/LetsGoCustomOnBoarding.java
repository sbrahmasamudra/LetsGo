package com.example.letsgo;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;


public class LetsGoCustomOnBoarding extends Activity {

    private static final String TAG = LetsGoCustomOnBoarding.class.getSimpleName();
    private TextView mReferredBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReferredBy = (TextView) findViewById(R.id.referredBy);
        mReferredBy.setVisibility(View.INVISIBLE);

        Intent i = getIntent();
        if (i != null) {
            String referredBy = "";
            try {
                referredBy = i.getStringExtra("referredBy");
            } catch (RuntimeException e) {
                referredBy = "";
            }
            if (referredBy != null && referredBy.length() > 0) {
                mReferredBy.setText(referredBy);
            }
        }

        mReferredBy.post(new Runnable() {
            @Override
            public void run() {
                revealView();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void revealView() {

        View myView = findViewById(R.id.referredBy);
        // get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }

}
