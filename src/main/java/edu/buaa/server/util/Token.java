package edu.buaa.server.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Token {

    public static final String BASE_URL = "http://140.143.19.133:8001";
    public static final String SEND_TOKEN = BASE_URL + "/addToken";
    public static final String REMOVE_TOKEN = BASE_URL + "/removeToken";

    public static String getToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        return token;
    }

    public static boolean sendToken(String token, String uid) {
        HttpURLConnection conn = null;
        try {
            JSON send = new JSON();
            send.put("token", token);
            send.put("uid", uid);
            URL url = new URL(SEND_TOKEN);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST");
            conn.connect();

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(send.toString());
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            reader.close();
            conn.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) conn.disconnect();
            return false;
        }
    }

    public static boolean removeToken(String token) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(REMOVE_TOKEN);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("POST");
            conn.connect();
            JSON ret = new JSON();
            ret.put("token", token);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.writeBytes(ret.toString());
            out.flush();
            out.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            reader.close();
            conn.disconnect();
            return true;
        } catch (Exception e) {
            if (conn != null) conn.disconnect();
            return false;
        }
    }
}
