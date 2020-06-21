package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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


public class Principal extends AppCompatActivity {

    private TextView textUser;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        textUser = findViewById(R.id.textUser);
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
                textUser.setText("Bem-vindo: " + user.getEmail());
            }else{
                if(!user.getDisplayName().isEmpty()) {
                    textUser.setText("Bem-vindo: " + user.getDisplayName());
                }else{
                    textUser.setText("Bem-vindo: " + user.getEmail());
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

        Toast.makeText(Principal.this,"Desconectado do sistema!",Toast.LENGTH_SHORT).show();
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

    public void minhaLista(View view) {
        Intent meusFilmes = new Intent(Principal.this, MeusFilmes.class);
        startActivity(meusFilmes);
        finish();
    }
}
