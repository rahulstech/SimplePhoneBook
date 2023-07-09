package rahulstech.android.namewithphotolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rahulstech.android.namewithphotolist.R;
import rahulstech.android.namewithphotolist.adapter.ContactsAdapter;
import rahulstech.android.namewithphotolist.database.model.Contact;
import rahulstech.android.namewithphotolist.listener.ItemClickHelper;
import rahulstech.android.namewithphotolist.util.Constants;
import rahulstech.android.namewithphotolist.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mContactList;
    private ContactsAdapter mContactAdapter;
    private ItemClickHelper mItemClickHelper;
    private MainActivityViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactList = findViewById(R.id.contacts_list);
        mContactList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mContactAdapter = new ContactsAdapter(this);
        mContactList.setAdapter(mContactAdapter);
        mItemClickHelper = new ItemClickHelper(this,mContactList);
        mItemClickHelper.setOnItemClickListener((rv,itemView,adapterPosition)->{
            onClickContact(mContactAdapter.getCurrentList().get(adapterPosition));
        });
        mContactList.addOnItemTouchListener(mItemClickHelper);

        mViewModel = new ViewModelProvider(this,(ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(MainActivityViewModel.class);
        mViewModel.getAllContact().observe(this,contacts -> onAllContactsLoaded(contacts));
    }

    private void onAllContactsLoaded(@Nullable List<Contact> contacts) {
        Log.d(TAG,"contacted fetched size="+contacts.size());
        mContactAdapter.submitList(contacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_contact) {
            createContact();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onClickContact(@NonNull Contact contact) {
        Log.d(TAG,"contact clicked "+contact);
        Intent intent = new Intent(this,ViewContact.class);
        intent.putExtra(Constants.EXTRA_CONTACT_ID,contact.getId());
        startActivity(intent);
    }

    private void createContact() {
        Intent intent = new Intent(this,InputContact.class);
        intent.setAction(Constants.ACTION_CREATE);
        startActivity(intent);
    }
}