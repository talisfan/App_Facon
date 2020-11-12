package pacote.faconapp.model.dominio.entidades;

import com.google.gson.annotations.SerializedName;

public class Cliente extends Usuario {

    @SerializedName("idProfissional")
    private int idProfissional;
    @SerializedName("idUsuario")
    private int idUsuario;
    @SerializedName("idProfissao")
    private int idProfissao;
    @SerializedName("dtExperiencia")
    private String experiencia;
    @SerializedName("formacao")
    private String formacao = "Sem formação declarada.";
    @SerializedName("descricao")
    private String descricao = "Sem descrição.";
    @SerializedName("estrelas")
    private int estrelas;
    @SerializedName("qntAv")
    private int qntAv;
    @SerializedName("categoria")
    private String categoria;
    @SerializedName("profissao")
    private String profissao;

    // MÉTODOS GETTERS & SETTERS

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getExperiencia() {
        return experiencia;
    }
    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public int getIdProfissional() {
        return idProfissional;
    }
    public void setIdProfissional(int idProfissional) {
        this.idProfissional = idProfissional;
    }

    public String getFormacao() {
        return formacao;
    }
    public void setFormacao(String formacao) {
        this.formacao = formacao;
    }

    public int getIdProfissao() {
        return idProfissao;
    }
    public void setIdProfissao(int idProfissao) {
        this.idProfissao = idProfissao;
    }

    public int getEstrelas() { return estrelas; }
    public void setEstrelas(int estrelas) { this.estrelas = estrelas; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getQntAv() { return qntAv; }
    public void setQntAv(int qntAv) { this.qntAv = qntAv; }

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
