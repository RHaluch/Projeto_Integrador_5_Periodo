package com.example.projeto_integrador5periodo;

import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilmeHolder extends RecyclerView.ViewHolder {

    public TextView textTitulo, textGenero, textClassificacao, textData, textNotaIMDB, textDiretor;
    public TextView textEnredo, textPais;
    public ImageView poster;
    public Button btnDeletar;

    public FilmeHolder(@NonNull View itemView) {
        super(itemView);

        textClassificacao = (TextView) itemView.findViewById(R.id.textClasssificacao);
        textTitulo = (TextView) itemView.findViewById(R.id.textTitulo);
        textGenero = (TextView) itemView.findViewById(R.id.textGenero);
        textData = (TextView) itemView.findViewById(R.id.textData);
        textNotaIMDB = (TextView) itemView.findViewById(R.id.textNotaIMDB);
        textDiretor = (TextView) itemView.findViewById(R.id.textDiretor);
        textEnredo = (TextView) itemView.findViewById(R.id.textEnredo);
        textPais = (TextView) itemView.findViewById(R.id.textPais);
        poster = (ImageView) itemView.findViewById(R.id.poster);
        btnDeletar = (Button) itemView.findViewById(R.id.btnDeletar);

    }
}
