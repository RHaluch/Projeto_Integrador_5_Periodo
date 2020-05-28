package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.projeto_integrador5periodo.Util.APISingleton;

public class Principal extends AppCompatActivity {

    private TextView textWelcome, resultado;
    private EditText editTitulo;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        textWelcome = findViewById(R.id.textWelcome);
        resultado = findViewById(R.id.resultado);
        editTitulo = findViewById(R.id.editTitulo);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            FirebaseUser user = mAuth.getCurrentUser();
            if(user.getDisplayName()==null) {
                textWelcome.setText("Bem vindo: "+user.getEmail());
            }else{
                textWelcome.setText("Bem vindo: "+user.getDisplayName());
            }
        }
    }

    public void sair(View view){

        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Principal.this,"Saiu com sucesso!",Toast.LENGTH_SHORT).show();
                    }
                });
        LoginManager.getInstance().logOut();

        Intent inicio = new Intent(Principal.this, Login.class);
        startActivity(inicio);
        finish();
    }

    public void buscarFilme(View view) {
        String url = "https://www.omdbapi.com/?apikey=1caed040&t=" + editTitulo.getText();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Filme filme = new Filme();
                try {
                    filme.setTitulo(response.getString("Title"));
                    filme.setClassificacao(response.getString("Rated"));
                    filme.setDataLancamento(response.getString("Released"));
                    filme.setGenero(response.getString("Genre"));
                    filme.setDiretor(response.getString("Director"));
                    filme.setEnredo(response.getString("Plot"));
                    filme.setPoster(response.getString("Poster"));
                    filme.setPais(response.getString("Country"));
                    filme.setNotaIMDB(response.getDouble("imdbRating"));
                    resultado.setText(filme.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        APISingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
