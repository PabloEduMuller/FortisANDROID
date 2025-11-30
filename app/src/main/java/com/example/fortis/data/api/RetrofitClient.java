package com.example.fortis.data.api;

import com.google.gson.Gson; // <-- 1. IMPORTAR GSON
import com.google.gson.GsonBuilder; // <-- 2. IMPORTAR GSON BUILDER

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // (Use o seu IP local aqui)
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;

    /**
     * Retorna uma instância do Retrofit.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {

            // --- 3. CONFIGURAR O GSON (O PARSER JSON) ---
            // Isto garante que o Android (Gson) e o Backend (Jackson)
            // usam o MESMO formato de data ISO-8601.
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .create();

            // --- 4. CONSTRUIR O RETROFIT COM O GSON CONFIGURADO ---
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // Adiciona o conversor Gson com o nosso formato de data
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    /**
     * Retorna a implementação da interface ApiService.
     */
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}