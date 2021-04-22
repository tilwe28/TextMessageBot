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
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tv_receivedMessage;

    MessageReceiver messageReceiver;
    SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_receivedMessage = findViewById(R.id.id_textView_receivedMessage);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE}, 0);
            return;
        } else {
            Log.d("TAG", "Permissions already Granted");
            messageReceiver = new MessageReceiver();
            IntentFilter messageIntentFilter = new IntentFilter();
            messageIntentFilter.addAction(android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(messageReceiver, messageIntentFilter);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("TAG", "Perimissions Result");
        if (permissions.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            messageReceiver = new MessageReceiver();
            IntentFilter messageIntentFilter = new IntentFilter();
            messageIntentFilter.addAction(android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            registerReceiver(messageReceiver, messageIntentFilter);
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "MessageReceiver");
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            String phoneNumber = "";
            for (int i=0; i<messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                phoneNumber = messages[i].getOriginatingAddress();
                Log.d("TAG", "From " + phoneNumber + " Message Body " + i + ": " + messages[i].getMessageBody());
            }

            try {
                smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, "Wassup", null, null);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Message Failed", Toast.LENGTH_LONG).show();
                Log.d("TAG", e.toString());
                e.printStackTrace();
            }

            Log.d("TAG", "Replied");
        }
    }
}