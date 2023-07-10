package rahulstech.android.namewithphotolist.provider;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

public class ContactImageProvider extends FileProvider {

    private static final String TAG = "CImgProvider";

    public static final String AUTHORITY = "rahulstech.android.namewithphotolist.contactimageprovider";

    public static File getTemporaryDirectory(@NonNull Context context) {
        File dir = new File(context.getExternalFilesDir(null),"intermediate");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static File getDisplayPhotosDirectory(@NonNull Context context) {
        File dir = new File(context.getExternalFilesDir(null),"DisplayPhotos");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    @Nullable
    public static File createTemporaryFile(@NonNull File dir) {
        String name = String.valueOf(System.currentTimeMillis());
        File file = new File(dir,name);
        try {
            if (!file.exists()) file.createNewFile();
            return file;
        }
        catch (IOException ex) {
            Log.e(TAG,"createTemporaryFile",ex);
        }
        return null;
    }

    @Nullable
    public static File createDisplayPhotoFile(@NonNull File dir, @NonNull String name) {
        File file = new File(dir,name);
        try {
            if (!file.exists()) file.createNewFile();
            return file;
        }
        catch (IOException ex) {
            Log.e(TAG,"createDisplayPhotoFile",ex);
        }
        return null;
    }

    @Nullable
    public static Uri createTemporaryUri(@NonNull Context context) {
        File dir = getTemporaryDirectory(context);
        File file = createTemporaryFile(dir);
        if (null == file) return null;
        return getUriForFile(context,AUTHORITY,file);
    }

    @Nullable
    public static Uri createDisplayPhotoUri(@NonNull Context context, @NonNull String name) {
        File dir = getDisplayPhotosDirectory(context);
        File file = createDisplayPhotoFile(dir,name);
        if (null == file) return null;
        return getUriForFile(context,AUTHORITY,file);
    }

    public ContactImageProvider() {}
}
