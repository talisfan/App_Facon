package pacote.faconapp.view.chat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.List;
import java.util.Random;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.MoneyTextWatcher;
import pacote.faconapp.R;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.constants.ExceptionsServer;
import pacote.faconapp.controller.ValidarProposta;
import pacote.faconapp.listener.mask.Mascaras;
import pacote.faconapp.model.data.ApiDb;
import pacote.faconapp.model.dominio.crud.CrudProposta;
import pacote.faconapp.model.dominio.crud.CrudUser;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.Proposta;
import pacote.faconapp.model.dominio.entidades.chat.Contact;
import pacote.faconapp.model.dominio.entidades.chat.ContatosFb;
import pacote.faconapp.model.dominio.entidades.chat.Message;
import pacote.faconapp.model.dominio.entidades.chat.UserFireBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    private ContatosFb user;
    private UserFireBase me;
    private String fromId;
    private String toId;
    private Context context;
    private EditText editChat;
    private Cliente cli;
    private CrudProposta crudProposta;
    private AlertDialog.Builder alertD;
    String msgProposta = "Clique aqui para visualizar a proposta: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        context = this;
        alertD = new AlertDialog.Builder(context);
        crudProposta = ApiDb.createService(CrudProposta.class);

        try {
            if (MetodosEstaticos.isConnected(this)) {
                throw new Exception(ExceptionsServer.NO_CONNECTION);
            }
            MetodosEstaticos.testDateHourAutomatic(this);
        } catch (Exception ex) {
            AlertDialog.Builder alertD = new AlertDialog.Builder(this);
            alertD.setTitle("Erro");
            alertD.setMessage(ex.getMessage());
        }

        user = (ContatosFb) getIntent().getExtras().getSerializable(ClassesConstants.PROFISSIONAL);
        cli = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);

        fromId = FirebaseAuth.getInstance().getUid();
        toId = user.getContato();

        RecyclerView rv = findViewById(R.id.recycler_chat);
        editChat = findViewById(R.id.edit_chat);
        Button btnChat = findViewById(R.id.btn_chat);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editChat.getText().toString());
            }
        });

        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        me = documentSnapshot.toObject(UserFireBase.class);
                        fetchMessages();
                    }
                });
    }

    private void fetchMessages() {
        if (me != null) {

            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                            if (documentChanges != null) {
                                for (DocumentChange doc : documentChanges) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        Message message = doc.getDocument().toObject(Message.class);
                                        adapter.add(new MessageItem(message));
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void sendMessage(String text) {

        editChat.setText(null);
        long timestamp = System.currentTimeMillis();

        final Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);

        if (!message.getText().isEmpty()) {
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Contact contact = new Contact();
                            contact.setContato(toId);
                            contact.setUsername(user.getUsername());
                            contact.setPhotoUrl(user.getProfileUrl());
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());
                            contact.setMe(true);

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(fromId)
                                    .collection("contacts")
                                    .document(toId)
                                    .set(contact);
                        }
                    });

            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(toId)
                    .collection(fromId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Contact contact = new Contact();
                            contact.setContato(fromId);
                            contact.setUsername(me.getUsername());
                            contact.setPhotoUrl(me.getProfileUrl());
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());
                            contact.setMe(false);

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(toId)
                                    .collection("contacts")
                                    .document(fromId)
                                    .set(contact);
                        }
                    });
        }
    }

    private class MessageItem extends Item<ViewHolder> {

        private final Message message;

        private MessageItem(Message message) {
            this.message = message;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txtMsg = viewHolder.itemView.findViewById(R.id.txt_msg);

            txtMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(txtMsg.getText().toString().contains("pRop201s")){

                        String token = txtMsg.getText().toString().replace(msgProposta, "");
                        Call<Proposta> call = crudProposta.getProposta(token, FirebaseAuth.getInstance().getUid());
                        call.enqueue(new Callback<Proposta>() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onResponse(Call<Proposta> call, Response<Proposta> response) {
                                Proposta proposta = response.body();

                                try {
                                    if (proposta == null) {
                                        throw new Exception("Falha ao receber resposta do servidor.");
                                    }
                                    if (proposta.error != null && proposta.error.equals("true")) {
                                        throw new Exception(proposta.msg);
                                    }

                                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View vDialog = inflater.inflate(R.layout.dialog_proposta, null);

                                    final TextView lblPrestador = vDialog.findViewById(R.id.lblPrestador);
                                    final TextView lblCliente = vDialog.findViewById(R.id.lblCliente);
                                    final TextView lblStatus = vDialog.findViewById(R.id.lblStatus);
                                    final EditText txtDtInicio = vDialog.findViewById(R.id.txtDtInicio);
                                    final EditText txtDtFim = vDialog.findViewById(R.id.txtDtFim);
                                    final EditText txtValor = vDialog.findViewById(R.id.txtValorServ);
                                    final EditText txtLocal = vDialog.findViewById(R.id.txtLocal);
                                    final EditText txtDesc = vDialog.findViewById(R.id.txtDesc);
                                    final Spinner spinnerFormaPag = vDialog.findViewById(R.id.spinnerFormaPag);

                                    Mascaras maskDtInicio = new Mascaras("##/##/####", txtDtInicio);
                                    txtDtInicio.addTextChangedListener(maskDtInicio);
                                    Mascaras maskDtFim = new Mascaras("##/##/####", txtDtFim);
                                    txtDtFim.addTextChangedListener(maskDtFim);
                                    // add formato moeda br
                                    txtValor.addTextChangedListener(new MoneyTextWatcher(txtValor));

                                    lblPrestador.setText(proposta.getPrestador());
                                    lblCliente.setText(proposta.getCliente());

                                    String status = proposta.getStatus();
                                    lblStatus.setText(status);
                                    switch (status){
                                        case "PENDENTE":
                                            lblStatus.setTextColor(R.color.yellow);
                                            break;

                                        case "CONCLUÍDO":
                                            lblStatus.setTextColor(R.color.green);
                                            break;

                                        case "ACEITO":
                                            lblStatus.setTextColor(R.color.blueBackGround);
                                            break;

                                        default:
                                            lblStatus.setTextColor(R.color.danger);
                                            break;
                                    }

                                    txtDtInicio.setText(MetodosEstaticos.convertDateInForBr(proposta.getDtInicio()));
                                    txtDtFim.setText(MetodosEstaticos.convertDateInForBr(proposta.getDtFim()));
                                    txtValor.setText(proposta.getValor().toString());
                                    txtLocal.setText(proposta.getLocal());
                                    txtDesc.setText(proposta.getDescricao());
                                    String[] items = new String[]{ proposta.getFormaPag() };

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
                                    spinnerFormaPag.setAdapter(adapter);

                                    alertD.setView(vDialog);
                                    alertD.setNegativeButton("CANCELAR", null);

                                    if(cli.getIdProfissional() == 0 && status.equals("PENDENTE")) {

                                        alertD.setNeutralButton("RECUSAR", new Dialog.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                atualizarProposta(proposta.getToken(), "RECUSADO");
                                            }
                                        });

                                        alertD.setPositiveButton("ACEITAR", new Dialog.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                atualizarProposta(proposta.getToken(), "ACEITO");
                                            }
                                        });

                                    }else if(cli.getIdProfissional() == 0 && status.equals("ACEITO")) {
                                        alertD.setNeutralButton("NÃO FEZ/TERMINOU", new Dialog.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                atualizarProposta(proposta.getToken(), "NÃO FEZ/TERMINOU");
                                            }
                                        });
                                        alertD.setPositiveButton("CONCLUÍDO", new Dialog.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                atualizarProposta(proposta.getToken(), "CONCLUÍDO");
                                            }
                                        });
                                    }
                                    else{
                                        alertD.setNegativeButton("OK", null);
                                    }
                                    alertD.show();

                                }catch (Exception ex){
                                    MetodosEstaticos.toastMsg(context, ex.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Call<Proposta> call, Throwable t) {
                                MetodosEstaticos.toastMsg(context, t.getMessage());
                            }
                        });
                    }
                }
            });

            ImageView imgMessage = viewHolder.itemView.findViewById(R.id.img_message_user);

            txtMsg.setText(message.getText());
            Picasso.get()
                    .load(message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                            ? me.getProfileUrl()
                            : user.getProfileUrl())
                    .into(imgMessage);
        }

        @Override
        public int getLayout() {
            return message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                    ? R.layout.item_to_message
                    : R.layout.item_from_message;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (cli.getIdProfissional() != 0) {
            getMenuInflater().inflate(R.menu.menu_chat_profissional, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (cli.getIdProfissional() != 0) {
            if (item.getItemId() == R.id.fazerProposta) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vDialog = inflater.inflate(R.layout.dialog_proposta, null);

                final TextView lblPrestador = vDialog.findViewById(R.id.lblPrestador);
                final TextView lblCliente = vDialog.findViewById(R.id.lblCliente);
                final TextView lblStatus = vDialog.findViewById(R.id.lblStatus);
                final EditText txtDtInicio = vDialog.findViewById(R.id.txtDtInicio);
                final EditText txtDtFim = vDialog.findViewById(R.id.txtDtFim);
                final EditText txtValor = vDialog.findViewById(R.id.txtValorServ);
                final EditText txtLocal = vDialog.findViewById(R.id.txtLocal);
                final EditText txtDesc = vDialog.findViewById(R.id.txtDesc);
                final Spinner spinnerFormaPag = vDialog.findViewById(R.id.spinnerFormaPag);

                Mascaras maskDtInicio = new Mascaras("##/##/####", txtDtInicio);
                txtDtInicio.addTextChangedListener(maskDtInicio);
                Mascaras maskDtFim = new Mascaras("##/##/####", txtDtFim);
                txtDtFim.addTextChangedListener(maskDtFim);
                // add formato moeda br
                txtValor.addTextChangedListener(new MoneyTextWatcher(txtValor));

                String[] items = new String[]{"Dinheiro", "Transferência (DOC/TED)", "Crédito", "Débito"};

                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
                spinnerFormaPag.setAdapter(adapter);

                lblCliente.setText("Cliente: " + user.getUsername());
                lblPrestador.setText("Prestador: " + cli.getNome());

                alertD.setView(vDialog);
                alertD.setNegativeButton("CANCELAR", null);
                alertD.setPositiveButton("ENVIAR", new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            double valor;
                            String dataInicio;
                            String dataFim;
                            try {
                                valor = Double.parseDouble(MoneyTextWatcher.formatPriceSave(txtValor.getText().toString()));
                            }catch (Exception ex){
                                throw new Exception("Erro ao converter valor do serviço.");
                            }
                            Proposta proposta = new Proposta( FirebaseAuth.getInstance().getUid(),
                                    cli.getNome(), user.getContato(), user.getUsername(), "PENDENTE", txtDtInicio.getText().toString(),
                                    txtDtFim.getText().toString(), valor, spinnerFormaPag.getSelectedItem().toString(),
                                    txtLocal.getText().toString(), txtDesc.getText().toString());

                            ValidarProposta validar = new ValidarProposta();

                            if (validar.validarProposta(proposta)) {

                                try {
                                    proposta.setDtInicio(MetodosEstaticos.convertDateBrForIn(txtDtInicio.getText().toString()));
                                    proposta.setDtFim(MetodosEstaticos.convertDateBrForIn(txtDtFim.getText().toString()));
                                }catch (Exception ex){
                                    throw new Exception("Erro na conversão de datas.");
                                }

                                Random ramdom = new Random();
                                String token = "pRop201s";
                                token += String.valueOf(Long.toHexString(ramdom.nextLong()));
                                token += String.valueOf(Long.toHexString(ramdom.nextLong()));
                                // string a.length = 40

                                proposta.setToken(token);

                                Call<Proposta> call = crudProposta.criarProposta(proposta);
                                call.enqueue(new Callback<Proposta>() {
                                    @Override
                                    public void onResponse(Call<Proposta> call, Response<Proposta> response) {
                                        Proposta prop = response.body();
                                        try {
                                            if (prop == null) {
                                                throw new Exception("Falha ao receber resposta do servidor.");
                                            }
                                            if (prop.error != null && prop.error.equals("true")) {
                                                throw new Exception(prop.msg);
                                            }
                                            MetodosEstaticos.toastMsg(context, "Proposta enviada com sucesso!");
                                            sendMessage(msgProposta + proposta.getToken());

                                        }catch (Exception ex){
                                            MetodosEstaticos.toastMsg(context, ex.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Proposta> call, Throwable t) {
                                        MetodosEstaticos.toastMsg(context, t.getMessage());
                                    }
                                });
                            }
                        } catch (Exception ex) {
                            MetodosEstaticos.toastMsg(context, ex.getMessage());
                        }
                    }
                });
                alertD.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //idetifica se proposta foi atualizada ou nao
    boolean flag = false;

    public void atualizarProposta(String token, String status){

        Call<Proposta> call = crudProposta.updateProposta(token, status);
        call.enqueue(new Callback<Proposta>() {
            @Override
            public void onResponse(Call<Proposta> call, Response<Proposta> response) {
                Proposta proposta = response.body();
                try {
                    if (proposta == null) {
                        throw new Exception("Falha ao receber resposta do servidor.");
                    }
                    if (proposta.error != null && proposta.error.equals("true")) {
                        throw new Exception(proposta.msg);
                    }
                    MetodosEstaticos.toastMsg(context, "Proposta atualizada com sucesso!");
                    sendMessage(msgProposta + proposta.getToken());

                }catch (Exception ex){
                    MetodosEstaticos.toastMsg(context, ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Proposta> call, Throwable t) {
                MetodosEstaticos.toastMsg(context, t.getMessage());
            }
        });
    }
}