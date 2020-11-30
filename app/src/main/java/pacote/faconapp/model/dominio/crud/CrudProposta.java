package pacote.faconapp.model.dominio.crud;

import pacote.faconapp.model.dominio.entidades.Proposta;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface CrudProposta {

    // cria proposta
    @POST("/proposta")
    Call<Proposta> criarProposta(@Body Proposta proposta);

    // resgata proposta
    @FormUrlEncoded
    @GET("/proposta")
    Call<Proposta> getProposta(@Field("token") String token);

    // atualiza proposta
    @FormUrlEncoded
    @PUT("/proposta")
    Call<Proposta> updateProposta(@Field("token") String token, @Field("statusProp") String status);

}
