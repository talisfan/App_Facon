package pacote.faconapp.model.dominio.entidades;

import com.google.gson.annotations.SerializedName;

public class Endereco {

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
}
