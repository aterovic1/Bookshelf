package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class ListeFragment extends Fragment {

    ArrayList<String> kategorije;
    ArrayList<String> autori;
    Boolean kat;

    public interface onItemClick {
        public void onItemClicked(int position, String type);
    }
    private onItemClick click;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.liste_fragment, container, false);
        return v;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        kat = true;

        BazaOpenHelper db = new BazaOpenHelper(getActivity());
        kategorije = db.dajKategorije();
        autori = db.dajAutoreSaBrojemKnjiga();

        final ListView listaKategorija = v.findViewById(R.id.listaKategorija);
        final Button dDodajKnjigu = v.findViewById(R.id.dDodajKnjigu);
        final EditText tekstPretraga = v.findViewById(R.id.tekstPretraga);
        final Button dDodajKategoriju = v.findViewById(R.id.dDodajKategoriju);
        final Button dPretraga = v.findViewById(R.id.dPretraga);
        final Button dKategorije = v.findViewById(R.id.dKategorije);
        final Button dAutori = v.findViewById(R.id.dAutori);
        final Button dDodajOnline = v.findViewById(R.id.dDodajOnline);

        if(getArguments() != null && getArguments().containsKey("Kategorije")) {
            //kategorije = getArguments().getStringArrayList("Kategorije");
            try {
                click = (onItemClick) getActivity();
            } catch (ClassCastException e) {
                throw new ClassCastException(getActivity().toString() + "Treba implementirati onItemClick");
            }

            listaKategorija.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (kat)
                        click.onItemClicked(i, "kategorije");
                    else
                        click.onItemClicked(i, "autori");
                }
            });
        }

        final ArrayAdapter<String> adapterKat;
        adapterKat = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, kategorije);

        //autori = KategorijeAkt.dajAutoreSaBrojemKnjiga();

        final ArrayAdapter<String> adapterAutor;
        adapterAutor = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, autori);

        dKategorije.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaKategorija.setAdapter(adapterKat);
                kat = true;
                dPretraga.setVisibility(View.VISIBLE);
                dDodajKategoriju.setVisibility(View.VISIBLE);
                tekstPretraga.setVisibility(View.VISIBLE);
            }
        });

        dAutori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //adapterAutor.notifyDataSetChanged();
                listaKategorija.setAdapter(adapterAutor);
                kat = false;
                dPretraga.setVisibility(View.GONE);
                dDodajKategoriju.setVisibility(View.GONE);
                tekstPretraga.setVisibility(View.GONE);
            }
        });

        dDodajKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DodavanjeKnjigeFragment dodavanjeKnjigeFragment = new DodavanjeKnjigeFragment();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle argumenti = new Bundle();
                argumenti.putStringArrayList("kategorije", kategorije);
                dodavanjeKnjigeFragment.setArguments(argumenti);
                transaction.replace(R.id.fragment1, dodavanjeKnjigeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        dDodajKategoriju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //KategorijeAkt.kategorije.add(tekstPretraga.getText().toString());

                BazaOpenHelper db = new BazaOpenHelper(getActivity());
                db.dodajKategoriju(tekstPretraga.getText().toString());

                kategorije = db.dajKategorije();

                adapterKat.clear();

                adapterKat.addAll(kategorije);
                listaKategorija.setAdapter(adapterKat);
                adapterKat.notifyDataSetChanged();

                adapterKat.getFilter().filter("");

                Toast.makeText(getContext(), "Kategorija dodana", Toast.LENGTH_SHORT).show();
                tekstPretraga.setText("");
                dDodajKategoriju.setEnabled(false);
            }
        });

        dPretraga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterKat.getFilter().filter(tekstPretraga.getText().toString());
                boolean postojiKategorija = false;
                for (String k: kategorije) {
                    if(k.toLowerCase().startsWith(tekstPretraga.getText().toString())) {
                        postojiKategorija = true;
                        break;
                    }
                }
                if(!postojiKategorija) {
                    Toast.makeText(getContext(), R.string.toastNemaRezultata, Toast.LENGTH_SHORT).show();
                    dDodajKategoriju.setEnabled(true);
                }
            }
        });

        dDodajOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                FragmentOnline fragmentOnline = new FragmentOnline();
                FragmentTransaction transaction = manager.beginTransaction();
                Bundle argumenti = new Bundle();
                argumenti.putStringArrayList("kategorije", kategorije);
                fragmentOnline.setArguments(argumenti);
                transaction.replace(R.id.fragment1, fragmentOnline);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

}
