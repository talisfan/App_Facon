package pacote.faconapp.model.dominio.crud;

import java.util.List;

import pacote.faconapp.model.dominio.entidades.Cliente;
import pacote.faconapp.model.dominio.entidades.FotosServicos;
import pacote.faconapp.model.dominio.entidades.Proposta;
import pacote.faconapp.model.dominio.entidades.TipoServico;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface CrudPro {

    @POST("/professionals/seekProfessions")
    Call<List<TipoServico>> seekProfissions(@Body TipoServico tipoServico);

    @POST("/professionals/registerProfessional")
    Call<Cliente> resgisterProfessional(@Body Cliente professional);

    @POST("/professionals/insertFoto")
    Call<FotosServicos> insertFoto(@Body FotosServicos fotosServicos);

    @FormUrlEncoded
    @POST("/professionals/deleteFoto")
    Call<FotosServicos> deleteFoto(@Field("idFoto") int idFoto);

    @FormUrlEncoded
    @PUT("/professionals/attDescricao")
    Call<Cliente> attDescricao(@Field("idProfissional") int idProfissional, @Field("descricao") String descricao);

    @FormUrlEncoded
    @PUT("/professionals/attFormacao")
    Call<Cliente> attFormacao(@Field("idProfissional") int idProfissional, @Field("formacao") String formacao);

    @GET("/professionals/fotos")
    Call<List<FotosServicos>> getFotosServices(@Query("idUser") int idUser);

    @GET("/professionals/infosPro")
    Call <Cliente> getInfosPro(@Query("idFb") String idFb);
}
