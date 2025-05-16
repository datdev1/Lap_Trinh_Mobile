package com.b21dccn216.pocketcocktail.dao;

import android.app.Application;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class PocketCocktailApplication extends Application {
    private static final String CLOUD_NAME = "pocketcocktail";
    private static final String API_KEY = "882651454616456";
    private static final String API_SECRET = "Jb8hb3lD7hWwKsZjTFBc3ZyEdxo";
    private static Cloudinary cloudinary;

    @Override
    public void onCreate() {
        super.onCreate();
        initCloudinary();
    }

    private void initCloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        config.put("secure", "true");

        // Initialize Cloudinary
        cloudinary = new Cloudinary(config);

        // Initialize MediaManager
        MediaManager.init(this, config);
    }

    public static Cloudinary getCloudinary() {
        return cloudinary;
    }
} 