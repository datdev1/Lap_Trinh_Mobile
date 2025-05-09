package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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

    private static final String IMGUR_CLIENT_ID = "e9e6cd7ad4a937b"; // <-- Thay bằng của bạn
    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/image";

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    public void uploadImageToImgur(Context context, Uri imageUri, String title ,UploadCallback callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);

            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            OkHttpClient client = new OkHttpClient();


            RequestBody requestBody = new FormBody.Builder()
                    .add("image", base64Image)
                    .add("type", "base64")
                    .add("title", title) // Thay đổi title ở đây
                    .build();


            Request request = new Request.Builder()
                    .url(IMGUR_UPLOAD_URL)
                    .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure(new IOException("Upload failed: " + response));
                        return;
                    }

                    try {
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
