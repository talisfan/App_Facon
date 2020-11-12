package pacote.faconapp.controller;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.model.dominio.entidades.Usuario;

public class ValidarCadCli {

    public boolean ValidarCadastro(Usuario user, Context context) throws Exception {
        try {
            if(!MetodosEstaticos.isConnected(context)){
                throw new Exception(ExceptionsServer.NO_CONNECTION);
            }
            MetodosEstaticos.testDateHourAutomatic(context);

            if (MetodosEstaticos.isCampoVazio(user.getNome())) {
                throw new Exception(ExceptionsCadastro.ERRO_NOME);

            } else if (user.getNome().length() < 7) {
                throw new Exception(ExceptionsCadastro.ERRO_NOME);

            } else if (MetodosEstaticos.isCampoVazio(user.getEmail())) {
                throw new Exception(ExceptionsCadastro.EMAIL_VAZIO);

            } else if (!MetodosEstaticos.isEmailValido(user.getEmail())) {
                throw new Exception(ExceptionsCadastro.EMAIL_INVALIDO);

            } else if (MetodosEstaticos.isCampoVazio(user.getTelCell())) {
                throw new Exception(ExceptionsCadastro.CELL_VAZIO);

            } else if (user.getTelCell().length() != 11) {
                throw new Exception(ExceptionsCadastro.CELL_INVALIDO);

            }
            if (!MetodosEstaticos.isCampoVazio(user.getTelFixo()) && user.getTelFixo().length() != 10) {
                throw new Exception(ExceptionsCadastro.TELL_FIXO_INVALIDO);
            }
            if (MetodosEstaticos.isCampoVazio(user.getCpf())) {
                throw new Exception(ExceptionsCadastro.CPF_VAZIO);

            } else if (user.getCpf().length() != 11) {
                throw new Exception(ExceptionsCadastro.CPF_INVALIDO);

            } else if (MetodosEstaticos.isCampoVazio(user.getRg())) {
                throw new Exception(ExceptionsCadastro.RG_VAZIO);

            } else if (user.getRg().length() != 9) {
                throw new Exception(ExceptionsCadastro.RG_INVALIDO);

            } else {
                //deixando tudo maiuscula (caso haja X no final)
                user.setRg(user.getRg().toUpperCase());

                String rg = user.getRg();
                //retirando apenas ultimo digito
                String rgF = rg.substring(0, rg.length() - 1);

                //testando se contem X no meio do RG
                if(rgF.contains("X")){
                    throw new Exception(ExceptionsCadastro.RG_INVALIDO);
                }
            }
            if (MetodosEstaticos.isCampoVazio(user.getDataNascimento())) {
                throw new Exception(ExceptionsCadastro.DT_NASCIMENTO_VAZIO);
            } else if (user.getDataNascimento().length() != 10) {
                throw new Exception(ExceptionsCadastro.DT_NASCIMENTO_INVALIDO);
            } else {
                //RECEBENDO DATA ATUAL DO CELL
                SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
                String dataAtual = date.format(Calendar.getInstance().getTime());

                //DIA
                String dia = user.getDataNascimento().substring(0, 2);
                int diaIn = Integer.valueOf(dia);

                String diaAtual = dataAtual.substring(0, 2);
                int diaAt = Integer.valueOf(diaAtual);

                if (diaIn > 31 || diaIn < 1) {
                    throw new Exception(ExceptionsCadastro.DT_NASCIMENTO_INVALIDO);
                }

                //MES
                String mes = user.getDataNascimento().substring(3, 5);
                int mesIn = Integer.valueOf(mes);

                String mesAtual = dataAtual.substring(3, 5);
                int mesAt = Integer.valueOf(mesAtual);

                if (mesIn > 12 || mesIn < 1) {
                    throw new Exception(ExceptionsCadastro.DT_NASCIMENTO_INVALIDO);
                }

                //ANO
                String ano = user.getDataNascimento().substring(6);
                int anoIn = Integer.valueOf(ano);

                String anoAtual = dataAtual.substring(6);
                int anoAt = Integer.valueOf(anoAtual);
                int idadeValida = anoAt - 80;

                if (anoIn > anoAt || anoIn < idadeValida) {
                    throw new Exception(ExceptionsCadastro.DT_NASCIMENTO_INVALIDO);
                }

                int idade = anoAt - anoIn;

                if (idade < 18) {
                    throw new Exception(ExceptionsCadastro.IDADE_MINIMA);
                } else if (idade == 18) {
                    if (mesAt == mesIn) {
                        if (diaIn > diaAt) {
                            throw new Exception(ExceptionsCadastro.IDADE_MINIMA);
                        }
                    } else if (mesIn > mesAt) {
                        throw new Exception(ExceptionsCadastro.IDADE_MINIMA);
                    }
                }
            }
            if (MetodosEstaticos.isCampoVazio(user.getSenha())) {
                throw new Exception(ExceptionsCadastro.SENHA_VAZIO);
            } else if (user.getSenha().length() < 6) {
                throw new Exception(ExceptionsCadastro.SENHA_CURTA);

            } else if (MetodosEstaticos.isCampoVazio(user.getSenhaConf())) {
                throw new Exception(ExceptionsCadastro.CONF_SENHA_VAZIO);

            } else if (!user.getSenha().equals(user.getSenhaConf())) {
                throw new Exception(ExceptionsCadastro.SENHAS_ERRADAS);

            }

            return true;

        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
