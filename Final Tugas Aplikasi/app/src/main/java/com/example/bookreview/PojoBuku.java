package com.example.bookreview;

public class PojoBuku {

    private String isbn, judul_buku, nama_pengarang ,kategori_buku, nama, rating, ulasan;

    public PojoBuku(String nama, String rating, String ulasan) {
        this.nama = nama;
        this.rating = rating;
        this.ulasan = ulasan;
    }

    public PojoBuku(){

    }


    public void setIsbn(String isbn){
        this.isbn = isbn;
    }

    public void setJudul_buku(String judul_buku) {
        this.judul_buku = judul_buku;
    }

    public void setNama_pengarang(String nama_pengarang) {
        this.nama_pengarang = nama_pengarang;
    }

    public void setKategori_buku(String kategori_buku) {
        this.kategori_buku = kategori_buku;
    }

    public String getIsbn (){
        return isbn;
    }

    public String getJudul_buku (){
        return judul_buku;
    }

    public String getNama_pengarang (){
        return nama_pengarang;
    }

    public String getKategori_buku(){
        return kategori_buku;
    }

    public String getNama() {
        return nama;
    }

    public String getRating() {
        return rating;
    }

    public String getUlasan() {
        return ulasan;
    }
}
