package pacote.faconapp.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.util.UUID;

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
import pacote.faconapp.model.dominio.entidades.chat.UserFireBase;
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
    private boolean isFotoSelected = false;

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
//        FirebaseApp.initializeApp(context);
//        user = new Cliente();

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


    public static ImageView convertBlobToImage(Context c, byte[] bytes) {

        try { // Tenta converter o blob em uma imagem
            //alimenta a imageStream com o que tem no bytes
            ByteArrayInputStream imageStream = new ByteArrayInputStream(bytes);

            //seta no bmp o stream carregado na linha de cima
            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

            ImageView img = new ImageView(c);
            img.setImageBitmap(bmp); // aqui seta a imagem no imageview

            return img; // retorna a imagem
        } catch (Exception ex) {
            MetodosEstaticos.toastMsg(c, "Erro ao converter o arquivo binário para imagem. " + ex.getLocalizedMessage());
            return new ImageView(c); // Retorna a imagem do jeito que está
        }
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
            alertD.setPositiveButton("OK", null);
            alertD.show();

        } catch (Exception ex) {
            MetodosEstaticos.toastMsg(context, ex.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mSelectedUri = data.getData();

        // Foto da galeria
        if (requestCode == 0) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                mViewHolder.foto.setImageDrawable(new BitmapDrawable(context.getResources(), bitmap));
                user.setFoto(bitmap);
                isFotoSelected = true;
            } catch (Exception ex) {
                MetodosEstaticos.toastMsg(context, ex.getMessage() + " - Erro ao setar foto.");
                isFotoSelected = false;
            }
        }

        // Foto da camera
        if (requestCode == 1) {
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mViewHolder.foto.setImageBitmap(imageBitmap);
                user.setFoto(imageBitmap);
                isFotoSelected = true;
            } catch (Exception ex) {
                MetodosEstaticos.toastMsg(context, ex.getMessage() + " - Erro ao setar foto.");
                isFotoSelected = false;
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
            if (!isFotoSelected) {
                throw new Exception("É necessário inserir uma foto para indentificação.");
            }
//            user.setEmail("talis@talis.com");
//            user.setSenha("e10adc3949ba59abbe56e057f20f883e");
//            createUserFb();

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
                            //createUserFb();
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

    private int contador = 0;

    private void createUserFb() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            contador = 0;
                            savePhotoFb();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        while (contador <= 5) {
                            Handler handler = new Handler();
                            long delay = 4000;
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    MetodosEstaticos.toastMsg(context, ex.getMessage() + " - Tentando novamente...");
                                    contador++;
                                    createUserFb();
                                }
                            }, delay);
                        }
                        if (contador > 5) {
                            MetodosEstaticos.toastMsg(context, "Não foi possível salvar sua foto no nosso banco de dados. Por favor tente mais tarde.");
                            it = new Intent(context, EscolhaUser.class);
                            it.putExtra(ClassesConstants.CLIENTE, user);

                            Handler handler = new Handler();
                            long delay = 3000;
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
                        }
                    }
                });
    }

    private void savePhotoFb() {
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

                                contador = 0;
                                String uid = FirebaseAuth.getInstance().getUid();
                                String username = user.getEmail();
                                String profileUrl = uri.toString();

                                UserFireBase us = new UserFireBase(uid, username, profileUrl);

                                FirebaseFirestore.getInstance().collection("users")
                                        .document(uid)
                                        .set(us)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
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
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception ex) {
                                                while (contador <= 5) {
                                                    Handler handler = new Handler();
                                                    long delay = 4000;
                                                    handler.postDelayed(new Runnable() {
                                                        public void run() {
                                                            MetodosEstaticos.toastMsg(context, ex.getMessage() + " - Tentando novamente...");
                                                            contador++;
                                                            savePhotoFb();
                                                        }
                                                    }, delay);
                                                }
                                                if (contador > 5) {
                                                    MetodosEstaticos.toastMsg(context, "Não foi possível salvar sua foto no nosso banco de dados. Por favor tente mais tarde.");
                                                    it = new Intent(context, EscolhaUser.class);
                                                    it.putExtra(ClassesConstants.CLIENTE, user);

                                                    Handler handler = new Handler();
                                                    long delay = 3000;
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
                                                }
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        while (contador <= 5) {
                            Handler handler = new Handler();
                            long delay = 4000;
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    MetodosEstaticos.toastMsg(context, ex.getMessage() + " - Tentando novamente...");
                                    contador++;
                                    savePhotoFb();
                                }
                            }, delay);
                        }
                        if (contador > 5) {
                            MetodosEstaticos.toastMsg(context, "Não foi possível salvar sua foto no nosso banco de dados. Por favor tente mais tarde.");
                            it = new Intent(context, EscolhaUser.class);
                            it.putExtra(ClassesConstants.CLIENTE, user);

                            Handler handler = new Handler();
                            long delay = 3000;
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
                        }
                    }
                });
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

