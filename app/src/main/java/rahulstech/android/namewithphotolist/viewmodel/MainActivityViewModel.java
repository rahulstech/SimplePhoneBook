package rahulstech.android.namewithphotolist.viewmodel;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import rahulstech.android.namewithphotolist.database.ContactDatabase;
import rahulstech.android.namewithphotolist.database.dao.ContactDao;
import rahulstech.android.namewithphotolist.database.model.Contact;

public class MainActivityViewModel extends BaseContactViewModel {

    private LiveData<List<Contact>> mContactsLiveData;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Contact>> getAllContact() {
        if (null == mContactsLiveData) {
            mContactsLiveData = getContactDao().getAllContacts();
        }
        return mContactsLiveData;
    }

    public void removeContacts(List<Contact> contacts) {
        // TODO: implement removeContacts
    }
}
