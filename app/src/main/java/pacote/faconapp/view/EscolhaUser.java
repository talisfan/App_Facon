package pacote.faconapp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.view.profissional.EscolhaCategoriaAtuacao;

public class EscolhaUser extends AppCompatActivity {

    private Intent it;
    private Cliente user;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_user);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        context = this;
        user = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);
    }

    public void escolhaUserCliente(View v) {
        it = new Intent(context, Home.class);
        it.putExtra(ClassesConstants.CLIENTE, user);
        startActivity(it);
        this.finish();
    }

    public void escolhaUserProfissional(View v){
        it = new Intent(context, EscolhaCategoriaAtuacao.class);
        it.putExtra(ClassesConstants.CLIENTE, user);
        startActivity(it);
        this.finish();
    }
}
