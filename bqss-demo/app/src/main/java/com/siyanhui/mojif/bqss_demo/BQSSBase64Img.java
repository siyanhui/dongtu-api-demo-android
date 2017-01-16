package com.siyanhui.mojif.bqss_demo;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.util.Base64.encodeToString;


/**
 * Created by fantasy on 17/1/4.
 * 图片URL转图片的base64信息
 */

public class BQSSBase64Img {
    public static String getImageStrFromUrl(String imgURL) {
        InputStream inputStream;
        ByteArrayOutputStream outputStream = null;
        try {
            URL url = new URL(imgURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.setInstanceFollowRedirects(true);
            inputStream = conn.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodeToString(outputStream.toByteArray(), 0, outputStream.toByteArray().length, Base64.DEFAULT);
    }
}
