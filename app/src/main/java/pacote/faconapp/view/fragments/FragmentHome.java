package pacote.faconapp.view.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.List;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.InfosLoginConstants;
import pacote.faconapp.constants.LoadingConstants;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudUser;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.TipoServico;
import pacote.faconapp.view.cliente.ListServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    AlertDialog.Builder alertD;
    private Context context;
    private Intent it;
    private EditText txtPesquisaServico;
    private CrudUser crudUser;
    private Cliente cli;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try {
            it = new Intent();
            context = getContext();
            alertD = new AlertDialog.Builder(context);
            crudUser = ApiDb.createService(CrudUser.class);

        } catch (Exception ex) {
            alertD.setTitle("Error: ");
            alertD.setMessage(ex.getMessage());
            alertD.setNegativeButton("OK", null);
            alertD.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        txtPesquisaServico = view.findViewById(R.id.txtPesquisaServico);

        try {
            cli = (Cliente) getArguments().getSerializable(ClassesConstants.CLIENTE);
            if (cli == null) { throw new Exception("Erro ao receber informações do usuário."); }

            //tratando caso o botão ENTER/SEARCH for clicado
            txtPesquisaServico.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || actionId == EditorInfo.IME_ACTION_DONE) {
                        buscarServico();
                    }
                    return false;
                }
            });

            View imgPesquisaServ = view.findViewById(R.id.imgPesquisaServ);
            imgPesquisaServ.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buscarServico();
                }
            });

            view.findViewById(R.id.btnConstrucao).setOnClickListener(this);
            view.findViewById(R.id.btnServDomestico).setOnClickListener(this);
            view.findViewById(R.id.btnAssisTec).setOnClickListener(this);
            view.findViewById(R.id.btnEventos).setOnClickListener(this);
            view.findViewById(R.id.btnModa).setOnClickListener(this);
            view.findViewById(R.id.btnTecnologia).setOnClickListener(this);
            view.findViewById(R.id.btnAulas).setOnClickListener(this);
            view.findViewById(R.id.btnConsultoria).setOnClickListener(this);
            view.findViewById(R.id.btnSaude).setOnClickListener(this);
            view.findViewById(R.id.btnVeiculos).setOnClickListener(this);

        } catch (Exception ex) {
            alertD.setTitle("Error: ");
            alertD.setMessage(ex.getMessage());
            alertD.setNegativeButton("OK", null);
            alertD.show();
        }

        return view;
    }

    public void buscarServico() {
        try {
            final TipoServico tipoServico = new TipoServico();
            final String servicoPesq = txtPesquisaServico.getText().toString();
            tipoServico.setCategoria("vazio");
            // profissao nesse caso profissao = pesquisa
            tipoServico.setProfissao("WHERE s.profissao like '%" + servicoPesq + "%' AND u.id <> " + cli.getId() +
                    " AND u.ativo = 1 group by p.idProfissional order by estrelas desc");

            Call<List<Cliente>> call = crudUser.seekProfessionals(tipoServico);
            call.enqueue(new Callback<List<Cliente>>() {
                @Override
                public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                    List<Cliente> listPro = response.body();

                    if (listPro.size() == 1) {
                        Cliente p = listPro.get(0);
                        if (p.error != null && p.error.equals("true")) {
                            if (p.msg.equals("vazio")) {
                                alertD.setTitle("Serviço não encontrado");
                                alertD.setMessage("Não encontramos nenhum profissional que trabalhe como " + servicoPesq + "...");
                                alertD.setNegativeButton("OK", null);
                                alertD.show();
                                return;
                            }
                        }
                    }
                    it = new Intent(context, ListServices.class);
                    it.putExtra(ClassesConstants.CLIENTE, cli);
                    it.putExtra(ClassesConstants.TIPO_SERVICO, tipoServico);
                    it.putExtra(LoadingConstants.BUSCA, servicoPesq);

                    // deixando primeira letra maiuscula
                    for (Cliente c : listPro) {
                        //Deixando primeira letra maiuscula
                        String p = String.valueOf(c.getProfissao().charAt(0));
                        c.setProfissao(p.toUpperCase() + c.getProfissao().substring(1));
                    }
                    it.putExtra(ClassesConstants.PROFISSIONAL_LIST, (Serializable) listPro);

                    startActivity(it);
                }

                @Override
                public void onFailure(Call<List<Cliente>> call, Throwable t) {
                    MetodosEstaticos.toastMsg(context, t.getMessage());
                }
            });

        } catch (Exception ex) {
            alertD.setTitle("Erro:");
            alertD.setMessage(ex.getMessage());
            alertD.show();
        }
    }

    public void clickOpcoes(String categoria) {
        Intent it = new Intent(getActivity(), ListServices.class);
        TipoServico tipoServico = new TipoServico();

        tipoServico.setCategoria("WHERE s.categoria = '" + categoria + "' AND u.id <> " + cli.getId() +
                " AND u.ativo = 1 group by p.idProfissional order by estrelas desc");
        tipoServico.setProfissao("vazio");
        it.putExtra(LoadingConstants.BUSCA, categoria);
        it.putExtra(ClassesConstants.CLIENTE, cli);
        it.putExtra(ClassesConstants.TIPO_SERVICO, tipoServico);
        startActivity(it);
    }

    @Override
    public void onClick(View v) {
        // passando categoria escolhida
        if (v.getId() == R.id.btnConstrucao) {
            clickOpcoes("Construção e Reparos");
        }
        if (v.getId() == R.id.btnServDomestico) {
            clickOpcoes("Serviços Domésticos");
        }
        if (v.getId() == R.id.btnAssisTec) {
            clickOpcoes("Assistência Técnica");
        }
        if (v.getId() == R.id.btnEventos) {
            clickOpcoes("Eventos");
        }
        if (v.getId() == R.id.btnModa) {
            clickOpcoes("Moda e Beleza");
        }
        if (v.getId() == R.id.btnTecnologia) {
            clickOpcoes("Tecnologia e Design");
        }
        if (v.getId() == R.id.btnAulas) {
            clickOpcoes("Aulas");
        }
        if (v.getId() == R.id.btnConsultoria) {
            clickOpcoes("Consultoria");
        }
        if (v.getId() == R.id.btnSaude) {
            clickOpcoes("Saúde");
        }
        if (v.getId() == R.id.btnVeiculos) {
            clickOpcoes("Veículos");
        }
    }
}