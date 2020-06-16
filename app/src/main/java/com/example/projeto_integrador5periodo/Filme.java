package com.example.projeto_integrador5periodo;

import android.graphics.Bitmap;

public class Filme{
    String titulo, classificacao, dataLancamento, genero, diretor, enredo, posterURL,
            pais, notaIMDB, idFireStore;
    Bitmap poster;

    public Filme(String titulo, String classificacao, String dataLancamento, String genero, String diretor,
                 String enredo, String posterURL, String pais, String notaIMDB, Bitmap poster) {
        this.titulo = titulo;
        this.classificacao = classificacao;
        this.dataLancamento = dataLancamento;
        this.genero = genero;
        this.diretor = diretor;
        this.enredo = enredo;
        this.posterURL = posterURL;
        this.pais = pais;
        this.notaIMDB = notaIMDB;
        this.poster = poster;
    }
    public Filme() {
    }

    public String getIdFireStore() {
        return idFireStore;
    }
    public void setIdFireStore(String idFireStore) {
        this.idFireStore = idFireStore;
    }

    public Bitmap getPoster() {
        return poster;
    }
    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getClassificacao() {
        return classificacao;
    }
    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public String getDataLancamento() {
        return dataLancamento;
    }
    public void setDataLancamento(String dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getGenero() {
        return genero;
    }
    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDiretor() {
        return diretor;
    }
    public void setDiretor(String diretor) {
        this.diretor = diretor;
    }

    public String getEnredo() {
        return enredo;
    }
    public void setEnredo(String enredo) {
        this.enredo = enredo;
    }

    public String getPosterURL() {
        return posterURL;
    }
    public void setPosterURL(String posterURL) {
        this.posterURL = posterURL;
    }

    public String getNotaIMDB() {
        return notaIMDB;
    }
    public void setNotaIMDB(String notaIMDB) {
        this.notaIMDB = notaIMDB;
    }

    public String getPais() {
        return pais;
    }
    public void setPais(String pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
        return "Filme{" +
                "titulo='" + titulo + '\'' +
                ", classificacao='" + classificacao + '\'' +
                ", dataLancamento='" + dataLancamento + '\'' +
                ", genero='" + genero + '\'' +
                ", diretor='" + diretor + '\'' +
                ", enredo='" + enredo + '\'' +
                ", poster='" + posterURL + '\'' +
                ", pais='" + pais + '\'' +
                ", notaIMDB=" + notaIMDB +
                '}';
    }
}
