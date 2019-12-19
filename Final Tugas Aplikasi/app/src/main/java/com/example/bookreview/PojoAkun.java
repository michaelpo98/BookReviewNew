package com.example.bookreview;

public class PojoAkun{

    private String username;
    private String password;
    private String password1;
    private String nama;

    public PojoAkun(String username, String password, String password1, String nama) {
        this.username = username;
        this.password = password;
        this.password1 = password1;
        this.nama = nama;
    }

    public PojoAkun(){

    }

    public void setUsername(String username){
        this.username=username;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public void setPassword1(String password1){
        this.password1=password1;
    }

    public void setNama (String nama){
        this.nama=nama;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getPassword1(){
        return password1;
    }

    public String getNama(){
        return nama;
    }
}
