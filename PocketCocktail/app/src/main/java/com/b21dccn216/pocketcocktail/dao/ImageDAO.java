package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageDAO {
    public static String ImageDaoFolderForDrink = "drink";
    public static String ImageDaoFolderForAvatar = "avatar";
    public static String ImageDaoFolderForIngredient = "ingredient";
    public static String ImageDaoFolderForCategory = "ingredient";

    private static final String IMGUR_CLIENT_ID = "af84fe7b3e738e6";
    private static final String IMGUR_CLIENT_SECRET = "1e5be60ad5fc5ed7e2e26f1f6970a69c24ce3d9a"; // Add your client secret here
    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";
    private static final String IMGUR_DELETE_URL = "https://api.imgur.com/3/image/";
    private static final String IMGUR_TOKEN_URL = "https://api.imgur.com/oauth2/token";
    
    private String accessToken;
    private final OkHttpClient client;

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface AuthCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public ImageDAO() {
        client = new OkHttpClient();
    }

    public void authenticate(String refreshToken, AuthCallback callback) {
        RequestBody requestBody = new FormBody.Builder()
                .add("refresh_token", refreshToken)
                .add("client_id", IMGUR_CLIENT_ID)
                .add("client_secret", IMGUR_CLIENT_SECRET)
                .add("grant_type", "refresh_token")
                .build();

        Request request = new Request.Builder()
                .url(IMGUR_TOKEN_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("Authentication failed: " + response));
                    return;
                }

                try {
                    assert response.body() != null;
                    JSONObject json = new JSONObject(response.body().string());
                    accessToken = json.getString("access_token");
                    callback.onSuccess();
                } catch (JSONException e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    public void uploadImageToImgur(Context context, Uri imageUri, String title, UploadCallback callback) {
        if (accessToken != null) {
            uploadImageAuthenticated(context, imageUri, title, callback);
        } else {
            uploadImageAnonymous(context, imageUri, title, callback);
        }
    }

    public void uploadImageAuthenticated(Context context, Uri imageUri, String title, UploadCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            assert inputStream != null;
            byte[] imageBytes = getBytes(inputStream);
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            RequestBody requestBody = new FormBody.Builder()
                    .add("image", base64Image)
                    .add("type", "base64")
                    .add("title", title)
                    .build();

            Request request = new Request.Builder()
                    .url(IMGUR_UPLOAD_URL)
                    .header("Authorization", "Bearer " + accessToken)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Upload failed: " + response));
                        return;
                    }

                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().string());
                        String link = json.getJSONObject("data").getString("link");
                        callback.onSuccess(link);
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void uploadImageAnonymous(Context context, Uri imageUri, String title, UploadCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            assert inputStream != null;
            byte[] imageBytes = getBytes(inputStream);
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            RequestBody requestBody = new FormBody.Builder()
                    .add("image", base64Image)
                    .add("type", "base64")
                    .add("title", title)
                    .build();

            Request request = new Request.Builder()
                    .url(IMGUR_UPLOAD_URL)
                    .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Upload failed: " + response));
                        return;
                    }

                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().string());
                        String link = json.getJSONObject("data").getString("link");
                        callback.onSuccess(link);
                    } catch (JSONException e) {
                        callback.onFailure(e);
                    }
                }
            });

        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void deleteImageFromImgur(String imgurUrl, DeleteCallback callback) {
        if (accessToken == null) {
            callback.onFailure(new IllegalStateException("Not authenticated. Call authenticate() first."));
            return;
        }

        String imageId = extractImageIdFromUrl(imgurUrl);
        if (imageId == null || imageId.isEmpty()) {
            callback.onFailure(new IllegalArgumentException("Invalid Imgur URL"));
            return;
        }

        Request request = new Request.Builder()
                .url(IMGUR_DELETE_URL + imageId)
                .header("Authorization", "Bearer " + accessToken)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ImageDAO", "Failed to delete image: " + e.getMessage());
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String error = "Delete failed: " + response;
                    Log.e("ImageDAO", error);
                    callback.onFailure(new IOException(error));
                    return;
                }
                callback.onSuccess();
            }
        });
    }

    private String extractImageIdFromUrl(String imgurUrl) {
        String[] parts = imgurUrl.split("/");
        String lastPart = parts[parts.length - 1];
        return lastPart.split("\\.")[0];
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;

        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}
