package rahulstech.android.namewithphotolist.database.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "contacts")
public class Contact {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String displayName;
    private String displayPhoto;
    private String phoneNumber;

    @Ignore
    private Drawable placeholder;
    @Ignore
    private Uri dpUri;

    public Contact(long id, String displayName, String displayPhoto, String phoneNumber) {
        this.id = id;
        this.displayName = displayName;
        this.phoneNumber = phoneNumber;
        setDisplayPhoto(displayPhoto);
    }

    @Ignore
    public Contact() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayPhoto() {
        return displayPhoto;
    }

    public void setDisplayPhoto(String displayPhoto) {
        this.displayPhoto = displayPhoto;
        if (TextUtils.isEmpty(displayPhoto)) {
            dpUri = null;
        }
        else {
            dpUri = Uri.parse(displayPhoto);
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPlaceholder(Drawable placeholder) {
        this.placeholder = placeholder;
    }

    public Drawable getPlaceholder() {
        return placeholder;
    }

    public Uri getDpUri() {
        return dpUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact)) return false;
        Contact contact = (Contact) o;
        return id == contact.id && displayName.equals(contact.displayName) && Objects.equals(displayPhoto, contact.displayPhoto) && Objects.equals(phoneNumber, contact.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, displayName, displayPhoto, phoneNumber);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", displayPhoto='" + displayPhoto + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", placeholder=" + placeholder +
                '}';
    }
}
