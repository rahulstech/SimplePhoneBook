package rahulstech.android.namewithphotolist.viewmodel;

import android.app.Application;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.namewithphotolist.concurrent.TaskManager;
import rahulstech.android.namewithphotolist.database.model.Contact;
import rahulstech.android.namewithphotolist.util.Callback;

public class ViewContactViewModel extends BaseContactViewModel {

    private static final int OPERATION_REMOVE_CONTACT = 1;

    private LiveData<Contact> mContactLiveData;

    private TaskManager mTaskManager = new TaskManager();

    public ViewContactViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Contact> findContactById(long id) {
        if (null == mContactLiveData) {
            mContactLiveData = getContactDao().getLiveContactById(id);
        }
        return mContactLiveData;
    }

    public void setRemoveContactListener(@NonNull Callback<Boolean,Void> callback) {
        Objects.requireNonNull(callback,"null == callback");
        mTaskManager.addTaskCallback(OPERATION_REMOVE_CONTACT,callback);
    }

    public void removeContact(@NonNull Contact contact) {
        mTaskManager.execute(OPERATION_REMOVE_CONTACT,param-> 1 == getContactDao().deleteContact(contact));
    }
}
