package com.example.bookreview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MenuUtama extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public Button btn_Kategori, btn_TambahBuku, btn_Cari;
    private AutoCompleteTextView auto_et_NamaBuku;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    private FirebaseDatabase fd;
    private DatabaseReference dRif;

    private String nama, cariBuku;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);

        auto_et_NamaBuku = findViewById(R.id.auto_et_NamaBuku);
        btn_Kategori = findViewById(R.id.btn_TambahKategoriM);
        btn_TambahBuku = findViewById(R.id.btn_TambahBukuM);
        btn_Cari = findViewById(R.id.bt_Cari);

        Intent iNama = getIntent();
        nama = iNama.getStringExtra("getNama");

        drawerLayout = findViewById(R.id.nav_barM);
        navigationView = findViewById(R.id.nav_menu);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MenuUtama.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View header =  navigationView.getHeaderView(0);
        TextView txtNama = header.findViewById(R.id.tv_nama_user);
        txtNama.setText(nama);

        fd = FirebaseDatabase.getInstance();
        dRif=fd.getReference();

        AutoFillBuku();


        if (!nama.equals("ADMIN")){
            btn_Kategori.setVisibility(View.INVISIBLE);
        }else{
            btn_Kategori.setVisibility(View.VISIBLE);
        }

        btn_Kategori.setOnClickListener(this);
        btn_TambahBuku.setOnClickListener(this);
        btn_Cari.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_TambahKategoriM :
                Intent ITambahKategori = new Intent(MenuUtama.this, Kategori.class);
                ITambahKategori.putExtra("JudulBuku",cariBuku);
                ITambahKategori.putExtra("getNama",nama);
                finish();
                startActivity(ITambahKategori);
                break;
            case R.id.btn_TambahBukuM :
                Intent ITambahBuku = new Intent(MenuUtama.this, Buku.class);
                ITambahBuku.putExtra("JudulBuku",cariBuku);
                ITambahBuku.putExtra("getNama",nama);
                finish();
                startActivity(ITambahBuku);
                break;
            case R.id.bt_Cari :
                cariBuku = auto_et_NamaBuku.getText().toString().trim();
                if (TextUtils.isEmpty(cariBuku)){
                    auto_et_NamaBuku.setError("Tidak Boleh Kosong");
                }else {
                    dRif.child("Buku").orderByChild("Judul").equalTo(cariBuku).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() == true) {
                                Intent intent = new Intent(MenuUtama.this, UlasBuku.class);
                                intent.putExtra("JudulBuku", cariBuku);
                                intent.putExtra("getNama", nama);
                                finish();
                                startActivity(intent);
                            } else if (dataSnapshot.exists() == false) {
                                Toast.makeText(MenuUtama.this, "Buku belum tedaftar", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                break;
        }
    }

    public void AutoFillBuku(){
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuUtama.this, R.layout.support_simple_spinner_dropdown_item);
        dRif.child("Buku").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot getData : dataSnapshot.getChildren()){
                    String suggest = getData.child("Judul").getValue(String.class);
                    adapter.add(suggest);
                }
                auto_et_NamaBuku.setThreshold(1);
                auto_et_NamaBuku.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int Item = menuItem.getItemId();
        switch (Item){
            case R.id.nav_signout :
                finish();
                break;
        }
        return false;
    }
}
