package pacote.faconapp.model.dominio.entidades;

import com.google.gson.annotations.SerializedName;

public class Cep {

    @SerializedName("cep")
    private String endCep;
    @SerializedName("logradouro")
    private String endRua;
    @SerializedName("bairro")
    private String endBairro;
    @SerializedName("localidade")
    private String endCidade;
    @SerializedName("uf")
    private String endEstado;

    //
    public String getEndCep() {
        return endCep;
    }

    public void setEndCep(String endCep) {
        this.endCep = endCep;
    }

    public String getEndRua() {
        return endRua;
    }

    public void setEndRua(String endRua) {
        this.endRua = endRua;
    }

    public String getEndBairro() {
        return endBairro;
    }

    public void setEndBairro(String endBairro) {
        this.endBairro = endBairro;
    }

    public String getEndCidade() {
        return endCidade;
    }

    public void setEndCidade(String endCidade) {
        this.endCidade = endCidade;
    }

    public String getEndEstado() {
        return endEstado;
    }

    public void setEndEstado(String endEstado) {
        this.endEstado = endEstado;
    }
}
