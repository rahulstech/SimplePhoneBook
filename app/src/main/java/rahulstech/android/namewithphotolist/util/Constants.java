package rahulstech.android.namewithphotolist.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import androidx.annotation.NonNull;

public class Constants {

    public static final String ACTION_CREATE = "action_create";

    public static final String ACTION_EDIT = "action_edit";

    public static final String EXTRA_CONTACT_ID = "contact_id";

    public static File getDisplayPhotoDirectory(@NonNull Context context) {
        return new File(context.getExternalFilesDir(null),"/DisplayPhotos");
    }
}
