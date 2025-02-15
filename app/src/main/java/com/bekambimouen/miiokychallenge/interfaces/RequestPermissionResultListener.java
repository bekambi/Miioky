package com.bekambimouen.miiokychallenge.interfaces;

public interface RequestPermissionResultListener {
    void onRequestPermission(int requestCode, String[] permissions, int[] grantResults);
}

