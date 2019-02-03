package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Knjiga implements Parcelable {
    String id;
    String naziv;
    ArrayList<Autor> autori = new ArrayList<Autor>();
    String opis;
    String datumObjavljivanja;
    URL slika;
    int brojStranica;
    String kategorija;
    boolean obojen;

    public Knjiga(String id, String naziv, ArrayList<Autor> autori, String opis, String datumObjavljivanja, URL slika, int brojStranica) {
        this.id = id;
        this.naziv = naziv;
        this.autori = autori;
        this.opis = opis;
        this.datumObjavljivanja = datumObjavljivanja;
        this.slika = slika;
        this.brojStranica = brojStranica;
        this.kategorija = null;
        this.obojen = false;
    }

    public Knjiga(String id, String naziv, ArrayList<Autor> autori, String opis, String datumObjavljivanja, URL slika, int brojStranica, boolean obojen) {
        this.id = id;
        this.naziv = naziv;
        this.autori = autori;
        this.opis = opis;
        this.datumObjavljivanja = datumObjavljivanja;
        this.slika = slika;
        this.brojStranica = brojStranica;
        this.kategorija = null;
        this.obojen = obojen;
    }

    public Knjiga(String naziv, String autor, String kategorija) {
        this.id = null;
        this.naziv = naziv;
        this.autori.add(new Autor(autor, null));
        this.opis = null;
        this.datumObjavljivanja = null;
        this.slika = null;
        this.brojStranica = 0;
        this.kategorija = kategorija;
        this.obojen = false;
    }

    protected Knjiga(Parcel in) throws MalformedURLException {
        id = in.readString();
        naziv = in.readString();
        autori = in.readArrayList(Autor.class.getClassLoader());
        opis = in.readString();
        datumObjavljivanja = in.readString();
        slika = new URL(in.readString());
        brojStranica = in.readInt();
        kategorija = in.readString();
        obojen = Boolean.getBoolean(in.readString());
    }

    public static final Creator<Knjiga> CREATOR = new Creator<Knjiga>() {
        @Override
        public Knjiga createFromParcel(Parcel in) {
            try {
                return new Knjiga(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public Knjiga[] newArray(int size) {
            return new Knjiga[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Autor> getAutori() {
        return autori;
    }

    public void setAutori(ArrayList<Autor> autori) {
        this.autori = autori;
    }

    public String getAutor() { return autori.get(0).getImeiPrezime(); }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getDatumObjavljivanja() {
        return datumObjavljivanja;
    }

    public void setDatumObjavljivanja(String datumObjavljivanja) {
        this.datumObjavljivanja = datumObjavljivanja;
    }

    public URL getSlika() {
        return slika;
    }

    public void setSlika(URL slika) {
        this.slika = slika;
    }

    public int getBrojStranica() {
        return brojStranica;
    }

    public void setBrojStranica(int brojStranica) {
        this.brojStranica = brojStranica;
    }

    public String getKategorija() { return kategorija; }

    public void setKategorija(String kategorija) { this.kategorija = kategorija; }

    public boolean getObojen() { return obojen; }

    public void setObojen(boolean obojen) { this.obojen = obojen; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(naziv);
        dest.writeList(autori);
        dest.writeString(opis);
        dest.writeString(datumObjavljivanja);
        dest.writeString(slika.toString());
        dest.writeInt(brojStranica);
        dest.writeString(kategorija);
        dest.writeString(String.valueOf(obojen));
    }
}
