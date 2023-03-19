package fr.opaleuhc.opalevelocity.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class HTTPUtils {

    public static String baseUrl = "http://opaleapi:8080/account/";
    public static String localUrl = "http://163.5.143.40:8082/account/";
    public static String API_KEY = "default";

    public static void setApiKey(String key) {
        API_KEY = key;
    }

    public static String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    public static String buildUrlWithParams(String url, HashMap<String, String> params) {
        params.put("api-key", API_KEY);
        StringBuilder urlBuilder = new StringBuilder(url);
        if (params.size() > 0) {
            urlBuilder.append("?");
            for (String key : params.keySet()) {
                urlBuilder.append(key).append("=").append(encodeValue(params.get(key))).append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        return urlBuilder.toString();
    }

    public static String makeGetRequest(String url, HashMap<String, String> params) {
        try {
            URL link = new URL(buildUrlWithParams(url, params));
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("GET");

            System.out.println("Response Code : " + conn.getResponseCode());
            if (conn.getResponseCode() > 201) {
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            StringBuilder response = new StringBuilder();
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void makePostRequestAsync(String url, HashMap<String, String> params) {
        CompletableFuture.runAsync(() -> makePostRequest(url, params));
    }

    public static void makePostRequest(String url, HashMap<String, String> params) {
        try {
            URL link = new URL(buildUrlWithParams(url, params));
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("POST");

            if (conn.getResponseCode() > 201) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makePutRequestAsync(String url, HashMap<String, String> params) {
        CompletableFuture.runAsync(() -> makePutRequest(url, params));
    }

    public static void makePutRequest(String url, HashMap<String, String> params) {
        try {
            URL link = new URL(buildUrlWithParams(url, params));
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("PUT");

            if (conn.getResponseCode() > 201) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeDeleteRequestAsync(String url, HashMap<String, String> params) {
        CompletableFuture.runAsync(() -> makeDeleteRequest(url, params));
    }

    public static void makeDeleteRequest(String url, HashMap<String, String> params) {
        try {
            URL link = new URL(buildUrlWithParams(url, params));
            HttpURLConnection conn = (HttpURLConnection) link.openConnection();
            conn.setRequestMethod("DELETE");

            if (conn.getResponseCode() > 201) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String makeGetRequestAsync(String url, HashMap<String, String> params) {
        return CompletableFuture.supplyAsync(() -> makeGetRequest(url, params)).join();
    }

}
