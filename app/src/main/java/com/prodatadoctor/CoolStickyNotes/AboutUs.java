package com.prodatadoctor.CoolStickyNotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.prodatadoctor.CoolStickyNotes.R;

public class AboutUs extends Activity {
    TextView textViewOk, tvWeb, tvMail;
    Button buttonYouTube, buttonTech;
    Intent intent;

    SharedPreferences pref, sharedPreferencesStopAd;
    int rate;
    AdView mAdView1, mAdView2,mAdView3;
    Boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        pref = AboutUs.this.getSharedPreferences("MyPref", AboutUs.this.MODE_PRIVATE);
        rate = pref.getInt("key", 0);


        sharedPreferencesStopAd = AboutUs.this.getSharedPreferences("payment", Context.MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);


        textViewOk =  findViewById(R.id.textViewOk);
        tvWeb =  findViewById(R.id.website);
        tvMail =  findViewById(R.id.mail);
        buttonYouTube = (Button) findViewById(R.id.youTube);
        buttonTech = findViewById(R.id.techSupport);

        tvWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.ProDataDoctor.com");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        tvMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@prodatadoctor.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "User Enquiry from App " + getResources().getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, "Dear ProDataDoctor.com Technical Support\n" +
                        "I downloaded your App " + getResources().getString(R.string.app_name) + " and have following Query:");
                intent.setType("message/rfc822");
                try {
                    startActivity(Intent.createChooser(intent, "Perform action using..."));
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(AboutUs.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonYouTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
                editor.putBoolean("subscribed", true);
                editor.apply();

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/TarunTyagi"));
                startActivity(intent);
            }
        });

        buttonTech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutUs.this, TechnicianActivity.class));
            }
        });
        
            showAds();
    }


    private void showAds() {

        if (check) {

            mAdView1 =  findViewById(R.id.adViewbanner1);
            mAdView2 =  findViewById(R.id.adViewbanner2);
            mAdView3 =  findViewById(R.id.adViewbanner3);

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView1.loadAd(adRequest);
            mAdView2.loadAd(adRequest);
            mAdView3.loadAd(adRequest);

            mAdView1.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mAdView1.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView1.setVisibility(View.VISIBLE);
                }
            });

            mAdView2.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mAdView2.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView2.setVisibility(View.VISIBLE);
                }
            });

            mAdView3.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mAdView3.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView3.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
