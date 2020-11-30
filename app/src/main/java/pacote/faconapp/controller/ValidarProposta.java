package pacote.faconapp.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.model.dominio.entidades.Proposta;

public class ValidarProposta {

    public boolean validarProposta(Proposta proposta) throws Exception {
        try {
            String exDtInvalida = "Data para prestação de serviço inválida.";

            //RECEBENDO DATA ATUAL DO CELL
            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            String dataAtual = date.format(Calendar.getInstance().getTime());

            String diaAtual = dataAtual.substring(0, 2);
            int diaAt = Integer.valueOf(diaAtual);

            String mesAtual = dataAtual.substring(3, 5);
            int mesAt = Integer.valueOf(mesAtual);

            String anoAtual = dataAtual.substring(6);
            int anoAt = Integer.valueOf(anoAtual);

            for(int i=0; i < 2; i++) {
                //ANO
                String ano = (i == 0) ? proposta.getDtInicio().substring(6) : proposta.getDtFim().substring(6);
                int anoIn = Integer.valueOf(ano);

                //MES
                String mes = (i == 0) ? proposta.getDtInicio().substring(3, 5) : proposta.getDtFim().substring(3, 5);
                int mesIn = Integer.valueOf(mes);

                //DIA
                String dia = (i == 0) ? proposta.getDtInicio().substring(0, 2) : proposta.getDtFim().substring(0, 2);
                int diaIn = Integer.valueOf(dia);

                if (anoIn < anoAt) {
                    throw new Exception(exDtInvalida);
                } else if (anoIn == anoAt) {
                    if (mesIn < mesAt) {
                        throw new Exception(exDtInvalida);
                    } else if (mesIn == mesAt) {
                        if (diaIn < diaAt) {
                            throw new Exception(exDtInvalida);
                        }
                    }
                }

                if (mesIn > 12 || mesIn < 1) {
                    throw new Exception(exDtInvalida);
                }

                if (diaIn > 31 || diaIn < 1) {
                    throw new Exception(exDtInvalida);
                }
            }

            return true;
        }catch (Exception ex){
            throw new Exception("");
        }
    }
}
