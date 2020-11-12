package pacote.faconapp.model.dominio.crud;

import java.util.List;

import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.TipoServico;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface CrudPro {

    @POST("/professionals/seekProfessions")
    Call<List<TipoServico>> seekProfissions(@Body TipoServico tipoServico);

    @POST("/professionals/registerProfessional")
    Call<Cliente> resgisterProfessional(@Body Cliente professional);

    @FormUrlEncoded
    @PUT("/professionals/attDescricao")
    Call<Cliente> attDescricao(@Field("idProfissional") int idProfissional, @Field("descricao") String descricao);

    @FormUrlEncoded
    @PUT("/professionals/attFormacao")
    Call<Cliente> attFormacao(@Field("idProfissional") int idProfissional, @Field("formacao") String formacao);

    @FormUrlEncoded
    @PUT("/professionals/attContato")
    Call<Cliente> attContato(@Field("email") String email, @Field("telCell") String telCell,
                             @Field("telFixo") String telFixo, @Field("id") int id);

    @FormUrlEncoded
    @PUT("/professionals/attEndereco")
    Call<Cliente> attEndereco(@Field("endCep") String endCep, @Field("endRua") String endRua,
                              @Field("endNum") String endNum, @Field("endCidade") String endCidade,
                              @Field("endEstado") String endEstado, @Field("endBairro") String endBairro,
                              @Field("id") int id);
}
