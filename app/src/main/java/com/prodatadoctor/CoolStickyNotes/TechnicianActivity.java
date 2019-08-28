package com.prodatadoctor.CoolStickyNotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TechnicianActivity extends AppCompatActivity {

    EditText code, mail;
    Button validate, submit;

    LinearLayout ll1, ll;

    SharedPreferences sharedPreferencesStopAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician);

        submit = (Button) findViewById(R.id.submit);
        validate = (Button) findViewById(R.id.validate);
        code = (EditText) findViewById(R.id.code);
        mail = (EditText) findViewById(R.id.mail);
        ll1 = (LinearLayout) findViewById(R.id.ll1);
        ll = (LinearLayout) findViewById(R.id.ll);
        ll.setVisibility(View.GONE);


        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText().toString().equals("6953832")) {

                    ll.setVisibility(View.VISIBLE);
                    ll1.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(TechnicianActivity.this);
                    dialog.setCancelable(false);
                    dialog.setTitle("Error");
                    dialog.setMessage("The unlock code that you entered is invalid. " +
                            "Please make sure that you have entered the code exactly same as received from our support team.");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    final AlertDialog alert = dialog.create();
                    alert.show();

                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mail.getText().toString().trim().equalsIgnoreCase("EK9CF2CD5D3A") && (!mail.getText().toString().equals(""))) {
                    sharedPreferencesStopAd = TechnicianActivity.this.getSharedPreferences("payment", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferencesStopAd.edit();
                    editor.putBoolean("check", false);
                    editor.apply();

                    MainActivity.check = sharedPreferencesStopAd.getBoolean("check", true);
                    Toast.makeText(getApplicationContext(), "Ads stopped successfully", Toast.LENGTH_SHORT).show();

                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in);
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TechnicianActivity.this);
                    dialog.setCancelable(false);
                    dialog.setTitle("Error");
                    dialog.setMessage("The key that you entered is invalid. " +
                            "Please make sure that you have entered the key exactly same as received from our support team.");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    final AlertDialog alert = dialog.create();
                    alert.show();
                }
            }
        });

    }
}
