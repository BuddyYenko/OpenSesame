package com.example.s215093585.opensesame;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.s215093585.opensesame.MySingleton;
import com.example.s215093585.opensesame.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnScan;
    String qrID;
    String url = "https://evolve-ict.co.za/fetch.php";
    AlertDialog.Builder builder;

    //qr code scanner object
    private IntentIntegrator qrScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnScan = (Button) findViewById(R.id.btnScan);
        //initializing scan object
        qrScanner = new IntentIntegrator(this);
        builder = new AlertDialog.Builder(MainActivity.this);

        btnScan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScanner.addExtra("SCAN_MODE", "QR_CODE_MODE");
        qrScanner.setCameraId(0);
        qrScanner.setPrompt("Please align your QR code with the tablet’s camera");

        qrScanner.initiateScan();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            } else {
                try
                {
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    qrID = obj.getString("qrID");
                    Toast.makeText(this, qrID, Toast.LENGTH_SHORT).show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");

                                        builder.setTitle("iHub Response");
                                        builder.setMessage(message);
                                        DisplayAlert(code);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("qrID", qrID);

                            return params;
                        }
                    };
                    MySingleton.getInstance(MainActivity.this).addToRequestque(stringRequest);


                }
                catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void DisplayAlert(String code) {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= 11) {
                    recreate();
                } else {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}