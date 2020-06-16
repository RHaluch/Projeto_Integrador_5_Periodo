package com.example.projeto_integrador5periodo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FilmeAdapter extends ArrayAdapter<Filme> {

    private List<Filme> items;

    public FilmeAdapter(Context context, int textViewResourceId, List<Filme> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            Context ctx = getContext();
            LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.filme_layout, null);
        }
        Filme filme = items.get(position);
        if (filme != null) {
            ((TextView) v.findViewById(R.id.textTitulo)).setText("Titulo: " + filme.getTitulo());
            ((TextView) v.findViewById(R.id.textClasssificacao)).setText("Classificação:" + filme.getClassificacao());
            ((TextView) v.findViewById(R.id.textData)).setText("Data: " + filme.getDataLancamento());
            ((TextView) v.findViewById(R.id.textGenero)).setText("Genero: " + filme.getGenero());
            ((TextView) v.findViewById(R.id.textDiretor)).setText("Diretor: "+ filme.getDiretor());
            ((TextView) v.findViewById(R.id.textPais)).setText("Pais: " + filme.getPais());
            ((TextView) v.findViewById(R.id.textNotaIMDB)).setText("Nota IMDB: " + filme.getNotaIMDB());
            ((TextView) v.findViewById(R.id.textEnredo)).setText("Enredo: " + filme.getEnredo());
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            buscarPoster(filme.getIdFireStore(), user.getEmail(), v);
        }
        return v;
    }

    private void buscarPoster(String idFilme, String userEmail, final View v){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pi-5-periodo.appspot.com/posters/"
                + userEmail + "/" + idFilme + ".jpeg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                ImageView poster = v.findViewById(R.id.poster);
                Picasso.get().load(uri).into(poster);
            }
        });
    }
}
