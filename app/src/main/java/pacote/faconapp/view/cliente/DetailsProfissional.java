package pacote.faconapp.view.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.constants.InfosLoginConstants;
import pacote.faconapp.model.dominio.entidades.Cliente;

public class DetailsProfissional extends AppCompatActivity {

    private AlertDialog.Builder alertD;
    private ViewHolder mViewHolder = new ViewHolder();
    private Cliente profissionalDetails;
    private int idUserLogado;

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

        try {
            profissionalDetails = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);
            idUserLogado = getIntent().getExtras().getInt(InfosLoginConstants.ID_USER);

            mViewHolder.nome.setText(profissionalDetails.getNome());
            mViewHolder.exp.setText("Experiência: " + MetodosEstaticos.calcularExpPro(profissionalDetails.getExperiencia()) + " (anos.meses)");
            mViewHolder.categoria.setText("Categoria: " + profissionalDetails.getCategoria());
            mViewHolder.profissao.setText("Profissão: " + profissionalDetails.getProfissao());
            mViewHolder.cidade.setText(profissionalDetails.getEnderecoCidade() + "/" + profissionalDetails.getEnderecoEstado());
            mViewHolder.qntAv.setText("Média de " + profissionalDetails.getQntAv() + " avaliações.");
            mViewHolder.descricao.setText(profissionalDetails.getDescricao());
            mViewHolder.formacao.setText(profissionalDetails.getFormacao());

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

    private static class ViewHolder {
        TextView nome;
        TextView exp;
        TextView categoria;
        TextView profissao;
        TextView cidade;
        TextView qntAv;
        EditText descricao;
        EditText formacao;
        TextView idade;
        ImageView estrelas;
    }
}