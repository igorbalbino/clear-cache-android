package br.com.igor.crearcache;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = " | MainActivity | ";
    private static final long  MEGABYTE = 1024L * 1024L;

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
                Toast.makeText(MainActivity.this, "O cache dos seus apps serão limpos...",
                        Toast.LENGTH_SHORT)
                .show();

                PackageManager pm = getPackageManager();
                List<ApplicationInfo> apps = pm.getInstalledApplications(0);
                clearCache(apps);
            }
        });
    }

    private void clearCache(List<ApplicationInfo> apps) {
        PackageManager pm = getPackageManager();
        double totalCache = 0;
        try {
            for (ApplicationInfo app : apps) {
                String appName = pm.getApplicationLabel(app).toString();
                String packageName = app.packageName;
                int versionCode = pm.getPackageInfo(packageName, 0).versionCode;

                byte[] bCacheSize = getCacheSize(app.packageName);
                double megabytes = (double) bCacheSize.length / 1000000.0;
                totalCache += megabytes;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        clear(app.packageName);
                    }
                });

                Log.d(TAG, "App name: " + appName + ", package name: " + packageName +
                        ", version code: " + versionCode + ", cache size(bytes): " + bCacheSize +
                        "\n");
            }
            Toast.makeText(this, "Cache dos apps apagado!" +
                "\nTotal de cache apagado: " + totalCache,
                    Toast.LENGTH_SHORT)
            .show();
            Log.i(TAG, "Total de cache apagado: " + totalCache);

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error PackageManager.NameNotFoundException: " + e);
            throw new RuntimeException(e);
        }
    }

    private byte[] getCacheSize(String packageName) {
        try {
            return getApplicationContext().getPackageManager()
                    .getApplicationInfo(packageName, PackageManager.GET_META_DATA).dataDir.getBytes();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Erro ao obter o tamanho do cache do aplicativo " + packageName, e);
            return "".getBytes();
        }
    }

    private void clear(String packageName) {
        try {
            getApplicationContext().deleteFile(packageName);
            Runtime runtime = Runtime.getRuntime();
            String command = "pm clear " + packageName;
            try {
                Process process = runtime.exec(command);
                process.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Erro ao limpar o cache do aplicativo " + packageName, e);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e(TAG, "Erro ao limpar o cache do aplicativo " + packageName, e);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao limpar o cache do aplicativo " + packageName, e);
        }
    }
}