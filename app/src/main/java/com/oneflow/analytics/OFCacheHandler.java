package com.oneflow.analytics;

import android.content.Context;

import com.oneflow.analytics.sdkdb.OFOneFlowSHP;
import com.oneflow.analytics.utils.OFConstants;
import com.oneflow.analytics.utils.OFHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OFCacheHandler extends Thread {
    Context context;
    String tag = this.getClass().getName();

    public OFCacheHandler(Context context) {
        this.context = context;
        OFHelper.v(tag, "1Flow CacheHandler constructor called");
    }

    @Override
    public void run() {
        super.run();
        OFHelper.v(tag, "1Flow CacheHandler started");
        downloadFileNew();
    }


    public void downloadFileNew() {

        try {

            String apiUrl = "https://cdn.1flow.app/index-dev.js";//original url
            // Create a URL object with the API endpoint
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the input stream containing the data
            InputStream inputStream = connection.getInputStream();

            // Create a cache file
            File cacheFile = new File(context.getCacheDir(), OFConstants.CACHE_FILE_NAME);

            // Create an output stream to write data to the cache file
            try (OutputStream outputStream = new FileOutputStream(cacheFile)) {

                //Read the data from the input stream and write it to the cache file
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);

                }

                // Close the streams
                inputStream.close();
            }

            OFHelper.v(tag, "Data downloaded and cached successfully.");
            OFOneFlowSHP.getInstance(context).storeValue(OFConstants.SHP_CACHE_FILE_UPDATE_TIME, System.currentTimeMillis());

        } catch (IOException e) {
            OFHelper.e(tag, "Error downloading and caching data: " + e.getMessage());
        }
    }

    public void createCacheFile(String fileData) {
        OFHelper.v(tag, "1Flow CacheHandler creating cache File");
        // Get the cache directory
        File cacheDir = context.getCacheDir();

        // Create a file within the cache directory
        String fileName = OFConstants.CACHE_FILE_NAME;
        File cacheFile = new File(cacheDir, fileName);

        try {

            // Create the cache file
            if (cacheFile.createNewFile()) {
                OFHelper.v(tag, "1Flow CacheHandler cache file created");
                // File creation successful
                // You can perform read/write operations on the cache file here

                // Example: Write data to the cache file
                try (FileWriter writer = new FileWriter(cacheFile)) {
                    writer.write(fileData);
                    writer.flush();
                }
                OFHelper.v(tag, "1Flow CacheHandler cache file content written[" + cacheFile.length() + "]");
            } else {
                // File creation failed
                // Handle the error or try a different approach
                OFHelper.v(tag, "1Flow CacheHandler cache file not created");

            }
        } catch (IOException e) {
            // Handle the exception
        }
    }
}
