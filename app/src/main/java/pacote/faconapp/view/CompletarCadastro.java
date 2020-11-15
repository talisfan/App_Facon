package pacote.faconapp.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.controller.ValidarCompletarCad;
import pacote.faconapp.listener.mask.Mascaras;
import pacote.faconapp.model.data.ApiCep;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudUser;
import pacote.faconapp.model.dominio.crud.ServiceCep;
import pacote.faconapp.model.dominio.entidades.Cep;
import pacote.faconapp.model.dominio.entidades.Cliente;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletarCadastro extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Cliente user;
    private AlertDialog.Builder alertD;
    private ServiceCep mServiceCep;
    private String cep;
    private Context context;
    private CrudUser crudUser;
    private Intent it;
    private Uri mSelectedUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_cadastro);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        context = this;
        mServiceCep = ApiCep.createService(ServiceCep.class);
        alertD = new AlertDialog.Builder(context);
        crudUser = ApiDb.createService(CrudUser.class);
        user = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);

        mViewHolder.txtCep = findViewById(R.id.txtCep);
        mViewHolder.txtRua = findViewById(R.id.txtRua);
        mViewHolder.txtNum = findViewById(R.id.txtEndNum);
        mViewHolder.txtBairro = findViewById(R.id.txtBairro);
        mViewHolder.txtCidade = findViewById(R.id.txtCidade);
        mViewHolder.txtEstado = findViewById(R.id.txtEstado);
        mViewHolder.foto = findViewById(R.id.addFoto);

        //criando mask
        Mascaras maskCep = new Mascaras("#####-###", mViewHolder.txtCep);
        //implementando a mask no EditText
        mViewHolder.txtCep.addTextChangedListener(maskCep);

        alertD.setTitle("Seja Bem Vindo ao Facon !");
        alertD.setMessage("Antes de continuar, precisamos dos seus dados de endereço, por motivos de segurança. ");
        alertD.setPositiveButton("OK", null);
        alertD.show();
    }

    public void selectPhoto(View v) {
        try {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vDialog = inflater.inflate(R.layout.dialog_add_foto, null);

            ImageView openCam = vDialog.findViewById(R.id.openCam);
            ImageView openGalery = vDialog.findViewById(R.id.openGalery);

            openCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 1);
                    }
                }
            });

            openGalery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 0);
                }
            });

            alertD.setView(vDialog);
            alertD.setTitle(null);
            alertD.setMessage(null);
            alertD.setPositiveButton(null, null);
            alertD.show();

        }catch (Exception ex){
            MetodosEstaticos.toastMsg(context, ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            mSelectedUri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                mViewHolder.foto.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
            } catch (IOException ex) {
                MetodosEstaticos.toastMsg(context, "Erro ao receber foto.");
            }
        }

        if (requestCode == 1) {
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mViewHolder.foto.setImageBitmap(imageBitmap);
                alertD.set
            }catch (Exception ex) {
                MetodosEstaticos.toastMsg(context, "Erro ao receber foto.");
            }
        }
    }

    public void buscaCep(View v) {
        try {
            if (mViewHolder.txtCep.getText().length() == 9) {
                if (MetodosEstaticos.isConnected(context)) {

                    Toast.makeText(this, "Buscando cep...", Toast.LENGTH_SHORT).show();
                    cep = mViewHolder.txtCep.getText().toString().replace("-", "");
                    Call<Cep> call = this.mServiceCep.buscaCep(cep);
                    //enqueue aguarda a resposta sem travar user
                    call.enqueue(new Callback<Cep>() {
                        @Override
                        public void onResponse(Call<Cep> call, Response<Cep> response) {
                            //Sucesso na requisição (mesmo que retorno seja de erro. ex: 404)
                            Cep listEnd = response.body();
                            if (listEnd != null && listEnd.getEndRua() != null) {
                                MetodosEstaticos.toastMsg(context, "Pronto !");
                            } else {
                                MetodosEstaticos.toastMsg(context, "Cep não encontrado !");
                            }
                            mViewHolder.txtRua.setText(listEnd.getEndRua());
                            mViewHolder.txtCidade.setText(listEnd.getEndCidade());
                            mViewHolder.txtEstado.setText(listEnd.getEndEstado());
                            mViewHolder.txtBairro.setText(listEnd.getEndBairro());
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
            } else {
                MetodosEstaticos.toastMsg(context, "Informe um cep válido !");
            }
        } catch (Exception ex) {
            alertD.setTitle("ERRO:");
            alertD.setMessage(ex.getMessage());
            alertD.show();
        }
    }

    public void completarCadastro(View v) {
        try {

            user.setEnderecoCidade(mViewHolder.txtCidade.getText().toString());
            user.setEndCep(mViewHolder.txtCep.getText().toString().replace("-", ""));
            user.setEnderecoBairro(mViewHolder.txtBairro.getText().toString());
            user.setEnderecoNum(mViewHolder.txtNum.getText().toString());
            user.setEnderecoEstado(mViewHolder.txtEstado.getText().toString());
            user.setEnderecoRua(mViewHolder.txtRua.getText().toString());

            ValidarCompletarCad validar = new ValidarCompletarCad();
            if (validar.validarCompletarCad(user, context)) {

                Call<Cliente> call = crudUser.completCad(user);
                //enqueue aguarda a resposta sem travar user
                call.enqueue(new Callback<Cliente>() {
                    @Override
                    public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                        try {
                            Cliente u = response.body();

                            if (u.error != null && u.error.equals("true")) {
                                throw new Exception(u.msg);
                            }

                            alertD.setTitle("TUDO OK !");
                            alertD.setMessage("Carregando...");
                            alertD.setPositiveButton(null, null);
                            alertD.show();

                            it = new Intent(context, EscolhaUser.class);
                            it.putExtra(ClassesConstants.CLIENTE, user);

                            Handler handler = new Handler();
                            long delay = 2000;
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    startActivity(it);
                                }
                            }, delay);
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    finish();
                                }
                            }, delay);
                        } catch (Exception ex) {
                            alertD.setTitle("Error");
                            alertD.setMessage(ex.getMessage());
                            alertD.setPositiveButton("OK", null);
                            alertD.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Cliente> call, Throwable t) {
                        MetodosEstaticos.testConnectionFailed(t, context);
                    }
                });
            }
        } catch (Exception ex) {

            MetodosEstaticos.toastMsg(context, ex.getMessage());

            if (ex.getMessage().equals(ExceptionsCadastro.CEP_INVALIDO)) {
                mViewHolder.txtCep.requestFocus();
            }
            if (ex.getMessage().equals(ExceptionsCadastro.NUM_INVALIDO)) {
                mViewHolder.txtNum.requestFocus();
            }
        }
    }

    private static class ViewHolder {
        private EditText txtCep;
        private EditText txtRua;
        private EditText txtCidade;
        private EditText txtBairro;
        private EditText txtEstado;
        private EditText txtNum;
        private ImageView foto;
    }
}

