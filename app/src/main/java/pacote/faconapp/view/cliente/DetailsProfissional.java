package pacote.faconapp.view.cliente;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.adapter.FotosServicosAdapter;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.constants.InfosLoginConstants;
import pacote.faconapp.listener.OnClickFoto;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudPro;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.FotosServicos;
import pacote.faconapp.model.dominio.entidades.chat.ContatosFb;
import pacote.faconapp.model.dominio.entidades.chat.UserFireBase;
import pacote.faconapp.view.CompletarCadastro;
import pacote.faconapp.view.EscolhaUser;
import pacote.faconapp.view.Home;
import pacote.faconapp.view.MainActivity;
import pacote.faconapp.view.chat.ChatActivity;
import pacote.faconapp.view.chat.ContactsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsProfissional extends AppCompatActivity {

    private AlertDialog.Builder alertD;
    private ViewHolder mViewHolder = new ViewHolder();
    private Cliente profissionalDetails;
    private Cliente cli;
    private Context context;
    private CrudPro crudPro;
    private OnClickFoto listener;
    private FotosServicosAdapter adapter;
    private LinearLayoutManager linearLayout;
    private String idFbPro;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_profissional);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        context = this;
        alertD = new AlertDialog.Builder(this);

        mViewHolder.lblSemFotos = findViewById(R.id.lblSemFotos);
        mViewHolder.recyclerView = findViewById(R.id.recyclerFotos);
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
            crudPro = ApiDb.createService(CrudPro.class);
            profissionalDetails = (Cliente) getIntent().getSerializableExtra(ClassesConstants.PROFISSIONAL);
            cli = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);
            idFbPro = getIntent().getExtras().getString(ClassesConstants.PRO_FB);

            linearLayout = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            listener = new OnClickFoto() {
                @Override
                public void onClick(int idFoto, String url) {

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View vDialog = inflater.inflate(R.layout.item_foto_maximzed, null);

                    ImageView img = vDialog.findViewById(R.id.fotoMax);

                    if (url != null) {
                        Picasso.get()
                                .load(url)
                                .into(img);
                    }

                    alertD.setView(vDialog);
                    alertD.setPositiveButton("OK", null);
                    alertD.show();
                }
            };

            if (idFbPro != null && !idFbPro.equals("")) {
                getInfosPro();
            }
            else{
                setInfos();
            }

        } catch (Exception ex) {
            alertD.setTitle("ERRO:");
            alertD.setMessage(ex.getMessage());
            alertD.show();
        }
    }

    private void getInfosPro() {

        Call<Cliente> call = crudPro.getInfosPro(idFbPro);
        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                Cliente pro = response.body();

                try {
                    if (pro == null) {
                        throw new Exception("Falha ao receber resposta do servidor.");
                    }
                    if (pro.error != null && pro.error.equals("true")) {
                        throw new Exception(pro.msg);
                    }
                    //Convertendo data yyyy-mm-dd para dd/MM/yyyy
//                    pro.setDataNascimento(MetodosEstaticos.convertDateInForBr(pro.getDataNascimento()));
                    profissionalDetails = pro;
                    setInfos();

                } catch (Exception ex) {
                    MetodosEstaticos.toastMsg(context, ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                //Falha na requisição
                MetodosEstaticos.testConnectionFailed(t, context);
                Intent it = new Intent(DetailsProfissional.this, MainActivity.class);
                it.putExtra(ClassesConstants.CLIENTE, cli);
                startActivity(it);
                DetailsProfissional.this.finish();
            }
        });
    }

    public void setInfos() throws Exception{
        mViewHolder.nome.setText(profissionalDetails.getNome());
        mViewHolder.exp.setText("Experiência: " + MetodosEstaticos.calcularExpPro(profissionalDetails.getExperiencia()) + " (anos.meses)");
        mViewHolder.categoria.setText("Categoria: " + profissionalDetails.getCategoria());
        mViewHolder.profissao.setText("Profissão: " + profissionalDetails.getProfissao());
        mViewHolder.cidade.setText(profissionalDetails.getEnderecoCidade() + "/" + profissionalDetails.getEnderecoEstado());
        mViewHolder.qntAv.setText("Média de " + profissionalDetails.getQntAv() + " avaliações.");
        mViewHolder.descricao.setText(profissionalDetails.getDescricao());
        mViewHolder.formacao.setText(profissionalDetails.getFormacao());

        if (profissionalDetails.getFoto() != null && !profissionalDetails.getFoto().equals("")) {
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

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        String dataAtual = date.format(Calendar.getInstance().getTime());
        String anoAtual = dataAtual.substring(6);
        int anoAt = Integer.valueOf(anoAtual);

        // dtNascimento = '2000-03-29T03:00:00.000Z'
        String ano = profissionalDetails.getDataNascimento().substring(0, 4);
        int anoDt = Integer.valueOf(ano);

        int dtNascimento = anoAt - anoDt;
        mViewHolder.idade.setText("Idade: " + dtNascimento + " anos.");

//        getFotos();
    }

    int contador = 0;

    private void getFotos() {
        Call<List<FotosServicos>> call = crudPro.getFotosServices(profissionalDetails.getIdProfissional());
        call.enqueue(new Callback<List<FotosServicos>>() {
            @Override
            public void onResponse(Call<List<FotosServicos>> call, Response<List<FotosServicos>> response) {
                List<FotosServicos> listFotos = response.body();
                if (listFotos == null) {
                    mViewHolder.lblSemFotos.setVisibility(View.VISIBLE);
                    mViewHolder.recyclerView.setVisibility(View.GONE);
                    return;
                }
                if (listFotos.size() == 1 && listFotos.get(0).error.equals("true")) {
                    if (listFotos.get(0).msg.equals("vazio")) {
                        mViewHolder.lblSemFotos.setVisibility(View.VISIBLE);
                        mViewHolder.recyclerView.setVisibility(View.GONE);
                        return;
                    } else {
                        MetodosEstaticos.toastMsg(context, listFotos.get(0).msg);
                    }
                } else {
                    setListFotos(listFotos);
                }
            }

            @Override
            public void onFailure(Call<List<FotosServicos>> call, Throwable t) {

                if (contador <= 5) {
                    Handler handler = new Handler();
                    long delay = 4000;
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            MetodosEstaticos.toastMsg(context, "Não foi possível obter as fotos dos serviços concluídos do profissional..."
                                    + "\nTentando novamente...");
                            contador++;
                            getFotos();
                        }
                    }, delay);
                }
                if (contador > 5) {
                    MetodosEstaticos.toastMsg(context, "Não foi possível obter as fotos dos serviços concluídos do profissional...");
                }

            }
        });
    }

    public void setListFotos(List<FotosServicos> list) {
        adapter = new FotosServicosAdapter(list, listener);
        mViewHolder.recyclerView.setAdapter(adapter);
        mViewHolder.recyclerView.setLayoutManager(linearLayout);
    }

    public void btnChat(View v) {

        //verificando se contato já existe
        FirebaseFirestore.getInstance().collection("/contatos")
                .whereEqualTo("idUser", FirebaseAuth.getInstance().getUid())
                .whereEqualTo("contato", profissionalDetails.getIdFb())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException ex) {
                        if (ex != null) {
                            MetodosEstaticos.toastMsg(context, ex.getMessage());
                            return;
                        }

                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();

                        // verificando profissional ja existe na lista de contatos
                        if (docs.size() > 0) {
                            for (DocumentSnapshot doc : docs) {
                                ContatosFb user = doc.toObject(ContatosFb.class);

                                if (user != null && user.getContato().equals(profissionalDetails.getIdFb())) {
                                    Intent intent = new Intent(DetailsProfissional.this, ChatActivity.class);
                                    ContatosFb pro = new ContatosFb(FirebaseAuth.getInstance().getUid(), profissionalDetails.getNome(), profissionalDetails.getFoto(), profissionalDetails.getIdFb());
                                    intent.putExtra(ClassesConstants.PROFISSIONAL, pro);
                                    intent.putExtra(ClassesConstants.CLIENTE, cli);
                                    startActivity(intent);
                                }
                            }
                        } else {
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
                                                            intent.putExtra(ClassesConstants.CLIENTE, cli);
                                                            startActivity(intent);
                                                        }
                                                    });
                                        }
                                    });
                        }
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
        TextView lblSemFotos;
        EditText descricao;
        EditText formacao;
        ImageView estrelas;
        ImageView fotoProfissional;
        Button btnNegociar;
        RecyclerView recyclerView;
    }
}