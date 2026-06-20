package com.mikatesoft.lobohuevos;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import androidx.core.content.FileProvider;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@CapacitorPlugin(name = "ApkShare")
public class ApkSharePlugin extends Plugin {
    @PluginMethod
    public void share(PluginCall call) {
        try {
            ApplicationInfo app = getContext()
                .getPackageManager()
                .getApplicationInfo(getContext().getPackageName(), 0);
            File src = new File(app.publicSourceDir);
            File cacheDir = new File(getContext().getCacheDir(), "shared");
            cacheDir.mkdirs();
            File dest = new File(cacheDir, "LoboRecogeHuevos.apk");
            FileInputStream in = new FileInputStream(src);
            FileOutputStream out = new FileOutputStream(dest);
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            Uri apkUri = FileProvider.getUriForFile(
                getContext(),
                getContext().getPackageName() + ".fileprovider",
                dest
            );
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_STREAM, apkUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getContext().startActivity(Intent.createChooser(intent, "Compartir APK"));
            call.resolve();
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }
}
