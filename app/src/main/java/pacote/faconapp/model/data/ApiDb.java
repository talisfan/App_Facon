package pacote.faconapp.model.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiDb {
    private static Retrofit getRetrofitClient(){
        return new Retrofit.Builder()
                .baseUrl("http://192.168.0.109:3000") //link sem / no final
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    //permite instanciar o serviço desejado (CRUD) independente de qual seja ele (interfaces)
    public static <S> S createService(Class<S> serviceClass){
        return getRetrofitClient().create(serviceClass);
    }

}
