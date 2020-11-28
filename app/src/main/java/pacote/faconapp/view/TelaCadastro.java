package pacote.faconapp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.controller.ValidarCadCli;
import pacote.faconapp.listener.mask.Mascaras;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudUser;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelaCadastro extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Intent it = null;
    private AlertDialog.Builder alertD;
    private CrudUser crudUser;
    private Context context;
    private Cliente user;
    private ValidarCadCli validarCad;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        context = this;
        alertD = new AlertDialog.Builder(context);
        validarCad = new ValidarCadCli();
        mViewHolder.txtNome = findViewById(R.id.cadTxtNome);
        mViewHolder.txtEmail = findViewById(R.id.cadTxtEmail);
        mViewHolder.txtTelCell = findViewById(R.id.cadTxtTelCell);
        mViewHolder.txtTelFixo = findViewById(R.id.cadTxtTelFixo);
        mViewHolder.txtCpf = findViewById(R.id.cadTxtCpf);
        mViewHolder.txtRg = findViewById(R.id.cadTxtRg);
        mViewHolder.txtDtNascimento = findViewById(R.id.cadTxtDtNascimento);
        mViewHolder.txtSenha = findViewById(R.id.cadTxtSenha);
        mViewHolder.txtSenhaConf = findViewById(R.id.cadTxtSenhaConf);
        mViewHolder.checkConcordo = findViewById(R.id.cadCheckConcordo);
        crudUser = ApiDb.createService(CrudUser.class);

        //criando mascara dos inputs
        Mascaras maskCpf = new Mascaras("###.###.###-##", mViewHolder.txtCpf);
        Mascaras maskRg = new Mascaras("##.###.###-#", mViewHolder.txtRg);
        Mascaras maskTelCell = new Mascaras("(##) #####-####", mViewHolder.txtTelCell);
        Mascaras maskTelFixo = new Mascaras("(##) ####-####", mViewHolder.txtTelFixo);
        Mascaras maskDtNascimento = new Mascaras("##/##/####", mViewHolder.txtDtNascimento);
        //implementando a mask no EditText
        mViewHolder.txtCpf.addTextChangedListener(maskCpf);
        mViewHolder.txtRg.addTextChangedListener(maskRg);
        mViewHolder.txtTelCell.addTextChangedListener(maskTelCell);
        mViewHolder.txtTelFixo.addTextChangedListener(maskTelFixo);
        mViewHolder.txtDtNascimento.addTextChangedListener(maskDtNascimento);
    }

    private static class ViewHolder {
        private EditText txtNome;
        private EditText txtEmail;
        private EditText txtTelCell;
        private EditText txtTelFixo;
        private EditText txtCpf;
        private EditText txtRg;
        private EditText txtSenha;
        private EditText txtSenhaConf;
        private EditText txtDtNascimento;
        private CheckBox checkConcordo;
    }

    public void clickAbrirTermos(View v) {
        alertD.setTitle("Termos e condições");
        alertD.setMessage("Exemplo de termos...");
        alertD.show();
    }

    private int contador = 0;
    public void clickCadastroValidar(View v) {
        try {
            user = new Cliente();

            user.setNome(mViewHolder.txtNome.getText().toString());
            user.setEmail(mViewHolder.txtEmail.getText().toString());
            user.setDataNascimento(mViewHolder.txtDtNascimento.getText().toString());
            user.setSenha(mViewHolder.txtSenha.getText().toString());
            user.setSenhaConf(mViewHolder.txtSenhaConf.getText().toString());
            user.setTelCell(MetodosEstaticos.formatarDados(mViewHolder.txtTelCell.getText().toString()));
            user.setTelFixo(MetodosEstaticos.formatarDados(mViewHolder.txtTelFixo.getText().toString()));
            user.setCpf(MetodosEstaticos.formatarDados(mViewHolder.txtCpf.getText().toString()));
            user.setRg(MetodosEstaticos.formatarDados(mViewHolder.txtRg.getText().toString()));

            if (!mViewHolder.checkConcordo.isChecked()) {
                throw new Exception(ExceptionsCadastro.CHECK_DESMARCADO);
            }

            if (validarCad.ValidarCadastro(user, context)) {
                byte[] bSenha = MetodosEstaticos.gerarHash(user.getSenha());
                user.setSenha(MetodosEstaticos.stringHexa(bSenha));
                //Fromatando data para padrão americano para inserir no banco
                user.setDataNascimento(MetodosEstaticos.convertDateBrForIn(user.getDataNascimento()));

                createUserFb();
            }
        } catch (Exception ex) {
            if (ex.getMessage().equals(ExceptionsCadastro.ERRO_NOME)) {
                mViewHolder.txtNome.requestFocus();

            } else if (ex.getMessage().equals(ExceptionsCadastro.EMAIL_VAZIO) || ex.getMessage().equals(ExceptionsCadastro.EMAIL_INVALIDO)) {
                mViewHolder.txtEmail.requestFocus();

            } else if (ex.getMessage().equals(ExceptionsCadastro.CELL_VAZIO) || ex.getMessage().equals(ExceptionsCadastro.CELL_INVALIDO)) {
                mViewHolder.txtTelCell.requestFocus();

            } else if (ex.getMessage().equals(ExceptionsCadastro.TELL_FIXO_INVALIDO)) {
                mViewHolder.txtTelFixo.requestFocus();

            } else if (ex.getMessage().equals(ExceptionsCadastro.CPF_VAZIO) || ex.getMessage().equals(ExceptionsCadastro.CPF_INVALIDO)) {
                mViewHolder.txtCpf.requestFocus();

            } else if (ex.getMessage().equals(ExceptionsCadastro.RG_VAZIO) || ex.getMessage().equals(ExceptionsCadastro.RG_INVALIDO)) {
                mViewHolder.txtRg.requestFocus();

            } else if (ex.getMessage().equals(ExceptionsCadastro.DT_NASCIMENTO_VAZIO) || ex.getMessage().equals(ExceptionsCadastro.DT_NASCIMENTO_INVALIDO)) {
                mViewHolder.txtDtNascimento.requestFocus();

            } else if (ex.getMessage().equals(ExceptionsCadastro.SENHA_VAZIO) || ex.getMessage().equals(ExceptionsCadastro.SENHA_CURTA)) {
                mViewHolder.txtSenha.requestFocus();

            } else if (ex.getMessage().equals(ExceptionsCadastro.CONF_SENHA_VAZIO) || ex.getMessage().equals(ExceptionsCadastro.SENHAS_ERRADAS)) {
                mViewHolder.txtSenhaConf.requestFocus();

            }
            MetodosEstaticos.toastMsg(context, ex.getMessage());
        }
    }

    // cadastrando user no firebase
    private void createUserFb() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getSenha())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            contador = 0;
                            if(task.getResult().getUser() != null) {
                                FirebaseUser a = task.getResult().getUser();
                                user.setIdFb(a.getUid());
                                createUserMySql();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        if(contador <= 5) {
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
                        if(contador > 5) {
                            MetodosEstaticos.toastMsg(context, "Erro FireBase. " + ex.getMessage());
                            it = new Intent(context, MainActivity.class);
                            it.putExtra(ClassesConstants.CLIENTE, user);

                            Handler handler = new Handler();
                            long delay = 5000;
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

    private void createUserMySql() {
        Call<Cliente> call = crudUser.registerUser(user);
        call.enqueue(new Callback<Cliente>() {
            @Override
            public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                try {
                    Usuario user = response.body();
                    if (user.error != null && user.error.equals("true")) {
                        throw new Exception(user.msg);
                    }

                    alertD.setTitle("CADASTRO EFETUADO COM SUCESSO !");
                    alertD.setMessage("Redirecionando...");
                    alertD.setNeutralButton(null, null);
                    alertD.setPositiveButton(null, null);
                    alertD.setNegativeButton(null, null);
                    alertD.show();
                    it = new Intent(context, MainActivity.class);

                    Handler handler = new Handler();
                    long delay = 4000;
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            startActivity(it);
                        }
                    }, delay);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            TelaCadastro.this.finish();
                        }
                    }, delay);

                } catch (Exception ex) {
                    alertD.setTitle("Error");
                    alertD.setMessage(ex.getMessage());
                    alertD.setNegativeButton("OK", null);
                    alertD.show();
                }
            }

            @Override
            public void onFailure(Call<Cliente> call, Throwable t) {
                if (t.getMessage().contains("Failed to connect")) {
                    MetodosEstaticos.toastMsg(context, ExceptionsServer.SERVER_ERROR);
                } else {
                    MetodosEstaticos.toastMsg(context, t.getMessage());
                }
                if(contador <= 5) {
                    Handler handler = new Handler();
                    long delay = 4000;
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            MetodosEstaticos.toastMsg(context, t.getMessage() + " - Tentando novamente...");
                            contador++;
                            createUserMySql();
                        }
                    }, delay);
                }
                if(contador > 5) {
                    MetodosEstaticos.toastMsg(context, "Erro MySql. " + t.getMessage());
                    it = new Intent(context, MainActivity.class);
                    it.putExtra(ClassesConstants.CLIENTE, user);

                    Handler handler = new Handler();
                    long delay = 5000;
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
}
