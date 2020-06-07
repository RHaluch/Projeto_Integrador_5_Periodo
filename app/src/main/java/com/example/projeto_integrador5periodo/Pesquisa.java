package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private TextView textUsuario, resultado;
    private EditText editTitulo, editAno;
    private FirebaseAuth mAuth;
    private ImageView poster;
    private FirebaseFirestore dataBase;
    private StorageReference storage;
    Filme filme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa);

        textUsuario = findViewById(R.id.textUsuario);
        resultado = findViewById(R.id.resultado);
        editTitulo = findViewById(R.id.editTitulo);
        editAno = findViewById(R.id.editAno);
        mAuth = FirebaseAuth.getInstance();
        poster = findViewById(R.id.poster);
        dataBase = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

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
            if(!ano.isEmpty()) {
                url = "https://www.omdbapi.com/?apikey=1caed040&t=" + editTitulo.getText() + "&y=" + ano;
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
            Toast.makeText(Pesquisa.this,"Por favor informe o titulo do filme!",Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarFilme(){
        poster.setImageBitmap(getBitmapFromURL(filme.getPosterURL()));
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

    public void adicionarFilme(View view) {
        if(filme==null){
            Toast.makeText(Pesquisa.this,"Favor encontrar um filme antes!",Toast.LENGTH_SHORT).show();
        }else{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            dataBase.collection("usuarios").document(user.getEmail()).collection("filmes").add(filme).addOnSuccessListener(
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
}

