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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Mobile Ads SDK safely
        MobileAds.initialize(this, initializationStatus -> {
            Log.d(TAG, "AdMob Initialized");
            fetchAd();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showAdIfAvailable();
    }

    public void fetchAd() {
        if (isAdAvailable()) return;

        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(this, AD_UNIT_ID, request, new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                Log.d(TAG, "App Open Ad Loaded ✅");
                MainActivity.this.appOpenAd = ad;
                showAdIfAvailable();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.e(TAG, "App Open Ad Failed: " + loadAdError.getMessage());
                new android.os.Handler().postDelayed(() -> fetchAd(), 5000);
            }
        });
    }

    public void showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable()) {
            appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    MainActivity.this.appOpenAd = null;
                    isShowingAd = false;
                    fetchAd(); 
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    MainActivity.this.appOpenAd = null;
                    isShowingAd = false;
                    fetchAd();
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            });
            appOpenAd.show(MainActivity.this);
        } else {
            fetchAd();
        }
    }

    private boolean isAdAvailable() {
        return appOpenAd != null;
    }
}
