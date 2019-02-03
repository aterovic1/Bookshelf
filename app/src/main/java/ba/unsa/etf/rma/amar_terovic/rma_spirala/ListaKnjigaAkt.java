package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ListaKnjigaAkt extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_knjiga_akt);

        String kategorija = getIntent().getStringExtra("kategorija");
        final ArrayList<Knjiga> knjigeKategorija = new ArrayList<>();
        for (Knjiga k: KategorijeAkt.knjige) {
            if(k.getKategorija().equals(kategorija))
                knjigeKategorija.add(k);
        }

        ListView listaKnjiga = findViewById(R.id.listaKnjiga);
        final KnjigaAdapter adapter = new KnjigaAdapter(this,R.layout.knjiga_element, knjigeKategorija);
        listaKnjiga.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Button dPovratak = findViewById(R.id.dPovratak);
        dPovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listaKnjiga.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //oboji u plavo 0xffaabbed
                Knjiga knjigaBojenje = knjigeKategorija.get(position);
                knjigaBojenje.setObojen(true);
                view.setBackgroundColor(0xffaabbed);
            }
        });
    }
}
