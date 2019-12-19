package com.example.bookreview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RatingdanUlasan extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_Judul, tv_Nama;
    private EditText et_Ulasan;
    private Spinner sp_Rating;
    private Button bt_Ulas;

    private String nama, judul, ulasan, rating;

    private ArrayList<String> Rating;

    private FirebaseDatabase fd;
    private DatabaseReference dRif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratingdan_ulasan);

        tv_Nama = findViewById(R.id.tv_nama_user);
        tv_Judul = findViewById(R.id.tv_JudulBuku);
        et_Ulasan = findViewById(R.id.et_Ulasan);
        sp_Rating = findViewById(R.id.spinner_rating);
        bt_Ulas = findViewById(R.id.bt_Submit);

        fd = FirebaseDatabase.getInstance();
        dRif = fd.getReference();

        Intent getintent = getIntent();
        nama = getintent.getStringExtra("getNama");
        judul = getintent.getStringExtra("JudulBuku");

        Rating = new ArrayList<String>();
        Rating.add("1");
        Rating.add("2");
        Rating.add("3");
        Rating.add("4");
        Rating.add("5");

        ArrayAdapter<String> AARating = new ArrayAdapter<String>(RatingdanUlasan.this, android.R.layout.simple_spinner_item , Rating);
        sp_Rating.setAdapter(AARating);

        tv_Nama.setText(nama);
        tv_Judul.setText(judul);

        bt_Ulas.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_Submit :
                KirimData();
                Intent intent = new Intent (RatingdanUlasan.this, UlasBuku.class);
                intent.putExtra("getNama", nama);
                intent.putExtra("JudulBuku", judul);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void KirimData(){
        ulasan = et_Ulasan.getText().toString().trim();
        rating = sp_Rating.getSelectedItem().toString();
        dRif.child("Buku").child(judul).child(nama).child("Nama").setValue(nama);
        dRif.child("Buku").child(judul).child(nama).child("Ulasan").setValue(ulasan);
        dRif.child("Buku").child(judul).child(nama).child("Rating").setValue(rating);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(RatingdanUlasan.this, UlasBuku.class);
        intent.putExtra("getNama", nama);
        intent.putExtra("JudulBuku", judul);
        startActivity(intent);
        finish();
    }

}
