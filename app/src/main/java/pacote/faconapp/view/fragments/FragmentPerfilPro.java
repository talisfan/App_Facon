package pacote.faconapp.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.controller.ValidarCadPro;
import pacote.faconapp.listener.mask.Mascaras;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudPro;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.view.profissional.EscolhaCategoriaAtuacao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPerfilPro extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentPerfilPro() {
        // Required empty public constructor
    }

    public static FragmentPerfilPro newInstance(String param1, String param2) {
        FragmentPerfilPro fragment = new FragmentPerfilPro();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Cliente cli;
    private ViewHolder mViewHolder = new ViewHolder();
    private AlertDialog.Builder alertD;
    private Context context;
    private CrudPro crudPro;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getContext();
        alertD = new AlertDialog.Builder(context);
        crudPro = ApiDb.createService(CrudPro.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_perfil_pro, container, false);
        try {
            cli = (Cliente) getArguments().getSerializable(ClassesConstants.CLIENTE);
            if (cli == null) {
                throw new Exception("Erro ao receber informações do usuário.");
            }

            mViewHolder.nome = v.findViewById(R.id.nome);
            mViewHolder.estrelas = v.findViewById(R.id.avPerfilPro);
            mViewHolder.qntAv = v.findViewById(R.id.qntAvPerfilPro);
            mViewHolder.exp = v.findViewById(R.id.lblExpPro);
            mViewHolder.categoria = v.findViewById(R.id.lblCategPro);
            mViewHolder.profissao = v.findViewById(R.id.lblServPro);
            mViewHolder.cidade = v.findViewById(R.id.lblCidade);
            mViewHolder.txtDescricao = v.findViewById(R.id.txtDescricao);
            mViewHolder.txtFormacao = v.findViewById(R.id.txtFormacao);
            mViewHolder.lblEditDesc = v.findViewById(R.id.lblEditDesc);
            mViewHolder.lblEditForm = v.findViewById(R.id.lblEditForm);

            if (cli.getIdProfissional() > 0) {
                //seta as infos do profissional caso o id dele seja maior q 0; se for 0 pq não é profissional
                setInfos();

                // Click para editar descricao
                mViewHolder.lblEditDesc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mViewHolder.lblEditDesc.getText().toString().equals("EDITAR")) {
                            mViewHolder.txtDescricao.setEnabled(true);
                            mViewHolder.lblEditDesc.setText(R.string.lbl_salvar);
                        } else {
                            MetodosEstaticos.snackMsg(view, "Carregando...");
                            mViewHolder.lblEditDesc.setText(R.string.lbl_editar);
                            mViewHolder.txtDescricao.setEnabled(false);
                            editDescricao();
                        }
                    }
                });

                // Click para editar formação
                mViewHolder.lblEditForm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        LayoutInflater inflater = requireActivity().getLayoutInflater();
                        View vDialog = inflater.inflate(R.layout.dialog_formacao, null);

                        final EditText txtInst = vDialog.findViewById(R.id.txtInst);
                        final EditText txtCurso = vDialog.findViewById(R.id.txtCurso);
                        final EditText txtDtInicio = vDialog.findViewById(R.id.txtDtInicio);
                        final EditText txtDtFim = vDialog.findViewById(R.id.txtDtFim);
                        final Spinner spinner = vDialog.findViewById(R.id.spinner);

                        Mascaras maskDtInicio = new Mascaras("##/####", txtDtInicio);
                        txtDtInicio.addTextChangedListener(maskDtInicio);

                        Mascaras maskDtFim = new Mascaras("##/####", txtDtFim);
                        txtDtFim.addTextChangedListener(maskDtFim);

                        String[] items = new String[]{"Graduação", "Pós-graduação", "Técnico", "Curso complementar"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
                        spinner.setAdapter(adapter);

                        alertD.setView(vDialog);
                        alertD.setNegativeButton("CANCELAR", null);
                        alertD.setPositiveButton("SALVAR", new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String formacao =
                                            spinner.getSelectedItem().toString() + " em " +
                                                    txtCurso.getText().toString() + " - " +
                                                    txtInst.getText().toString() + " - Início: " +
                                                    txtDtInicio.getText().toString() + " - Fim: " +
                                                    txtDtFim.getText().toString();

                                    ValidarCadPro validar = new ValidarCadPro();

                                    if (validar.validarFormacao(
                                            txtInst.getText().toString(),
                                            txtCurso.getText().toString(),
                                            txtDtInicio.getText().toString(),
                                            txtDtFim.getText().toString(),
                                            context)) {

                                        MetodosEstaticos.snackMsg(view, "Carregando...");
                                        editFormacao(formacao);
                                    }
                                } catch (Exception ex) {
                                    MetodosEstaticos.snackMsg(getView(), ex.getMessage());
                                }
                            }
                        });
                        alertD.show();
                    }
                });
            } else {
                v.findViewById(R.id.infosPro).setVisibility(View.GONE);
                v.findViewById(R.id.noPro).setVisibility(View.VISIBLE);
                v.findViewById(R.id.btnCadPro).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EscolhaCategoriaAtuacao.class);
                        intent.putExtra(ClassesConstants.CLIENTE, cli);
                        startActivity(intent);
                    }
                });
            }
        }catch (Exception ex){
            alertD.setTitle("Error");
            alertD.setMessage(ex.getMessage());
            alertD.setNegativeButton("OK", null);
            alertD.show();
        }

        return v;
    }

    public void setInfos() {
        mViewHolder.nome.setText(cli.getNome());
        mViewHolder.exp.setText("Experiência: " + MetodosEstaticos.calcularExpPro(cli.getExperiencia()) + " (anos.meses)");
        mViewHolder.categoria.setText("Categoria: " + cli.getCategoria());
        //Deixando primeira letra maiuscula
        String p = String.valueOf(cli.getProfissao().charAt(0));
        cli.setProfissao(p.toUpperCase() + cli.getProfissao().substring(1));
        mViewHolder.profissao.setText("Profissão: " + cli.getProfissao());
        mViewHolder.cidade.setText("De: " + cli.getEnderecoCidade() + "/" + cli.getEnderecoEstado());
        mViewHolder.qntAv.setText("Média de " + cli.getQntAv() + " avaliações.");
        mViewHolder.txtDescricao.setText(cli.getDescricao());
        mViewHolder.txtFormacao.setText(cli.getFormacao());

        switch (cli.getEstrelas()) {
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
    }

    private void editDescricao() {
        Call<Cliente> call = crudPro.attDescricao(cli.getIdProfissional(), mViewHolder.txtDescricao.getText().toString());
        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                //Sucesso na requisição (mesmo que retorno seja de erro. ex: 404)
                Cliente c = response.body();

                try {
                    if (c.error != null && c.error.equals("true")) {
                        mViewHolder.txtDescricao.setText(cli.getDescricao());
                        throw new Exception(c.msg);
                    } else {
                        cli.setDescricao(mViewHolder.txtDescricao.getText().toString());
                        MetodosEstaticos.snackMsg(getView(), "Alterações salvas.");
                    }
                } catch (Exception ex) {
                    MetodosEstaticos.snackMsg(getView(), ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                //Falha na requisição
                mViewHolder.txtDescricao.setText(cli.getDescricao());
                MetodosEstaticos.testConnectionFailed(t, context);
            }
        });
    }

    private void editFormacao(final String formacao) {
        Call<Cliente> call = crudPro.attFormacao(cli.getIdProfissional(), formacao);
        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                //Sucesso na requisição (mesmo que retorno seja de erro. ex: 404)
                Cliente c = response.body();
                try {
                    if (c.error != null && c.error.equals("true")) {
                        throw new Exception(c.msg);
                    } else {
                        MetodosEstaticos.snackMsg(getView(), "Alterações salvas.");
                        mViewHolder.txtFormacao.setText(formacao);
                        cli.setFormacao(formacao);
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

    private static class ViewHolder {
        TextView nome;
        TextView exp;
        TextView categoria;
        TextView profissao;
        TextView cidade;
        TextView qntAv;
        TextView lblEditDesc;
        TextView lblEditForm;
        EditText txtDescricao;
        EditText txtFormacao;
        ImageView estrelas;
    }
}