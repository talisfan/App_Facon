package pacote.faconapp.controller;

import android.content.Context;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.constants.ExceptionsServer;

public class ValidarLogin {

    private Context context;

    public ValidarLogin(Context context) {
        this.context = context;
    }

    public boolean validarLogin(String email, String senha) throws Exception {

        if (!MetodosEstaticos.isConnected(context)) {
            throw new Exception(ExceptionsServer.NO_CONNECTION);
        }
        if (MetodosEstaticos.isCampoVazio(email)) {
            throw new Exception(ExceptionsCadastro.EMAIL_VAZIO);
        } else if (!MetodosEstaticos.isEmailValido(email)) {
            throw new Exception(ExceptionsCadastro.EMAIL_INVALIDO);
        } else if (MetodosEstaticos.isCampoVazio(senha)) {
            throw new Exception(ExceptionsCadastro.SENHA_VAZIO);
        }

        return true;
    }
}
