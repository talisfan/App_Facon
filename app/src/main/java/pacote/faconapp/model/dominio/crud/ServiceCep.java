package pacote.faconapp.model.dominio.crud;

import pacote.faconapp.model.dominio.entidades.Cep;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ServiceCep {

    @GET("/ws/{cep}/json")
    Call<Cep> buscaCep(@Path("cep") String cep);
}
