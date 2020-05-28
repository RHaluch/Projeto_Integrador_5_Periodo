package com.example.projeto_integrador5periodo;

public class Filme {
    String titulo, classificacao, dataLancamento, genero, diretor, enredo, poster, pais;
    double notaIMDB;

    public Filme(String titulo, String classificacao, String dataLancamento, String genero, String diretor, String enredo, String poster, String pais, double notaIMDB) {
        this.titulo = titulo;
        this.classificacao = classificacao;
        this.dataLancamento = dataLancamento;
        this.genero = genero;
        this.diretor = diretor;
        this.enredo = enredo;
        this.poster = poster;
        this.pais = pais;
        this.notaIMDB = notaIMDB;
    }
    public Filme() {
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

    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }

    public double getNotaIMDB() {
        return notaIMDB;
    }
    public void setNotaIMDB(double notaIMDB) {
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
                ", poster='" + poster + '\'' +
                ", pais='" + pais + '\'' +
                ", notaIMDB=" + notaIMDB +
                '}';
    }
}
