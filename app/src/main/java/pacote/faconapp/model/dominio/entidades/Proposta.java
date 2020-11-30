package pacote.faconapp.model.dominio.entidades;

import com.google.gson.annotations.SerializedName;

public class Proposta {
    @SerializedName("token")
    private String token;
    @SerializedName("prestador")
    private String prestador;
    @SerializedName("idFbPrestador")
    private String idFbPrestador;
    @SerializedName("cliente")
    private String cliente;
    @SerializedName("idFbCliente")
    private String idFbCliente;
    @SerializedName("statusProp")
    private String status;
    @SerializedName("dtInicio")
    private String dtInicio;
    @SerializedName("dtFim")
    private String dtFim;
    @SerializedName("valor")
    private Double valor;
    @SerializedName("formaPag")
    private String formaPag;
    @SerializedName("localServ")
    private String local;
    @SerializedName("descricao")
    private String descricao;

    @SerializedName("error")
    public String error;
    @SerializedName("msg")
    public String msg;

    public Proposta(String idFbPrestador, String prestador, String idFbCliente, String cliente, String status, String dtInicio, String dtFim, double valor,
                    String formaPag, String local, String descricao){

        this.idFbPrestador = idFbPrestador;
        this.idFbCliente = idFbCliente;
        this.prestador = prestador;
        this.cliente = cliente;
        this.status = status;
        this.dtInicio = dtInicio;
        this.valor = valor;
        this.formaPag = formaPag;
        this.local = local;
        this.descricao = descricao;
    }

    public void setStatus(String status) { this.status = status; }
    public void setToken(String token){ this.token = token; }
    public void setDtInicio(String dtInicio){ this.dtInicio = dtInicio; }
    public void setDtFim(String dtFim){ this.dtFim = dtFim; }

    public String getToken(){ return token; }
    public String getStatus() { return status; }
    public String getPrestador() { return prestador; }
    public String getIdFbPrestador() { return idFbPrestador; }
    public String getCliente() { return cliente; }
    public String getIdFbCliente() { return idFbCliente; }
    public String getDtInicio() { return dtInicio; }
    public String getDtFim() { return dtFim; }
    public Double getValor() { return valor; }
    public String getFormaPag() { return formaPag; }
    public String getLocal() { return local; }
    public String getDescricao() { return descricao; }
}
