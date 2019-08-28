package com.prodatadoctor.CoolStickyNotes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GiftsAndOffers extends Activity implements View.OnClickListener {

    TextView textViewTitle,textViewOrderId,textViewOrderInfo,response;
    Button buttonEmailOffer,buttonBuyOffer,buttonSendOrderNumberOffer,buttonYouTubeOffer,buttonContactUsOffer,buttonLinkOffer;

    Button buttonBarcodeEmail,buttonBarcodeEnquiry,buttonBarcodeLink,buttonDataRecoveryEmail,buttonDataRecoveryEnquiry,buttonDataRecoveryLink,
            buttonBulkSMSEmail,buttonBulkSMSEnquiry,buttonBulkSMSLink,buttonExcelEmail,buttonExcelEnquiry,buttonExcelLink;

   // ImageButton imageButtonBack;

    LinearLayout linearLayoutOrderInfo;

    Intent intent,i;
    String appname="";

    String orderId,Info,mainTitle,email;

    SharedPreferences sp;

    public GiftsAndOffers() {
        // Required empty public constructor
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gift_new);

        sp=getSharedPreferences("ashish",MODE_PRIVATE);
        orderId=sp.getString("order","");
        Info=sp.getString("info","");


        textViewTitle= (TextView) findViewById(R.id.title);
        appname=getResources().getString(R.string.app_name);



        buttonEmailOffer= (Button) findViewById(R.id.buttonEmail);
        buttonBuyOffer= (Button) findViewById(R.id.buttonBuyNow);

        buttonYouTubeOffer= (Button) findViewById(R.id.buttonYouTube);
        buttonContactUsOffer= (Button) findViewById(R.id.buttonContact);
      //  imageButtonBack= (ImageButton) findViewById(R.id.back);

        buttonBarcodeEmail= (Button) findViewById(R.id.buttonBarcodeEmail);
        buttonBarcodeEnquiry= (Button) findViewById(R.id.buttonBarcodeEnquiry);
        buttonBarcodeLink= (Button) findViewById(R.id.buttonBarcodeLink);
        buttonDataRecoveryEmail= (Button) findViewById(R.id.buttonDataRecoveryEmail);
        buttonDataRecoveryEnquiry= (Button) findViewById(R.id.buttonDataRecoveryEnquiry);
        buttonDataRecoveryLink= (Button) findViewById(R.id.buttonDataRecoveryLink);
        // buttonBulkSMSEmail= (Button) findViewById(R.id.buttonBulkSMSEmail);
        // buttonBulkSMSEnquiry= (Button) findViewById(R.id.buttonBulkSMSEnquiry);
        //buttonBulkSMSLink= (Button) findViewById(R.id.buttonBulkSMSLink);
        buttonExcelEmail= (Button) findViewById(R.id.buttonExcelEmail);
        buttonExcelEnquiry= (Button) findViewById(R.id.buttonExcelEnquiry);
        buttonExcelLink= (Button) findViewById(R.id.buttonExcelLink);

        buttonEmailOffer.setOnClickListener(this);
        buttonBuyOffer.setOnClickListener(this);

        buttonYouTubeOffer.setOnClickListener(this);
        buttonContactUsOffer.setOnClickListener(this);

      //  imageButtonBack.setOnClickListener(this);

        buttonBarcodeEmail.setOnClickListener(this);
        buttonBarcodeEnquiry.setOnClickListener(this);
        buttonBarcodeLink.setOnClickListener(this);
        buttonDataRecoveryEmail.setOnClickListener(this);
        buttonDataRecoveryEnquiry.setOnClickListener(this);
        buttonDataRecoveryLink.setOnClickListener(this);
        // buttonBulkSMSEmail.setOnClickListener(this);
        // buttonBulkSMSEnquiry.setOnClickListener(this);
        // buttonBulkSMSLink.setOnClickListener(this);
        buttonExcelEmail.setOnClickListener(this);
        buttonExcelEnquiry.setOnClickListener(this);
        buttonExcelLink.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.buttonEmail:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, "");
                intent.putExtra(Intent.EXTRA_SUBJECT, "How to send SMS using DRPU Bulk SMS Software");
                intent.putExtra(Intent.EXTRA_TEXT,  "DRPU Bulk SMS Software works the same way as you are sending messages from your Phone. You can send messages to any type of mobile device. Software can be used from anywhere and you can send messages to any location.\n" +
                        " \n" +
                        "DRPU Bulk SMS software also support Excel (XLS, XLSX) import, text file import, copy-paste as well as manual input for mobile Numbers and SMS Texts data entry. Besides this software also has many excel based advanced features. You can use dynamic SMS feature of DRPU Bulk SMS software that picks names and other personalized details automatically from your excel and sends personalized SMS to each recipients.\n" +
                        " \n" +
                        "Software is available for Windows as well as for Mac based computers. Here are the steps for sending SMS using DRPU Bulk SMS Software:\n" +
                        " \n" +
                        "1.       Download DRPU Bulk SMS Software in your computer,\n" +
                        "2.       Connect your mobile Device using USB Cable,\n" +
                        "3.       Run DRPU Bulk SMS Software and select your mobile Device,\n" +
                        "4.       Enter Mobile numbers from Excel or TEXT File or Copy Paste or Manually and Send SMS.\n" +
                        " \n" +
                        "Product’s Price written on website is one time charge. Once installed, software never expires. You can order DRPU Bulk SMS Software from following website:\n" +
                        " \n" +
                        "http://www.sendgroupsms.com\n" +
                        " \n" +
                        "You can choose any transaction currency on order form. Once your transaction is completed, you'll get FULL version download link instantly. You can find all Payment option on order from.\n" +
                        " \n" +
                        "If you require any further assistance, please feel free to Support team at: support@SendGroupSMS.com\n" +
                        " \n" +
                        "Regards");
                startActivity(Intent.createChooser(intent, "send mail"));
                break;

            case R.id.buttonBuyNow:

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sendgroupsms.com/bulk-sms/order-bulk-sms-software-android-mobilephone.html")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    //Toast.makeText(GiftsAndOffers.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sendgroupsms.com/bulk-sms/order-bulk-sms-software-android-mobilephone.html")));
                }

                break;


            case R.id.buttonYouTube:

                SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
                editor.putBoolean("subscribed", true);
                editor.apply();

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/TarunTyagi"));
                startActivity(intent);
                break;

            case R.id.buttonContact:
                i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@SendGroupSms.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "User Enquiry from App: "+appname);
                i.putExtra(Intent.EXTRA_TEXT   , "Dear SendGroupSms.com Team\n" +
                        "I read about DRPU Bulk SMS software from "+appname+" and I have following Query:");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(GiftsAndOffers.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            //<<<<<----------Amazing Start here-------------------------------->>>>>>>>

            case R.id.buttonBarcodeEmail :

                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, "");
                intent.putExtra(Intent.EXTRA_SUBJECT, "How to Generate Barcode Labels using DRPU Barcode Software");
                intent.putExtra(Intent.EXTRA_TEXT,  "DRPU Barcode Software has all type of popular Barcode fonts, Major Stationary and Labels templates support. It is suitable for Barcode requirements for any type of Businesses and requirements. DRPU Barcode Software has maximum compatibility features for international formats.\n" +
                        " \n" +
                        "Besides this, DRPU Barcode software has a number of Process automation features such as Batch Processing, Excel data Import features, Exports generated barcodes to various image formats and PDF etc and a number of other automation and designing features as per various industry needs. Software has no such limitations and works with all type of printers including Laser Printers, thermal printers and inkjet printers etc.\n" +
                        " \n" +
                        "You can order DRPU Barcode Label Maker Software online from:\n" +
                        " \n" +
                        "http://www.generate-barcode.com/generate-barcode/order-online.html\n" +
                        " \n" +
                        "You can choose any transaction currency on order form. Once your transaction is completed, you'll get FULL version download link instantly. You can find all Payment options on order from such as Credit Cards/PayPal/Bank Transfers/FAX etc.\n" +
                        " \n" +
                        "If you require any further assistance, please feel free to contact support team at: support@generate-barcode.com");

                startActivity(Intent.createChooser(intent, "send mail"));
                break;

            case R.id.buttonBarcodeEnquiry :
                i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@Generate-Barcode.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "User Enquiry from App: "+appname);
                i.putExtra(Intent.EXTRA_TEXT   , "Dear Generate-Barcode.com Team\n" +
                        "I read about DRPU Barcode Label software from "+appname+" and I have following Query:");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(GiftsAndOffers.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.buttonBarcodeLink :
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.generate-barcode.com/"));
                startActivity(intent);
                break;


            case R.id.buttonDataRecoveryEmail :
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, "");
                intent.putExtra(Intent.EXTRA_SUBJECT, "\tHow to Recover Data using Data Doctor Data Recovery Software");
                intent.putExtra(Intent.EXTRA_TEXT,  "Here are the steps to recover lost data using Data Doctor Data Recovery Software:\n" +
                        " \n" +
                        "1.       Download DDR Data Recovery Software from: http://www.DataRecoverySoftware.com\n" +
                        "2.       Connect your storage media to your computer (from which you want to recover your data)\n" +
                        "3.       Follow Recovery Steps as suggested by DDR Data Recovery Software\n" +
                        "4.       Save recovered Data to your computer.\n" +
                        " \n" +
                        "If you have not purchased the DDR Recovery Software yet, you can also order data recovery software online form following URL of our website:\n" +
                        " \n" +
                        "http://www.datarecoverysoftware.com/datarecoverysoftware/order-online.html\n" +
                        " \n" +
                        "You can choose any transaction currency on order form. Once your transaction is completed, you'll get FULL version download link instantly. You can find all Payment option on order from.\n" +
                        " \n" +
                        "It is onetime charge. Once installed, our software never expires.\n" +
                        " \n" +
                        "If you require any further assistance, please feel free to contact DataRecoverySoftware.com Support Team at Email ID: support@DataRecoverySoftware.com");

                startActivity(Intent.createChooser(intent, "send mail"));
                break;

            case R.id.buttonDataRecoveryEnquiry :
                i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@datarecoverysoftware.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "User Enquiry from App: "+appname);
                i.putExtra(Intent.EXTRA_TEXT   , "Dear datarecoverysoftware.com Team\n" +
                        "I read about DDR Data Recovery Software from "+appname+" and I have following Query:");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(GiftsAndOffers.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.buttonDataRecoveryLink :
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.datarecoverysoftware.com/"));
                startActivity(intent);
                break;

            case R.id.buttonExcelEmail :

                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, "");
                intent.putExtra(Intent.EXTRA_SUBJECT, "DRPU Conversion Software");
                intent.putExtra(Intent.EXTRA_TEXT,  "DRPU Software has variety of Data and file conversion software tools suitable for the requirements of all type of users and Businesses.\n" +
                        " \n" +
                        "You can order DRPU Conversion Software online from:\n" +
                        " \n" +
                        "http://www.drpudatabase.com/drpudatabase/purchase-online.html\n" +
                        " \n" +
                        "You can choose any transaction currency on order form. Once your transaction is completed, you'll get FULL version download link instantly. You can find all Payment options on order from such as Credit Cards/PayPal/Bank Transfers/FAX etc.\n" +
                        " \n" +
                        "If you require any further assistance, please feel free to contact support team at: support@drpudatabase.com");

                startActivity(Intent.createChooser(intent, "send mail"));


                break;

            case R.id.buttonExcelEnquiry :
                i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"support@DRPUdatabase.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "User Enquiry from App: "+appname);
                i.putExtra(Intent.EXTRA_TEXT   , "Dear DRPUdatabase.com Team\n" +
                        "I read about DRPU Excel to Phonebook software from "+ appname +" and I have following Query:");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(GiftsAndOffers.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.buttonExcelLink :
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.drpudatabase.com/"));
                startActivity(intent);
                break;

        }

    }


}