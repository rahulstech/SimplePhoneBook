package rahulstech.android.namewithphotolist.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import jahirfiquitiva.libs.textdrawable.TextDrawable;
import rahulstech.android.namewithphotolist.R;
import rahulstech.android.namewithphotolist.database.model.Contact;
import rahulstech.android.namewithphotolist.util.ColorGenerator;
import rahulstech.android.namewithphotolist.util.Constants;
import rahulstech.android.namewithphotolist.viewmodel.ViewContactViewModel;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewContact extends AppCompatActivity {

    private static final String TAG = "ViewContact";

    private ViewContactViewModel mViewModel;

    private Contact mContact;

    private ImageView mDisplayPhoto;
    private TextView mDisplayName;
    private TextView mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getIntent().hasExtra(Constants.EXTRA_CONTACT_ID)) {
            Log.i(TAG,"ViewContact called but no contact_id set as intent extra");
            Toast.makeText(this, "unable to load contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_view_contact);
        mDisplayPhoto = findViewById(R.id.display_photo);
        mDisplayName = findViewById(R.id.display_name);
        mPhoneNumber = findViewById(R.id.phone_number);

        mViewModel = new ViewModelProvider(this,(ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ViewContactViewModel.class);
        long contactId = getIntent().getExtras().getLong(Constants.EXTRA_CONTACT_ID);
        mViewModel.findContactById(contactId).observe(this,this::onContactLoaded);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.setRemoveContactListener(removed -> {
            onContactRemoved(removed);
            return null;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_contact_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_contact) {
            editContact();
            return true;
        }
        else if (id == R.id.delete_contact) {
            deleteContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onContactLoaded(@Nullable Contact contact) {
        if (null == contact) {
            Toast.makeText(this, "unable to load contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.mContact = contact;
        Uri dpUri = contact.getDpUri();
        if (null == dpUri) {
            String displayName = contact.getDisplayName();
            String text = displayName.substring(0, 1);
            int color = ColorGenerator.MATERIAL.getColor(displayName);
            Drawable placeholder = new TextDrawable.Builder().build(text, color);
            contact.setPlaceholder(placeholder);
            mDisplayPhoto.setImageDrawable(placeholder);
        }
        else {
            mDisplayPhoto.setImageURI(dpUri);
        }
        mDisplayName.setText(contact.getDisplayName());
        mPhoneNumber.setText(contact.getPhoneNumber());
    }

    private void editContact() {
        Intent intent = new Intent(this,InputContact.class);
        intent.setAction(Constants.ACTION_EDIT);
        intent.putExtra(Constants.EXTRA_CONTACT_ID,mContact.getId());
        startActivityForResult(intent,0);
    }

    private void deleteContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You are about to delete \""+mContact.getDisplayName()+"\" permanently. Are you sure?");
        builder.setPositiveButton("No",null);
        builder.setNegativeButton("Yes",(di,which)->{
            mViewModel.removeContact(mContact);
        });
        builder.show();
    }

    private void onContactRemoved(boolean removed) {
        if (removed) {
            Toast.makeText(this, "contact removed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Toast.makeText(this, "unable to remove contact", Toast.LENGTH_SHORT).show();
    }
}