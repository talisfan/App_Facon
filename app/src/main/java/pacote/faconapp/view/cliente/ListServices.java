package pacote.faconapp.view.cliente;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.adapter.ProfissionalListAdapter;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.InfosLoginConstants;
import pacote.faconapp.constants.LoadingConstants;
import pacote.faconapp.listener.OnClickProfissional;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudUser;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.TipoServico;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListServices extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private ProfissionalListAdapter profissionalListAdapter;
    private LinearLayoutManager linearLayout;

    private AlertDialog.Builder alertD;
    private OnClickProfissional listener;
    private Intent it;
    private Context context;
    private int idUser;

    private CrudUser crudUser;
    private TipoServico tipoServico;
    private List<Cliente> proListMelhorAv;
    private List<Cliente> proListMelhorXp;
    private List<Cliente> proListAlfab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_services);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        context = this;
        alertD = new AlertDialog.Builder(context);
        linearLayout = new LinearLayoutManager(context);
        crudUser = ApiDb.createService(CrudUser.class);

        mViewHolder.lblSemProDaCateg = findViewById(R.id.lblSemProDaCateg);
        mViewHolder.recyclerServices = findViewById(R.id.recyclerServices);
        mViewHolder.lbl = findViewById(R.id.lblTituloCateg);
        mViewHolder.progressBar = findViewById(R.id.progressBar);

        proListMelhorAv = (List<Cliente>) getIntent().getSerializableExtra(ClassesConstants.PROFISSIONAL_LIST);
        tipoServico = (TipoServico) getIntent().getSerializableExtra(ClassesConstants.TIPO_SERVICO);
        idUser = getIntent().getExtras().getInt(InfosLoginConstants.ID_USER);
        String busca = getIntent().getExtras().getString(LoadingConstants.BUSCA);

        if (tipoServico.getCategoria().equals("vazio")) {
            mViewHolder.lbl.setText("Mostrando todos " + busca + "...");
        } else {
            mViewHolder.lbl.setText("Mostrando todos profissionais de " + busca);
        }

        //click que recebe Id do profissional pela variavel listener da lista e passara para DetailsProfissional em um bundle
        listener = new OnClickProfissional() {
            @Override
            public void onClickProfissional(Cliente pro) {
                it = new Intent(context, DetailsProfissional.class);
                it.putExtra(InfosLoginConstants.ID_USER, idUser);
                it.putExtra(ClassesConstants.CLIENTE, pro);
                startActivity(it);
            }
        };

        try {
            if (tipoServico.getProfissao().equals("vazio")) {

                mViewHolder.progressBar.setVisibility(View.VISIBLE);

                Call<List<Cliente>> call = crudUser.seekProfessionals(tipoServico);
                call.enqueue(new Callback<List<Cliente>>() {
                    @Override
                    public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                        List<Cliente> listPro = response.body();

                        if (listPro != null && listPro.size() == 1) {
                            Cliente p = listPro.get(0);
                            if (p.error.equals("true")) {
                                if (p.msg.equals("vazio")) {
                                    mViewHolder.lblSemProDaCateg.setVisibility(View.VISIBLE);
                                    mViewHolder.progressBar.setVisibility(View.GONE);
                                    return;
                                }
                            }
                        }

                        if(listPro != null) {
                            mViewHolder.progressBar.setVisibility(View.GONE);
                            listPro = primeiraMaius(listPro);
                            proListMelhorAv = listPro;
                            setList(listPro);
                        }else{
                            MetodosEstaticos.toastMsg(context, "Erro ao receber lista de profissionais.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Cliente>> call, Throwable t) {
                        mViewHolder.progressBar.setVisibility(View.GONE);
                        MetodosEstaticos.toastMsg(context, t.getMessage());
                    }
                });

            } else {

                setList(proListMelhorAv);
            }

        } catch (Exception ex) {

            alertD.setTitle("ERRO:");
            alertD.setMessage(ex.getMessage());
            alertD.show();
        }
    }

    public void setList(List<Cliente> list) {

        profissionalListAdapter = new ProfissionalListAdapter(list, listener);
        mViewHolder.recyclerServices.setAdapter(profissionalListAdapter);
        mViewHolder.recyclerServices.setLayoutManager(linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);
        try {

            switch (item.getItemId()) {
                case R.id.filter_maiorExp:

                    if(proListMelhorXp == null){
                        mViewHolder.progressBar.setVisibility(View.VISIBLE);
                        // criado objeto local com mesmo valo do global para poder alterar valor
                        // das propriedades e fazer a consulta
                        TipoServico tipoServico1 = new TipoServico();
                        tipoServico1.setCategoria(tipoServico.getCategoria());
                        tipoServico1.setProfissao(tipoServico.getProfissao());

                        if (tipoServico1.getProfissao().equals("vazio")) {
                            tipoServico1.setCategoria(tipoServico1.getCategoria().replace
                                    ("order by estrelas desc", "order by p.dtExperiencia asc"));
                        }
                        if (tipoServico1.getCategoria().equals("vazio")) {
                            tipoServico1.setProfissao(tipoServico1.getProfissao().replace
                                    ("order by estrelas desc", "order by p.dtExperiencia asc"));
                        }

                        Call<List<Cliente>> call = crudUser.seekProfessionals(tipoServico1);
                        call.enqueue(new Callback<List<Cliente>>() {
                            @Override
                            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                                List<Cliente> listPro = response.body();

                                if (listPro != null && listPro.size() == 1) {
                                    Cliente p = listPro.get(0);
                                    if (p.error.equals("true")) {
                                        if (p.msg.equals("vazio")) {
                                            mViewHolder.lblSemProDaCateg.setVisibility(View.VISIBLE);
                                            mViewHolder.progressBar.setVisibility(View.GONE);
                                            return;
                                        }
                                    }
                                }
                                if(listPro != null) {
                                    listPro = primeiraMaius(listPro);
                                    proListMelhorXp = listPro;
                                    setList(listPro);
                                }else{
                                    MetodosEstaticos.toastMsg(context, "Erro ao filtrar lista de profissionais.");
                                }
                                mViewHolder.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                                mViewHolder.progressBar.setVisibility(View.GONE);
                                MetodosEstaticos.testConnectionFailed(t, context);
                            }
                        });
                    } else {
                        setList(proListMelhorXp);
                    }

                    break;

                case R.id.filter_melhorAv:

                    setList(proListMelhorAv);

                    break;

                case R.id.filter_alfabetico:

                    if (proListAlfab == null) {

                        mViewHolder.progressBar.setVisibility(View.VISIBLE);
                        TipoServico tipoServico1 = new TipoServico();
                        tipoServico1.setCategoria(tipoServico.getCategoria());
                        tipoServico1.setProfissao(tipoServico.getProfissao());

                        if (tipoServico1.getProfissao().equals("vazio")) {
                            tipoServico1.setCategoria(tipoServico1.getCategoria().replace
                                    ("order by estrelas desc", "order by u.nome asc"));
                        }else {
                            tipoServico1.setProfissao(tipoServico1.getProfissao().replace
                                    ("order by estrelas desc", "order by u.nome asc"));
                        }
                        mViewHolder.progressBar.setVisibility(View.VISIBLE);

                        Call<List<Cliente>> call = crudUser.seekProfessionals(tipoServico1);
                        call.enqueue(new Callback<List<Cliente>>() {
                            @Override
                            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                                List<Cliente> listPro = response.body();

                                if (listPro != null && listPro.size() == 1) {
                                    Cliente p = listPro.get(0);
                                    if (p.error.equals("true")) {
                                        if (p.msg.equals("vazio")) {
                                            mViewHolder.lblSemProDaCateg.setVisibility(View.VISIBLE);
                                            mViewHolder.progressBar.setVisibility(View.GONE);
                                            return;
                                        }
                                    }
                                }
                                if(listPro != null) {
                                    mViewHolder.progressBar.setVisibility(View.GONE);
                                    listPro = primeiraMaius(listPro);
                                    proListAlfab = listPro;
                                    setList(listPro);
                                }
                                else{
                                    MetodosEstaticos.toastMsg(context, "Erro ao filtrar profissionais.");
                                }
                                mViewHolder.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                                mViewHolder.progressBar.setVisibility(View.GONE);
                                MetodosEstaticos.toastMsg(context, t.getMessage());
                            }
                        });
                    } else {
                        setList(proListAlfab);
                    }

                    break;

                case R.id.filter_distancia:

                    break;
            }

        } catch (Exception ex) {
            alertD.setTitle("Erro:");
            alertD.setMessage(ex.getMessage());
            alertD.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public List<Cliente> primeiraMaius(List<Cliente> list){
        for(Cliente c : list){
            //Deixando primeira letra maiuscula
            String p = String.valueOf(c.getProfissao().charAt(0));
            c.setProfissao(p.toUpperCase() + c.getProfissao().substring(1));
        }
        return list;
    }

    private static class ViewHolder {
        RecyclerView recyclerServices;
        TextView lbl;
        TextView lblSemProDaCateg;
        ProgressBar progressBar;
    }

}
