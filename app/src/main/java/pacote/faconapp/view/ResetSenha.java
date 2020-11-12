package pacote.faconapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pacote.faconapp.R;
import pacote.faconapp.constants.ExceptionsCadastro;

public class ResetSenha extends AppCompatActivity {
    private ViewHolder mViewHolder = new ViewHolder();
    private Intent it = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_senha);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        mViewHolder.txtEmailReset = (EditText) findViewById(R.id.txtEmailReset);
    }

    private static class ViewHolder {
        private EditText txtEmailReset;
    }

    public void clickResetSenha(View v) {

        String email = mViewHolder.txtEmailReset.getText().toString();

        if (isCampoVazio(email)) {
            Toast.makeText(this, ExceptionsCadastro.EMAIL_VAZIO, Toast.LENGTH_SHORT).show();
            return;
        } else if (isEmailValido(email)) {
            Toast.makeText(this, "E-mail enviado com sucesso !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, ExceptionsCadastro.EMAIL_INVALIDO, Toast.LENGTH_SHORT).show();
            return;
        }

        this.it = new Intent(ResetSenha.this, MainActivity.class);

        Handler handler = new Handler();
        long delay = 2000; // tempo de delay em millisegundos
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
