package com.androidtuto.war;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.InterstitialAd;

/**
 * Created by Kim on 2015-05-13.
 */
public class Advertise implements AdListener {
    private String LOG_TAG="정보";
    private InterstitialAd interstitial;
    AdRequest adRequest;

    Advertise(Context context){
        interstitial = new InterstitialAd((Activity)context, "ca-app-pub-2164030080539874/9830748147");
        adRequest = new AdRequest();
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(this);
    }

    public void onDismissScreen(Ad ad) {
        Log.d(LOG_TAG, "onDismissScreen");
        interstitial.loadAd(adRequest);
    }

    /** Called when an ad was not received. */
    public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode error) {
        String message = "onFailedToReceiveAd (" + error + ")";
        Log.d(LOG_TAG, message);
    }

    /**
     * Called when an ad is clicked and going to start a new Activity that will
     * leave the application (e.g. breaking out to the Browser or Maps
     * application).
     */
    public void onLeaveApplication(Ad ad) {
        Log.d(LOG_TAG, "onLeaveApplication");
    }

    /**
     * Called when an Activity is created in front of the app (e.g. an
     * interstitial is shown, or an ad is clicked and launches a new Activity).
     */
    public void onPresentScreen(Ad ad) {
        Log.d(LOG_TAG, "onPresentScreen");
        //interstitial.show();
        // Toast.makeText(this, "onPresentScreen", Toast.LENGTH_SHORT).show();
    }

    /** Called when an ad is received. */
    public void onReceiveAd(Ad ad) {
        Log.d(LOG_TAG, "onReceiveAd");
        //Toast.makeText(this, "onReceiveAd", Toast.LENGTH_SHORT).show();
    }
    public void showAd(){
        interstitial.show();
    }
}
