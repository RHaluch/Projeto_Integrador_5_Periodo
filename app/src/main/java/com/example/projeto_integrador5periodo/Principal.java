package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class Principal extends AppCompatActivity {

    private TextView textWelcome, resultado;
    private EditText editTitulo, editAno;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        textWelcome = findViewById(R.id.textWelcome);
        resultado = findViewById(R.id.resultado);
        editTitulo = findViewById(R.id.editTitulo);
        editAno = findViewById(R.id.editAno);
        mAuth = FirebaseAuth.getInstance();
        poster = findViewById(R.id.poster);
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
            if(user.getDisplayName().isEmpty()) {
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
                    }
                });
        LoginManager.getInstance().logOut();

        Toast.makeText(Principal.this,"Saiu com sucesso!",Toast.LENGTH_SHORT).show();
        Intent inicio = new Intent(Principal.this, Login.class);
        startActivity(inicio);
        finish();
    }

    public void buscarFilme(View view) {
        String titulo = editTitulo.getText().toString();
        String ano = editAno.getText().toString();

        if(!titulo.isEmpty()) {
            String url;
            if(!ano.isEmpty()) {
                url = "https://www.omdbapi.com/?apikey=1caed040&t=" + editTitulo.getText() + "y=" + ano;
            }else{
                url = "https://www.omdbapi.com/?apikey=1caed040&t=" + editTitulo.getText();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("Response").contains("False")){
                            resultado.setText("Filme não encontrado!");
                            poster.setImageBitmap(null);
                        }else {
                            Filme filme = new Filme();
                            filme.setTitulo(response.getString("Title"));
                            filme.setClassificacao(response.getString("Rated"));
                            filme.setDataLancamento(response.getString("Released"));
                            filme.setGenero(response.getString("Genre"));
                            filme.setDiretor(response.getString("Director"));
                            filme.setEnredo(response.getString("Plot"));
                            filme.setPoster(response.getString("Poster"));
                            filme.setPais(response.getString("Country"));
                            filme.setNotaIMDB(response.getDouble("imdbRating"));
                            mostrarFilme(filme);
                        }
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
        }else{
            Toast.makeText(Principal.this,"Por favor informe o titulo do filme!",Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarFilme(Filme filme){
        Bitmap posterFilme = getBitmapFromURL(filme.getPoster());
        poster.setImageBitmap(posterFilme);
        resultado.setText(filme.toString());
    }
    private Bitmap getBitmapFromURL(String src) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
            StrictMode.setThreadPolicy(policy);
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
