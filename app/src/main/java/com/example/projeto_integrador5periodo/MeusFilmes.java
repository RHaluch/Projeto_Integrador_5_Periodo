package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeusFilmes extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private FilmeAdapter adapter;
    private TextView textUser, textAchou;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_filmes);

        textUser = findViewById(R.id.textUser);
        textAchou = findViewById(R.id.textAchou);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            FirebaseUser user = mAuth.getCurrentUser();
            if(user.getDisplayName()==null) {
                textUser.setText(user.getEmail());
            }else{
                if(!user.getDisplayName().isEmpty()) {
                    textUser.setText(user.getDisplayName());
                }else{
                    textUser.setText(user.getEmail());
                }
            }
        }
        buscarMeusFilmes();
    }

    public void buscarMeusFilmes() {
        Query query;
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        CollectionReference filmes;

        try{
            filmes = db.collection("usuarios").document(user.getEmail()).collection("filmes");
        }catch(Exception e){
            filmes = db.collection("usuarios").document(user.getUid()).collection("filmes");
        }

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
                    if(lista.size()>0) {
                        textAchou.setVisibility(View.INVISIBLE);
                        Collections.sort(lista, new Comparator<Filme>() {
                            @Override
                            public int compare(Filme item, Filme t1) {
                                String s1 = item.getTitulo();
                                String s2 = t1.getTitulo();
                                return s1.compareToIgnoreCase(s2);
                            }
                        });
                    }else{
                        textAchou.setVisibility(View.VISIBLE);
                    }
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

    public void sair(View view){

        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
        LoginManager.getInstance().logOut();

        Toast.makeText(MeusFilmes.this,"Desconectado do sistema!",Toast.LENGTH_SHORT).show();
        Intent inicio = new Intent(MeusFilmes.this, Login.class);
        startActivity(inicio);
        finish();
    }
}
