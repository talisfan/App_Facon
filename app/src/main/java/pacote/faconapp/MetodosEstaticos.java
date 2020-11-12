package pacote.faconapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import pacote.faconapp.constants.ExceptionsServer;

public class MetodosEstaticos {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( cm != null ) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        }

        return false;
    }

    public static void toastMsg(Context context, String txt){
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
    }

    public static void snackMsg(View view, String msg){
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public static String stringHexa(byte[] bytes) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
            int parteBaixa = bytes[i] & 0xf;
            if (parteAlta == 0) s.append('0');
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();
    }

    public static byte[] gerarHash(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static void testConnectionFailed(Throwable t, Context context){
        if (t.getMessage().toLowerCase().contains("failed to connect")) {
            toastMsg(context, ExceptionsServer.SERVER_ERROR);
        } else {
            toastMsg(context, "Error: " + t.getMessage());
        }
    }

    public static String convertDateBrForIn(String dataIn) throws Exception{
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse(dataIn, formato);
        String dateForm = String.valueOf(data);
        return dateForm;
    }

    public static String convertDateInForBr(String dataIn) throws Exception{
        //converte de String para Date
        DateFormat formatUS = new SimpleDateFormat("yyyy-mm-dd");
        Date date = formatUS.parse(dataIn);
        //Depois formata data
        DateFormat formatBR = new SimpleDateFormat("dd/mm/yyyy");
        String dateForm = formatBR.format(date);
        return dateForm;
    }

    public static String calcularExpPro(String exp){

        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        String dataAtual = date.format(Calendar.getInstance().getTime());
        String anoAtual = dataAtual.substring(6);
        int anoAt = Integer.valueOf(anoAtual);
        String mesAtual = dataAtual.substring(3, 5);
        int mesAt = Integer.valueOf(mesAtual);

        // exp = '2000-03-29T03:00:00.000Z'
        String experiencia = "";
        String ano = exp.substring(0, 4);
        int anoIn = Integer.valueOf(ano);
        String mes = exp.substring(5, 7);
        int mesIn = Integer.valueOf(mes);

        int expMeses = mesAt - mesIn;
        int expAnos = anoAt - anoIn;
        experiencia = expAnos + "." + expMeses;

        return experiencia;
    }

    public static void testDateHourAutomatic(Context context) throws Exception{

        int type = 0;
        type =  Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME, 0);
        if(type != 1){
            throw new Exception("É necessário estar com data e hora automática ligado.");
        }
    }

    public static boolean isCampoVazio(String valor) {
        return (TextUtils.isEmpty(valor) || valor.trim().isEmpty()); //trim remove espaços em branco
    }

    public static boolean isEmailValido(String email) {
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches()); //retorna falso se não for email
    }

    //metodo para remover caracteres especias de string
    public static String formatarDados(String dado) {
        dado = dado.replaceAll("\\.", "");
        dado = dado.replaceAll("-", "");
        dado = dado.replaceAll("\\(", "");
        dado = dado.replaceAll("\\)", "");
        dado = dado.replaceAll(" ", "");
        return dado;
    }
}
