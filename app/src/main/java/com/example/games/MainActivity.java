package com.example.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;


import fi.iki.elonen.NanoHTTPD;



public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_PORT = 8080;

    private AndroiddWebServer androidWebServer;
    private BroadcastReceiver broadcastReceiverNetworkState;
    private static boolean isStarted = false;


    private CoordinatorLayout coordinatorLayout;
    private EditText editTextPort;
    private FloatingActionButton floatingActionButtonOnOff;
    private View textViewMessage;
    private TextView textViewIpAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        editTextPort = (EditText) findViewById(R.id.editTextPort);
        textViewMessage = findViewById(R.id.textViewMessage);
        textViewIpAccess = (TextView) findViewById(R.id.textViewIpAccess);
        setIpAccess();
        floatingActionButtonOnOff = (FloatingActionButton) findViewById(R.id.floatingActionButtonOnOff);
        floatingActionButtonOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectedInWifi()) {
                    if (!isStarted && startAndroiddWebServer()) {
                        isStarted = true;
                        textViewMessage.setVisibility(View.VISIBLE);
                        floatingActionButtonOnOff.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorGreen));
                        editTextPort.setEnabled(false);
                    } else if (stopAndroiddWebServer()) {
                        isStarted = false;
                        textViewMessage.setVisibility(View.INVISIBLE);
                        floatingActionButtonOnOff.setBackgroundTintList(ContextCompat.getColorStateList(MainActivity.this, R.color.colorRed));
                        editTextPort.setEnabled(true);
                    }
                } else {
                    Snackbar.make(coordinatorLayout, getString(R.string.wifi_message), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        // INIT BROADCAST RECEIVER TO LISTEN NETWORK STATE CHANGED
        initBroadcastReceiverNetworkStateChanged();
       // androidWebServer = new AndroidWebServer(port);
       // androidWebServer.start();
    }

    //region Start And Stop AndroidWebServer
    private boolean startAndroiddWebServer() {
        if (!isStarted) {
            int port = getPortFromEditText();
            try {
                if (port == 0) {
                    throw new Exception();
                }
                androidWebServer = new AndroiddWebServer(port);
                androidWebServer.start();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(coordinatorLayout, "The PORT " + port + " doesn't work, please change it between 1000 and 9999.", Snackbar.LENGTH_LONG).show();
            }
        }
        return false;
    }

    private boolean stopAndroiddWebServer() {
        if (isStarted && androidWebServer != null) {
            androidWebServer.stop();
            return true;
        }
        return false;
    }
    //endregion


    //region Private utils Method
    private void setIpAccess() {
        textViewIpAccess.setText(getIpAccess());
    }

    private void initBroadcastReceiverNetworkStateChanged() {
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        broadcastReceiverNetworkState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setIpAccess();
            }
        };
        super.registerReceiver(broadcastReceiverNetworkState, filters);
    }

    private String getIpAccess() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":";
    }

    private int getPortFromEditText() {
        String valueEditText = editTextPort.getText().toString();
        return (valueEditText.length() > 0) ? Integer.parseInt(valueEditText) : DEFAULT_PORT;
    }

    public boolean isConnectedInWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiManager.isWifiEnabled() && networkInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }
    //endregion


    public boolean onKeyDown(int keyCode, KeyEvent evt) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isStarted) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.warning)
                        .setMessage(R.string.dialog_exit_message)
                        .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.cancel), null)
                        .show();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAndroiddWebServer();
        isStarted = false;
        if (broadcastReceiverNetworkState != null) {
            unregisterReceiver(broadcastReceiverNetworkState);
        }
    }



    public class AndroiddWebServer extends NanoHTTPD {

        /*private String indexString = null;

        {
            try {
                indexString = readFile("in/index.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        private Context mContext;
        Response.Status HTTP_OK = Response.Status.OK;
        public static final String
                MIME_PLAINTEXT = "text/plain",
                MIME_HTML = "text/html",
                MIME_JS = "application/javascript",
                MIME_CSS = "text/css",
                MIME_PNG = "image/png",
                MIME_DEFAULT_BINARY = "application/octet-stream",
                MIME_JSON = "application/json",
                MIME_JAVASCRIPT = "text/javascript",
                MIME_XML = "text/xml";
        private static final int PORT = 8080;
        private static final String TAG = "HttpServer";
        private AssetManager assetManager = getAssets();


        public AndroiddWebServer(int port) {
            super(port);
        }

        /*public AndroiddWebServer(String hostname, int port) {
            super(hostname, port);
        }

        public AndroiddWebServer(int port, Context mContext) {
            super(port);
            this.mContext = mContext;
        }*/

        ////////////////////////////////// Test from storage directory ///////////////////////////////

        /*@Override
        public Response serve(IHTTPSession session) {
            File rootDir = new File( Environment.getExternalStorageDirectory() +  File.separator  + session.getUri());
            File[] files2 = rootDir.listFiles();
            String answer = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>sdcard0 - TECNO P5 - WiFi File Transfer Pro</title>";
            for (File detailsOfFiles : files2) {
                if(detailsOfFiles.isFile()){
                    answer += detailsOfFiles.getAbsolutePath() + "<br>";
                }else{
                    answer += "<a href=\"" + detailsOfFiles.getAbsolutePath()
                            + "\" alt = \"\">" + detailsOfFiles.getAbsolutePath()
                            + "</a><br>";
                }
            }
            answer += "</head></html>";
            return newFixedLengthResponse(answer);
        }*/
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////


////////////////////////////////////Reading files/////////////////////////////////////////////
    public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        Log.d("", "SERVE ::  URI " + uri);
        final StringBuilder buf = new StringBuilder();
        for (Map.Entry<Object, Object> kv : header.entrySet())
            buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
        InputStream mbuffer = null;
        long totalBytes = 0;

        try {
            if (uri != null) {

                if (uri.contains(".js")) {
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    return new NanoHTTPD.Response(HTTP_OK, MIME_JS, mbuffer, totalBytes);
                } else if (uri.contains(".css")) {
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    return new NanoHTTPD.Response(HTTP_OK, MIME_CSS, mbuffer, totalBytes);

                } else if (uri.contains(".png")) {
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                    return new NanoHTTPD.Response(HTTP_OK, MIME_PNG, mbuffer, totalBytes);

                } else if (uri.contains(".json")) {
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                    return new NanoHTTPD.Response(HTTP_OK, MIME_JSON, mbuffer, totalBytes);

                } else if (uri.contains(".js")) {
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                    return new NanoHTTPD.Response(HTTP_OK, MIME_JAVASCRIPT, mbuffer, totalBytes);
                } else if (uri.contains("/mnt/sdcard")) {
                    Log.d("", "request for media on sdCard " + uri);
                    File request = new File(uri);
                    mbuffer = new FileInputStream(request);
                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
                    String mimeType = fileNameMap.getContentTypeFor(uri);

                    Response streamResponse = new Response(HTTP_OK, mimeType, mbuffer, totalBytes);
                    Random rnd = new Random();
                    String etag = Integer.toHexString(rnd.nextInt());
                    streamResponse.addHeader("ETag", etag);
                    streamResponse.addHeader("Connection", "Keep-alive");


                    return streamResponse;
                } else {
                    mbuffer = mContext.getAssets().open("index.html");
                    return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, mbuffer, totalBytes);
                }
            }

        } catch (IOException e) {
            Log.d("", "Error opening file" + uri.substring(1));
            e.printStackTrace();
        }

        return null;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////



        /*@Override
        public Response serve(IHTTPSession session) {
            return newFixedLengthResponse(indexString);
        }
        private String readFile(String path) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(path));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                return sb.toString();
            } finally {
                br.close();
            }
        }*/


        ///////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////

        /*@Override
        public Response serve(IHTTPSession session) {
            String answer="";
            String mime_type = NanoHTTPD.MIME_HTML;
            InputStream inputStream;

            Log.d("Http ","Server is running");

            try {

                //inputStream = getAssets().open("index.html");
                //int size = inputStream.available();
                //byte[] buffer = new byte[size];
                //inputStream.read(buffer);
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("index.html")));
                StringBuilder sb = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                    answer += line;
                }
                reader.close();

                reader = new BufferedReader(new InputStreamReader(getAssets().open("style.css")));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                    answer += line;
                }
                reader.close();

                reader = new BufferedReader(new InputStreamReader(getAssets().open("data.json")));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                    answer += line;
                }
                reader.close();

                reader = new BufferedReader(new InputStreamReader(getAssets().open("offline.json")));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                    answer += line;
                }
                reader.close();

                reader = new BufferedReader(new InputStreamReader(getAssets().open("sw.js")));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                    answer += line;
                }
                reader.close();

                reader = new BufferedReader(new InputStreamReader(getAssets().open("workermain.js")));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                    answer += line;
                }
                reader.close();

            } catch(IOException ioe) {
                Log.w("Httpd", ioe.toString());
            }
            //return new NanoHTTPD.Response(HTTP_OK,NanoHTTPD.MIME_HTML,answer);
            return newFixedLengthResponse(answer);
        }*/

    }

}