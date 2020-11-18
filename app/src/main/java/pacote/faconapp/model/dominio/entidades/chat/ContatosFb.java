package pacote.faconapp.model.dominio.entidades.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class ContatosFb implements Parcelable {

    private String idUser;
    private String username;
    private String profileUrl;
    private String contato;

    private ContatosFb() {

    }

    public ContatosFb(String uuid, String username, String profileUrl, String contato) {
        this.idUser = uuid;
        this.username = username;
        this.profileUrl = profileUrl;
        this.contato = contato;
    }

    protected ContatosFb(Parcel in) {
        idUser = in.readString();
        username = in.readString();
        profileUrl = in.readString();
        contato = in.readString();
    }

    public static final Creator<ContatosFb> CREATOR = new Creator<ContatosFb>() {
        @Override
        public ContatosFb createFromParcel(Parcel in) {
            return new ContatosFb(in);
        }

        @Override
        public ContatosFb[] newArray(int size) {
            return new ContatosFb[size];
        }
    };

    public String getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getContato() { return contato; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idUser);
        dest.writeString(username);
        dest.writeString(profileUrl);
        dest.writeString(contato);
    }
}
