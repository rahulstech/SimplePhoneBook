package rahulstech.android.namewithphotolist.database.dao;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import rahulstech.android.namewithphotolist.database.ContactDatabase;
import rahulstech.android.namewithphotolist.database.model.Contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ContactDaoTest {

    ContactDatabase db;
    ContactDao contactDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context,ContactDatabase.class)
                .allowMainThreadQueries()
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        addSampleContacts(db);
                    }
                })
                .build();
        contactDao = db.getContactDao();
    }

    @After
    public void closeDb() throws Exception {
        db.close();
    }

    @Test
    public void testAddContact() {
        Contact contact = new Contact(0,"Rahul Bagchi","photo_rahul.jpg","+918547996622");
        long id = contactDao.addContact(contact);
        assertTrue(id > 0);
    }

    @Test
    public void testGetLiveContactById() throws Exception {
        Handler handler = new Handler(Looper.getMainLooper());
        CountDownLatch latch = new CountDownLatch(1);
        LiveData<Contact> liveData = contactDao.getLiveContactById(3);
        Observer<Contact> observer = contact -> {
            Contact expected = new Contact(3,"Subham Poddar","photo_subham.jpg","+918107448855");
            assertEquals(expected,contact);
            latch.countDown();
        };
        handler.post(() -> liveData.observeForever(observer));
        latch.await(2,TimeUnit.SECONDS);
        handler.post(() -> liveData.removeObserver(observer));
    }

    @Test
    public void testGetAllContacts() throws Exception {
        Handler handler = new Handler(Looper.getMainLooper());
        CountDownLatch latch = new CountDownLatch(1);
        LiveData<List<Contact>> liveData = contactDao.getAllContacts();
        Observer<List<Contact>> observer = contacts -> {
            List<Contact> expected = Arrays.asList(
                    new Contact(1,"Arun Sinha","photo_arun.jpg","+917845889966"),
                    new Contact(2,"Barun Roy","photo_barun.jpg","+919856447722"),
                    new Contact(4,"Om Prakash","photo_om.jpg","+919569335577"),
                    new Contact(3,"Subham Poddar","photo_subham.jpg","+918107448855")
            );
            assertEquals(expected,contacts);
            latch.countDown();
        };
        handler.post(() -> liveData.observeForever(observer));
        latch.await(2, TimeUnit.SECONDS);
        handler.post(() -> liveData.removeObserver(observer));
    }

    @Test
    public void testEditContact() {
        Contact contact = new Contact(1,"Arun Biswas",null,"+917845889967");
        int changes = contactDao.editContact(contact);
        assertEquals(1,changes);
    }

    @Test
    public void testDeleteContact() {
        Contact contact = new Contact(2,"Barun Roy","photo_barun.jpg","+919856447722");
        int changes = contactDao.deleteContact(contact);
        assertEquals(1,changes);
    }

    @Test
    public void testDeleteContacts() {
        List<Contact> contacts = Arrays.asList(
                new Contact(1,"Arun Biswas","photo_arun.jpg","+917845889967"),
                new Contact(2,"Barun Roy","photo_barun.jpg","+919856447722")
        );
        int changes = contactDao.deleteContacts(contacts);
        assertEquals(2,changes);
    }

    private void addSampleContacts(SupportSQLiteDatabase db) {
        db.execSQL("INSERT INTO `contacts` (`id`,`displayName`,`displayPhoto`,`phoneNumber`) " +
                "VALUES (1,\"Arun Sinha\",\"photo_arun.jpg\",\"+917845889966\");");
        db.execSQL("INSERT INTO `contacts` (`id`,`displayName`,`displayPhoto`,`phoneNumber`) " +
                "VALUES (2,\"Barun Roy\",\"photo_barun.jpg\",\"+919856447722\");");
        db.execSQL("INSERT INTO `contacts` (`id`,`displayName`,`displayPhoto`,`phoneNumber`) " +
                "VALUES (3,\"Subham Poddar\",\"photo_subham.jpg\",\"+918107448855\");");
        db.execSQL("INSERT INTO `contacts` (`id`,`displayName`,`displayPhoto`,`phoneNumber`) " +
                "VALUES (4,\"Om Prakash\",\"photo_om.jpg\",\"+919569335577\");");
    }
}
