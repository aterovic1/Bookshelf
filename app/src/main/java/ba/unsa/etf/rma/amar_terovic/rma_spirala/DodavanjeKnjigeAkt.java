package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DodavanjeKnjigeAkt extends AppCompatActivity {

    private static int LOAD_IMAGE = 1;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodavanje_knjige_akt);

        Bundle b = getIntent().getExtras();
        final ArrayList<String> kategorije = b.getStringArrayList("kategorije");

        Spinner sKategorije = findViewById(R.id.sKategorijaKnjige);

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, kategorije);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sKategorije.setAdapter(adapter);

        Button dPonisti = findViewById(R.id.dPonisti);
        dPonisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button dNadjiSliku = findViewById(R.id.dNadjiSliku);
        dNadjiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent();
                myIntent.setAction(Intent.ACTION_GET_CONTENT);
                myIntent.setType("image/*");
                startActivityForResult(myIntent, LOAD_IMAGE);
            }
        });

        Button dUpisiKnjigu = findViewById(R.id.dUpisiKnjigu);
        dUpisiKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText autor = findViewById(R.id.imeAutora);
                EditText naziv = findViewById(R.id.nazivKnjige);
                Spinner kategorija = findViewById(R.id.sKategorijaKnjige);
                Knjiga knjiga = new Knjiga(naziv.getText().toString(), autor.getText().toString(), kategorija.getSelectedItem().toString());
                KategorijeAkt.knjige.add(knjiga);
                try {
                    saveImage(intent, naziv.getText().toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Toast.makeText(DodavanjeKnjigeAkt.this, "Kniga uspješno upisana", Toast.LENGTH_SHORT).show();

                ImageView slika = findViewById(R.id.naslovnaStr);
                autor.setText("");
                naziv.setText("");
                slika.setImageURI(null);
            }
        });

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            ImageView naslovna = findViewById(R.id.naslovnaStr);
            naslovna.setImageURI(pickedImage);
            intent = data;
        }
    }

    private void saveImage(Intent data, String naziv) throws FileNotFoundException {
        FileOutputStream outputStream;
        outputStream = openFileOutput(naziv, Context.MODE_PRIVATE);
        try {
            getBitmapFromUri(data.getData()).compress(Bitmap.CompressFormat.JPEG,90,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
