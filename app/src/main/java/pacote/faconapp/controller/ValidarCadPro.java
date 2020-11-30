package pacote.faconapp.controller;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.model.dominio.entidades.Cliente;

public class ValidarCadPro {

    private String dateInvalidException = "Data inválida.";

    public boolean validarCadPro(Cliente pro, Context context) throws Exception {
        try {

            if(!MetodosEstaticos.isConnected(context)){
                throw new Exception(ExceptionsServer.NO_CONNECTION);
            }
            MetodosEstaticos.testDateHourAutomatic(context);

            String expShortException = "É necessário ter no mínimo 1 mês de experiência na profissão.";
            //recebendo data atual
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            String dataAtual = date.format(Calendar.getInstance().getTime());

            if(pro.getExperiencia().length() != 7){
                throw new Exception(dateInvalidException);
            }

            //MES
            String mes = pro.getExperiencia().substring(0, 2);
            int mesIn = Integer.valueOf(mes);

            String mesAtual = dataAtual.substring(3, 5);
            int mesAt = Integer.valueOf(mesAtual);

            if (mesIn > 12 || mesIn < 1) {
                throw new Exception(dateInvalidException);
            }

            //ANO
            String ano = pro.getExperiencia().substring(3);
            int anoIn = Integer.valueOf(ano);

            String anoAtual = dataAtual.substring(6);
            int anoAt = Integer.valueOf(anoAtual);
            int expMaxValida = anoAt - 60;

            if (anoIn > anoAt || anoIn < expMaxValida) {
                throw new Exception(dateInvalidException);
            }else if(anoIn == anoAt && mesIn >= mesAt){
                throw new Exception(expShortException);
            }

            return true;
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    public boolean validarFormacao(String txtCurso, String inst, String dtInicio, String dtFim, Context context) throws Exception{
        if(txtCurso.length() < 5){
            throw new Exception("Curso inválido.");
        }
        if(inst.length() < 6){
            throw new Exception("Informe a instituição e o campus.");
        }

        if(!MetodosEstaticos.isConnected(context)){
            throw new Exception(ExceptionsServer.NO_CONNECTION);
        }
        MetodosEstaticos.testDateHourAutomatic(context);

        String formShortException = "É necessário estar cursando no mínimo a 1 mês.";

        //recebendo data atual
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        String dataAtual = date.format(Calendar.getInstance().getTime());

        if(dtInicio.length() != 7 || dtFim.length() != 7){
            throw new Exception(dateInvalidException);
        }

        String mesAtual = dataAtual.substring(3, 5);
        int mesAt = Integer.valueOf(mesAtual);

        String anoAtual = dataAtual.substring(6);
        int anoAt = Integer.valueOf(anoAtual);

        for(int i=0; i<2; i++) {
            //MES
            String mes = (i==0) ? dtInicio.substring(0, 2) : dtFim.substring(0, 2);
            int mesIn = Integer.valueOf(mes);

            if (mesIn > 12 || mesIn < 1) {
                throw new Exception(dateInvalidException);
            }

            //ANO
            String ano = dtInicio.substring(3);
            int anoIn = Integer.valueOf(ano);

            int formMaxValida = anoAt - 50;

            if (anoIn > anoAt || anoIn < formMaxValida) {
                throw new Exception(dateInvalidException);
            } else if (anoIn == anoAt && mesIn >= mesAt) {
                throw new Exception(formShortException);
            }
        }
        return true;
    }

    public boolean validarContato(Context context, Cliente cliente) throws Exception{

        if(!MetodosEstaticos.isConnected(context)){
            throw new Exception(ExceptionsServer.NO_CONNECTION);
        }
        if(MetodosEstaticos.isCampoVazio(cliente.getEmail()) || MetodosEstaticos.isCampoVazio(cliente.getTelCell()) || cliente.getTelCell() == null){
            throw new Exception(ExceptionsCadastro.EMAIL_TELL_REQUIRED);
        }
        if (!MetodosEstaticos.isEmailValido(cliente.getEmail()) || cliente.getEmail() == null) {
            throw new Exception(ExceptionsCadastro.EMAIL_INVALIDO);
        }
        if (cliente.getTelCell().length() != 11) {
            throw new Exception(ExceptionsCadastro.CELL_INVALIDO);
        }
        if(!MetodosEstaticos.isCampoVazio(cliente.getTelFixo()) && cliente.getTelFixo().length() != 10){
            throw new Exception(ExceptionsCadastro.TELL_FIXO_INVALIDO);
        }
        return true;
    }
}
