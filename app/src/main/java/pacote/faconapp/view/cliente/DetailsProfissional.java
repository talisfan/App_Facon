package pacote.faconapp.view.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.constants.InfosLoginConstants;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.chat.ContatosFb;
import pacote.faconapp.model.dominio.entidades.chat.UserFireBase;
import pacote.faconapp.view.EscolhaUser;
import pacote.faconapp.view.chat.ChatActivity;
import pacote.faconapp.view.chat.ContactsActivity;

public class DetailsProfissional extends AppCompatActivity {

    private AlertDialog.Builder alertD;
    private ViewHolder mViewHolder = new ViewHolder();
    private Cliente profissionalDetails;
    private Cliente cli;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_profissional);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        alertD = new AlertDialog.Builder(this);

        mViewHolder.nome = findViewById(R.id.lblNomePro);
        mViewHolder.exp = findViewById(R.id.lblExpPro);
        mViewHolder.categoria = findViewById(R.id.lblCategPro);
        mViewHolder.profissao = findViewById(R.id.lblServPro);
        mViewHolder.cidade = findViewById(R.id.lblCidade);
        mViewHolder.estrelas = findViewById(R.id.avPro);
        mViewHolder.qntAv = findViewById(R.id.lblQntAv);
        mViewHolder.descricao = findViewById(R.id.txtDescricao);
        mViewHolder.formacao = findViewById(R.id.txtFormacao);
        mViewHolder.idade = findViewById(R.id.lblIdade);
        mViewHolder.fotoProfissional = findViewById(R.id.fotoProfissional);
        mViewHolder.btnNegociar = findViewById(R.id.btnNegociar);

        try {
            profissionalDetails = (Cliente) getIntent().getSerializableExtra(ClassesConstants.PROFISSIONAL);
            cli = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);

            mViewHolder.nome.setText(profissionalDetails.getNome());
            mViewHolder.exp.setText("Experiência: " + MetodosEstaticos.calcularExpPro(profissionalDetails.getExperiencia()) + " (anos.meses)");
            mViewHolder.categoria.setText("Categoria: " + profissionalDetails.getCategoria());
            mViewHolder.profissao.setText("Profissão: " + profissionalDetails.getProfissao());
            mViewHolder.cidade.setText(profissionalDetails.getEnderecoCidade() + "/" + profissionalDetails.getEnderecoEstado());
            mViewHolder.qntAv.setText("Média de " + profissionalDetails.getQntAv() + " avaliações.");
            mViewHolder.descricao.setText(profissionalDetails.getDescricao());
            mViewHolder.formacao.setText(profissionalDetails.getFormacao());

            if(profissionalDetails.getFoto() != null && !profissionalDetails.getFoto().equals("")) {
                Picasso.get()
                        .load(profissionalDetails.getFoto())
                        .into(mViewHolder.fotoProfissional);
            }

            switch (profissionalDetails.getEstrelas()) {
                case 1:
                    mViewHolder.estrelas.setImageResource(R.drawable.stars_one);
                    break;
                case 2:
                    mViewHolder.estrelas.setImageResource(R.drawable.stars_two);
                    break;
                case 3:
                    mViewHolder.estrelas.setImageResource(R.drawable.stars_three);
                    break;
                case 4:
                    mViewHolder.estrelas.setImageResource(R.drawable.stars_four);
                    break;
                case 5:
                    mViewHolder.estrelas.setImageResource(R.drawable.stars_five);
                    break;
                default:
                    mViewHolder.estrelas.setImageResource(R.drawable.stars_default);
                    mViewHolder.qntAv.setVisibility(View.INVISIBLE);
                    break;
            }

            if(!MetodosEstaticos.isConnected(this)){
                throw new Exception(ExceptionsServer.NO_CONNECTION);
            }
            MetodosEstaticos.testDateHourAutomatic(this);

            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
            String dataAtual = date.format(Calendar.getInstance().getTime());
            String anoAtual = dataAtual.substring(6);
            int anoAt = Integer.valueOf(anoAtual);

            // dtNascimento = '2000-03-29T03:00:00.000Z'
            String ano = profissionalDetails.getDataNascimento().substring(0, 4);
            int anoDt = Integer.valueOf(ano);

            int dtNascimento = anoAt - anoDt;
            mViewHolder.idade.setText("Idade: " + dtNascimento + " anos.");

        } catch (Exception ex) {
            alertD.setTitle("ERRO:");
            alertD.setMessage(ex.getMessage());
            alertD.show();
        }
    }

    public void btnChat(View v){

        // adicionando usuario aos contatos do profissional
        ContatosFb us = new ContatosFb(profissionalDetails.getIdFb(), cli.getNome(), cli.getFoto(), FirebaseAuth.getInstance().getUid());

        FirebaseFirestore.getInstance().collection("contatos")
                .document(UUID.randomUUID().toString())
                .set(us)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        ContatosFb user = new ContatosFb(FirebaseAuth.getInstance().getUid(), profissionalDetails.getNome(), profissionalDetails.getFoto(), profissionalDetails.getIdFb());

                        // adicionando profissional aos contatos do user logado
                        FirebaseFirestore.getInstance().collection("contatos")
                                .document(UUID.randomUUID().toString())
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(DetailsProfissional.this, ChatActivity.class);
                                        intent.putExtra(ClassesConstants.PROFISSIONAL, user);
                                        startActivity(intent);
                                    }
                                });
                    }
                });
    }

    private static class ViewHolder {
        TextView nome;
        TextView exp;
        TextView categoria;
        TextView profissao;
        TextView cidade;
        TextView qntAv;
        TextView idade;
        EditText descricao;
        EditText formacao;
        ImageView estrelas;
        ImageView fotoProfissional;
        Button btnNegociar;
    }
}