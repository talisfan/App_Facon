package pacote.faconapp.model.dominio.crud;

import pacote.faconapp.model.dominio.entidades.Proposta;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CrudProposta {

    // cria proposta
    @POST("/proposta")
    Call<Proposta> criarProposta(@Body Proposta proposta);

    // resgata proposta
    @FormUrlEncoded
    @GET("/proposta?token={token}&idFb={idFb}")
    Call<Proposta> getProposta(@Path("token") String token, @Path("idFb") String idFbUserLogado);

    // atualiza proposta
    @FormUrlEncoded
    @PUT("/proposta")
    Call<Proposta> updateProposta(@Field("token") String token, @Field("statusProp") String status);

}
