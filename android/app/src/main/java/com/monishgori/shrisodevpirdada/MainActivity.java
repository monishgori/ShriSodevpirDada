package com.monishgori.shrisodevpirdada;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.getcapacitor.BridgeActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class MainActivity extends BridgeActivity {
    private static final String AD_UNIT_ID = "ca-app-pub-5914382038291713/6144381379";
    private static final String TAG = "AdMobNative";
    private AppOpenAd appOpenAd = null;
    private boolean isShowingAd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Mobile Ads SDK
        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "AdMob Initialized");
            fetchAd();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showAdIfAvailable();
    }

    /**
     * Request an ad
     */
    public void fetchAd() {
        if (isAdAvailable()) {
            return;
        }

        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(
                this, AD_UNIT_ID, request,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        Log.d(TAG, "App Open Ad Loaded ✅ (Ready)");
                        MainActivity.this.appOpenAd = ad;
                        // For the very first launch, try to show it immediately
                        showAdIfAvailable();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "App Open Ad Failed: " + loadAdError.getMessage());
                        // Retry after 5s
                        new android.os.Handler().postDelayed(() -> fetchAd(), 5000);
                    }
                });
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            Log.d(TAG, "Displaying App Open Ad...");

            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    MainActivity.this.appOpenAd = null;
                    isShowingAd = false;
                    Log.d(TAG, "Ad Dismissed");
                    fetchAd(); 
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    MainActivity.this.appOpenAd = null;
                    isShowingAd = false;
                    Log.e(TAG, "Ad Failed to Show: " + adError.getMessage());
                    fetchAd();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            });

            appOpenAd.show(MainActivity.this);

        } else {
            Log.d(TAG, "Ad not ready or already showing.");
            fetchAd();
        }
    }

    /**
     * Check if ad exists and can be shown.
     */
    private boolean isAdAvailable() {
        return appOpenAd != null;
    }
}
