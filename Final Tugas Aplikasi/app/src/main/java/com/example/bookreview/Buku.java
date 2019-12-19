package com.example.bookreview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class Buku extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    private static final int GALLERY_REQUEST = 1888 ;
    private ImageView iv_Buku;
    private EditText et_Isbn, et_JudulBuku, et_NamaPengarang;
    private Spinner s_Kategori;
    private Button bt_Tambah;

    private NavigationView nv ;
    private DrawerLayout dl;
    private ActionBarDrawerToggle acdt;

    private FirebaseDatabase fd;
    private DatabaseReference dRef;

    private FirebaseStorage fs;
    private StorageReference sRef;

    private String nama;

    private Uri selectedImage;

    private long i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buku);

        iv_Buku = findViewById(R.id.iv_BukuB);

        et_Isbn = findViewById(R.id.et_ISBNBukuB);
        et_JudulBuku = findViewById(R.id.et_JudulBukuB);
        et_NamaPengarang = findViewById(R.id.et_NamaPengarangB);

        bt_Tambah = findViewById(R.id.bt_TambahB);

        s_Kategori = findViewById(R.id.s_Kategori);

        nv = findViewById(R.id.nav_bar);
        dl = findViewById(R.id.dl);
        acdt = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);
        dl.addDrawerListener(acdt);
        acdt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_Tambah.setOnClickListener(this);

        fd = FirebaseDatabase.getInstance();
        dRef = fd.getReference();

        fs = FirebaseStorage.getInstance();
        sRef = fs.getReference();

        KategoriBuku();

        Intent getIntent = getIntent();
        nama = getIntent.getStringExtra("getNama");

        View v = nv.getHeaderView(0);
        TextView ubah = v.findViewById(R.id.tv_nama_user);
        ubah.setText(nama);

        iv_Buku.setOnClickListener(this);
        bt_Tambah.setOnClickListener(this);
        nv.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_TambahB :
                Boolean isEmpty = false;
                final PojoBuku PB1 = new PojoBuku();
                PB1.setIsbn(et_Isbn.getText().toString().trim());
                PB1.setJudul_buku(et_JudulBuku.getText().toString().trim());
                PB1.setNama_pengarang(et_NamaPengarang.getText().toString().trim());
                PB1.setKategori_buku(s_Kategori.getSelectedItem().toString());
                if (TextUtils.isEmpty(PB1.getIsbn())){
                    isEmpty=true;
                    et_Isbn.setError("Field ini tidak boleh kosong");
                }
                if (TextUtils.isEmpty(PB1.getJudul_buku())){
                    isEmpty=true;
                    et_JudulBuku.setError("Field ini tidak boleh kosong");
                }
                if (TextUtils.isEmpty(PB1.getNama_pengarang())){
                    isEmpty = true;
                    et_Isbn.setError("Field ini tidak boleh kosong");
                }
                if (iv_Buku.getDrawable() == null){
                    isEmpty = true;
                }
                if (!isEmpty){
                    dRef.child("Buku").orderByChild("Judul").equalTo(PB1.getJudul_buku()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()==false)  {
                                dRef.child("Buku").orderByChild("ISBN").equalTo(PB1.getIsbn()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()==true){
                                            Toast.makeText(getApplicationContext(), "ISBN buku telah tedaftar", Toast.LENGTH_SHORT).show();
                                        }else {
                                            dRef.child("Buku").child(PB1.getJudul_buku()).child("ISBN").setValue(PB1.getIsbn());
                                            dRef.child("Buku").child(PB1.getJudul_buku()).child("Judul").setValue(PB1.getJudul_buku());
                                            dRef.child("Buku").child(PB1.getJudul_buku()).child("Kategori").setValue(PB1.getKategori_buku());
                                            dRef.child("Buku").child(PB1.getJudul_buku()).child("Pengarang").setValue(PB1.getNama_pengarang());
                                            uploadGambar();
                                            Toast.makeText(getApplicationContext(), "Berhasil memasukkan data buku", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Buku.this, MenuUtama.class);
                                            intent.putExtra("getNama", nama);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }else if (dataSnapshot.exists()==true) {
                                Toast.makeText(getApplicationContext(), "Judul buku telah tedaftar", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                break;
            case  R.id.iv_BukuB :
                MemasukkanGambar();
                break;
        }
    }

    private void KategoriBuku(){
        dRef.child("Kategori").orderByChild("namaKategori").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> sAKategori = new ArrayList <String>();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    String namaKategori = data.child("namaKategori").getValue(String.class);
                    sAKategori.add(namaKategori);
                }
                ArrayAdapter<String> AASKategori = new ArrayAdapter<String>(Buku.this,
                        android.R.layout.simple_spinner_item, sAKategori);
                s_Kategori.setAdapter(AASKategori);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void MemasukkanGambar() {
        Intent iGambar = new Intent(Intent.ACTION_PICK);
        iGambar.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        iGambar.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(iGambar, GALLERY_REQUEST);
    }

    private void uploadGambar(){
        if (selectedImage!=null){
            dRef.child("Buku").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        i = dataSnapshot.getChildrenCount();
                    }
                   final StorageReference ref = sRef.child("Images"+i);
                    ref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadURL = uri;
                                    dRef.child("Buku").child(et_JudulBuku.getText().toString().trim()).child("Gambar").setValue(String.valueOf(downloadURL));
                                }
                            });
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case GALLERY_REQUEST :
                    selectedImage = data.getData();
                    iv_Buku.setImageURI(selectedImage);
                    break;
            }
        }

    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Buku.this, MenuUtama.class);
        intent.putExtra("getNama", nama);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (acdt.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_signout:
                Intent intent = new Intent(Buku.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }
}
