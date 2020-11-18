package pacote.faconapp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.ExceptionsCadastro;
import pacote.faconapp.constants.InfosLoginConstants;
import pacote.faconapp.controller.ValidarLogin;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.data.SecurityPreferences;
import pacote.faconapp.model.dominio.crud.CrudUser;
import pacote.faconapp.model.dominio.entidades.Cliente;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private SecurityPreferences mSecurityPreferences;
    private Intent it;
    private Context context;
    private ValidarLogin validarLogin;
    private Cliente cli;
    private CrudUser crudUser;
    private String senha = "";
    private String email = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewHolder.progressBar = findViewById(R.id.progress);
        mViewHolder.btnLembrar = findViewById(R.id.lembrarSenha);
        mViewHolder.txtEmail = findViewById(R.id.txtEmail);
        mViewHolder.txtSenha = findViewById(R.id.txtSenha);
        //tratando botao ENTER
        mViewHolder.txtSenha.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }
        });

        context = this;
        validarLogin = new ValidarLogin(context);
        mSecurityPreferences = new SecurityPreferences(context);
        crudUser = ApiDb.createService(CrudUser.class);

        String email = mSecurityPreferences.getStoredSting(InfosLoginConstants.EMAIL);
        String senha = mSecurityPreferences.getStoredSting(InfosLoginConstants.SENHA);

        if (email != "") {
            mViewHolder.btnLembrar.setChecked(true);
            mViewHolder.txtEmail.setText(email);
            mViewHolder.txtSenha.setText(senha);
        }
    }

    private static class ViewHolder {
        private EditText txtEmail;
        private EditText txtSenha;
        private Switch btnLembrar;
        private ProgressBar progressBar;
    }

    public void clickLogin(View v) {
        login();
    }

    private int contador = 0;

    public void login() {
        try {
            mViewHolder.progressBar.setVisibility(View.VISIBLE);
            email = mViewHolder.txtEmail.getText().toString();
            senha = mViewHolder.txtSenha.getText().toString();

            //verificando se checkBox está ativada ou não
            if (mViewHolder.btnLembrar.isChecked()) {
                mSecurityPreferences.storeString(InfosLoginConstants.EMAIL, email);
                mSecurityPreferences.storeString(InfosLoginConstants.SENHA, senha);
            } else {
                mSecurityPreferences.storeString(InfosLoginConstants.EMAIL, "");
            }
            if (validarLogin.validarLogin(email, senha)) {

                byte[] bSenha = MetodosEstaticos.gerarHash(senha);
                senha = MetodosEstaticos.stringHexa(bSenha);

                Call<Cliente> call = crudUser.login(email, senha);
                call.enqueue(new Callback<Cliente>() {
                    @Override
                    public void onResponse(Call<Cliente> call, Response<Cliente> response) {
                        //Sucesso na requisição (mesmo que retorno seja de erro. ex: 404)
                        cli = response.body();

                        try {

                            if (cli.error != null && cli.error.equals("true")) {
                                throw new Exception(cli.msg);
                            } else if (cli.getAtivo() != 1) {
                                throw new Exception("Usuário inativo. Verifique sua caixa de e-mail.");
                            }
                            //Convertendo data yyyy-mm-dd para dd/MM/yyyy
                            cli.setDataNascimento(MetodosEstaticos.convertDateInForBr(cli.getDataNascimento()));

                            loginFb();

                            if (cli.getEndCep() == null) { //testando se ja houve o primeiro acesso pelo Cep
                                it = new Intent(context, CompletarCadastro.class);
                            } else {
                                it = new Intent(context, Home.class);
                            }

                            it.putExtra(ClassesConstants.CLIENTE, cli);
                            mViewHolder.progressBar.setVisibility(View.INVISIBLE);
                            startActivity(it);
                            MainActivity.this.finish();

                        } catch (Exception ex) {
                            mViewHolder.progressBar.setVisibility(View.INVISIBLE);
                            MetodosEstaticos.toastMsg(context, ex.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<Cliente> call, Throwable t) {
                        //Falha na requisição
                        MetodosEstaticos.testConnectionFailed(t, context);
                        mViewHolder.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        } catch (Exception ex) {
            mViewHolder.progressBar.setVisibility(View.INVISIBLE);
            if (ex.getMessage().equals(ExceptionsCadastro.EMAIL_VAZIO) || ex.getMessage().equals(ExceptionsCadastro.EMAIL_INVALIDO)) {
                mViewHolder.txtEmail.requestFocus();
            }
            if (ex.getMessage().equals(ExceptionsCadastro.SENHA_VAZIO)) {
                mViewHolder.txtSenha.requestFocus();
            }
            MetodosEstaticos.toastMsg(context, ex.getMessage());
        }
    }

    private void loginFb(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        if(contador < 5){
                            loginFb();
                        }else{
                            MetodosEstaticos.toastMsg(context, "Erro ao fazer login...");
                            Handler handler = new Handler();
                            long delay = 3000;
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    MainActivity.this.finish();
                                }
                            }, delay);
                        }
                    }
                });
    }

    public void clickAbrirResetSenha(View v) {
        it = new Intent(context, ResetSenha.class);
        //primeiro parametro de onde/origem que surge a intenção e segundo aponta para onde vai
        startActivity(it);
    }

    public void clickAbrirCadastro(View v) {
        it = new Intent(context, TelaCadastro.class);
        startActivity(it);
    }
}
