package com.scatterform.chatabox;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import com.scatterform.chatabox.dummy.DummyContent;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.net.URI;

public class MainActivity extends AppCompatActivity implements ChatMessageFragment.OnFragmentInteractionListener, HistoryFragment.OnListFragmentInteractionListener,
                                                               MembersFragment.OnListFragmentInteractionListener, RewardedVideoAdListener {

    FirebaseApp mApp;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    String mDisplayName;

    ViewPager mViewPage;
    FragmentAdapter mFragmentAdapter;
    TabLayout mTabLayout;

    AdView mAdView;
    InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;

    int mCounter = 0;

    String TAG = "ChattyTest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "Application started");

        initFirebase();
        initViewPager();

        initAds();

    }

    private void initFirebase() {
        mApp = FirebaseApp.getInstance();
        mDatabase = FirebaseDatabase.getInstance(mApp);
        mAuth = FirebaseAuth.getInstance(mApp);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    Log.e(TAG, "User is logged In : Email [" + user.getEmail() + "] Display Name [" + user.getDisplayName() + "]");
                    String displayName = user.getDisplayName();

                    if (displayName != null)
                        mDisplayName = displayName;
                    else
                        mDisplayName = "Unknown DisplayName";

//                    for (UserInfo userInfo : user.getProviderData()) {
//                        if (displayName == null && userInfo.getDisplayName() != null) {
//                            displayName = userInfo.getDisplayName();
//
//                        }
//                    }
//
//                    mDisplayName = displayName;

                } else {
                    Log.e(TAG, "No User Is Logged In");
                    mDisplayName = "Anonymous";

                    mAuth.removeAuthStateListener(mAuthListener);
                    Intent activityIntent = new Intent(getApplicationContext(), SignIn.class);
                    startActivityForResult(activityIntent, 101);
                }

            }
        };

        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 101 && (resultCode == RESULT_OK)) {

            mDisplayName = data.getStringExtra("displayname");

            Log.e(TAG, "Display Name " + mDisplayName);
            mAuth.addAuthStateListener(mAuthListener);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            Log.e(TAG, "Logged Out");
            mAuth.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void initViewPager(){
        mViewPage = findViewById(R.id.viewPager);
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPage.setAdapter(mFragmentAdapter);
        mTabLayout = findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPage);

        mViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mCounter += 1;

                if (mCounter == 500) {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }


                } else if (mCounter == 1000){
                    if(mRewardedVideoAd.isLoaded()){
                        mRewardedVideoAd.show();
                    }

                    mCounter = 0;
                }

            }


            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void onFragmentInteraction(Uri uri){
        Log.e(TAG, "Fragment Interaction Listener");
    }

    public void onHistoryListFragmentInteraction(DummyContent.DummyItem item){
        Log.e(TAG, "Members Fragment Interaction Listener");
    }

    public void onMembersListFragmentInteraction(DummyContent.DummyItem item){
        Log.e(TAG, "Members Fragment Interaction Listener");
    }

    public void initAds(){
        MobileAds.initialize(this, "ca-app-pub-3082219041927920~1805242709");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this); //ca-app-pub-3940256099942544/5224354917
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());


    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
