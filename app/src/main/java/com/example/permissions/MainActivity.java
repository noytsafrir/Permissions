package com.example.permissions;

import static androidx.core.location.LocationManagerCompat.isLocationEnabled;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 123;
    private ShapeableImageView main_IMG_login_image;
    private MaterialTextView main_LBL_welcome_app;
    private MaterialButton main_BTN_grant_permissions;
    private AppCompatImageView main_IMG_V_location;
    private AppCompatImageView main_IMG_V_bluetooth;
    private AppCompatImageView main_IMG_V_battery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
    }

    private void initViews() {
        main_BTN_grant_permissions.setOnClickListener(v -> grantLoginPermission());
    }

    private void grantLoginPermission() {
        if(!checkLocationStatus()) {
            // Permission denied
        } else if(!isBluetoothEnabled()) {
            main_IMG_V_location.setImageResource(R.drawable.ic_green_v);
            // Permission denied
            Toast.makeText(MainActivity.this, "Please turn on your Bluetooth", Toast.LENGTH_LONG).show();
        } else if(!isBatteryLevelSufficient()) {
            main_IMG_V_bluetooth.setImageResource(R.drawable.ic_green_v);
            // Permission denied
            Toast.makeText(MainActivity.this, "Your battery is too low, try again after charge it", Toast.LENGTH_LONG).show();
        } else{
            // Permission granted

            main_IMG_V_bluetooth.setImageResource(R.drawable.ic_green_v);
            main_IMG_V_battery.setImageResource(R.drawable.ic_green_v);
            main_IMG_login_image.setImageResource(R.drawable.ic_open_lock);
            main_BTN_grant_permissions.setVisibility(View.GONE);
            main_LBL_welcome_app.setText("Welcome to the app");
        }
    }

    private boolean checkLocationStatus() {
        if(checkPermissionsStatus(this)) {
            if (isLocationEnabled(this)) {
                return true;
            } else {
                // Location services are not enabled
                Toast.makeText(MainActivity.this, "Please turn on your location", Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            // Permissions are not granted
            requestLocationPermissions();
                return false;
        }
    }

    //Return true if the app has the location permission
    private boolean checkPermissionsStatus(Context context) {
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        return hasFineLocationPermission || hasCoarseLocationPermission;
    }

    private boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            return mode != Settings.Secure.LOCATION_MODE_OFF;        }
    }


    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{"android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION"},
                REQUEST_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                if (!isLocationEnabled(this)) {
                    Toast.makeText(this, "Please turn on you location", Toast.LENGTH_SHORT).show();
                    // Proceed to the welcome screen or any other action
                }
            } else {
                Toast.makeText(this, "Location permissions are not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    private boolean isBatteryLevelSufficient() {
        // Create an IntentFilter to listen for the battery changed broadcast
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        // Register the broadcast receiver to receive the battery status
        Intent batteryStatus = registerReceiver(null, batteryFilter);
        // Get the battery level from the battery status
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        // Get the maximum battery scale from the battery status
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        // Calculate the battery percentage
        float batteryPct = level / (float) scale * 100;
        // Return true if the battery percentage is above 50%
        return batteryPct > 30;
    }

    private void findViews() {
        main_BTN_grant_permissions = (findViewById(R.id.main_BTN_grant_permissions));
        main_IMG_login_image = (findViewById(R.id.main_IMG_login_image));
        main_LBL_welcome_app = (findViewById(R.id.main_LBL_welcome_app));
        main_IMG_V_location = (findViewById(R.id.main_IMG_V_location));
        main_IMG_V_bluetooth = (findViewById(R.id.main_IMG_V_bluetooth));
        main_IMG_V_battery = (findViewById(R.id.main_IMG_V_battery));
    }
}