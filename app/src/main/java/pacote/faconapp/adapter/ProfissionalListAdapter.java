package pacote.faconapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pacote.faconapp.MetodosEstaticos;
import pacote.faconapp.R;
import pacote.faconapp.listener.OnClickProfissional;
import pacote.faconapp.model.dominio.entidades.Cliente;

public class ProfissionalListAdapter extends RecyclerView.Adapter<ProfissionalListAdapter.ServicesViewHolder> {

    //criando outra lista
    private List<Cliente> mListProfissional;
    private OnClickProfissional mOnClickProfissional;

    //construtor com parametros de lista
    public ProfissionalListAdapter(List<Cliente> profissionals, OnClickProfissional listener) {
        this.mListProfissional = profissionals;
        this.mOnClickProfissional = listener;
    }

    @NonNull
    @Override
    public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View serviceView = inflater.inflate(R.layout.row_services_list, parent, false);
        return new ServicesViewHolder(serviceView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfissionalListAdapter.ServicesViewHolder holder, int position) {
        //criando elemento profissional e pegando posição do array (mListProfissional)
        Cliente profissional = this.mListProfissional.get(position);
        holder.bindData(profissional, this.mOnClickProfissional);
    }

    //retorna quantos itens estam presente na listagem / a viewHolder está trabalhando
    @Override
    public int getItemCount() {
        //passando tamanho da lista
        return this.mListProfissional.size();
    }

    //// VIEWHOLDER
    ////-----------------------------------------------------------------------
    public class ServicesViewHolder extends RecyclerView.ViewHolder {

        private TextView mNomeProfissional;
        private TextView mProfissao;
        private TextView mExperiencia;
        private ImageView mFoto;
        private ImageView mAvaliacao;
        private  TextView mDetalhes;

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);

            this.mNomeProfissional = itemView.findViewById(R.id.lblNomeProfissional);
            this.mProfissao = itemView.findViewById(R.id.lblProfissao);
            this.mExperiencia = itemView.findViewById(R.id.lblExperiencia);
            this.mFoto = itemView.findViewById(R.id.foto);
            this.mAvaliacao = itemView.findViewById(R.id.stars);
            this.mDetalhes = itemView.findViewById(R.id.lblDetalhes);
        }

        //responsavel por setar na tela as linhas
        public void bindData(final Cliente profissional, final OnClickProfissional listener) {

            switch (profissional.getEstrelas()) {
                case 1:
                    this.mAvaliacao.setImageResource(R.drawable.stars_one);
                    break;
                case 2:
                    this.mAvaliacao.setImageResource(R.drawable.stars_two);
                    break;
                case 3:
                    this.mAvaliacao.setImageResource(R.drawable.stars_three);
                    break;
                case 4:
                    this.mAvaliacao.setImageResource(R.drawable.stars_four);
                    break;
                case 5:
                    this.mAvaliacao.setImageResource(R.drawable.stars_five);
                    break;
                default:
                    this.mAvaliacao.setImageResource(R.drawable.stars_default);
                    break;
            }

            String exp = MetodosEstaticos.calcularExpPro(profissional.getExperiencia());

            mNomeProfissional.setText(profissional.getNome());
            mProfissao.setText(profissional.getProfissao());
            mExperiencia.setText(" a " + exp + " meses.");
            //this.mFoto.setImageDrawable(profissional.getFoto());

            this.mDetalhes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //salvando id do profissional como click no listener
                    listener.onClickProfissional(profissional);
                }
            });
        }
    }
}
