package com.example.bookreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UlasBuku extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private Button bt_Ulas;

    private FirebaseDatabase fd;
    private DatabaseReference dRif;

    private RecyclerView lv_Comment;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager rLayoutManager;

    private ImageView iv_Buku;
    private TextView tv_ISBN, tv_Judul, tv_Pengarang, tv_Kategori, tv_total_rating;

    private String nama, judul;
    private String nama_user, rating, ulasan;

    private String totalrating;
    private double hitungtotalrating = 0;

    int i = 0;

    //PojoBuku PB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ulas_buku);

        iv_Buku = findViewById(R.id.img_Buku);

        bt_Ulas = findViewById(R.id.bt_isiUlasan);

        tv_ISBN = findViewById(R.id.tv_isbn_buku);
        tv_Judul = findViewById(R.id.tv_nama_buku);
        tv_Pengarang = findViewById(R.id.tv_pengarang_buku);
        tv_Kategori = findViewById(R.id.tv_kategori_buku);
        tv_total_rating = findViewById(R.id.tv_total_rating_buku);

        lv_Comment = findViewById(R.id.lv_buku);
        lv_Comment.setHasFixedSize(true);
        rLayoutManager = new LinearLayoutManager(this);
        lv_Comment.setLayoutManager(rLayoutManager);

        fd = FirebaseDatabase.getInstance();
        dRif = fd.getReference();

        drawerLayout = findViewById(R.id.nav_bar_UB);
        navigationView = findViewById(R.id.nav_bar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(UlasBuku.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent Inama = getIntent();
        nama = Inama.getStringExtra("getNama");
        judul = Inama.getStringExtra("JudulBuku");

        dRif.child("Buku").orderByChild("Judul").equalTo(judul).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*for (DataSnapshot data : dataSnapshot.getChildren()) {
                    tv_ISBN.setText(data.child("ISBN").getValue().toString());
                    tv_Judul.setText(data.child("Judul").getValue().toString());
                    tv_Pengarang.setText(data.child("Pengarang").getValue().toString());
                    tv_Kategori.setText(data.child("Kategori").getValue().toString());
                    String Download_img = data.child("Gambar").getValue(String.class);
                    Uri img = Uri.parse(Download_img);
                    Glide.with(UlasBuku.this).load(img).into(iv_Buku);
                }*/

                if(dataSnapshot.exists()){
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        tv_ISBN.setText(data.child("ISBN").getValue().toString());
                        tv_Judul.setText(data.child("Judul").getValue().toString());
                        tv_Pengarang.setText(data.child("Pengarang").getValue().toString());
                        tv_Kategori.setText(data.child("Kategori").getValue().toString());
                        String Download_img = data.child("Gambar").getValue(String.class);
                        Uri img = Uri.parse(Download_img);
                        Glide.with(UlasBuku.this).load(img).into(iv_Buku);}
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        View vNama = navigationView.getHeaderView(0);
        TextView tvNama = vNama.findViewById(R.id.tv_nama_user);
        tvNama.setText(nama);

        TampilBuku();
        totalRating();

        bt_Ulas.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int item = menuItem.getItemId();
        switch (item) {
            case R.id.nav_signout:
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_isiUlasan:
                Intent intent = new Intent(UlasBuku.this, RatingdanUlasan.class);
                intent.putExtra("getNama", nama);
                intent.putExtra("JudulBuku", judul);
                startActivity(intent);
                finish();
                break;
        }
    }

   public void TampilBuku() {
        dRif.child("Buku").child(judul).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<PojoBuku> A_pu = new ArrayList<PojoBuku>();
                rAdapter = new Adapter(A_pu);
                for (DataSnapshot data : dataSnapshot.getChildren()){
                   nama_user = data.child("Nama").getValue(String.class);
                   rating = data.child("Rating").getValue(String.class);
                   ulasan = data.child("Ulasan").getValue(String.class);
                   if (nama_user!=null) {
                       PojoBuku pb = new PojoBuku(nama_user, rating, ulasan);
                       A_pu.add(pb);
                   }
                }
                lv_Comment.setAdapter(rAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void totalRating (){
        dRif.child("Buku").child(judul).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    totalrating = data.child("Rating").getValue(String.class);
                    if (data.child("Rating").exists() == true){
                        hitungtotalrating = Double.parseDouble(totalrating)  + hitungtotalrating;
                        i++;
                    }
                }
                if (hitungtotalrating>0) {
                    tv_total_rating.setText(String.valueOf(hitungtotalrating / i));
                } else if (hitungtotalrating <= 0){
                    tv_total_rating.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(UlasBuku.this, MenuUtama.class);
        intent.putExtra("getNama", nama);
        intent.putExtra("JudulBuku", judul);
        startActivity(intent);
        finish();
    }

}

