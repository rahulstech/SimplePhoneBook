package rahulstech.android.namewithphotolist.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import rahulstech.android.namewithphotolist.database.dao.ContactDao;
import rahulstech.android.namewithphotolist.database.model.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactDatabase extends RoomDatabase {


    private static ContactDatabase mInstance;

    public static ContactDatabase getInstance(@NonNull Context context) {
        if (null == mInstance) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(),ContactDatabase.class,"contacts.db3")
                    .build();
        }
        return mInstance;
    }


    public abstract ContactDao getContactDao();
}
