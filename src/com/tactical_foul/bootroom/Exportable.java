package com.tactical_foul.bootroom;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: scaldwell
 * Date: 9/26/13
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Exportable {

    private static final String BASE_URL = "http://beams.herokuapp.com";

    public void export() {
        ExportTask et = new ExportTask();
        et.execute();
    }

    /**
     * Append a subpath to the base url for exporting
     */
    protected String extendURL(String extension) {
        return BASE_URL + extension;
    }

    /**
     * Returns the url to send export data to as a String
     */
    protected abstract String exportURL();

    /**
     * Get the data to include in the export post request as a list of key, value pairs
     */
    protected abstract List<NameValuePair> getPostParams();

    /**
     * @return the string to be used as a log tag in the export task
     */
    protected abstract String logTag();

    private class ExportTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            try {
                HttpURLConnection conn = Connectivity.openURL(exportURL(), "POST");
                List<NameValuePair> postParams = getPostParams();
                Connectivity.postContent(conn, postParams);
                conn.connect();
                // read the response
                Connectivity.readResponse(conn);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            Log.d(logTag(), "finished export");
        }
    }
}
