package com.b21dccn216.pocketcocktail.dao;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;

public class ImageDAOCloudinary {
    public static String ImageDaoFolderForDrink = "drink";
    public static String ImageDaoFolderForAvatar = "avatar";
    public static String ImageDaoFolderForIngredient = "ingredient";
    public static String ImageDaoFolderForCategory = "category";

    private Context context;
    private Cloudinary cloudinary;

    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public ImageDAOCloudinary(Context context) {
        this.context = context;
        this.cloudinary = PocketCocktailApplication.getCloudinary();
    }

    public void uploadImage(Context context, Uri imageUri, String folder, String title, ImageUploadCallback callback) {
        try {
            String requestId = MediaManager.get()
                    .upload(imageUri)
                    .option("folder", folder)
                    .option("public_id", title)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            Log.d("Cloudinary", "Upload started");
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                            Log.d("Cloudinary", "Upload progress: " + bytes + "/" + totalBytes);
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String url = (String) resultData.get("secure_url");
                            callback.onSuccess(url);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            callback.onFailure(new Exception(error.getDescription()));
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                            callback.onFailure(new Exception(error.getDescription()));
                        }
                    })
                    .dispatch();
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void deleteImage(String publicId, DeleteCallback callback) {
        new Thread(() -> {
            try {
                Log.d("Cloudinary", "Attempting to delete image with public_id: " + publicId);
                Map<String, String> options = new HashMap<>();
                Map result = cloudinary.uploader().destroy(publicId, options);
                
                if (result != null) {
                    Log.d("Cloudinary", "Delete result: " + result.toString());
                    if (result.containsKey("result") && result.get("result").equals("ok")) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> callback.onSuccess());
                    } else {
                        String errorMsg = result.containsKey("error") ? result.get("error").toString() : "Unknown error";
                        Log.e("Cloudinary", "Delete failed with error: " + errorMsg);
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> 
                            callback.onFailure(new Exception(errorMsg)));
                    }
                } else {
                    Log.e("Cloudinary", "Delete failed: Result is null");
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> 
                        callback.onFailure(new Exception("Delete failed: Result is null")));
                }
            } catch (Exception e) {
                Log.e("Cloudinary", "Delete failed with exception: " + e.getMessage(), e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> callback.onFailure(e));
            }
        }).start();
    }

    public void deleteImageByUrl(String imageUrl, DeleteCallback callback) {
        try {
            Log.d("Cloudinary", "Attempting to delete image with URL: " + imageUrl);
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId == null) {
                Log.e("Cloudinary", "Invalid Cloudinary URL: " + imageUrl);
                callback.onFailure(new IllegalArgumentException("Invalid Cloudinary URL: " + imageUrl));
                return;
            }
            Log.d("Cloudinary", "Extracted public_id: " + publicId);
            deleteImage(publicId, callback);
        } catch (Exception e) {
            Log.e("Cloudinary", "Error in deleteImageByUrl: " + e.getMessage(), e);
            callback.onFailure(e);
        }
    }

    private String extractPublicIdFromUrl(String cloudinaryUrl) {
        try {
            Log.d("Cloudinary", "Extracting public_id from URL: " + cloudinaryUrl);
            // Example URL: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/folder/image.jpg
            // or: https://res.cloudinary.com/cloud_name/image/upload/folder/image.jpg
            String[] parts = cloudinaryUrl.split("/upload/");
            if (parts.length < 2) {
                Log.e("Cloudinary", "Invalid URL format: " + cloudinaryUrl);
                return null;
            }

            // Get the part after /upload/
            String path = parts[1];
            Log.d("Cloudinary", "Path after /upload/: " + path);
            
            // Remove version if exists (v1234567890/)
            if (path.startsWith("v")) {
                int firstSlash = path.indexOf("/");
                if (firstSlash > 0) {
                    path = path.substring(firstSlash + 1);
                }
                Log.d("Cloudinary", "Path after removing version: " + path);
            }

            // Remove file extension
            int lastDotIndex = path.lastIndexOf(".");
            if (lastDotIndex > 0) {
                path = path.substring(0, lastDotIndex);
                Log.d("Cloudinary", "Final public_id: " + path);
            }

            return path;
        } catch (Exception e) {
            Log.e("Cloudinary", "Error extracting public_id from URL: " + e.getMessage(), e);
            return null;
        }
    }
}
