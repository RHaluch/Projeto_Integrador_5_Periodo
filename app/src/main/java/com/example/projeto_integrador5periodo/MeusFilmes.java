package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class MeusFilmes extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_filmes);
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
                        buscarPoster(filme.getIdFireStore(), user.getEmail());
                        //ImageView imageView = findViewById(R.id.imageView);
                        mostrarGrid(filme);
                       /* EditText trazerTitulo = findViewById(R.id.trazerTitulo);
                        trazerTitulo.setVisibility(View.VISIBLE);
                        trazerTitulo.setText(filme.toString());*/
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeusFilmes.this,"Falha ao buscar o filme informado!",Toast.LENGTH_SHORT).show();
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
                        buscarPoster(filme.getIdFireStore(), user.getEmail());

                        mostrarGrid(filme);
                        lista.add(filme);
                    }
                    //EditText trazerTitulo = findViewById(R.id.trazerTitulo);
                    //trazerTitulo.setVisibility(View.VISIBLE);
                    //trazerTitulo.setText(lista.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeusFilmes.this,"Falha ao buscar lista de filmes!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarPoster(String idFilme, String userEmail){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pi-5-periodo.appspot.com/posters/"
                + userEmail + "/" + idFilme + ".jpeg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                ImageView poster = findViewById(R.id.poster);
                Picasso.get().load(uri).into(poster);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeusFilmes.this,"Falha no download no Poster!",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void mostrarGrid(Filme filme) {
        GridLayout grid = findViewById(R.id.Grid);
        TextView titulo = findViewById(R.id.textTitulo);
        titulo.setText(filme.getTitulo());
        grid.setVisibility(View.VISIBLE);
    }
}
