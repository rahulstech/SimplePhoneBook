package rahulstech.android.namewithphotolist.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import rahulstech.android.namewithphotolist.database.model.Contact;

@Dao
public interface ContactDao {

    @Insert
    long addContact(Contact contact);

    @Query("SELECT * FROM `contacts` WHERE `id` = :id")
    LiveData<Contact> getLiveContactById(long id);

    @Query("SELECT * FROM `contacts` WHERE `id` = :id")
    Contact getContactById(long id);

    @Query("SELECT * FROM `contacts` ORDER BY `displayName` COLLATE NOCASE ASC")
    LiveData<List<Contact>> getAllContacts();

    @Update
    int editContact(Contact contact);

    @Delete
    int deleteContact(Contact contact);

    @Transaction
    @Delete
    int deleteContacts(List<Contact> contacts);
}
