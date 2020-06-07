package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Principal extends AppCompatActivity {

    private TextView textWelcome;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        textWelcome = findViewById(R.id.textWelcome);
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
                if(!user.getDisplayName().isEmpty()) {
                    textWelcome.setText("Bem vindo: " + user.getDisplayName());
                }else{
                    textWelcome.setText("Bem vindo: "+user.getEmail());
                }
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
        Intent buscar = new Intent(Principal.this, Pesquisa.class);
        startActivity(buscar);
        finish();
    }

    public void abrirMascara(View view) {
        Intent mascara = new Intent(Principal.this, Mascara.class);
        startActivity(mascara);
        finish();
    }

    public void meuFilmeTitulo(final View View){
        Query query;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        CollectionReference filmes = db.collection("usuarios").document(user.getEmail()).collection("filmes");
        EditText trazerTitulo = findViewById(R.id.trazerTitulo);
        query = filmes.whereEqualTo("titulo", trazerTitulo.getText().toString());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Filme filme = new Filme();
                        filme.setIdFireStore(doc.getId());
                        filme.setGenero(doc.getString("genero"));
                        filme.setDataLancamento(doc.getString("dataLancamento"));
                        filme.setClassificacao(doc.getString("classificacao"));
                        filme.setTitulo(doc.getString("titulo"));
                        filme.setDiretor(doc.getString("diretor"));
                        filme.setEnredo(doc.getString("enredo"));
                        filme.setNotaIMDB(doc.getString("notaIMDB"));
                        filme.setPais(doc.getString("pais"));
                        //buscarPoster(filme.getIdFireStore(), user.getEmail());
                        //filme.setPoster(imageView.getImage);
                        EditText trazerTitulo = findViewById(R.id.trazerTitulo);
                        trazerTitulo.setVisibility(View.VISIBLE);
                        trazerTitulo.setText(filme.toString());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Principal.this,"Falha ao buscar o filme informado!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void buscarMeusFilmes(View view) {
        Query query;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        CollectionReference filmes = db.collection("usuarios").document(user.getEmail()).collection("filmes");
        query = filmes;

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<Filme> lista = new ArrayList();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Filme filme = new Filme();
                        filme.setIdFireStore(doc.getId());
                        filme.setGenero(doc.getString("genero"));
                        filme.setDataLancamento(doc.getString("dataLancamento"));
                        filme.setClassificacao(doc.getString("classificacao"));
                        filme.setTitulo(doc.getString("titulo"));
                        filme.setDiretor(doc.getString("diretor"));
                        filme.setEnredo(doc.getString("enredo"));
                        filme.setNotaIMDB(doc.getString("notaIMDB"));
                        filme.setPais(doc.getString("pais"));
                        //buscarPoster(filme.getIdFireStore(), user.getEmail());
                        //filme.setPoster(imageView.getImage);
                        lista.add(filme);
                    }
                    EditText trazerTitulo = findViewById(R.id.trazerTitulo);
                    trazerTitulo.setVisibility(View.VISIBLE);
                    trazerTitulo.setText(lista.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Principal.this,"Falha ao buscar lista de filmes!",Toast.LENGTH_SHORT).show();
            }
        });
    }

   /* private void buscarPoster(String idFilme, String userEmail){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pi-5-periodo.appspot.com/posters/"
                + userEmail + "/" + idFilme + ".jpeg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
               Picasso.get().load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Principal.this,"Falha no download no Poster!",Toast.LENGTH_LONG).show();
            }
        });

        return null;
    }*/
}
