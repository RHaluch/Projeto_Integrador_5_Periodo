package com.example.projeto_integrador5periodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText editLogin, editSenha;
    private GoogleSignInClient mGoogleSignInClient; //Cliente do Google
    private FirebaseAuth mAuth; //Firebase Autenticador
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Objetos Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.signInGoogle).setOnClickListener(this);

        //Instancia Firebase
        mAuth = FirebaseAuth.getInstance();

        //Facebook
        mCallbackManager = CallbackManager.Factory.create();
        findViewById(R.id.signInFacebook).setOnClickListener(this);

        editLogin = findViewById(R.id.editLogin);
        editSenha = findViewById(R.id.editSenha);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser()!=null){
            /*GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
            if(googleAccount != null) {
                Intent principal = new Intent(Login.this, Principal.class);
                startActivity(principal);
                //updateUI();
            }*/
            updateUI();
        }
    }

    public void cadastrar(View view) {
        Intent novoLogin = new Intent(Login.this, LoginCadastrar.class);
        startActivity(novoLogin);
        finish();
    }

    public void entrarEmail(View view) {
        String login = editLogin.getText().toString();
        String senha = editSenha.getText().toString();

        mAuth.signInWithEmailAndPassword(login,senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI();
                }else{
                    Toast.makeText(Login.this,"Falha ao Autenticar Usuario!",Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            //Google
            case R.id.signInGoogle:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
                break;

                //Facebook
            case R.id.signInFacebook:
                LoginButton loginButton = findViewById(R.id.signInFacebook);
                loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(Login.this,"Login com facobook efetuado!",Toast.LENGTH_SHORT).show();
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                       Toast.makeText(Login.this,"Login com facobook cancelado!",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(Login.this,"Erro ao tentar login com facobook!",Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { //Google
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUI() {
        Intent principal = new Intent(Login.this, Principal.class);
        startActivity(principal);
        finish();
    }

    //LOGIN COM GOOGLE -------------------------------------------------------------------------------------------
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(Login.this,"Falha ao Logar com Conta Goggle! Codigo do Erro =" + e.getStatusCode(),Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this,"Entrou Com Google!",Toast.LENGTH_SHORT).show();
                            updateUI();
                        } else {
                            Toast.makeText(Login.this,"Falha ao autenticar Com Google!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //LOGIN COM FACEBOOK ----------------------------------------------------------------------------------------------
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUI();
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
