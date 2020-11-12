package pacote.faconapp.model.dominio.entidades;

public class Status {
    private int id;
    private int idCliente;
    private int idProfissional;
    private int idPagamento;
    private String dataInicio;
    private String dataFim;
    private String statusServico;
    private String termosContrato;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdProfissional() { return idProfissional; }
    public void setIdProfissional(int idProfissional) { this.idProfissional = idProfissional; }

    public int getIdPagamento() { return idPagamento; }
    public void setIdPagamento(int idPagamento) { this.idPagamento = idPagamento; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }

    public String getStatusServico() { return statusServico; }
    public void setStatusServico(String statusServico) { this.statusServico = statusServico; }

    public String getTermosContrato() { return termosContrato; }
    public void setTermosContrato(String termosContrato) { this.termosContrato = termosContrato; }
}
