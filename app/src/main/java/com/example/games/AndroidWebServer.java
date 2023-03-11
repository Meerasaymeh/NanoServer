package com.example.games;

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
import java.util.Random;


//import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
//import fi.iki.elonen.NanoHTTPD.Response;

/*public class AndroidWebServer extends NanoHTTPD {
    private Context mContext;
    Response.Status HTTP_OK = Response.Status.OK;
    public static final String
            MIME_PLAINTEXT = "text/plain",
            MIME_HTML = "text/html",
            MIME_JS = "application/javascript",
            MIME_CSS = "text/css",
            MIME_PNG = "image/png",
            MIME_DEFAULT_BINARY = "application/octet-stream",
            MIME_XML = "text/xml";
    private static final int PORT = 8080;
    private static final String TAG = "HttpServer";
    private AssetManager assetManager = getAssets();


    public AndroidWebServer(int port) {
        super(port);
    }

    public AndroidWebServer(String hostname, int port) {
        super(hostname, port);
    }

    public AndroidWebServer(int port, Context mContext) {
        super(port);
        this.mContext = mContext;
    }



    /*public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
        Log.d("","SERVE ::  URI "+uri);
        final StringBuilder buf = new StringBuilder();
        for (Map.Entry<Object, Object> kv : header.entrySet())
            buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
        InputStream mbuffer = null;

        try {
            if(uri!=null){

                if(uri.contains(".js")){
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    return new NanoHTTPD.Response(HTTP_OK, MIME_JS, mbuffer);
                }else if(uri.contains(".css")){
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    return new NanoHTTPD.Response(HTTP_OK, MIME_CSS, mbuffer);

                }else if(uri.contains(".png")){
                    mbuffer = mContext.getAssets().open(uri.substring(1));
                    // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                    return new NanoHTTPD.Response(HTTP_OK, MIME_PNG, mbuffer);
                }else if (uri.contains("/mnt/sdcard")){
                    Log.d("","request for media on sdCard "+uri);
                    File request = new File(uri);
                    mbuffer = new FileInputStream(request);
                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
                    String mimeType = fileNameMap.getContentTypeFor(uri);

                    Response streamResponse = new Response(HTTP_OK, mimeType, mbuffer);
                    Random rnd = new Random();
                    String etag = Integer.toHexString( rnd.nextInt() );
                    streamResponse.addHeader( "ETag", etag);
                    streamResponse.addHeader( "Connection", "Keep-alive");


                    return streamResponse;
                }else{
                    mbuffer = mContext.getAssets().open("index.html");
                    return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, mbuffer);
                }
            }

        } catch (IOException e) {
            Log.d("","Error opening file"+uri.substring(1));
            e.printStackTrace();
        }

        return null;

    }*/


   // @Override
    /*public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {
        String answer="";
        //String mimeType = "";
        try {
            // Open file from SD Card
           // File root = Environment.getExternalStorageDirectory();
           // FileReader index = new FileReader(root.getAbsolutePath() + "index.html");
            //BufferedReader reader = new BufferedReader(index);
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("index.html"),"UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                answer += line;
            }
            reader.close();

        } catch(IOException ioe) {
            Log.w("Httpd", ioe.toString());
        }


        //return new NanoHTTPD.Response(HTTP_OK,NanoHTTPD.MIME_HTML,answer);
        return newFixedLengthResponse(answer);
    }*/



    /*@Override
    public Response serve(IHTTPSession session) {

        assetManager = mContext.getAssets();
        InputStream inputStream;
        Response response = newChunkedResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, null);
        String uri = session.getUri();

        try {
            if (session.getMethod() == Method.GET && uri != null) {
                if (uri.contains(".js")) {
                    inputStream = assetManager.open(uri.substring(1));
                    return newChunkedResponse(Response.Status.OK, MIME_JS, inputStream);
                } else if (uri.contains(".css")) {
                    inputStream = assetManager.open(uri.substring(1));
                    return newChunkedResponse(Response.Status.OK, MIME_CSS, inputStream);
                } else if (uri.contains(".png") || uri.contains(".jpg")) {
                    inputStream = assetManager.open(uri.substring(1));
                    return newChunkedResponse(Response.Status.OK, MIME_PNG, inputStream);
                } else if (uri.contains("/mnt/sdcard")) {
                    Log.d(TAG, "request for media on sdCard " + uri);
                    File file = new File(uri);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
                    String contentType = fileNameMap.getContentTypeFor(uri);
                    Response streamResponse = newChunkedResponse(Response.Status.OK, contentType, fileInputStream);
                    Random random = new Random();
                    String hexString = Integer.toHexString(random.nextInt());
                    streamResponse.addHeader("ETag", hexString);
                    streamResponse.addHeader("Connection", "Keep-alive");
                    return streamResponse;
                } else {
                    inputStream = assetManager.open("index.html");
                    return newChunkedResponse(Response.Status.OK, MIME_HTML, inputStream);
                }
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        return response;
        /*String mime_type = NanoHTTPD.MIME_HTML;
        Method method = session.getMethod();
        String uri = session.getUri();
        Map<String, String> files = new HashMap<>();
        InputStream mbuffer = null;
        if (uri.contains(".js")) {
            try {
                mbuffer = mContext.getAssets().open(uri.substring(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new NanoHTTPD.Response(HTTP_OK, MIME_JS, mbuffer);
        } else if (uri.contains(".css")) {
            try {
                mbuffer = mContext.getAssets().open(uri.substring(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new NanoHTTPD.Response(HTTP_OK, MIME_CSS, mbuffer);

        } else if (uri.contains(".png") || uri.contains(".jpg")) {
            try {
                mbuffer = mContext.getAssets().open(uri.substring(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
            return new NanoHTTPD.Response(HTTP_OK, MIME_PNG, mbuffer);
        } else if (uri.contains("/mnt/sdcard")) {
            Log.d("", "request for media on sdCard " + uri);
            File request = new File(uri);
            try {
                mbuffer = new FileInputStream(request);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String mimeType = fileNameMap.getContentTypeFor(uri);

            Response streamResponse = new Response(HTTP_OK,mimeType, mbuffer);
            Random rnd = new Random();
            String etag = Integer.toHexString(rnd.nextInt());
            streamResponse.addHeader("ETag", etag);
            streamResponse.addHeader("Connection", "Keep-alive");
        }*/
        ///////////////////////////////////////////////////
            //SharedPreferences prefs = OpenRAP.getContext().getSharedPreferences(MainActivity.mypreference, MODE_PRIVATE);
            //OpenRAP app = (OpenRAP) OpenRAP.getContext();
            //Storage storage = new Storage(OpenRAP.getContext());
        /*if(method.toString().equalsIgnoreCase("GET")){
            String path;
            if(uri.equals("/")){
                path="/index.html";
            }else{
                path = uri;
                try{
                    if(path.substring(path.length()-2, path.length()).equalsIgnoreCase("js")){
                        mime_type = String.valueOf(MIME_TYPES);
                    }else if(path.substring(path.length()-3, path.length()).equalsIgnoreCase("css")){
                        mime_type = String.valueOf(MIME_TYPES);
                    }
                }catch(Exception e){

                }
            }
        }*/

        /*String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n";
            msg += "<p>Your name: <input type='text' name='username'></p>\n";
            msg += "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
            //return newFixedLengthResponse( msg + "</body></html>\n" );*/

        //}
//}
