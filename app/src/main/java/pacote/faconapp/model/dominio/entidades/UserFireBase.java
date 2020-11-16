package pacote.faconapp.model.dominio.entidades;

import android.os.Parcel;
import android.os.Parcelable;

public class UserFireBase implements Parcelable {

    private String uuid;
    private String username;
    private String profileUrl;
    private String token;
    private boolean online;

    private UserFireBase() {

    }

    public UserFireBase(String uuid,String username, String profileUrl) {
        this.uuid = uuid;
        this.username = username;
        this.profileUrl = profileUrl;
    }

    protected UserFireBase(Parcel in) {
        uuid = in.readString();
        username = in.readString();
        profileUrl = in.readString();
        token = in.readString();
        online = in.readInt() == 1;
    }

    public static final Creator<UserFireBase> CREATOR = new Creator<UserFireBase>() {
        @Override
        public UserFireBase createFromParcel(Parcel in) {
            return new UserFireBase(in);
        }

        @Override
        public UserFireBase[] newArray(int size) {
            return new UserFireBase[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getToken() {
        return token;
    }

    public boolean isOnline() {
        return online;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(username);
        dest.writeString(profileUrl);
        dest.writeString(token);
        dest.writeInt(online ? 1 : 0);
    }
}
