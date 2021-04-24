package com.example.textmessagebot;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Declaring UI elements and common variables
    TextView tv_permissions, tv_phoneNumber, tv_state, tv_message, tv_response;

    String phoneNumber="", message="", clothing="", brand="", color="", size="";
    boolean state1 = true, state2 = false, state3 = false, state4 = false;
    int c = 0, cost = 0;

    MessageReceiver messageReceiver;
    SmsManager smsManager;
    Handler delayedResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting UI elements
        tv_permissions = findViewById(R.id.id_textView_permissions);
        tv_phoneNumber = findViewById(R.id.id_textView_phoneNumber);
        tv_state = findViewById(R.id. id_textView_state);
        tv_message = findViewById(R.id.id_textView_message);
        tv_response = findViewById(R.id.id_textView_response);

        tv_state.setText("State: 1");

        //Checking permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            tv_permissions.setText("Permissions Not Granted");
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, 0);
            return;
        } else {
            Log.d("TAG", "Permissions already Granted");
            tv_permissions.setText("Permissions Granted");

            messageReceiver = new MessageReceiver();
            IntentFilter messageIntentFilter = new IntentFilter();
            messageIntentFilter.addAction(android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(messageReceiver, messageIntentFilter);

            delayedResponse = new Handler();
        }
    }//onCreate

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "Permissions Result");
        if (permissions.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            tv_permissions.setText("Permissions Granted");

            messageReceiver = new MessageReceiver();
            IntentFilter messageIntentFilter = new IntentFilter();
            messageIntentFilter.addAction(android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(messageReceiver, messageIntentFilter);

            delayedResponse = new Handler();
        }
    }//onRequestPermissionResult

    public class MessageReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "MessageReceiver");
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];

            for (int i=0; i<messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                phoneNumber = messages[i].getOriginatingAddress();
                message = messages[i].getMessageBody();
                tv_phoneNumber.setText("Phone Number: " + phoneNumber);
                tv_message.setText("Message: " + message);
                Log.d("TAG", "From " + phoneNumber + " Message Body " + i + ": " + message);
            }

            //States
            if (state1) {
                String[] greetings = {"Hey, welcome to Clothes Store! What can I help you with today?", "Hi, this is Clothes Store! What are you shopping for?", "Hello! What would you like from Clothes Store today?", "Welcome to Clothes Store! What type of clothing are you interested in?"};
                int r = (int)(Math.random()*((3-0)+1))+0;
                if (message.contains("Hey") ||message.contains("Hi") || message.toLowerCase().contains("hello")) {
                    delayedResponse.postDelayed(sendSms((greetings[r] + " (Shirts, Pants/ Shorts, Shoes, or Hats/ Glasses)")), 3000);
                    c++;
                } else delayedResponse.postDelayed(sendSms("I don't understand"), 3000);
                if (c==1) {
                    state1 = false;
                    state2 = true;
                    tv_state.setText("State 2");
                }
            }//state1
            else if (state2) {
                if (c==1 && message.toLowerCase().contains("shirt")) {
                    clothing = "shirt";
                    delayedResponse.postDelayed(sendSms("What brand of shirts are you interested in? (Polo Ralph Lauren, Tommy Hilfiger, Calvin Klein, or Nike"), 3000);
                    c++;
                } else if (c==1 && (message.toLowerCase().contains("pant") || message.toLowerCase().contains("short"))) {
                    if (message.toLowerCase().contains("pant"))
                        clothing = "pants";
                    if (message.toLowerCase().contains("short"))
                        clothing = "shorts";
                    delayedResponse.postDelayed(sendSms("What brand of pants/ shorts are you interested in? (Levi's, Hollister, Nike, or Adidas"), 3000);
                    c++;
                } else if (c==1 && message.toLowerCase().contains("shoe")) {
                    clothing = "shoes";
                    delayedResponse.postDelayed(sendSms("What brand of shoes are you interested in? (Vans, Nike, Adidas, or Jordan"), 3000);
                    c++;
                } else if (c==1 && (message.toLowerCase().contains("hat") || message.toLowerCase().contains("glasses"))) {
                    if (message.toLowerCase().contains("hat"))
                        clothing = "hat";
                    if (message.toLowerCase().contains("glasses"))
                        clothing = "glasses";
                    delayedResponse.postDelayed(sendSms("What brand of hats/ glasses are you interested in? (Lock & Co. or Ray-Bans)"), 3000);
                    c++;
                } else if (c==2) {
                    int yn = 0;
                    if (message.toLowerCase().contains("polo"))
                        brand = "Polo Ralph Lauren";
                    else if (message.toLowerCase().contains("tommy"))
                        brand = "Tommy Hilfiger";
                    else if (message.toLowerCase().contains("calvin"))
                        brand = "Calvin Klein";
                    else if (message.toLowerCase().contains("nike"))
                        brand = "Nike";
                    else if (message.toLowerCase().contains("levi"))
                        brand = "Levi's";
                    else if (message.toLowerCase().contains("hollister"))
                        brand = "Hollister";
                    else if (message.toLowerCase().contains("adidas"))
                        brand = "Adidas";
                    else if (message.toLowerCase().contains("vans"))
                        brand = "Vans";
                    else if (message.toLowerCase().contains("jordan"))
                        brand = "Jordan";
                    else if (message.toLowerCase().contains("lock"))
                        brand = "Lock & Co.";
                    else if (message.toLowerCase().contains("ray"))
                        brand = "Ray-Bans";
                    else {
                        delayedResponse.postDelayed(sendSms("I don't understand"), 3000);
                        yn = -1;
                    }
                    if (yn==0) {
                        delayedResponse.postDelayed(sendSms("Ok and what color do you want the " + brand + " " + clothing + " to be? (Red, Blue, or Yellow)"), 3000);
                        c++;
                    }
                } else if (c==3 && (message.toLowerCase().contains("red") || message.toLowerCase().contains("blue") || message.toLowerCase().contains("yellow"))) {
                    color = message;
                    switch (clothing) {
                        case "shirt": delayedResponse.postDelayed(sendSms("What is the size of your shirt? (Small, Medium, or Large)"), 3000);
                                      cost = 10;
                            break;
                        case "pants": delayedResponse.postDelayed(sendSms("What is the size of your pants? (Waist #, Length #)"), 3000);
                                      cost = 20;
                            break;
                        case "shorts": delayedResponse.postDelayed(sendSms("What is the size of your shorts? (size #)"), 3000);
                                       cost = 15;
                            break;
                        case "shoes": delayedResponse.postDelayed(sendSms("What is the size of your shoes? (size #)"), 3000);
                                      cost = 60;
                            break;
                        case "hat": delayedResponse.postDelayed(sendSms("What is the size of your hat? (Head size #)"), 3000);
                                    cost = 12;
                            break;
                        case "glasses": delayedResponse.postDelayed(sendSms("What is the size of your glasses? (Small, Medium, or Large)"), 3000);
                                        cost = 35;
                            break;
                    }
                    c++;
                } else delayedResponse.postDelayed(sendSms("I don't understand"), 3000);
                if (c==4) {
                    state2 = false;
                    state3 = true;
                    tv_state.setText("State 3");
                }
            }//state2
            else if (state3) {
                if (c==4 && (message.toLowerCase().contains("small") || message.toLowerCase().contains("medium") || message.toLowerCase().contains("large") || message.contains("0") || message.contains("1") || message.contains("2") || message.contains("3") || message.contains("4") || message.contains("5") || message.contains("6") || message.contains("8") || message.contains("9"))) {
                    size = message;
                    delayedResponse.postDelayed(sendSms("The total cost will be $" + cost + " for the size " + size + " " + color + " " + brand + " " + clothing + "."), 3000);
                    delayedResponse.postDelayed(sendSms("What is your address?"), 3000);
                    c++;
                } else if (c==5) {
                    delayedResponse.postDelayed(sendSms("Okay, your order will be delivered to " + message), 3000);
                    delayedResponse.postDelayed(sendSms("Do you want regular, prime, speedy, or same day shipping? (Price increases for anything other than regular shipping)"), 3000);
                    c++;
                } else delayedResponse.postDelayed(sendSms("I don't understand"), 3000);
                if (c==6) {
                    state3 = false;
                    state4 = true;
                    tv_state.setText("State 4");
                }
            }//state3
            else if (state4) {
                if (c==6) {
                    if (message.toLowerCase().contains("regular")) {
                        delayedResponse.postDelayed(sendSms("Your order will arrive in 7 business days. Thank you for shopping with Clothes Store!"), 3000);
                        c++;
                    } else if (message.toLowerCase().contains("prime")) {
                        delayedResponse.postDelayed(sendSms("Your order will arrive in 2 business days. Thank you for shopping with Clothes Store!"), 3000);
                        c++;
                    }
                    else if (message.toLowerCase().contains("speedy")) {
                        delayedResponse.postDelayed(sendSms("Your order will arrive in 1 business days. Thank you for shopping with Clothes Store!"), 3000);
                        c++;
                    }
                    else if (message.toLowerCase().contains("same day")) {
                        delayedResponse.postDelayed(sendSms("Your order will arrive today. Thank you for shopping with Clothes Store!"), 3000);
                        c++;
                    }
                    else delayedResponse.postDelayed(sendSms("I don't understand"), 3000);
                }
                if (c==7) {
                    state4 = false;
                    tv_state.setText("Order Complete");
                }
            }//state4

        }//onReceive
    }//MessageReceiver

    public Runnable sendSms(String m) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                tv_response.setText("Response: " + m);
                try {
                    smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, m, null, null);
                    Log.d("TAG", "Delayed reply");
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Message Failed", Toast.LENGTH_LONG).show();
                    Log.d("TAG", e.toString());
                    e.printStackTrace();
                }
            }
        };
        return runnable;
    }//sendSms
}//MainActivity