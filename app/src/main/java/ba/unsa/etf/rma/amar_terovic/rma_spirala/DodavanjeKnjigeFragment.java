package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;

public class DodavanjeKnjigeFragment extends Fragment {

    private static int LOAD_IMAGE = 1;
    Intent intent;
    ArrayList<String> kategorije;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.dodavanje_knjige_fragment, container, false);

        final Button dPonisti = v.findViewById(R.id.dPonisti);
        final Spinner sKategorije = v.findViewById(R.id.sKategorijaKnjige);
        final Button dNadjiSliku = v.findViewById(R.id.dNadjiSliku);
        final Button dUpisiKnjigu = v.findViewById(R.id.dUpisiKnjigu);
        final EditText autor = v.findViewById(R.id.imeAutora);
        final EditText naziv = v.findViewById(R.id.nazivKnjige);
        final ImageView slika = v.findViewById(R.id.naslovnaStr);

        kategorije = getArguments().getStringArrayList("kategorije");

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, kategorije);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sKategorije.setAdapter(adapter);

        dPonisti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        dNadjiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent();
                myIntent.setAction(Intent.ACTION_GET_CONTENT);
                myIntent.setType("image/*");
                startActivityForResult(myIntent, LOAD_IMAGE);
            }
        });

        dUpisiKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Knjiga knjiga = new Knjiga(naziv.getText().toString(), autor.getText().toString(), sKategorije.getSelectedItem().toString());
                KategorijeAkt.knjige.add(knjiga);

                try {
                    saveImage(intent, naziv.getText().toString());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(), R.string.toastKnjigaUpisana, Toast.LENGTH_SHORT).show();
                autor.setText("");
                naziv.setText("");
                slika.setImageURI(null);
            }
        });


        return v;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            ImageView naslovna = getView().findViewById(R.id.naslovnaStr);
            naslovna.setImageURI(pickedImage);
            intent = data;
        }
    }

    private void saveImage(Intent data, String naziv) throws FileNotFoundException {
        FileOutputStream outputStream;
        outputStream = getContext().openFileOutput(naziv, Context.MODE_PRIVATE);
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
