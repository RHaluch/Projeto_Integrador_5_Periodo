package com.example.projeto_integrador5periodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.github.dhaval2404.imagepicker.ImagePicker;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Mascara extends AppCompatActivity {

    private File imageFile;
    private Button btnCarregar, btnVerificar;
    private ImageView imageView;
    private ProgressBar loading;
    private ImageView imagePicked;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mascara);
        btnCarregar = findViewById(R.id.btnCarregar);
        btnVerificar = findViewById(R.id.btnverificarMascara);
        imageView = findViewById(R.id.imageView);
        loading = findViewById(R.id.loading);
        info = findViewById(R.id.info);

    }

    public void carregarFoto(View view) {
        info.setVisibility(View.INVISIBLE);
        ImagePicker.Companion.with(this).compress(1024).maxResultSize(1080, 1080).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            Uri uri = data.getData();
            imageView.setImageURI(uri);
            imageFile = ImagePicker.Companion.getFile(data);
        }else if(resultCode==ImagePicker.RESULT_ERROR){
            Toast.makeText(Mascara.this,ImagePicker.Companion.getError(data),Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Mascara.this,"Tarefa cancelada",Toast.LENGTH_SHORT).show();
        }
    }

    public void processar(View view) {
        String postUrl= "http://192.168.15.12:5000";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // Read BitMap by file path
        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(imageFile), options);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        RequestBody postBodyImage = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                .build();
        info.setVisibility(View.VISIBLE);
        info.setText("Processando por favor aguarde...");
        loading.setVisibility(View.VISIBLE);
        postRequest(postUrl, postBodyImage);
    }

    public void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        info.setText("Falha ao conectar com servidor!");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    // Remember to set the bitmap in the main thread.
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            loading.setVisibility(View.GONE);
                            info.setVisibility(View.GONE);
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                } else {
                    //Handle the error
                }
            }
        });
    }
}
