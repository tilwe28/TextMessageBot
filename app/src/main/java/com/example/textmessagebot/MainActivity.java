package com.example.textmessagebot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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

    String phoneNumber = "";
    String message = "";
    boolean state1 = true, state2 = false, state3 = false, state4 = false;

    MessageReceiver messageReceiver;
    SmsManager smsManager;

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
        }
    }//onRequestPermissionResult

    public class MessageReceiver extends BroadcastReceiver {
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

            if (state1) {
                tv_state.setText("State: 1");
                tv_response.setText("Response: Hello there!");
                Handler delayedResponse = new Handler();
                delayedResponse.postDelayed(sendSms("Hello there!"), 5000);
            }

        }//onReceive
    }//MessageReceiver

    public Runnable sendSms(String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
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