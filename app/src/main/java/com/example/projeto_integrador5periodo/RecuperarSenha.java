package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarSenha extends AppCompatActivity {

    private EditText editEmail;
    private Button btnBack, btnReset;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        editEmail = (EditText) findViewById(R.id.editEmail);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnBack = (Button) findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();
    }

    public void voltar(View view) {
        Intent login = new Intent(RecuperarSenha.this, Login.class);
        startActivity(login);
        finish();
    }

    public void voltar() {
        Intent login = new Intent(RecuperarSenha.this, Login.class);
        startActivity(login);
        finish();
    }

    public void reset(View view) {
        String email = editEmail.getText().toString();
        if(email.isEmpty()){
            editEmail.setError("Digite seu email");
            editEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperarSenha.this, "Email enviado!", Toast.LENGTH_SHORT).show();
                            voltar();
                        } else {
                            Toast.makeText(RecuperarSenha.this, "Falha ao enviar email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
