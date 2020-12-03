package pacote.faconapp.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.constants.ExceptionsCadastro;

public class ResetSenha extends AppCompatActivity {

    private Intent it = null;
    private EditText txtEmailReset;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_senha);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        txtEmailReset = findViewById(R.id.txtEmailReset);
        context = this;
    }

    public void clickResetSenha(View v) {

        String email = txtEmailReset.getText().toString();

        if (isCampoVazio(email)) {
            Toast.makeText(context, ExceptionsCadastro.EMAIL_VAZIO, Toast.LENGTH_SHORT).show();
            return;
        } else if (isEmailValido(email)) {

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                AlertDialog.Builder alertD = new AlertDialog.Builder(context);
                                alertD.setTitle("Sucesso!");
                                alertD.setMessage("Um email foi enviado para sua caixa de entrada com o link para recuperação de senha.");
                                alertD.setPositiveButton("OK", null);
                                alertD.show();
                                ResetSenha.this.finish();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, ExceptionsCadastro.EMAIL_INVALIDO, Toast.LENGTH_SHORT).show();
            return;
        }

        this.it = new Intent(ResetSenha.this, MainActivity.class);

        Handler handler = new Handler();
        long delay = 3000; // tempo de delay em millisegundos
        handler.postDelayed(new Runnable() {
            public void run() {
                // codigo a ser executado apos delay
                startActivity(it);
            }
        }, delay);
    }

    private boolean isEmailValido(String email) {
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches()); //retorna falso se não for email
    }

    private boolean isCampoVazio(String valor) {
        return (TextUtils.isEmpty(valor) || valor.trim().isEmpty()); //trim remove espaços em branco
    }
}
