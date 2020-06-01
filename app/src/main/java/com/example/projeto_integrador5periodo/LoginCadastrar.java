package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginCadastrar extends AppCompatActivity {

    private EditText editLogin, editSenha, editConSenha;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_cadastrar);

        editLogin = findViewById(R.id.editLogin);
        editSenha = findViewById(R.id.editSenha);
        editConSenha = findViewById(R.id.editConSenha);

        mAuth = FirebaseAuth.getInstance();
    }

    public void registrar(View view) {
        String login = editLogin.getText().toString();
        String senha = editSenha.getText().toString();
        String confirmacao = editConSenha.getText().toString();

        if (login.isEmpty()){
            editLogin.setError("Preencha o campo com seu email para login");
            editLogin.requestFocus();
            return;
        }
        if(senha.isEmpty()){
            editSenha.setError("Digite uma senha");
            editSenha.requestFocus();
            return;
        }
        if(confirmacao.isEmpty()){
            editConSenha.setError("Comfirme sua senha");
            editConSenha.requestFocus();
            return;
        }

        if (senha.equals(confirmacao)){
            mAuth.createUserWithEmailAndPassword(login,senha)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            }else {
                                Toast.makeText(LoginCadastrar.this, "Falha ao criar o usuario", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }else {
            editConSenha.setError("A confirmação deve ser igual a senha");
            editConSenha.requestFocus();
            updateUI(null);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            Intent principal = new Intent(LoginCadastrar.this,Principal.class);
            startActivity(principal);
            finish();
        }
    }

    public void voltar(View view) {
        Intent login = new Intent(LoginCadastrar.this,Login.class);
        startActivity(login);
    }
}
