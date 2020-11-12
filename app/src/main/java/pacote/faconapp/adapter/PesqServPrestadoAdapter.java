package pacote.faconapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pacote.faconapp.R;
import pacote.faconapp.model.dominio.entidades.TipoServico;
import pacote.faconapp.listener.OnClickProfissao;

public class PesqServPrestadoAdapter extends RecyclerView.Adapter<PesqServPrestadoAdapter.PesqServPrestadoViewHolder> {

    private List<TipoServico> mTipoServico;
    private OnClickProfissao mOnClickList;

    public PesqServPrestadoAdapter (List<TipoServico> tipoServico, OnClickProfissao listener) {
        this.mTipoServico = tipoServico;
        this.mOnClickList = listener;
    }

    @NonNull
    @Override
    public PesqServPrestadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View serviceView = inflater.inflate(R.layout.row_pesquisa_serv_prestado, parent, false);
        return new PesqServPrestadoViewHolder(serviceView);
    }

    @Override
    public void onBindViewHolder(@NonNull PesqServPrestadoAdapter.PesqServPrestadoViewHolder holder, int position) {
        TipoServico tipoServico = this.mTipoServico.get(position);
        holder.bindData(tipoServico, this.mOnClickList);
    }

    @Override
    public int getItemCount() {
        return this.mTipoServico.size();
    }

    //// VIEWHOLDER
    ////-----------------------------------------------------------------------
    public class PesqServPrestadoViewHolder extends RecyclerView.ViewHolder {

        private TextView mProfissao;

        public PesqServPrestadoViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mProfissao = itemView.findViewById(R.id.lblProfissao);
        }

        //responsavel por setar na tela as linhas
        public void bindData(final TipoServico tipoServico, final OnClickProfissao listener){

            this.mProfissao.setText(tipoServico.getProfissao());

            this.mProfissao.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {

                    listener.onClick(tipoServico.getId(), tipoServico.getProfissao());
                }
            });
        }
    }
}
