package com.example.projeto_integrador5periodo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.List;

public class FilmeAdapter extends RecyclerView.Adapter<FilmeHolder> {

    private final List<Filme> items;
    private FirebaseFirestore dataBase;
    private StorageReference storage;

    public FilmeAdapter(List<Filme> items) {
        this.items = items;
    }

    public void removerFilme(Filme filme) {
        dataBase = FirebaseFirestore.getInstance();
        int position = items.indexOf(filme);

        deletarPoster(filme.getIdFireStore());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dataBase.collection("usuarios").document(user.getUid())
                .collection("filmes").document(filme.getIdFireStore()).delete();
        items.remove(position);

        notifyItemRemoved(position);
    }

    private void deletarPoster(String idFireStore) {

        dataBase = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        StorageReference imageRef = storage.child("posters/" + user.getUid() + "/" + idFireStore + ".jpeg");
        imageRef.delete();
    }

    @NonNull
    @Override
    public FilmeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilmeHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filme_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FilmeHolder holder, final int position) {

        holder.textTitulo.setText("Titulo: " + items.get(position).getTitulo());
        holder.textClassificacao.setText("Classificação: " + items.get(position).getClassificacao());
        holder.textGenero.setText("Genero: " + items.get(position).getGenero());
        holder.textData.setText("Data de Lançamento: " + items.get(position).getDataLancamento());
        holder.textDiretor.setText("Diretor: " + items.get(position).getDiretor());
        holder.textPais.setText("País: " + items.get(position).getPais());
        holder.textEnredo.setText("Enredo: " + items.get(position).getEnredo());
        holder.textNotaIMDB.setText("Nota IMDB: " + items.get(position).getNotaIMDB());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://pi-5-periodo.appspot.com/posters/"
                + user.getUid() + "/" + items.get(position).getIdFireStore() + ".jpeg");

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                Picasso.get().load(uri).into(holder.poster);
            }
        });

        final Filme filme = items.get(position);
        holder.btnDeletar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmação")
                        .setMessage("Tem certeza que deseja excluir este filme?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Filme filme = items.get(position);
                                removerFilme(filme);

                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}
