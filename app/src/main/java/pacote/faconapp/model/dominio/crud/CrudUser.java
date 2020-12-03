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

public interface CrudUser {

    //Faz conexao com banco para login retornando as infos em objeto de profissional, para caso seja
    @FormUrlEncoded
    @POST("/users/login")
    Call<Cliente> login(@Field("email") String email);

    @POST("/users/registerUser")
    Call<Cliente> registerUser(@Body Cliente usuario);

    @POST("/users/seekProfessionals")
    Call<List<Cliente>> seekProfessionals(@Body TipoServico tipoServico);

    @PUT("users/completCad")
    Call<Cliente> completCad(@Body Cliente usuario);

    @FormUrlEncoded
    @PUT("/users/attContato")
    Call<Cliente> attContato(@Field("telCell") String telCell, @Field("telFixo") String telFixo, @Field("id") int id);

    @FormUrlEncoded
    @PUT("/users/attEndereco")
    Call<Cliente> attEndereco(@Field("endCep") String endCep, @Field("endRua") String endRua,
                              @Field("endNum") String endNum, @Field("endCidade") String endCidade,
                              @Field("endEstado") String endEstado, @Field("endBairro") String endBairro,
                              @Field("id") int id);
}
