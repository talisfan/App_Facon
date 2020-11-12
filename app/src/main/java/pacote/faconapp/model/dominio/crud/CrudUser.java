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
    Call<Cliente> login(@Field("email") String email,
                        @Field("senha") String senha);

    @POST("/users/registerUser")
    Call<Cliente> registerUser(@Body Cliente usuario);

    @POST("/users/seekProfessionals")
    Call<List<Cliente>> seekProfessionals(@Body TipoServico tipoServico);

    @PUT("users/completCad")
    Call<Cliente> completCad(@Body Cliente usuario);
}
