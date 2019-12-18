package com.example.letsgo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

public class FakeMainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FloatingActionButton mFab;
    private EditText mReferredByET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                genLink(mReferredByET.getText().toString());
                //Animation shake = AnimationUtils.loadAnimation(getBaseContext(), R.anim.shake);
                //mFab.startAnimation(shake);
            }
        });

        mReferredByET = (EditText) findViewById(R.id.referredByET);
        mReferredByET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    genLink(mReferredByET.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBranch();
    }

    private void initBranch() {
        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    boolean clickedBranchLink = false;
                    try {
                        clickedBranchLink = referringParams.getBoolean("+clicked_branch_link");
                    } catch (JSONException e) {
                    }
                    if (clickedBranchLink) {
                        // do stuff with Branch link data
                        try {
                            String referredBy = referringParams.getString("referredBy");
                            if (referredBy != null && referredBy.length() > 0) {
                                Intent i = new Intent(getApplicationContext(), LetsGoCustomOnBoarding.class);
                                i.putExtra("referredBy", referredBy);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                        }
                    } else {
                        //do stuff without Branch link data
                    }
                }
                else {
                    // error occurred
                    Log.e(TAG, error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    private void genLink(@NonNull String name) {
        if (name.length() == 0) {
            Toast.makeText(this, "Please enter a name for your referral link", Toast.LENGTH_LONG).show();
            return;
        }

        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("item/12345")
                .setTitle("My Content Title")
                .setContentDescription("My Content Description")
                .setContentImageUrl("https://example.com/mycontent-12345.png")
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("referredBy", name);

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$desktop_url", "http://example.com/home")
                .addControlParameter("$ios_url", "http://example.com/ios");

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(FakeMainActivity.this, "Check this out!", "This stuff is awesome: ")
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share To");

        branchUniversalObject.showShareSheet(this,
                linkProperties,
                shareSheetStyle,
                new Branch.BranchLinkShareListener() {
                    @Override
                    public void onShareLinkDialogLaunched() {
                    }

                    @Override
                    public void onShareLinkDialogDismissed() {
                    }

                    @Override
                    public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                    }

                    @Override
                    public void onChannelSelected(String channelName) {
                    }
                }, new Branch.IChannelProperties() {
                    @Override
                    public String getSharingTitleForChannel(String channel) {
                        return channel.contains("Messaging") ? "title for SMS" :
                                channel.contains("Slack") ? "title for slack" :
                                        channel.contains("Gmail") ? "title for gmail" : null;
                    }

                    @Override
                    public String getSharingMessageForChannel(String channel) {
                        return channel.contains("Messaging") ? "message for SMS" :
                                channel.contains("Slack") ? "message for slack" :
                                        channel.contains("Gmail") ? "message for gmail" : null;
                    }
                });
    }
}