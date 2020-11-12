package pacote.faconapp.controller;

import android.content.Context;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.model.dominio.entidades.Cliente;

public class ValidarCompletarCad {

    public boolean validarCompletarCad(Cliente cliente, Context context) throws Exception {

        if (!MetodosEstaticos.isConnected(context)) {
            throw new Exception(ExceptionsServer.NO_CONNECTION);
        }
        if (MetodosEstaticos.isCampoVazio(cliente.getEndCep()) || cliente.getEndCep().length() != 8 || cliente.getEndCep() == null) {
            throw new Exception(ExceptionsCadastro.CEP_INVALIDO);
        }
        if (MetodosEstaticos.isCampoVazio(cliente.getEnderecoNum()) || cliente.getEnderecoNum() == null) {
            throw new Exception(ExceptionsCadastro.NUM_INVALIDO);
        }
        if (MetodosEstaticos.isCampoVazio(cliente.getEnderecoRua()) || cliente.getEnderecoRua() == null) {
            throw new Exception(ExceptionsCadastro.ENDERECO_INVALIDO);
        }
        return true;
    }
}
