package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class KnjigaAdapter extends ArrayAdapter<Knjiga> {
    int resource;
    static ArrayList<Knjiga> knjige;

    public KnjigaAdapter(Context context, int _resource, List<Knjiga> items) {
        super(context, _resource, items);
        resource = _resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LinearLayout newView;
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().
                    getSystemService(inflater);
            li.inflate(resource, newView, true);
        }
        else {
            newView = (LinearLayout)convertView;
        }

        final Knjiga knjiga = getItem(position);

        //naziv knjige
        TextView nazivKnjige = newView.findViewById(R.id.eNaziv);
        nazivKnjige.setText(knjiga.getNaziv());

        //ime autora
        TextView imeAutora = newView.findViewById(R.id.eAutor);
        ArrayList<Autor> authors = knjiga.getAutori();
        String autori = "";
        for(int i = 0; i < authors.size(); i++) {
            autori += authors.get(i).getImeiPrezime();
            if(i != authors.size() - 1)
                autori += ", ";
        }
        imeAutora.setText(autori);

        //datum objavljivanja
        TextView datumObjavljivanja = newView.findViewById(R.id.eDatumObjavljivanja);
        if(knjiga.getDatumObjavljivanja() != null)
            datumObjavljivanja.setText(knjiga.getDatumObjavljivanja());

        //opis
        TextView opis = newView.findViewById(R.id.eOpis);
        if(knjiga.getOpis() != null)
            opis.setText(knjiga.getOpis());

        //broj stranica
        TextView brojStranica = newView.findViewById(R.id.eBrojStranica);
        if(knjiga.getBrojStranica() != 0)
            brojStranica.setText(String.valueOf(knjiga.getBrojStranica()));

        //bojenje pozadine
        BazaOpenHelper db = new BazaOpenHelper(getContext());
        if(db.daLiJeObojena(knjiga)) {
            newView.setBackgroundColor(newView.getResources().getColor(R.color.colorHighlighted));
        }
        /*if(knjiga.getObojen() == true) {
            newView.setBackgroundColor(newView.getResources().getColor(R.color.colorHighlighted));
        }*/

        //naslovna slika
        ImageView naslovnaStr = newView.findViewById(R.id.eNaslovna);

        if(knjiga.getSlika() != null) {
            Picasso.get().load(knjiga.getSlika().toString()).into(naslovnaStr);
        }
        else {
            try {
                naslovnaStr.setImageBitmap(BitmapFactory.decodeStream(getContext().openFileInput(nazivKnjige.getText().toString())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                //POSTAVI HARDKODIRANU SLIKU
                naslovnaStr.setImageResource(R.drawable.knjigaslika);
                //naslovnaStr.setImageDrawable(getContext().getResources().getDrawable(R.drawable.knjigaslika));
            }
        }

        //preporuka
        Button preporuci = newView.findViewById(R.id.dPreporuci);
        preporuci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KategorijeAkt myActivity = (KategorijeAkt) getContext();
                FragmentManager manager = myActivity.getSupportFragmentManager();
                FragmentPreporuci fragmentPreporuci = new FragmentPreporuci();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle argumenti = new Bundle();
                argumenti.putString("naziv", knjiga.getNaziv());
                if(knjiga.getAutori() != null)
                    argumenti.putString("autor", knjiga.getAutori().get(0).getImeiPrezime());
                fragmentPreporuci.setArguments(argumenti);
                transaction.replace(R.id.fragment1, fragmentPreporuci);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return newView;
    }
}
