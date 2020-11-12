package pacote.faconapp.view.profissional;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.TipoServico;

public class EscolhaCategoriaAtuacao extends AppCompatActivity implements View.OnClickListener {

    Intent it;
    Cliente cli;
    private TipoServico tipoServico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_categoria_atuacao);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);

        tipoServico = new TipoServico();
        it = new Intent(this, EscolhaServicoPrestado.class);
        cli = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);

        findViewById(R.id.btnConstrucao).setOnClickListener(this);
        findViewById(R.id.btnServDomestico).setOnClickListener(this);
        findViewById(R.id.btnAssisTec).setOnClickListener(this);
        findViewById(R.id.btnEventos).setOnClickListener(this);
        findViewById(R.id.btnModa).setOnClickListener(this);
        findViewById(R.id.btnTecnologia).setOnClickListener(this);
        findViewById(R.id.btnAulas).setOnClickListener(this);
        findViewById(R.id.btnConsultoria).setOnClickListener(this);
        findViewById(R.id.btnSaude).setOnClickListener(this);
        findViewById(R.id.btnVeiculos).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        // passando categoria escolhida
        if(v.getId() == R.id.btnConstrucao){
            tipoServico.setCategoria("Construção e Reparos");
        }
        if(v.getId() == R.id.btnServDomestico){
            tipoServico.setCategoria("Serviços Domésticos");
        }
        if(v.getId() == R.id.btnAssisTec){
            tipoServico.setCategoria("Assistência Técnica");
        }if(v.getId() == R.id.btnEventos){
            tipoServico.setCategoria("Eventos");
        }
        if(v.getId() == R.id.btnModa){
            tipoServico.setCategoria("Moda e Beleza");
        }
        if(v.getId() == R.id.btnTecnologia){
            tipoServico.setCategoria("Tecnologia e Design");
        }
        if(v.getId() == R.id.btnAulas){
            tipoServico.setCategoria("Aulas");
        }
        if(v.getId() == R.id.btnConsultoria){
            tipoServico.setCategoria("Consultoria");
        }
        if(v.getId() == R.id.btnSaude){
            tipoServico.setCategoria("Saúde");
        }
        if(v.getId() == R.id.btnVeiculos){
            tipoServico.setCategoria("Veículos");
        }

        //passando id do user/profissional
        it.putExtra(ClassesConstants.CLIENTE, cli);
        it.putExtra(ClassesConstants.TIPO_SERVICO, tipoServico);

        startActivity(it);
    }
}