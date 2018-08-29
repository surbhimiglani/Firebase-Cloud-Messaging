package com.example.surbhimiglani.googlecloudmessagingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {

    Button button;
    static String emailExist;
    String token;
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://fcmproject.somee.com/app.asmx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button) findViewById(R.id.button2);

        AsyncEmailVerify asyncEmailVerify=new AsyncEmailVerify(token);
        asyncEmailVerify.execute();

        SharedPreferences sf=getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        token=sf.getString(getString(R.string.FCM_TOKEN), FirebaseInstanceId.getInstance().getToken());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toast(token);
              AsyncEmailVerify asyncEmailVerify=new AsyncEmailVerify(token);
                asyncEmailVerify.execute();
            }
        });
    }

    private class AsyncEmailVerify extends AsyncTask<String, Void, Void> {

        String emailText2;

        public AsyncEmailVerify(String emailText2) {
            this.emailText2 = emailText2;
        }

        @Override
        protected Void doInBackground(String... params) {

            invokeEmailVerfy(emailText2,"InsertDeviceId","http://tempuri.org/InsertDeviceId");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(isNetworkAvailable()) {
                toast(emailExist + "...." + emailText2);
            }
            else
            {
                toast("Please check your internet connection");
            }
        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public void toast(String text){
        Toast.makeText(getApplicationContext(), text,Toast.LENGTH_SHORT ).show();
    }

    public static String invokeEmailVerfy(String email,String name, String webMethName) {

        SoapObject request = new SoapObject(NAMESPACE, name);
        PropertyInfo sayHelloPI = new PropertyInfo();
        sayHelloPI.setName("dId");
        sayHelloPI.setValue(email);
        sayHelloPI.setType(String.class);
        request.addProperty(sayHelloPI);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try {
            androidHttpTransport.call(webMethName, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            emailExist=response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            emailExist=""+e;
        }
        return emailExist;
    }

    private boolean isNetworkAvailable() {                           // check if the network is available
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }



}
