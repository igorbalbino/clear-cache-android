package br.com.igor.crearcache;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = " | MainActivity | ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBtnListener();
    }

    private void setBtnListener() {
        Button button = findViewById(R.id.btnClearCache);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "O cache dos seus pps estão sendo limpos...", Toast.LENGTH_SHORT)
                        .show();
                PackageManager pm = getPackageManager();
                List<ApplicationInfo> apps = pm.getInstalledApplications(0);
                clearCache(apps);
            }
        });
    }

    private void clearCache(List<ApplicationInfo> apps) {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> cloneApps = apps;
        try {
            for (ApplicationInfo app : apps) {
                // Retrieve information about the app, such as its name, package name, etc.
                String appName = pm.getApplicationLabel(app).toString();
                String packageName = app.packageName;
                int versionCode = pm.getPackageInfo(packageName, 0).versionCode;

                // Do something with the app information
                Log.d(TAG, "App name: " + appName + ", package name: " + packageName + ", version code: " + versionCode);
                // remove primeira posição da lista, pq o app ja foi limpo
                cloneApps.remove(0);
            }
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.clearApplicationUserData();
        } catch(Exception e) {
            Log.e(TAG, "Error: PackageManager.NameNotFoundException.\n" + e.getMessage());
            apps = null;
            clearCache(cloneApps);
        }
    }
}