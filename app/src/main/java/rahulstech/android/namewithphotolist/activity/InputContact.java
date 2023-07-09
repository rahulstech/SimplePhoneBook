package rahulstech.android.namewithphotolist.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import rahulstech.android.namewithphotolist.R;
import rahulstech.android.namewithphotolist.database.model.Contact;
import rahulstech.android.namewithphotolist.util.Constants;
import rahulstech.android.namewithphotolist.viewmodel.InputContactViewModel;

public class InputContact extends AppCompatActivity {

    private static final String TAG = "InputContact";

    private static final int PICK_DISPlAY_PHOTO = 10;

    private static final String KEY_DISPLAY_PHOTO_URI = "display_photo_uri";
    private static final String KEY_HAS_DISPLAY_PHOTO = "has_display_photo";
    private static final String KEY_DISPlAY_PHOTO_CHANGED = "display_photo_changed";

    private ImageView mDisplayPhoto;
    private TextInputLayout mInputDisplayName;
    private EditText mDisplayName;
    private EditText mPhoneNumber;

    private InputContactViewModel viewModel;

    private Uri mDisplayPhotoUri = null;
    private boolean mHasDisplayPhoto = false;
    private boolean mIsDisplayPhotoChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String action = getIntent().getAction();
        if (Constants.ACTION_EDIT.equals(action) && !getIntent().hasExtra(Constants.EXTRA_CONTACT_ID)) {
            Toast.makeText(this, "unable to load contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_input_contact);

        mDisplayPhoto = findViewById(R.id.display_photo);
        mInputDisplayName = findViewById(R.id.input_display_name);
        mDisplayName = findViewById(R.id.display_name);
        mPhoneNumber = findViewById(R.id.phone_number);
        mDisplayPhoto.setOnClickListener(v -> onClickDisplayPhoto());

        if (null != savedInstanceState) {
            mDisplayPhotoUri = savedInstanceState.getParcelable(KEY_DISPLAY_PHOTO_URI);
            mHasDisplayPhoto = savedInstanceState.getBoolean(KEY_HAS_DISPLAY_PHOTO);
            mIsDisplayPhotoChanged = savedInstanceState.getBoolean(KEY_DISPlAY_PHOTO_CHANGED);
        }

        viewModel = new ViewModelProvider(this,
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(InputContactViewModel.class);

        if (Constants.ACTION_EDIT.equals(action)) {
            long contactId = getIntent().getExtras().getLong(Constants.EXTRA_CONTACT_ID);
            viewModel.findContactById(contactId).observe(this,this::onContactLoaded);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.setAddContactCallback(saved -> {
            onContactSaved(saved);
            return null;
        });
        viewModel.setEditContactCallback(saved -> {
            onContactSaved(saved);
            return null;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_contact_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save) {
            saveContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DISPlAY_PHOTO) {
            if (RESULT_OK == resultCode && null != data) {
                changeDisplayPhoto(data.getData());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_DISPLAY_PHOTO_URI,mDisplayPhotoUri);
        outState.putBoolean(KEY_HAS_DISPLAY_PHOTO,mHasDisplayPhoto);
        outState.putBoolean(KEY_DISPlAY_PHOTO_CHANGED,mIsDisplayPhotoChanged);
    }

    private void onContactLoaded(@Nullable Contact contact) {
        if (null == contact) {
            Toast.makeText(this, "unable to load contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!TextUtils.isEmpty(contact.getDisplayPhoto())) {
            mDisplayPhotoUri = Uri.parse(contact.getDisplayPhoto());
            mHasDisplayPhoto = true;
        }
        setDisplayPhoto(contact.getDpUri());
        mDisplayName.setText(contact.getDisplayName());
        mPhoneNumber.setText(contact.getPhoneNumber());
    }

    private void onClickDisplayPhoto() {
        String[] options;
        if (!mHasDisplayPhoto) {
            options = new String[]{
                    "Open Camera",
                    "Pick Photo"
            };
        }
        else {
            options = new String[]{
                    "Open Camera",
                    "Pick Photo",
                    "Remove Photo"
            };
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setSingleChoiceItems(options, -1, (di, which) -> {
                    if (0 == which) {
                        openCamera();
                    } else if (1 == which) {
                        choosePhoto();
                    }
                    else if (2 == which) {
                        changeDisplayPhoto(null);
                    }
                    di.cancel();
                })
                .create();
        dialog.show();
    }

    private void setDisplayPhoto(@Nullable Uri uri) {
        Log.d(TAG,"oldUri="+mDisplayPhotoUri+" newUri="+uri);
        if (null == uri) {
            mDisplayPhotoUri = null;
            mHasDisplayPhoto = false;
            mDisplayPhoto.setImageResource(R.drawable.ic_display_photo_placeholder);
        }
        else {
            mDisplayPhotoUri = uri;
            mHasDisplayPhoto = true;
            mDisplayPhoto.setImageURI(uri);
        }
    }

    private void openCamera() {
        ImagePicker.with(this)
                .cameraOnly()
                .compress(1024)
                .cropSquare()
                .saveDir(getExternalCacheDir())
                .start(PICK_DISPlAY_PHOTO);
    }

    private void choosePhoto() {
        ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .cropSquare()
                .saveDir(getExternalCacheDir())
                .start(PICK_DISPlAY_PHOTO);
    }

    private void changeDisplayPhoto(@Nullable Uri uri) {
        setDisplayPhoto(uri);
        mIsDisplayPhotoChanged = true;
    }

    private void saveContact() {
        mInputDisplayName.setError(null);

        String displayName = mDisplayName.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

        if (TextUtils.isEmpty(displayName)) {
            mInputDisplayName.setError("display name not set");
            return;
        }

        Uri uri = mDisplayPhotoUri;
        if (mIsDisplayPhotoChanged) {
            try {
                uri = saveDisplayPhoto();
                Log.d(TAG, "display_photo_uri=" + uri);
            } catch (IOException ex) {
                Toast.makeText(this, "unable to save contact", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        Contact contact = new Contact();
        if (null == uri) {
            contact.setDisplayPhoto(null);
        }
        else {
            contact.setDisplayPhoto(uri.toString());
        }
        contact.setDisplayName(displayName);
        contact.setPhoneNumber(phoneNumber);

        String action = getIntent().getAction();
        if (Constants.ACTION_CREATE.equals(action)) {
            viewModel.addContact(contact);
        }
        else if (Constants.ACTION_EDIT.equals(action)){
            long contactId = getIntent().getLongExtra(Constants.EXTRA_CONTACT_ID,0);
            contact.setId(contactId);
            viewModel.editContact(contact);
        }
    }

    private Uri saveDisplayPhoto() throws IOException {
        if (!mHasDisplayPhoto) return null;
        try {
            File dir = Constants.getDisplayPhotoDirectory(this);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String imageName = UUID.randomUUID().toString();
            File imageFile = new File(dir,imageName);
            InputStream in = getContentResolver().openInputStream(mDisplayPhotoUri);
            Uri uri = Uri.fromFile(imageFile);
            OutputStream out = getContentResolver().openOutputStream(uri);
            byte[] buff = new byte[512];
            while (in.read(buff) > 0) {
                out.write(buff);
                out.flush();
            }
            out.close();
            in.close();

            return uri;
        }
        catch (Exception ex) {
            Log.e(TAG,"save display photo",ex);
            throw new IOException("fail to save display photo");
        }
    }
    
    private void onContactSaved(boolean saved) {
        Log.d(TAG,"action="+getIntent().getAction()+" saved="+saved);
        String message;
        if (saved) {
            message = "contact saved";
        }
        else {
            message = "contact not saved";
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}