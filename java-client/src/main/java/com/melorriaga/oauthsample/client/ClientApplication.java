package com.melorriaga.oauthsample.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;

public class ClientApplication {

	interface Api {

		@GET("http://localhost:9001/resource/")
		Call<String> securedCall(@Header("Authorization") String authorization);    // you can add Headers using a parameter

		@FormUrlEncoded
		@POST("http://localhost:9000/oauthsample/oauth/token")
        Call<TokenResponse> authenticate(
                @Header("Authorization") String authorization,  // you can add Headers using a parameter
                @Field("grant_type") String grantType,
                @Field("client_id") String clientId,
                @Field("client_secret") String clientSecret,
                @Field("redirect_uri") String redirect_uri,
                @Field("username") String username,
                @Field("password") String password
        );

	}

	public static void main(String[] args) throws Exception {
        // setup OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() { // you can add Headers using an Interceptor
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        String accessToken = null; // get access_token from somewhere (maybe Android SharedPreferences)
                        if (accessToken != null) {
                            Request originalRequest = chain.request();
                            Request requestWithHeader = originalRequest.newBuilder()
                                    .header("Authorization", "Bearer fbd21883-5468-463a-b9c6-c40df3782102")
                                    .build();
                            return chain.proceed(requestWithHeader);
                        } else {
                            return chain.proceed(chain.request());
                        }
                    }
                })
                .build();

        // setup Gson
        Gson gson = new GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

	    // setup Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://localhost/")
                .client(okHttpClient)
                .build();
        Api api = retrofit.create(Api.class);

        // unauthorized call
        Response<String> securedCallResponse = api.securedCall(null).execute();
        System.out.println(securedCallResponse.code() + " - " + securedCallResponse.message());

        // get token
        Response<TokenResponse> authenticateResponse = api.authenticate(
                "Basic Y2xpZW50OnNlY3JldA==",
                "password",
                "client",
                "secret",
                "http://github.com/matoelorriaga",
                "matias",
                "abc123")
                .execute();
        System.out.println("access_token: " + authenticateResponse.body().accessToken);
        System.out.println("refresh_token: " + authenticateResponse.body().refreshToken);
        System.out.println("expires_in: " + authenticateResponse.body().expiresIn);

        // now, authorized call
        Response<String> authorizedSecuredCallResponse = api.securedCall("Bearer " + authenticateResponse.body().accessToken).execute();
        System.out.println(authorizedSecuredCallResponse.code() + " - " + authorizedSecuredCallResponse.message());
    }

}
