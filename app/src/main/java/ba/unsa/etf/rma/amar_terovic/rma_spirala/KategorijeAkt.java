package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.app.PendingIntent.getActivity;

public class KategorijeAkt extends AppCompatActivity implements ListeFragment.onItemClick {

    Boolean siriLayout = false;
    public static ArrayList<Knjiga> knjige = new ArrayList<>();
    public static ArrayList<String> kategorije = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_kategorije_akt);

        kategorije = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.kategorije)));

        BazaOpenHelper db = new BazaOpenHelper(this);
        //db.Apgrejd();
        for(String k:kategorije) {
            db.dodajKategoriju(k);
        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.widget.FrameLayout knjigeLayout = (FrameLayout)findViewById(R.id.fragment2);

        //slucaj layouta za siroke ekrane
        if(knjigeLayout != null) {
            siriLayout = true;
            KnjigeFragment knjigeFragment;
            knjigeFragment = (KnjigeFragment)fragmentManager.findFragmentById(R.id.fragment2);
            if(knjigeFragment == null) {
                knjigeFragment = new KnjigeFragment();
                fragmentManager.beginTransaction().replace(R.id.fragment2, knjigeFragment).commit();
            }
        }

        //Dodjeljivanje fragmenta FragmentLista
        ListeFragment listeFragment = (ListeFragment)fragmentManager.findFragmentById(R.id.fragment1);
        if(listeFragment == null) {
            listeFragment = new ListeFragment();
            Bundle argumenti = new Bundle();
            argumenti.putStringArrayList("Kategorije", db.dajKategorije());
            listeFragment.setArguments(argumenti);
            fragmentManager.beginTransaction().replace(R.id.fragment1, listeFragment).commit();
        }
        else {
            fragmentManager.popBackStack(null, android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    public ArrayList<String> dajAutoreKnjiga() {
        BazaOpenHelper db = new BazaOpenHelper(getApplicationContext());
        return db.dajAutore();
    }

    public ArrayList<String> dajAutoreSaBrojemKnjiga() {
        BazaOpenHelper db = new BazaOpenHelper(getApplicationContext());
        return db.dajAutoreSaBrojemKnjiga();
    }

    public ArrayList<String> dajKategorije() {
        BazaOpenHelper db = new BazaOpenHelper(getApplicationContext());
        return db.dajKategorije();
    }

    @Override
    public void onItemClicked(int position, String type) {
        Bundle arguments = new Bundle();
        if(type.equalsIgnoreCase("kategorije")) {

            arguments.putString("kategorija", dajKategorije().get(position));
        }
        else {
            ArrayList<String> autori = dajAutoreKnjiga();
            arguments.putString("autor", autori.get(position));
        }
        KnjigeFragment knjigeFragment = new KnjigeFragment();
        knjigeFragment.setArguments(arguments);
        if(siriLayout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment2, knjigeFragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment1, knjigeFragment).addToBackStack(null).commit();
        }
    }
}
