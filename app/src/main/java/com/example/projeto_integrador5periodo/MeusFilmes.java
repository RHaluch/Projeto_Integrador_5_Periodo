package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeusFilmes extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private FilmeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_filmes);

        //listView = findViewById(R.id.listaFilmes);

        /*listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Filme filme = (Filme)listView.getItemAtPosition(i);
                Toast.makeText(MeusFilmes.this,filme.getIdFireStore(),Toast.LENGTH_SHORT).show();
            }
        });*/
    }

   /* public void meuFilmeTitulo(final View View){
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
                        trazerTitulo.setText(filme.toString());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeusFilmes.this,"Falha ao buscar o filme informado!",Toast.LENGTH_SHORT).show();
            }
        });

    }*/

    public void buscarMeusFilmes(View view) {
        Query query;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        CollectionReference filmes = db.collection("usuarios").document(user.getEmail()).collection("filmes");
        query = filmes;

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    List<Filme> lista = new ArrayList<>();
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
                        lista.add(filme);
                    }

                    Collections.sort(lista,new Comparator<Filme>() {
                        @Override
                        public int compare(Filme item, Filme t1) {
                            String s1 = item.getTitulo();
                            String s2 = t1.getTitulo();
                            return s1.compareToIgnoreCase(s2);
                        }
                    });
                    preencherRecyclerView(lista);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeusFilmes.this,"Falha ao buscar lista de filmes!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void preencherRecyclerView(List<Filme> lista){
        recyclerView = (RecyclerView)findViewById(R.id.listaFilmes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new FilmeAdapter(lista);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public void voltarPrincipal(View view) {
        Intent principal = new Intent(MeusFilmes.this, Principal.class);
        startActivity(principal);
    }
}
