package pacote.faconapp.model.dominio.entidades;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TipoServico implements Serializable {
    private int id;
    @SerializedName("categoria")
    private String categoria;
    @SerializedName("profissao")
    private String profissao;
    @SerializedName("error")
    public String error;
    @SerializedName("msg")
    public String msg;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getProfissao() { return profissao; }
    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }
}
