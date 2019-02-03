package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class FragmentOnline extends Fragment implements DohvatiKnjige.IDohvatiKnjigeDone, DohvatiNajnovije.IDohvatiNajnovijeDone, KnjigaResultReceiver.Receiver {

    ArrayList<String> kategorije;
    ArrayList<Knjiga> knjige;
    ArrayList<String> knjigeNazivi = new ArrayList<String>();
    Spinner sRezultat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.online_fragment, container, false);

        final Button dPretraga = v.findViewById(R.id.dRun);
        final Button dDodajKnjigu = v.findViewById(R.id.dAdd);
        final Button dPovratak = v.findViewById(R.id.dPovratak);
        final EditText tekstUpit = v.findViewById(R.id.tekstUpit);
        final Spinner sKategorije = v.findViewById(R.id.sKategorije);
        sRezultat = v.findViewById(R.id.sRezultat);

        kategorije = getArguments().getStringArrayList("kategorije");
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, kategorije);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sKategorije.setAdapter(adapter);

        dPretraga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String unos = tekstUpit.getText().toString();
                if(unos.contains("autor:")) {
                    unos = unos.substring(6, unos.length());
                    new DohvatiNajnovije(FragmentOnline.this).execute(unos);
                }
                else if(unos.contains("korisnik:")) {
                    unos = unos.substring(9, unos.length());

                    Intent intent = new Intent(Intent.ACTION_SYNC, null, getActivity(), KnjigePoznanika.class);
                    KnjigaResultReceiver mReceiver = new KnjigaResultReceiver(new Handler());
                    mReceiver.setReceiver(FragmentOnline.this);
                    intent.putExtra("idKorisnika", unos);
                    intent.putExtra("receiver", mReceiver);
                    getActivity().startService(intent);
                }
                else {
                    String[] rijeci = unos.split(";");
                    new DohvatiKnjige(FragmentOnline.this).execute(rijeci);
                }
            }
        });

        dDodajKnjigu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Knjiga izabranaKnjiga = null;
                if(sRezultat.getSelectedItem() != null)
                    izabranaKnjiga = dajKnjiguSaNazivom(sRezultat.getSelectedItem().toString());
                else {
                    Toast.makeText(getContext(), R.string.toastNijeOdabranaKnjiga, Toast.LENGTH_SHORT).show();
                    return;
                }
                izabranaKnjiga.setKategorija(sKategorije.getSelectedItem().toString());
                //KategorijeAkt.knjige.add(izabranaKnjiga);
                BazaOpenHelper bazaOpenHelper = new BazaOpenHelper(getActivity());
                long id = bazaOpenHelper.dodajKnjigu(izabranaKnjiga);
                if(id != -1)
                    Toast.makeText(getContext(), R.string.toastKnjigaUpisana, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), R.string.toastKnjigaNijeUpisana, Toast.LENGTH_LONG).show();
            }
        });

        dPovratak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return v;
    }

    private Knjiga dajKnjiguSaNazivom(String naziv) {
        for(int i = 0; i < knjige.size(); i++) {
            if(knjige.get(i).getNaziv().equals(naziv))
                return knjige.get(i);
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDohvatiDone(ArrayList<Knjiga> knjige) {
        this.knjige = knjige;
        OsvjeziRezultate();
    }

    @Override
    public void onNajnovijeDone(ArrayList<Knjiga> knjige) {
        this.knjige = knjige;
        OsvjeziRezultate();
    }

    @Override
    public void onReceiveKnjiga(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case 0:

                break;
            case 1:
                knjige = resultData.getParcelableArrayList("listaKnjiga");
                OsvjeziRezultate();
                break;
            case 2:
                String error = "GRESKA!";
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void OsvjeziRezultate() {
        knjigeNazivi.clear();
        for (Knjiga k: knjige) {
            knjigeNazivi.add(k.getNaziv());
        }

        ArrayAdapter<String> ad;
        ad = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, knjigeNazivi);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sRezultat.setAdapter(ad);
    }
}
