package rahulstech.android.namewithphotolist.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import rahulstech.android.namewithphotolist.concurrent.TaskManager;
import rahulstech.android.namewithphotolist.database.model.Contact;
import rahulstech.android.namewithphotolist.util.Callback;

public class InputContactViewModel extends BaseContactViewModel {

    private static final int OPERATION_ADD_CONTACT = 1;
    private static final int OPERATION_EDIT_CONTACT = 2;
    private static final int OPERATION_FIND_CONTACT = 3;

    private TaskManager mDbTasks = new TaskManager();

    public InputContactViewModel(@NonNull Application application) {
        super(application);
    }

    public void setFindContactCallback(@NonNull Callback<Contact,Void> callback) {
        mDbTasks.addTaskCallback(OPERATION_FIND_CONTACT,callback);
    }

    public void findContactById(long id) {
        mDbTasks.execute(OPERATION_FIND_CONTACT,param -> {
            Contact contact = getContactDao().getContactById(id);
            return contact;
        });
    }

    public void setAddContactCallback(@NonNull Callback<Boolean,Void> callback) {
        mDbTasks.addTaskCallback(OPERATION_ADD_CONTACT,callback);
    }

    public void addContact(@NonNull Contact contact) {
        mDbTasks.execute(OPERATION_ADD_CONTACT,param -> 0 < getContactDao().addContact(contact));
    }

    public void setEditContactCallback(@NonNull Callback<Boolean,Void> callback) {
        mDbTasks.addTaskCallback(OPERATION_EDIT_CONTACT,callback);
    }

    public void editContact(@NonNull Contact contact) {
        mDbTasks.execute(OPERATION_EDIT_CONTACT,param -> 1 == getContactDao().editContact(contact));
    }
}
