package com.androidtuto.war;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;

import com.androidtuto.war.util.IabBroadcastReceiver;
import com.androidtuto.war.util.IabHelper;
import com.androidtuto.war.util.IabResult;
import com.androidtuto.war.util.Inventory;
import com.androidtuto.war.util.Purchase;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class MyActivity extends Activity implements IabBroadcastReceiver.IabBroadcastListener {

    private MainGLSurfaceView mGLSurfaceView;
    protected AdView adView;

    static public float m_resolution;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        m_resolution = (float)dm.heightPixels / (float)dm.widthPixels;

        super.onCreate(savedInstanceState);

        // 서피스뷰 생성을 위한 매트릭스
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        mGLSurfaceView = new MainGLSurfaceView(this, width, height);


        checkNetwork();

        //setContentView(mGLSurfaceView);

        User.INSTANCE.init(this);

        // Create AdMob LayOut
        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(mGLSurfaceView);
        setContentView(layout);

        if(User.INSTANCE.getAd() == false) {
            adView = new AdView(this, new com.google.ads.AdSize(300, 45), "ca-app-pub-2164030080539874/5690083344"); // Your AdMob ID
            AdRequest request = new AdRequest();
            adView.loadAd(request);
            RelativeLayout.LayoutParams adParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            adParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(adView, adParams);
        }

        Manager.INSTANCE.setActivity(this);






        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhb1Qfv4Vr9J2L7B3i8qGU/k1XIihmjniPdlk1bxIui5ZKD9sheZCyK0hv2Fpy8h2tei+N7zT1Oapa7CCyqPKI3v+iogG5q1bFHoRaefnl1bStvzFcxNOwoqc9brQhuvVR1oWTPY8do/4YNu+h6E4T1KxHnw80Ot8+cSQjRQlQpk8O5ztLtz5kLJjdZyW/1a31utOxSSr81vqLm2FJxabzbVgSv0J8PFkOb8RmDBSBkXEzQrbgKDiEQJoxWhnQ9Fn4utjgFb4CiHPXgnaAiTTNOMdJ2HWkgkV+s4i/qcjjHnAsaMDWIFbh91x2Z24ssDEKLPN2nV1XT8melT+xY2qHQIDAQAB";

        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR")) {
            throw new RuntimeException("Please put your app's public key in MainActivity.java. See README.");
        }
        if (getPackageName().startsWith("com.example")) {
            throw new RuntimeException("Please change the sample's package name! See README.");
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(MyActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        if(Manager.INSTANCE.isMainVisible())
            Manager.INSTANCE.hideUI();
        else {
            AlertDialog.Builder d = new AlertDialog.Builder(this);
            d.setMessage("Flee the battlefield?");
            d.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            d.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            d.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Manager.INSTANCE.setPause(true);
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Manager.INSTANCE.setPause(false);
        mGLSurfaceView.onResume();
    }

    private void checkNetwork(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo_3G = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo networkInfo_wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(networkInfo_3G.isConnectedOrConnecting() || networkInfo_wifi.isConnectedOrConnecting()){
        }else{
            AlertDialog dialog = createDialogBox();
            dialog.show();
        }
    }
    private AlertDialog createDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please check your network connection");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


            }

        });
        AlertDialog dialog = builder.create();

        return dialog;

    }
















    // Debug tag, for logging
    static final String TAG = "TrivialDrive";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            Purchase premiumPurchase = inventory.getPurchase("advertise");
            if(premiumPurchase != null && verifyDeveloperPayload(premiumPurchase))
                User.INSTANCE.setAd(true);

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase;
            gasPurchase = inventory.getPurchase("gold1");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase("gold1"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("gold2");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase("gold2"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("gold3");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase("gold3"), mConsumeFinishedListener);
                return;
            }

            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

    public void buyItem(String item){
        String payload = "";

        mHelper.launchPurchaseFlow(this, item, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals("advertise")){
                User.INSTANCE.setAd(true);
            }
            else if (purchase.getSku().equals("gold1")) {
                User.INSTANCE.addCoin(30000);
                User.INSTANCE.writeUserData();
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            else if (purchase.getSku().equals("gold2")) {
                User.INSTANCE.addCoin(100000);
                User.INSTANCE.writeUserData();
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            else if (purchase.getSku().equals("gold3")) {
                User.INSTANCE.addCoin(200000);
                User.INSTANCE.writeUserData();
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
            }
            else {
                complain("Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
}


 