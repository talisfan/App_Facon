package pacote.faconapp.view.profissional;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.adapter.PesqServPrestadoAdapter;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.controller.ValidarCadPro;
import pacote.faconapp.listener.OnClickProfissao;
import pacote.faconapp.listener.mask.Mascaras;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudPro;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.TipoServico;
import pacote.faconapp.view.Home;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EscolhaServicoPrestado extends AppCompatActivity {

    private List<TipoServico> serviceList;
    private Context context;
    private ViewHolder mViewHolder = new ViewHolder();
    private OnClickProfissao listener;
    private PesqServPrestadoAdapter pesqServPrestadoAdapter;
    private LinearLayoutManager linearLayout;
    private AlertDialog.Builder alertD;
    private CrudPro crudPro;
    private ValidarCadPro validarCadPro;
    private TipoServico tipoServico;
    private EditText pesq;
    private Cliente pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_servico_prestado);

        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
            context = this;
            validarCadPro = new ValidarCadPro();
            alertD = new AlertDialog.Builder(context);
            linearLayout = new LinearLayoutManager(context);
            crudPro = ApiDb.createService(CrudPro.class);

            mViewHolder.lblTempoExperiencia = findViewById(R.id.lblTempoExperiencia);
            mViewHolder.lblExemplo = findViewById(R.id.lblExemplo);
            mViewHolder.txtTempoExp = findViewById(R.id.txtTempoExp);
            mViewHolder.btnEnviarExp = findViewById(R.id.btnEnviarExp);
            mViewHolder.txtServicoPrestado = findViewById(R.id.txtPesquisaServicoPrestado);
            mViewHolder.pesquisa = findViewById(R.id.txtPesquisaServicoPrestado);
            mViewHolder.recyclerView = findViewById(R.id.recyclerPesquisaServPrestados);
            mViewHolder.progressBar = findViewById(R.id.progressBar);

            pro = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);
            tipoServico = (TipoServico) getIntent().getSerializableExtra(ClassesConstants.TIPO_SERVICO);

            Mascaras maskDtNascimento = new Mascaras("##/####", mViewHolder.txtTempoExp);
            mViewHolder.txtTempoExp.addTextChangedListener(maskDtNascimento);

            listener = new OnClickProfissao() {
                @Override
                public void onClick(final int id, final String profissao) {
                    alertD.setTitle("CONFIRMAÇÃO");
                    alertD.setMessage("Sua profissão é " + profissao + ", está correto ?");
                    alertD.setNegativeButton("SIM", new DialogInterface.OnClickListener() {
                        //resposta ao click de confirmação da profissao
                        public void onClick(DialogInterface dialog, int whichButton) {
                            mViewHolder.pesquisa.setText(profissao);
                            tipoServico.setProfissao(profissao);
                            tipoServico.setId(id);

                            pro.setIdProfissao(id);
                            pro.setProfissao(profissao);
                            pro.setCategoria(tipoServico.getCategoria());

                            mViewHolder.recyclerView.setVisibility(View.GONE);
                            mViewHolder.lblTempoExperiencia.setVisibility(View.VISIBLE);
                            mViewHolder.lblExemplo.setVisibility(View.VISIBLE);
                            mViewHolder.txtTempoExp.setVisibility(View.VISIBLE);
                            mViewHolder.btnEnviarExp.setVisibility(View.VISIBLE);
                            mViewHolder.txtServicoPrestado.setEnabled(false);
                        }
                    });
                    alertD.setNeutralButton("NÃO", null);
                    alertD.show();
                }
            };

            //tratando caso o botão ENTER/SEARCH for clicado
            pesq = findViewById(R.id.txtPesquisaServicoPrestado);
            pesq.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || actionId == EditorInfo.IME_ACTION_DONE) {
                        pesq.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pesquisarServico(pesq);
                            }
                        });
                    }
                    return false;
                }
            });

            setList();

        } catch (Exception ex) {
            alertD.setTitle("ERRO:");
            alertD.setMessage(ex.getMessage());
            alertD.show();
        }
    }

    public void pesquisarServico(View v) {
        try {
            String profissao = mViewHolder.pesquisa.getText().toString().toLowerCase();
            tipoServico.setProfissao("AND profissao like '%" + profissao + "%'");
            setList();

        } catch (Exception ex) {
            MetodosEstaticos.toastMsg(context, ex.getMessage());
        }
    }

    public void setList() {

        try {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            Call<List<TipoServico>> call = crudPro.seekProfissions(tipoServico);
            call.enqueue(new Callback<List<TipoServico>>() {
                @Override
                public void onResponse(Call<List<TipoServico>> call, Response<List<TipoServico>> response) {
                    serviceList = response.body();
                    mViewHolder.progressBar.setVisibility(View.GONE);

                    if (serviceList != null && serviceList.size() == 1) {
                        TipoServico s = serviceList.get(0);
                        if (s.error.equals("true")) {
                            if (s.msg.equals("vazio")) {

                                mViewHolder.recyclerView.setAdapter(null);
                                LinearLayoutManager linearLayout = new LinearLayoutManager(context);
                                mViewHolder.recyclerView.setLayoutManager(linearLayout);
                                alertD.setTitle("OPS...");
                                alertD.setMessage(
                                    "Parece que ainda não temos o seu serviço cadastrado na nossa base " +
                                    "de dados.\nDeseja enviar sua profissão para ser analisada e, possivelmente, " +
                                    "adicionada posteriormente ?");
                                alertD.setNegativeButton("SIM", new DialogInterface.OnClickListener() {
                                    //resposta ao click de confirmação da profissao
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                    }
                                });
                                alertD.setNeutralButton("NÃO", null);
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                                return;

                            } else {
                                MetodosEstaticos.toastMsg(context, s.msg);
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                                return;
                            }
                        }
                    }

                    if (serviceList != null) {
                        pesqServPrestadoAdapter = new PesqServPrestadoAdapter(serviceList, listener);
                        mViewHolder.recyclerView.setAdapter(pesqServPrestadoAdapter);
                        mViewHolder.recyclerView.setLayoutManager(linearLayout);
                    }
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<List<TipoServico>> call, Throwable t) {
                    MetodosEstaticos.testConnectionFailed(t, context);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                }
            });
        } catch (Exception ex) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            MetodosEstaticos.toastMsg(context, ex.getMessage());
        }
    }

    public void enviarExp(View v) {
        try {
                pro.setExperiencia(mViewHolder.txtTempoExp.getText().toString());

            if (validarCadPro.validarCadPro(pro, context)) {
                // convertendo data para inserir no banco
                pro.setExperiencia(MetodosEstaticos.convertDateBrForIn("01/" + pro.getExperiencia()));
                mViewHolder.progressBar.setVisibility(View.VISIBLE);

                Call<Cliente> call = crudPro.resgisterProfessional(pro);
                call.enqueue(new Callback<Cliente>() {
                    @Override
                    public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                        Cliente p = response.body();
                        mViewHolder.progressBar.setVisibility(View.GONE);

                        if (p.error.equals("true")) {
                            MetodosEstaticos.toastMsg(context, p.msg);
                        }else {
                            pro.setIdProfissional(p.getIdProfissional());

                            alertD.setTitle("SUCESSO!");
                            alertD.setMessage("Profissional cadastrado com sucesso.\nRedirecionando...");
                            alertD.setNeutralButton(null, null);
                            alertD.setPositiveButton(null, null);
                            alertD.setNegativeButton(null, null);
                            alertD.show();

                            final Intent it = new Intent(context, Home.class);
                            it.putExtra(ClassesConstants.CLIENTE, pro);

                            Handler handler = new Handler();
                            long delay = 3000;
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finishAffinity();
                                    startActivity(it);
                                }
                            }, delay);
                        }
                    }

                    @Override
                    public void onFailure(Call<Cliente> call, Throwable t) {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        MetodosEstaticos.testConnectionFailed(t, context);
                    }
                });
            }

        } catch (Exception ex) {
            alertD.setTitle("ERRO:");
            alertD.setMessage(ex.getMessage());
            alertD.setNeutralButton(null, null);
            alertD.setPositiveButton(null, null);
            alertD.setNegativeButton("OK", null);
            alertD.show();
        }
    }

    private class ViewHolder {
        RecyclerView recyclerView;
        EditText pesquisa;
        EditText txtTempoExp;
        TextView lblTempoExperiencia;
        TextView lblExemplo;
        Button btnEnviarExp;
        EditText txtServicoPrestado;
        ProgressBar progressBar;
    }
}