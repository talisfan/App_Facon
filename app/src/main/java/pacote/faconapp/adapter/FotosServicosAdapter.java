package pacote.faconapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import pacote.faconapp.R;
import pacote.faconapp.listener.OnClickFoto;
import pacote.faconapp.model.dominio.entidades.FotosServicos;

public class FotosServicosAdapter extends RecyclerView.Adapter<FotosServicosAdapter.FotosServicosViewHolder> {

    private List<FotosServicos> fotos;
    private OnClickFoto mOnClickList;

    public FotosServicosAdapter (List<FotosServicos> fotos, OnClickFoto listener) {
        this.fotos = fotos;
        this.mOnClickList = listener;
    }

    @NonNull
    @Override
    public FotosServicosAdapter.FotosServicosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View serviceView = inflater.inflate(R.layout.row_fotos_servicos, parent, false);
        return new FotosServicosAdapter.FotosServicosViewHolder(serviceView);
    }

    @Override
    public void onBindViewHolder(@NonNull FotosServicosAdapter.FotosServicosViewHolder holder, int position) {
        FotosServicos fotos = this.fotos.get(position);
        holder.bindData(fotos, this.mOnClickList);
    }

    @Override
    public int getItemCount() {
        return this.fotos.size();
    }

    //// VIEWHOLDER
    ////-----------------------------------------------------------------------
    public class FotosServicosViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgFoto;

        public FotosServicosViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imgFoto = itemView.findViewById(R.id.fotoServ);
        }

        //responsavel por setar infos na tela
        public void bindData(final FotosServicos foto, final OnClickFoto listener){

            if(foto != null) {
                Picasso.get()
                        .load(foto.getUrl())
                        .into(imgFoto);
            }

            this.imgFoto.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    listener.onClick(foto.getId(), foto.getUrl());
                }
            });
        }
    }
}

