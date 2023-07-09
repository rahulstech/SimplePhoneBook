package rahulstech.android.namewithphotolist.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import jahirfiquitiva.libs.textdrawable.TextDrawable;
import rahulstech.android.namewithphotolist.R;
import rahulstech.android.namewithphotolist.database.model.Contact;
import rahulstech.android.namewithphotolist.util.ColorGenerator;

public class ContactsAdapter extends ListAdapter<Contact,ContactsAdapter.ContactViewHolder> {

    private static final String TAG = "ContactsAdapter";

    private static DiffUtil.ItemCallback<Contact> DIFF_CALLBACK = new DiffUtil.ItemCallback<Contact>() {
        @Override
        public boolean areItemsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Contact oldItem, @NonNull Contact newItem) {
            return oldItem.equals(newItem);
        }
    };

    private LayoutInflater mInflater;

    public ContactsAdapter(@NonNull Context context) {
        super(DIFF_CALLBACK);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.contact_item,parent,false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = getItem(position);
        holder.bind(contact);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView mDisplayPhoto;
        TextView mDisplayName;
        TextView mPhoneNumber;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            mDisplayPhoto = itemView.findViewById(R.id.display_photo);
            mDisplayName = itemView.findViewById(R.id.display_name);
            mPhoneNumber = itemView.findViewById(R.id.phone_number);
        }

        public void bind(@Nullable Contact contact) {
            if (null != contact) {
                bindNonNull(contact);
            }
            else {
                bindNull();
            }
        }

        public void bindNonNull(@NonNull Contact contact) {
            Log.d(TAG,"binding contact="+contact);
            String displayPhoto = contact.getDisplayPhoto();
            if (!TextUtils.isEmpty(displayPhoto)) {
                mDisplayPhoto.setImageURI(Uri.parse(displayPhoto));
            }
            else {
                Drawable placeholder = contact.getPlaceholder();
                if (null == placeholder) {
                    placeholder = createTextDisplayPhotoPlaceHolder(contact.getDisplayName());
                    contact.setPlaceholder(placeholder);
                }
                mDisplayPhoto.setImageDrawable(placeholder);
            }
            mDisplayName.setText(contact.getDisplayName());
            mPhoneNumber.setText(contact.getPhoneNumber());
        }

        public void bindNull() {
            // TODO: implement bindNull
        }

        private Drawable createTextDisplayPhotoPlaceHolder(@NonNull String displayName) {
            String text = displayName.substring(0,1);
            int color = ColorGenerator.MATERIAL.getColor(displayName);
            TextDrawable drawable = new TextDrawable.Builder().buildRect(text,color);
            return drawable;
        }
    }
}
