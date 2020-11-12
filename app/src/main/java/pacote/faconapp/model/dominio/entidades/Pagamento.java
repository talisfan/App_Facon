package pacote.faconapp.model.dominio.entidades;

public class Pagamento {

    private int id;
    private String formaPagamento;
    private float valorPagamento;
    private String dataPagamento;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public float getValorPagamento() { return valorPagamento; }
    public void setValorPagamento(float valorPagamento) { this.valorPagamento = valorPagamento; }

    public String getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(String dataPagamento) { this.dataPagamento = dataPagamento; }

    //--------

    public void realizarPagamento(){}
}
