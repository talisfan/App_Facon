package pacote.faconapp.model.dominio.entidades;

import com.google.gson.annotations.SerializedName;

public class FotosServicos {

    @SerializedName("idFoto")
    private int idFoto;
    @SerializedName("idUser")
    private int idUser;
    @SerializedName("url")
    private String url;

    @SerializedName("error")
    public String error;
    @SerializedName("msg")
    public String msg;


    public int getId() {
        return idFoto;
    }

    public void setId(int idFoto) {
        this.idFoto = idFoto;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
