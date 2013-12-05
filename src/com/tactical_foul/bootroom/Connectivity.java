
package com.tactical_foul.bootroom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;

import android.util.Log;

/**
 * Utility class for making http(s) requests to a webserver: setting up a
 * connection, adding and formatting request/post parameters, and reading a
 * response.
 * 
 * @author scaldwell
 */
public class Connectivity {

    protected final static String CHARSET = "UTF-8";

    private final static String TAG = "BootroomConnectivity";

    // response keys
    public final static String SUCCESS_TOKEN = "0";

    // app wide cookie manager and store
    public final static CookieManager mCookieManager = new CookieManager();
    
    public static void init() {
        CookieHandler.setDefault(mCookieManager);
    }

    /**
     * @param conn
     * @param postQuery
     * @throws IOException
     */
    public static void sendQuery(HttpURLConnection conn, String postQuery)
            throws IOException {
        OutputStream os;
        os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,
                "UTF-8"));
        writer.write(postQuery);
        writer.close();
        os.close();
    }

    /**
     * @param conn
     * @param params
     * @throws IOException
     */
    public static void postContent(HttpURLConnection conn, String postQuery)
            throws IOException {
        // format a list of parameters into a post query and then write it to
        // the connection's output stream
        // String postQuery = Connectivity.getQuery(params);
        conn.setFixedLengthStreamingMode(postQuery.getBytes().length);
        Log.d(TAG, "posting " + postQuery);
        Connectivity.sendQuery(conn, postQuery);
    }

    /**
     * @param conn
     * @return
     * @throws IOException
     */
    public static String readResponse(HttpURLConnection conn)
            throws IOException {
        int response;
        String contentAsString;
        response = conn.getResponseCode();
        Log.d(TAG, "Response Code = " + response);
        InputStream is = conn.getInputStream();
        // Convert the InputStream into a string
        contentAsString = Connectivity.readIt(is);
        is.close();
        return contentAsString.trim();
    }

    // Reads an InputStream and converts it to a String.
    /**
     * @param stream
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static String readIt(InputStream stream)
            throws IOException, UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, CHARSET));
        StringBuffer sb = new StringBuffer();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        return sb.toString();
    }

    /**
     * formats a list of name, value pairs to be used in a http query
     * 
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getQuery(List<NameValuePair> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(pair.getName(), CHARSET));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), CHARSET));
        }
        return result.toString();
    }

    /**
     * @param url
     * @param method
     * @return
     * @throws IOException
     */
    public static HttpURLConnection openURL(String url, String method) throws IOException {
        Log.d(TAG, "Opening url: " + url);
        URL Url = new URL(url);
        HttpURLConnection conn;
        // open https connection if appropriate
        if (Url.getProtocol().equalsIgnoreCase("https")) {
            conn = (HttpsURLConnection) Url.openConnection();
        }
        else {
            conn = (HttpURLConnection) Url.openConnection();
        }
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setInstanceFollowRedirects(true);
        if (method.equalsIgnoreCase("POST")) {
            conn.setDoOutput(true);
        }
        if (url.endsWith("json"))
            conn.addRequestProperty("Content-Type", "application/json");
        return conn;
    }
}
