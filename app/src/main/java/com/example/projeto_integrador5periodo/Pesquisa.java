package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class Pesquisa extends AppCompatActivity {

    private TextView textUsuario, textTitulo, textNotaIMDB, textGenero, textDiretor, textEnredo;
    private TextView textData, textClassificacao, textPais;
    private GridLayout grid;
    private EditText editTitulo, editAno;
    private FirebaseAuth mAuth;
    private ImageView poster;
    private FirebaseFirestore dataBase;
    private StorageReference storage;
    private GoogleSignInClient mGoogleSignInClient;
    Filme filme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa);

        textUsuario = findViewById(R.id.textUsuario);
        textTitulo = findViewById(R.id.textTitulo);
        textNotaIMDB = findViewById(R.id.textNotaIMDB);
        textGenero = findViewById(R.id.textGenero);
        textDiretor = findViewById(R.id.textDiretor);
        textData = findViewById(R.id.textData);
        textClassificacao = findViewById(R.id.textClasssificacao);
        textPais = findViewById(R.id.textPais);
        textEnredo = findViewById(R.id.textEnredo);
        editTitulo = findViewById(R.id.editTitulo);
        editAno = findViewById(R.id.editAno);
        mAuth = FirebaseAuth.getInstance();
        poster = findViewById(R.id.poster);
        grid = findViewById(R.id.Grid);
        dataBase = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
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
                textUsuario.setText(user.getEmail());
            }else{
                if(!user.getDisplayName().isEmpty()) {
                    textUsuario.setText(user.getDisplayName());
                }else{
                    textUsuario.setText(user.getEmail());
                }
            }
        }
    }

    public void voltar(View view){
        Intent principal = new Intent(Pesquisa.this, Principal.class);
        startActivity(principal);
        finish();
    }

    public void buscarFilme(View view) {
        String titulo = editTitulo.getText().toString();
        String ano = editAno.getText().toString();

        if(!titulo.isEmpty()) {
            String url;
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editTitulo.getWindowToken(), 0);

            if(!ano.isEmpty()) {
                url = "https://www.omdbapi.com/?apikey=1caed040&type=movie&t=" + editTitulo.getText() + "&y=" + ano;
            }else{
                url = "https://www.omdbapi.com/?apikey=1caed040&type=movie&t=" + editTitulo.getText();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(response.getString("Response").contains("False")){
                            Toast.makeText(Pesquisa.this,"Filme não encontrado!",Toast.LENGTH_LONG).show();
                            grid.setVisibility(View.GONE);
                            poster.setImageBitmap(null);
                            filme = null;
                        }else {
                            filme = new Filme();
                            filme.setTitulo(response.getString("Title"));
                            filme.setClassificacao(response.getString("Rated"));
                            filme.setDataLancamento(response.getString("Released"));
                            filme.setGenero(response.getString("Genre"));
                            filme.setDiretor(response.getString("Director"));
                            filme.setEnredo(response.getString("Plot"));
                            filme.setPosterURL(response.getString("Poster"));
                            filme.setPais(response.getString("Country"));
                            filme.setNotaIMDB(response.getString("imdbRating"));
                            mostrarFilme();
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
            editTitulo.setError("Digite um nome de filme para procurar!");
            editTitulo.requestFocus();
        }
    }

    private void mostrarFilme(){
        poster.setImageBitmap(getBitmapFromURL(filme.getPosterURL()));
        textTitulo.setText("Titulo: " + filme.getTitulo());
        textClassificacao.setText("Classificação: " + filme.getClassificacao());
        textGenero.setText("Genero: " + filme.getGenero());
        textData.setText("Data de Lançamento: " + filme.getDataLancamento());
        textDiretor.setText("Diretor: " + filme.getDiretor());
        textPais.setText("País: " + filme.getPais());
        textEnredo.setText("Enredo: " + filme.getEnredo());
        textNotaIMDB.setText("Nota IMDB: " + filme.getNotaIMDB());
        grid.setVisibility(View.VISIBLE);
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

    public void adicionarFilme(View view) {
        if(filme==null){
            Toast.makeText(Pesquisa.this,"Favor encontrar um filme antes!",Toast.LENGTH_SHORT).show();
        }else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            dataBase.collection("usuarios").document(user.getUid()).collection("filmes").add(filme).addOnSuccessListener(
                    new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String idFilme = documentReference.getId();
                            StorageReference imageRef = storage.child("posters/" + documentReference.getParent().getParent().getId() + "/" + idFilme+".jpeg");

                            Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = imageRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Pesquisa.this,"Falha no upload do poster",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(Pesquisa.this,"Filme salvo com sucesso!",Toast.LENGTH_LONG).show();
                                    finish();
                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                }
                            });
                        }
                    }
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Pesquisa.this,"Falha ao adicionar filme!",Toast.LENGTH_LONG).show();
                }
            });
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

        Toast.makeText(Pesquisa.this,"Desconectado do sistema!",Toast.LENGTH_SHORT).show();
        Intent inicio = new Intent(Pesquisa.this, Login.class);
        startActivity(inicio);
        finish();
    }
}

