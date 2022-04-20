package com.example.explorejournal;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerConnection {

    String host;
    JSONObject result;

    public ServerConnection(String host){
        this.host = host;
        this.result = null;
    }

    public JSONObject get(String request){
        result = null;
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                try {
                    URL url = new URL(host + "/" + request);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    // Handle response
                    int responsecode = conn.getResponseCode();
                    if(responsecode != 200){
                        throw new IllegalStateException();
                    }

                    Scanner in = new Scanner(conn.getInputStream());
                    if(!in.hasNext()) {
                        result = null;
                    } else {
                        result = new JSONObject(in.nextLine());
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS);
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
