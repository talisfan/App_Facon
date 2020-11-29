package pacote.faconapp.view.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.adapter.ChatApplication;
import pacote.faconapp.constants.ClassesConstants;
import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.chat.Contact;
import pacote.faconapp.model.dominio.entidades.chat.ContatosFb;
import pacote.faconapp.view.MainActivity;

public class MessagesActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    private Context context;
    private Cliente cli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_logo_blue);
        context = this;
        cli = (Cliente) getIntent().getSerializableExtra(ClassesConstants.CLIENTE);

        RecyclerView rv = findViewById(R.id.recycler_contact);
        rv.setLayoutManager(new LinearLayoutManager(context));

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);

                ContactItem contactItem = (ContactItem) item;
                intent.putExtra(ClassesConstants.PROFISSIONAL, contactItem.contact);
                intent.putExtra(ClassesConstants.CLIENTE, cli);
                startActivity(intent);
            }
        });

        fetchLastMessage();
    }

    private void fetchLastMessage() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance().collection("/last-messages")
                .document(uid)
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges =queryDocumentSnapshots.getDocumentChanges();

                        if (documentChanges != null) {
                            for (DocumentChange doc: documentChanges) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    ContatosFb contact = doc.getDocument().toObject(ContatosFb.class);

                                    adapter.add(new ContactItem(contact));
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contacts:
                Intent intent = new Intent(MessagesActivity.this, ContactsActivity.class);
                intent.putExtra(ClassesConstants.CLIENTE, cli);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ContactItem extends Item<ViewHolder> {

        private final ContatosFb contact;

        private ContactItem(ContatosFb contact) {
            this.contact = contact;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView username = viewHolder.itemView.findViewById(R.id.textView6);
            TextView message = viewHolder.itemView.findViewById(R.id.textView7);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageView2);

            username.setText(contact.getUsername());
            if(contact.isMe()) {
                message.setText( "Você: " + contact.getLastMessage());
            }else{
                message.setText(contact.getLastMessage());
            }
            if(contact.getProfileUrl() != null && !contact.getProfileUrl().equals("")) {
                contact.setPhotoUrl(contact.getProfileUrl());
                Picasso.get()
                        .load(contact.getProfileUrl())
                        .into(imgPhoto);
            }
            if(contact.getPhotoUrl() != null && !contact.getPhotoUrl().equals("")) {
                contact.setProfileUrl(contact.getPhotoUrl());
                Picasso.get()
                        .load(contact.getPhotoUrl())
                        .into(imgPhoto);
            }
        }

        @Override
        public int getLayout() {
            return R.layout.item_user_message;
        }
    }
}