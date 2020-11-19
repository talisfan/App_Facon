package pacote.faconapp.model.dominio.entidades.chat;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ContatosFb implements Serializable {

    private String idUser;
    private String username;
    private String profileUrl;
    private String photoUrl; // ambos são mesma coisa mas com finalidades diferentes
    private String contato;
    private String lastMessage;
    private long timestamp;

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    private ContatosFb() { }

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

    public String getIdUser() {
        return idUser;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }

    public String getContato() { return contato; }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
