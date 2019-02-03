package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class KnjigeFragment extends Fragment {

    private String autor;
    private String kategorija;
    ArrayList<Knjiga> knjigeAutorKategorija;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.knjige_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ListView knjige = getView().findViewById(R.id.listaKnjiga);

        knjigeAutorKategorija = new ArrayList<>();

        if(getArguments() != null && getArguments().containsKey("autor")) {
            BazaOpenHelper db = new BazaOpenHelper(getActivity());
            knjigeAutorKategorija = db.knjigeAutora(db.dajIDAutora(getArguments().getString("autor")));
            /*autor = getArguments().getString("autor");
            for (Knjiga k: KategorijeAkt.knjige) {
                ArrayList<Autor> authors = k.getAutori();
                for(int i = 0; i < authors.size(); i++) {
                    if (authors.get(i).getImeiPrezime().equalsIgnoreCase(autor))
                        knjigeAutorKategorija.add(k);
                }
            }*/
        }
        else if(getArguments() != null && getArguments().containsKey("kategorija")) {
            kategorija = getArguments().getString("kategorija");
            BazaOpenHelper db = new BazaOpenHelper(getActivity());
            knjigeAutorKategorija = db.knjigeKategorije(db.dajIDKategorije(kategorija));
            /*for (Knjiga k: KategorijeAkt.knjige) {
                if(k.getKategorija().equalsIgnoreCase(kategorija))
                    knjigeAutorKategorija.add(k);
            }*/
        }

        final KnjigaAdapter adapter;
        adapter = new KnjigaAdapter(getActivity(),R.layout.knjiga_element, knjigeAutorKategorija);
        knjige.setAdapter(adapter);

        Button dPovratak = getView().findViewById(R.id.dPovratak);
        dPovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        knjige.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //oboji u plavo 0xffaabbed
                Knjiga knjigaBojenje = knjigeAutorKategorija.get(position);
                //knjigaBojenje.setObojen(true);
                BazaOpenHelper db = new BazaOpenHelper(getActivity());
                db.obojiKnjigu(knjigaBojenje);
                view.setBackgroundColor(getResources().getColor(R.color.colorHighlighted));
            }
        });
    }
}
