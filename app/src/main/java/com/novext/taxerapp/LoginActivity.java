package com.novext.taxerapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    loginButton = (LoginButton) findViewById(R.id.btnFacebook);

    loginButton.setReadPermissions("email");



    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Toast.makeText(LoginActivity.this, loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
            Toast.makeText(LoginActivity.this, loginResult.getAccessToken().getToken(), Toast.LENGTH_SHORT).show();

            final AccessToken accessToken = loginResult.getAccessToken();

            final JSONObject obj = new JSONObject();
            final OKHttp okHttp = new OKHttp();
            final String url  = "https://dietapplication.herokuapp.com/api/users/social";

            GraphRequestAsyncTask request_faccebook = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject user, GraphResponse graphResponse) {


                    try {
                        obj.put("firstname",user.optString("name"));
                        obj.put("lastname",user.optString("email"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).executeAsync();
        }
        @Override
        public void onCancel() {

        }
        @Override
        public void onError(FacebookException error) {
            Log.e("AQUI ERROR 2", String.valueOf(error));
        }
    });
    String txtGoogle = "Iniciar sesión con Google";
    setGooglePlusButtonText(btnLoginGoogle,txtGoogle);
}

}
