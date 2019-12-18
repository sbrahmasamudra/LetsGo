package com.example.letsgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Calendar;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.ContentMetadata;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;



public class MainActivity<lp, shareSheetStyle, linkProperties> extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();
        // Branch init
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                    //Log.i("BRANCH SDK", referringParams.toString());
                    Log.i("BranchConfigTest", "deep link data:" + referringParams.toString());
                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.getInstance().reInitSession(this, branchReferralInitListener);
    }

    private Branch.BranchUniversalReferralInitListener branchReferralInitListener =
            new Branch.BranchUniversalReferralInitListener() {
                @Override
                public void onInitFinished(BranchUniversalObject branchUniversalObject,
                                           LinkProperties linkProperties, BranchError branchError) {
                    // do something with branchUniversalObject/linkProperties..
                }
            };


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
                .addControlParameter("$desktop_url", "http://www.letsgo.com/home")
                .addControlParameter("$ios_url", "http://m.example.com/ios");

        ShareSheetStyle shareSheetStyle = new ShareSheetStyle(MainActivity.this, "Check this out!", "This stuff is awesome: ")
                .setCopyUrlStyle(getResources().getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(getResources().getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .setAsFullWidthStyle(true);

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
