package rahulstech.android.namewithphotolist.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import rahulstech.android.namewithphotolist.R;
import rahulstech.android.namewithphotolist.database.model.Contact;
import rahulstech.android.namewithphotolist.provider.ContactImageProvider;
import rahulstech.android.namewithphotolist.util.Constants;
import rahulstech.android.namewithphotolist.viewmodel.InputContactViewModel;

public class InputContact extends AppCompatActivity {

    private static final String TAG = "InputContact";

    private static final String KEY_CONTACT_LOADED = "contact_loaded";
    private static final String KEY_OLD_DISPLAY_PHOTO_URI = "old_display_photo_uri";
    private static final String KEY_CAPTURE_PHOTO = "capture_photo";
    private static final String KEY_DISPLAY_PHOTO_URI = "display_photo_uri";
    private static final String KEY_DISPlAY_PHOTO_CHANGED = "display_photo_changed";

    private ImageView mDisplayPhoto;
    private TextInputLayout mInputDisplayName;
    private EditText mDisplayName;
    private EditText mPhoneNumber;

    private InputContactViewModel viewModel;

    private boolean mContactLoaded = false;
    private Uri mOldDisplayPhotoUri = null;
    private File mCapturePhoto = null;
    private Uri mDisplayPhotoUri = null;
    private boolean mIsDisplayPhotoChanged = false;

    private ActivityResultContract<Uri,Uri> mOpenCameraContract = new ActivityResultContract<Uri,Uri>(){
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Uri uri) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            return intent;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            return RESULT_OK == resultCode ?
                    ContactImageProvider.getUriForFile(InputContact.this,ContactImageProvider.AUTHORITY,mCapturePhoto)
                    : null;
        }
    };
    private ActivityResultContract<String,Uri> mPickImageContract = new ActivityResultContract<String,Uri>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, String type) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(type);
            return intent;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            return RESULT_OK == resultCode ? intent.getData() : null;
        }
    };
    private ActivityResultContract<Uri[],Uri> mCropImageContract = new ActivityResultContract<Uri[], Uri>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Uri[] uris) {
            Uri src = uris[0];
            Uri dest = uris[1];
            UCrop.Options options = new UCrop.Options();
            options.setAspectRatioOptions(0,new AspectRatio("1:1",1f,1f));
            options.setMaxBitmapSize(1024);
            return UCrop.of(src,dest)
                    .withOptions(options)
                    .getIntent(InputContact.this);
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            return RESULT_OK == resultCode ? UCrop.getOutput(intent) : null;
        }
    };
    private ActivityResultLauncher<Uri> mOpenCameraLauncher;
    private ActivityResultLauncher<String> mPickImageLauncher;
    private ActivityResultLauncher<Uri[]> mCropImageLauncher;

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
            mContactLoaded = savedInstanceState.getBoolean(KEY_CONTACT_LOADED);
            mOldDisplayPhotoUri = savedInstanceState.getParcelable(KEY_OLD_DISPLAY_PHOTO_URI);
            mCapturePhoto = (File) savedInstanceState.getSerializable(KEY_CAPTURE_PHOTO);
            mDisplayPhotoUri = savedInstanceState.getParcelable(KEY_DISPLAY_PHOTO_URI);
            mIsDisplayPhotoChanged = savedInstanceState.getBoolean(KEY_DISPlAY_PHOTO_CHANGED);
        }

        setDisplayPhoto(mDisplayPhotoUri);
        viewModel = new ViewModelProvider(this,
                (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(InputContactViewModel.class);
        if (Constants.ACTION_EDIT.equals(action) && !mContactLoaded) {
            long contactId = getIntent().getExtras().getLong(Constants.EXTRA_CONTACT_ID);
            viewModel.setFindContactCallback(contact -> {
                onContactLoaded(contact);
                return null;
            });
            viewModel.findContactById(contactId);
        }
        mOpenCameraLauncher = registerForActivityResult(mOpenCameraContract, this::modifyImage);
        mPickImageLauncher = registerForActivityResult(mPickImageContract, this::modifyImage);
        mCropImageLauncher = registerForActivityResult(mCropImageContract,result -> {
            if (null != mCapturePhoto) delete(Uri.fromFile(mCapturePhoto));
            changeDisplayPhoto(result);
        });
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
        else if (id == R.id.cancel) {
            cancel();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_CONTACT_LOADED, mContactLoaded);
        outState.putParcelable(KEY_OLD_DISPLAY_PHOTO_URI,mOldDisplayPhotoUri);
        outState.putSerializable(KEY_CAPTURE_PHOTO, mCapturePhoto);
        outState.putParcelable(KEY_DISPLAY_PHOTO_URI,mDisplayPhotoUri);
        outState.putBoolean(KEY_DISPlAY_PHOTO_CHANGED,mIsDisplayPhotoChanged);
    }

    private void onContactLoaded(@Nullable Contact contact) {
        mContactLoaded = true;
        if (null == contact) {
            Toast.makeText(this, "unable to load contact", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mOldDisplayPhotoUri = contact.getDpUri(this);
        setDisplayPhoto(mOldDisplayPhotoUri);
        mDisplayName.setText(contact.getDisplayName());
        mPhoneNumber.setText(contact.getPhoneNumber());
    }

    private void onClickDisplayPhoto() {
        String[] options;
        if (null == mDisplayPhotoUri) {
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
            mDisplayPhoto.setImageResource(R.drawable.ic_display_photo_placeholder);
        }
        else {
            mDisplayPhotoUri = uri;
            mDisplayPhoto.setImageURI(uri);
        }
    }

    private void openCamera() {
        mCapturePhoto = ContactImageProvider.createTemporaryFile(ContactImageProvider.getTemporaryDirectory(this));
        if (null == mCapturePhoto) {
            Toast.makeText(this,"unable to open camera",Toast.LENGTH_SHORT).show();
            return;
        }
        Uri uri = ContactImageProvider.getUriForFile(this,ContactImageProvider.AUTHORITY,mCapturePhoto);
        mOpenCameraLauncher.launch(uri);
    }

    private void choosePhoto() {
        mPickImageLauncher.launch("image/*");
    }

    private void modifyImage(@Nullable Uri src) {
        if (null == src) return;
        Uri dest = Uri.fromFile(ContactImageProvider.createTemporaryFile(ContactImageProvider.getTemporaryDirectory(this)));
        mCropImageLauncher.launch(new Uri[]{src,dest});
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

        String displayPhoto = null;
        if (mIsDisplayPhotoChanged) {
            try {
                displayPhoto = saveDisplayPhoto(mDisplayPhotoUri);
                Log.d(TAG, "display_photo_name=" + displayPhoto);
            } catch (IOException ex) {
                Toast.makeText(this, "unable to save contact", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        else if (null != mDisplayPhotoUri) {
            displayPhoto = mDisplayPhotoUri.getLastPathSegment();
        }

        Contact contact = new Contact();
        contact.setDisplayPhoto(displayPhoto);
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

    private String saveDisplayPhoto(@NonNull Uri src) throws IOException {
        delete(mOldDisplayPhotoUri);
        if (null == src) return null;
        String imageName = UUID.randomUUID().toString();
        File imageFile = ContactImageProvider.createDisplayPhotoFile(ContactImageProvider.getDisplayPhotosDirectory(this),imageName);
        Uri dest = Uri.fromFile(imageFile);
        InputStream in = getContentResolver().openInputStream(src);
        OutputStream out = getContentResolver().openOutputStream(dest);
        byte[] buff = new byte[512];
        while (in.read(buff) > 0) {
            out.write(buff);
            out.flush();
        }
        out.close();
        in.close();
        delete(src);
        return imageName;
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

    private void delete(Uri uri) {
        Log.d(TAG,"delete uri="+uri);
        if (null == uri) return;
        if (!uri.getScheme().equals("file")) return;
        File file = new File(uri.getPath());
        if (file.exists()) {
            file.delete();
        }
    }

    private void cancel() {
        // TODO: warn before exit
        finish();
    }
}