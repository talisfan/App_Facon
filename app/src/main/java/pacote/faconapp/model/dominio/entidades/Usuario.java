package pacote.faconapp.model.dominio.entidades;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public abstract class Usuario implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("nome")
    private String nome;
    @SerializedName("dtNascimento")
    private String dataNascimento;
    @SerializedName("cpf")
    private String cpf;
    @SerializedName("rg")
    private String rg;
    @SerializedName("email")
    private String email;
    @SerializedName("endCep")
    private String endCep;
    @SerializedName("endRua")
    private String endRua;
    @SerializedName("endBairro")
    private String endBairro;
    @SerializedName("endCidade")
    private String endCidade;
    @SerializedName("endEstado")
    private String endEstado;
    @SerializedName("endNum")
    private String endNum;
    @SerializedName("telCell")
    private String telCell;
    @SerializedName("telFixo")
    private String telFixo;
    @SerializedName("foto")
    private String foto;
    @SerializedName("ativo")
    private int ativo;
    private String senha;
    private String senhaConf;

    @SerializedName("error")
    public String error;
    @SerializedName("msg")
    public String msg;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnderecoRua() {
        return endRua;
    }
    public void setEnderecoRua(String enderecoRua) {
        this.endRua = enderecoRua;
    }

    public String getEnderecoCidade() { return endCidade; }
    public void setEnderecoCidade(String enderecoCidade) {
        this.endCidade = enderecoCidade;
    }

    public String getEnderecoBairro() {
        return endBairro;
    }
    public void setEnderecoBairro(String enderecoBairro) { this.endBairro = enderecoBairro; }

    public String getEnderecoEstado() {
        return endEstado;
    }
    public void setEnderecoEstado(String enderecoEstado) {
        this.endEstado = enderecoEstado;
    }

    public String getEnderecoNum() {
        return endNum;
    }
    public void setEnderecoNum(String enderecoNum) {
        this.endNum = enderecoNum;
    }

    public String getEndCep() {
        return endCep;
    }
    public void setEndCep(String endCep) {
        this.endCep = endCep;
    }

    public String getTelCell() { return telCell; }
    public void setTelCell(String telCelular) {
        this.telCell = telCelular;
    }

    public String getTelFixo() {
        return telFixo;
    }
    public void setTelFixo(String telFixo) {
        this.telFixo = telFixo;
    }

    public String getSenha(){ return senha;}
    public void setSenha(String senha){ this.senha = senha; }

    public String getSenhaConf(){ return senhaConf;}
    public void setSenhaConf(String senhaConf){ this.senhaConf = senhaConf; }

    public int getAtivo() {
        return ativo;
    }
    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public String getFoto() {
        return foto;
    }
    public void setFoto(String foto) {
        this.foto = foto;
    }

    //--------------------------

    public void contatarPrestador(){ }
    public void aceitarProposta(){ }
    public void recusarProposta(){ }
    public void avaliarProfissional(){ }
    public void fazerDenuncia(){ }
}
