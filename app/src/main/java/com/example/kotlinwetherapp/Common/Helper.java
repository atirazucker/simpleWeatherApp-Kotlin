package com.example.kotlinwetherapp.Common;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Helper {
    static String stream = null;

    public Helper(){}

    public String getHTTPSData(String urlString)
    {
        try{
            URL url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK)
            {
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                 StringBuilder sb = new StringBuilder();
                 String line;
                 while((line = r.readLine()) != null)
                     sb.append(line);
                 stream = sb.toString();
                 urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }
}
