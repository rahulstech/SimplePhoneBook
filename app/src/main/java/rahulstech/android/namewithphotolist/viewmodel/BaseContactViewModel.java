package rahulstech.android.namewithphotolist.viewmodel;

import android.app.Application;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import rahulstech.android.namewithphotolist.concurrent.AppExecutors;
import rahulstech.android.namewithphotolist.concurrent.TaskManager;
import rahulstech.android.namewithphotolist.database.ContactDatabase;
import rahulstech.android.namewithphotolist.database.dao.ContactDao;
import rahulstech.android.namewithphotolist.util.Callback;

public abstract class BaseContactViewModel extends AndroidViewModel {

    private ContactDatabase contactDb;
    private ContactDao contactDao;


    public BaseContactViewModel(@NonNull Application application) {
        super(application);

        contactDb = ContactDatabase.getInstance(application);
        contactDao = contactDb.getContactDao();
    }

    public ContactDatabase getContactDb() {
        return contactDb;
    }

    public ContactDao getContactDao() {
        return contactDao;
    }
}
