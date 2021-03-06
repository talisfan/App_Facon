package pacote.faconapp.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.adapter.FotosServicosAdapter;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.controller.ValidarCadPro;
import pacote.faconapp.listener.OnClickFoto;
import pacote.faconapp.listener.mask.Mascaras;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudPro;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.FotosServicos;
import pacote.faconapp.model.dominio.entidades.chat.UserFireBase;
import pacote.faconapp.view.EscolhaUser;
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
    private OnClickFoto listener;
    private FotosServicosAdapter adapter;
    private LinearLayoutManager linearLayout;

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
                alertD.setNeutralButton("EXCLUIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFoto(idFoto);
                    }
                });
                alertD.show();
            }
        };
    }

    private void deleteFoto(int idFoto) {
        Call<FotosServicos> call = crudPro.deleteFoto(idFoto);
        call.enqueue(new Callback<FotosServicos>() {
            @Override
            public void onResponse(Call<FotosServicos> call, Response<FotosServicos> response) {
                FotosServicos f = response.body();
                if (f == null) {
                    MetodosEstaticos.toastMsg(context, "Falha ao deletar foto. Tente novamente.");
                    return;
                }else if(f.error.equals("true")){
                    MetodosEstaticos.toastMsg(context, f.msg);
                }else {
                    MetodosEstaticos.toastMsg(context, "Foto excluída com sucesso!");
                    getFotos();
                }
            }

            @Override
            public void onFailure(Call<FotosServicos> call, Throwable t) {

                MetodosEstaticos.toastMsg(context, "Falha ao deletar foto. Tente novamente.");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_perfil_pro, container, false);
        try {
            cli = (Cliente) getArguments().getSerializable(ClassesConstants.CLIENTE);
            if (cli == null) {
                throw new Exception("Erro ao receber informações do usuário.");
            }

            mViewHolder.lblSemFotos = v.findViewById(R.id.lblSemFotos);
            mViewHolder.recyclerView = v.findViewById(R.id.recyclerFotos);
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
            mViewHolder.btnAddFoto = v.findViewById(R.id.btnAddFotoS);

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
                            String descricao = mViewHolder.txtDescricao.getText().toString();
                            if (MetodosEstaticos.isCampoVazio(descricao)) {
                                mViewHolder.txtDescricao.setText("Sem descrição...");
                            }
                            MetodosEstaticos.snackMsg(view, "Carregando...");
                            mViewHolder.lblEditDesc.setText(R.string.lbl_editar);
                            mViewHolder.txtDescricao.setEnabled(false);
                            editDescricao(descricao);
                        }
                    }
                });

                mViewHolder.btnAddFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addFoto();
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
                                            txtCurso.getText().toString(),
                                            txtInst.getText().toString(),
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

                getFotos();

            } // se não for profissional...
            else {
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
        } catch (Exception ex) {
            alertD.setTitle("Error");
            alertD.setMessage(ex.getMessage());
            alertD.setNegativeButton("OK", null);
            alertD.show();
        }

        return v;
    }

    int contador = 0;

    private void getFotos() {
        Call<List<FotosServicos>> call = crudPro.getFotosServices(cli.getIdProfissional());
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
                            MetodosEstaticos.toastMsg(context, "Não foi possível obter as fotos dos serviços concluídos...\nError: "
                                    + t.getMessage() + "Tentando novamente...");
                            contador++;
                            getFotos();
                        }
                    }, delay);
                }
                if (contador > 5) {
                    MetodosEstaticos.toastMsg(context, "Não foi possível obter as suas fotos no nosso banco de dados. Por favor tente mais tarde.");
                }

            }
        });
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

    private void editDescricao(String descricao) {
        Call<Cliente> call = crudPro.attDescricao(cli.getIdProfissional(), descricao);
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

    public void setListFotos(List<FotosServicos> list) {
        adapter = new FotosServicosAdapter(list, listener);
        mViewHolder.recyclerView.setAdapter(adapter);
        mViewHolder.recyclerView.setLayoutManager(linearLayout);
    }

    public void addFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri mSelectedUri = data.getData();
        savePhotoFb(mSelectedUri);
    }

    private void savePhotoFb(Uri mSelectedUri) {
        // Save User Firebase
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(mSelectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FotosServicos foto = new FotosServicos();
                                foto.setUrl(uri.toString());
                                foto.setIdUser(cli.getIdProfissional());

                                Call<FotosServicos> call = crudPro.insertFoto(foto);
                                call.enqueue(new Callback<FotosServicos>() {
                                    @Override
                                    public void onResponse(Call<FotosServicos> call, Response<FotosServicos> response) {
                                        //Sucesso na requisição (mesmo que retorno seja de erro. ex: 404)
                                        FotosServicos f = response.body();
                                        try {
                                            if (f.error != null && f.error.equals("true")) {
                                                throw new Exception(f.msg);
                                            } else {
                                                MetodosEstaticos.toastMsg(context, "Foto adicionada com sucesso!");
                                                getFotos();
                                            }
                                        } catch (Exception ex) {
                                            MetodosEstaticos.snackMsg(getView(), ex.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<FotosServicos> call, Throwable t) {
                                        //Falha na requisição
                                        MetodosEstaticos.testConnectionFailed(t, context);
                                    }
                                });
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
        TextView lblEditDesc;
        TextView lblEditForm;
        TextView lblSemFotos;
        EditText txtDescricao;
        EditText txtFormacao;
        ImageView estrelas;
        Button btnAddFoto;
        RecyclerView recyclerView;
    }
}