package com.dellhoak.counter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

public class MainActivity extends AppCompatActivity {

    TextView count;
    Button plusebtn, minusbtn, resetbtn, reviewButton;
    private int counter;
    final Context context = this;
    private final int REQUEST_CODE =11;
    ReviewManager reviewManager;
    ReviewInfo reviewInfo;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        count = findViewById(R.id.count);
        plusebtn = findViewById(R.id.plusbtn);
        minusbtn = findViewById(R.id.minusbtn);
        resetbtn = findViewById(R.id.resetbtn);
        reviewButton = findViewById(R.id.reviewButton);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //interstitial ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8341489867883063/1332655597");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        plusebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pluscounter();
            }
        });

        minusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minuscounter();
            }
        });

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initcounter();
            }
        });

        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE, MainActivity.this , REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        reviewManager = ReviewManagerFactory.create(MainActivity.this);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }

                Task<ReviewInfo> request = reviewManager.requestReviewFlow();
                request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                    @Override
                    public void onComplete(Task<ReviewInfo> task) {
                        if (task.isSuccessful()){
                            reviewInfo = task.getResult();
                            Task<Void> flow = reviewManager.launchReviewFlow(MainActivity.this, reviewInfo);
                            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {

                                }
                            });


                        }else {
                            Toast.makeText(MainActivity.this, "Error" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            Toast.makeText(this, "start download", Toast.LENGTH_SHORT).show();
        }
    }

    private void initcounter() {
        counter = 00;
        count.setText(counter + "");
    }

    private void pluscounter() {
        counter++;
        count.setText(counter + "");
    }

    private void minuscounter() {
        if (counter>=1){
            counter--;
            count.setText(counter + "");
        }

    }

    public void onBackPressed(){

        final Dialog dialogmain = new Dialog(context);
        dialogmain.setContentView(R.layout.custom_dialogbox);
        ImageView closepopup = (ImageView) dialogmain.findViewById(R.id.closethebox);
        Button dialogButton1 = (Button) dialogmain.findViewById(R.id.yesbtn);
        Button dialogButton2 = (Button) dialogmain.findViewById(R.id.nobtn);
        // if button is clicked, close the custom dialog
        closepopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogmain.dismiss();
            }
        });
        dialogButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialogButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogmain.dismiss();

            }
        });
        dialogmain.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogmain.show();
    }

}