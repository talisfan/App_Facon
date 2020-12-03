package pacote.faconapp.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.controller.ValidarCadPro;
import pacote.faconapp.controller.ValidarCompletarCad;
import pacote.faconapp.listener.mask.Mascaras;
import pacote.faconapp.model.data.ApiCep;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudPro;
import pacote.faconapp.model.dominio.crud.CrudUser;
import pacote.faconapp.model.dominio.crud.ServiceCep;
import pacote.faconapp.model.dominio.entidades.Cep;
import pacote.faconapp.model.dominio.entidades.Cliente;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentInfosPessoais extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentInfosPessoais() {
        // Required empty public constructor
    }

    public static FragmentInfosPessoais newInstance(String param1, String param2) {
        FragmentInfosPessoais fragment = new FragmentInfosPessoais();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Cliente cli;
    private AlertDialog.Builder alertD;
    private ViewHolder mViewHolder= new ViewHolder();
    private Context context;
    private CrudUser crudUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_infos_pessoais, container, false);
        context = getContext();
        alertD = new AlertDialog.Builder(context);
        crudUser = ApiDb.createService(CrudUser.class);

        try {
            cli = (Cliente) getArguments().getSerializable(ClassesConstants.CLIENTE);
            if (cli == null) { throw new Exception("Erro ao receber informações do usuário."); }

            mViewHolder.foto = view.findViewById(R.id.fotoP);
            mViewHolder.nome = view.findViewById(R.id.nomeP);
            mViewHolder.dtNascimento = view.findViewById(R.id.dtNascimentoP);
            mViewHolder.email = view.findViewById(R.id.emailP);
            mViewHolder.telCell = view.findViewById(R.id.telCellP);
            mViewHolder.telFixo = view.findViewById(R.id.telFixoP);
            mViewHolder.endCep = view.findViewById(R.id.endCepP);
            mViewHolder.endRua = view.findViewById(R.id.endRuaP);
            mViewHolder.endBairro = view.findViewById(R.id.endBairroP);
            mViewHolder.endCidade = view.findViewById(R.id.endCidadeP);
            mViewHolder.endEstado = view.findViewById(R.id.endEstadoP);
            mViewHolder.endNum = view.findViewById(R.id.endNumP);
            mViewHolder.editInfos = view.findViewById(R.id.editInfos);
            mViewHolder.editEnd = view.findViewById(R.id.editEnd);

            mViewHolder.nome.setText(cli.getNome());
            mViewHolder.dtNascimento.setText("Nascimento: " + cli.getDataNascimento());
            mViewHolder.email.setText("E-mail: "+ cli.getEmail());
            mViewHolder.telCell.setText("Tel Cel: " + cli.getTelCell());
            mViewHolder.endCep.setText("Cep: " + cli.getEndCep());
            mViewHolder.endRua.setText(cli.getEnderecoRua());
            mViewHolder.endNum.setText(" - "+ cli.getEnderecoNum());
            mViewHolder.endBairro.setText(cli.getEnderecoBairro());
            mViewHolder.endCidade.setText(cli.getEnderecoCidade() + " - ");
            mViewHolder.endEstado.setText(cli.getEnderecoEstado());

            if(cli.getTelFixo() != null && !cli.getTelFixo().equals("")) { mViewHolder.telFixo.setText("Tel Fixo: " + cli.getTelFixo()); }
            else{ mViewHolder.telFixo.setVisibility(View.GONE); }

            if(cli.getFoto() != null && cli.getFoto() != ""){
                Picasso.get()
                        .load(cli.getFoto())
                        .into(mViewHolder.foto);
            }

            // eventos de click para editar informacoes de contato
            mViewHolder.editInfos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    LayoutInflater inflater = requireActivity().getLayoutInflater();
                    View vDialog = inflater.inflate(R.layout.dialog_contato, null);

                    final EditText txtTelCell = vDialog.findViewById(R.id.txtTelCell);
                    final EditText txtTelFixo = vDialog.findViewById(R.id.txtTelFixo);

                    Mascaras maskTelCell = new Mascaras("(##) #####-####", txtTelCell);
                    txtTelCell.addTextChangedListener(maskTelCell);
                    Mascaras maskTelFixo = new Mascaras("(##) ####-####", txtTelFixo);
                    txtTelFixo.addTextChangedListener(maskTelFixo);

                    txtTelCell.setText(cli.getTelCell());
                    txtTelFixo.setText(cli.getTelFixo() == null ? "" : cli.getTelFixo());

                    alertD.setView(vDialog);
                    alertD.setNegativeButton("CANCELAR", null);
                    alertD.setPositiveButton("SALVAR", new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Cliente c = new Cliente();
                                c.setTelCell(MetodosEstaticos.formatarDados(txtTelCell.getText().toString()));
                                c.setTelFixo(MetodosEstaticos.formatarDados(txtTelFixo.getText().toString()));
                                c.setId(cli.getId());

                                ValidarCadPro validar = new ValidarCadPro();
                                if (validar.validarContato(context, c)) {
                                    MetodosEstaticos.snackMsg(view, "Carregando...");
                                    editContato(c);
                                }
                            } catch (Exception ex) {
                                MetodosEstaticos.snackMsg(getView(), ex.getMessage());
                            }
                        }
                    });
                    alertD.show();
                }
            });

            // eventos de click para editar endereco
            mViewHolder.editEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    LayoutInflater inflater = requireActivity().getLayoutInflater();
                    View vDialog = inflater.inflate(R.layout.dialog_endereco, null);
                    final ServiceCep mServiceCep = ApiCep.createService(ServiceCep.class);

                    final EditText txtCep = vDialog.findViewById(R.id.txtCep);
                    final EditText txtRua = vDialog.findViewById(R.id.txtRua);
                    final EditText txtEndNum = vDialog.findViewById(R.id.txtEndNum);
                    final EditText txtCidade = vDialog.findViewById(R.id.txtCidade);
                    final EditText txtEstado = vDialog.findViewById(R.id.txtEstado);
                    final EditText txtBairro = vDialog.findViewById(R.id.txtBairro);
                    final ImageView lupa = vDialog.findViewById(R.id.imgLupa);

                    lupa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(txtCep.getText().length() == 9) {
                                if (MetodosEstaticos.isConnected(context)) {

                                    String cep = txtCep.getText().toString().replace("-", "");
                                    Call<Cep> call = mServiceCep.buscaCep(cep);
                                    call.enqueue(new Callback<Cep>() {
                                        @Override
                                        public void onResponse(Call<Cep> call, Response<Cep> response) {
                                            Cep listEnd = response.body();
                                            if (listEnd != null && listEnd.getEndRua() != null) {

                                                MetodosEstaticos.toastMsg(context,"Pronto !");
                                                txtRua.setText(listEnd.getEndRua());
                                                txtCidade.setText(listEnd.getEndCidade());
                                                txtEstado.setText(listEnd.getEndEstado());
                                                txtBairro.setText(listEnd.getEndBairro());

                                            } else {
                                                MetodosEstaticos.toastMsg(context,"Cep não encontrado !");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Cep> call, Throwable t) {
                                            //Falha na requisição
                                            alertD.setTitle("ERRO:");
                                            alertD.setMessage(t.getMessage());
                                            alertD.show();
                                        }
                                    });
                                } else {
                                    MetodosEstaticos.toastMsg(context, ExceptionsServer.NO_CONNECTION);
                                }
                            }else{
                                MetodosEstaticos.toastMsg(context, ExceptionsCadastro.CEP_INVALIDO);
                            }
                        }
                    });

                    Mascaras maskCep = new Mascaras("#####-###", txtCep);
                    txtCep.addTextChangedListener(maskCep);

                    txtCep.setText(cli.getEndCep());
                    txtRua.setText(cli.getEnderecoRua());
                    txtEndNum.setText(cli.getEnderecoNum());
                    txtCidade.setText(cli.getEnderecoCidade());
                    txtEstado.setText(cli.getEnderecoEstado());
                    txtBairro.setText(cli.getEnderecoBairro());

                    alertD.setView(vDialog);
                    alertD.setNegativeButton("CANCELAR", null);
                    alertD.setPositiveButton("SALVAR", new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Cliente c = new Cliente();
                                c.setEndCep(MetodosEstaticos.formatarDados(txtCep.getText().toString()));
                                c.setEnderecoRua(txtRua.getText().toString());
                                c.setEnderecoNum(txtEndNum.getText().toString());
                                c.setEnderecoCidade(txtCidade.getText().toString());
                                c.setEnderecoBairro(txtBairro.getText().toString());
                                c.setEnderecoEstado(txtEstado.getText().toString());
                                c.setId(cli.getId());

                                ValidarCompletarCad validar = new ValidarCompletarCad();
                                if (validar.validarCompletarCad(c, context)) {
                                    MetodosEstaticos.snackMsg(view, "Carregando...");
                                    editEndereco(c);
                                }
                            } catch (Exception ex) {
                                MetodosEstaticos.snackMsg(getView(), ex.getMessage());
                            }
                        }
                    });
                    alertD.show();
                }
            });

        }catch (Exception ex){
            alertD.setTitle("Error");
            alertD.setMessage(ex.getMessage());
            alertD.setNegativeButton("OK", null);
            alertD.show();
        }
        return view;
    }

    public void editContato(final Cliente cliente){
        Call<Cliente> call = crudUser.attContato(cliente.getTelCell(), cliente.getTelFixo(), cliente.getId());
        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                //Sucesso na requisição (mesmo que retorno seja de erro. ex: 404)
                Cliente c = response.body();
                try {
                    if (c.error != null && c.error.equals("true")) {
                        throw new Exception(c.msg);
                    } else {
                        cli.setTelCell(cliente.getTelCell());
                        MetodosEstaticos.snackMsg(getView(), "Alterações salvas.");

                        mViewHolder.telCell.setText("Tel Cel: " + cliente.getTelCell());
                        if(cliente.getTelFixo() != null) {
                            cli.setTelFixo(cliente.getTelFixo());
                            if(!MetodosEstaticos.isCampoVazio(cliente.getTelFixo())){
                                mViewHolder.telFixo.setVisibility(View.VISIBLE);
                                mViewHolder.telFixo.setText("Tel Fixo: " + cliente.getTelFixo());
                            }else{
                                mViewHolder.telFixo.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception ex) {
                    MetodosEstaticos.snackMsg(getView(), ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                //Falha na requisição
                MetodosEstaticos.testConnectionFailed(t, context);
            }
        });
    }

    public void editEndereco(final Cliente cliente){
        Call<Cliente> call = crudUser.attEndereco(
                cliente.getEndCep(), cliente.getEnderecoRua(), cliente.getEnderecoNum(), cliente.getEnderecoCidade(),
                cliente.getEnderecoEstado(), cliente.getEnderecoBairro(), cliente.getId()
        );
        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                //Sucesso na requisição (mesmo que retorno seja de erro. ex: 404)
                Cliente c = response.body();
                try {
                    if (c.error != null && c.error.equals("true")) {
                        throw new Exception(c.msg);
                    } else {
                        cli.setEndCep(cliente.getEndCep());
                        cli.setEnderecoRua(cliente.getEnderecoRua());
                        cli.setEnderecoBairro(cliente.getEnderecoBairro());
                        cli.setEnderecoCidade(cliente.getEnderecoCidade());
                        cli.setEnderecoEstado(cliente.getEnderecoEstado());
                        cli.setEnderecoNum(cliente.getEnderecoNum());

                        MetodosEstaticos.snackMsg(getView(), "Alterações salvas.");

                        mViewHolder.endCep.setText("Cep: " + cliente.getEndCep());
                        mViewHolder.endRua.setText(cliente.getEnderecoRua());
                        mViewHolder.endNum.setText(" - " + cliente.getEnderecoNum());
                        mViewHolder.endBairro.setText(cliente.getEnderecoBairro());
                        mViewHolder.endCidade.setText(cliente.getEnderecoCidade());
                        mViewHolder.endEstado.setText(cliente.getEnderecoEstado());
                    }
                } catch (Exception ex) {
                    MetodosEstaticos.snackMsg(getView(), ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                //Falha na requisição
                MetodosEstaticos.testConnectionFailed(t, context);
            }
        });
    }

    private class ViewHolder{
        ImageView foto;
        TextView nome;
        TextView dtNascimento;
        TextView email;
        TextView telCell;
        TextView telFixo;
        TextView endCep;
        TextView endRua;
        TextView endCidade;
        TextView endBairro;
        TextView endEstado;
        TextView endNum;
        TextView editInfos;
        TextView editEnd;
    }
}