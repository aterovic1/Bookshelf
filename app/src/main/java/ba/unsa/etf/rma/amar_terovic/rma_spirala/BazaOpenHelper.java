package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class BazaOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Biblioteka";
    public static final String KATEGORIJE_TABLE = "Kategorija";
    public static final String KNJIGE_TABLE = "Knjiga";
    public static final String AUTORI_TABLE = "Autor";
    public static final String AUTORSTVA_TABLE = "Autorstvo";
    public static final int DATABASE_VERSION = 1;

    private static final String KATEGORIJE_CREATE = "create table " +
            KATEGORIJE_TABLE + "(" +
            "_id integer primary key autoincrement, " +
            "naziv text unique not null);";

    private static final String KNJIGE_CREATE = "create table " +
            KNJIGE_TABLE + "(" +
            "_id integer primary key autoincrement, " +
            "naziv text unique not null, " +
            "opis text, " +
            "datumObjavljivanja text, " +
            "brojStranica integer, " +
            "idWebServis integer, " +
            "idkategorije integer, " +
            "slika text, " +
            "pregledana integer);";

    private static final String AUTORI_CREATE = "create table " +
            AUTORI_TABLE + "(" +
            "_id integer primary key autoincrement, " +
            "ime text unique not null);";

    private static final String AUTORSTVA_CREATE = "create table " +
            AUTORSTVA_TABLE + "(" +
            "_id integer primary key autoincrement, " +
            "idautora integer, " +
            "idknjige integer);";

    public BazaOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BazaOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(KATEGORIJE_CREATE);
        db.execSQL(KNJIGE_CREATE);
        db.execSQL(AUTORI_CREATE);
        db.execSQL(AUTORSTVA_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + KATEGORIJE_TABLE);
        db.execSQL("drop table if exists " + KNJIGE_TABLE);
        db.execSQL("drop table if exists " + AUTORI_TABLE);
        db.execSQL("drop table if exists " + AUTORSTVA_TABLE);

        onCreate(db);
    }

    public void Apgrejd() {
        onUpgrade(this.getReadableDatabase(), 1, 2);
    }

    public long dodajKategoriju(String kategorija) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues novaKategorija = new ContentValues();
        novaKategorija.put("naziv", kategorija);
        return db.insert(KATEGORIJE_TABLE, null, novaKategorija);
    }

    public long _dodajKategoriju(String kategorija) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor kategorijeCursor = db.rawQuery("select naziv from " + KATEGORIJE_TABLE + ";", null);
        while (kategorijeCursor.moveToNext()) {
            if(kategorijeCursor.getString(kategorijeCursor.getColumnIndex("naziv")).equals(kategorija))
                return -1; //kategorija vec postoji
        }
        kategorijeCursor.close();

        ContentValues novaKategorija = new ContentValues();
        novaKategorija.put("naziv", kategorija);
        db.insert(KATEGORIJE_TABLE, null, novaKategorija);
        //vrati id nove kategorije
        Cursor idNoveKat = db.rawQuery("select _id from " + KATEGORIJE_TABLE +
        " where naziv = '" + kategorija + "';", null);
        idNoveKat.moveToFirst();
        return idNoveKat.getLong(idNoveKat.getColumnIndex("_id"));
    }

    public long dodajKnjigu(Knjiga knjiga) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues novaKnjiga = new ContentValues();
        novaKnjiga.put("naziv", knjiga.getNaziv());
        novaKnjiga.put("opis", knjiga.getOpis());
        novaKnjiga.put("datumObjavljivanja", knjiga.getDatumObjavljivanja());
        novaKnjiga.put("brojStranica", knjiga.getBrojStranica());
        novaKnjiga.put("idWebServis", knjiga.getId());
        novaKnjiga.put("idkategorije", dajIDKategorije(knjiga.getKategorija()));
        novaKnjiga.put("slika", knjiga.getSlika().toString());
        novaKnjiga.put("pregledana", 0);
        long novaKnjigaID = db.insert(KNJIGE_TABLE, null, novaKnjiga);

        boolean imaAutor;
        for(int i = 0; i < knjiga.getAutori().size(); i++) {
            imaAutor = false;
            Cursor autoriCursor = db.rawQuery("select ime from " + AUTORI_TABLE + ";", null);
            while (autoriCursor.moveToNext()) {
                if(autoriCursor.getString(autoriCursor.getColumnIndex("ime")).equals
                        (knjiga.getAutori().get(i).getImeiPrezime())) {
                    imaAutor = true;
                }
            }
            autoriCursor.close();
            long autorID;
            if (!imaAutor) {
                ContentValues noviAutor = new ContentValues();
                noviAutor.put("ime", knjiga.getAutori().get(i).getImeiPrezime());
                autorID = db.insert(AUTORI_TABLE, null, noviAutor);
            }
            else {
                autorID = dajIDAutora(knjiga.getAutori().get(i).getImeiPrezime());
            }
            ContentValues novoAutorstvo = new ContentValues();
            novoAutorstvo.put("idautora", autorID);
            novoAutorstvo.put("idknjige", novaKnjigaID);
            db.insert(AUTORSTVA_TABLE, null, novoAutorstvo);
        }
        //vrati id nove knjige
        return novaKnjigaID;
    }

    public long _dodajKnjigu(Knjiga knjiga) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor knjigeCursor = db.rawQuery("select naziv from " + KNJIGE_TABLE + ";", null);
        while (knjigeCursor.moveToNext()) {
            if(knjigeCursor.getString(knjigeCursor.getColumnIndex("naziv")).equals(knjiga.getNaziv()))
                return -1; //knjiga vec postoji
        }
        knjigeCursor.close();

        ContentValues novaKnjiga = new ContentValues();
        novaKnjiga.put("naziv", knjiga.getNaziv());
        novaKnjiga.put("opis", knjiga.getOpis());
        novaKnjiga.put("datumObjavljivanja", knjiga.getDatumObjavljivanja());
        novaKnjiga.put("brojStranica", knjiga.getBrojStranica());
        novaKnjiga.put("idWebServis", knjiga.getId());
        novaKnjiga.put("idkategorije", dajIDKategorije(knjiga.getKategorija()));
        novaKnjiga.put("slika", knjiga.getSlika().toString());
        novaKnjiga.put("pregledana", 0);
        db.insert(KNJIGE_TABLE, null, novaKnjiga);

        //daj id knjige koja se ubacuje
        Cursor idNoveKnjige = db.rawQuery("select _id from " + KNJIGE_TABLE +
                " where naziv = '" + knjiga.getNaziv() + "';", null);
        idNoveKnjige.moveToFirst();
        long novaKnjigaID = idNoveKnjige.getLong(idNoveKnjige.getColumnIndex("_id"));
        idNoveKnjige.close();

        boolean imaAutor;
        for(int i = 0; i < knjiga.getAutori().size(); i++) {
            imaAutor = false;
            Cursor autoriCursor = db.rawQuery("select ime from " + AUTORI_TABLE + ";", null);
            while (autoriCursor.moveToNext()) {
                if(autoriCursor.getString(autoriCursor.getColumnIndex("ime")).equals
                        (knjiga.getAutori().get(i).getImeiPrezime())) {
                    imaAutor = true;
                }
            }
            autoriCursor.close();
            long autorID;
            if (!imaAutor) {
                ContentValues noviAutor = new ContentValues();
                noviAutor.put("ime", knjiga.getAutori().get(i).getImeiPrezime());
                db.insert(AUTORI_TABLE, null, noviAutor);
                autorID = dajIDAutora(knjiga.getAutori().get(i).getImeiPrezime());
            }
            else {
                autorID = dajIDAutora(knjiga.getAutori().get(i).getImeiPrezime());
            }
            ContentValues novoAutorstvo = new ContentValues();
            novoAutorstvo.put("idautora", autorID);
            novoAutorstvo.put("idknjige", novaKnjigaID);
            db.insert(AUTORSTVA_TABLE, null, novoAutorstvo);
        }
        //vrati id nove knjige
        return novaKnjigaID;
    }

    ArrayList<Knjiga> knjigeKategorije(long idKategorije) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] kolone = {"_id", "naziv", "opis", "datumObjavljivanja", "brojStranica", "idWebServis",
                "idkategorije", "slika", "pregledana"};
        Cursor knjigeCursor = db.query(KNJIGE_TABLE, kolone, "idkategorije = " + idKategorije,
                null, null, null, null);

        ArrayList<Knjiga> knjige = new ArrayList<>();

        while (knjigeCursor.moveToNext()) {
            long id = knjigeCursor.getLong(knjigeCursor.getColumnIndex("_id"));
            String naziv = knjigeCursor.getString(knjigeCursor.getColumnIndex("naziv"));
            String opis = knjigeCursor.getString(knjigeCursor.getColumnIndex("opis"));
            String datum = knjigeCursor.getString(knjigeCursor.getColumnIndex("datumObjavljivanja"));
            int brojStranica = knjigeCursor.getInt(knjigeCursor.getColumnIndex("brojStranica"));
            long idWeb = knjigeCursor.getLong(knjigeCursor.getColumnIndex("idWebServis"));
            long idkategorije = knjigeCursor.getLong(knjigeCursor.getColumnIndex("idkategorije"));
            String slika = knjigeCursor.getString(knjigeCursor.getColumnIndex("slika"));
            int pregledana = knjigeCursor.getInt(knjigeCursor.getColumnIndex("pregledana"));

            Cursor autorCursor = db.rawQuery("select * from " + AUTORI_TABLE +
                    " where _id in " + "(select idautora from " + AUTORSTVA_TABLE +
                    " where idknjige = " + id + ")", null);

            ArrayList<Autor> autori = new ArrayList<>();

            while (autorCursor.moveToNext()) {
                long idAutor = autorCursor.getLong(autorCursor.getColumnIndex("_id"));
                String ime = autorCursor.getString(autorCursor.getColumnIndex("ime"));
                Autor noviAutor = new Autor(ime, String.valueOf(idWeb));
                autori.add(noviAutor);
            }
            autorCursor.close();

            URL urlSlika = null;
            try {
                urlSlika = new URL(slika);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            boolean obojena = (pregledana == 1 ? true : false);
            Knjiga novaKnjiga = new Knjiga(String.valueOf(idWeb), naziv, autori, opis, datum, urlSlika, brojStranica);
            knjige.add(novaKnjiga);
        }
        knjigeCursor.close();

        return knjige;
    }

    ArrayList<Knjiga> knjigeAutora(long idAutora) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor knjigeCursor = db.rawQuery("select * from " + KNJIGE_TABLE +
            " where _id in " + "(select idknjige from " + AUTORSTVA_TABLE +
            " where idautora = " + idAutora + ");", null);

        ArrayList<Knjiga> knjige = new ArrayList<>();

        while (knjigeCursor.moveToNext()) {
            long id = knjigeCursor.getLong(knjigeCursor.getColumnIndex("_id"));
            String naziv = knjigeCursor.getString(knjigeCursor.getColumnIndex("naziv"));
            String opis = knjigeCursor.getString(knjigeCursor.getColumnIndex("opis"));
            String datum = knjigeCursor.getString(knjigeCursor.getColumnIndex("datumObjavljivanja"));
            int brojStranica = knjigeCursor.getInt(knjigeCursor.getColumnIndex("brojStranica"));
            long idWeb = knjigeCursor.getLong(knjigeCursor.getColumnIndex("idWebServis"));
            long idkategorije = knjigeCursor.getLong(knjigeCursor.getColumnIndex("idkategorije"));
            String slika = knjigeCursor.getString(knjigeCursor.getColumnIndex("slika"));
            int pregledana = knjigeCursor.getInt(knjigeCursor.getColumnIndex("pregledana"));

            Cursor autorCursor = db.rawQuery("select * from " + AUTORI_TABLE +
                    " where _id in " + "(select idautora from " + AUTORSTVA_TABLE +
                    " where idknjige = " + id + ");", null);

            ArrayList<Autor> autori = new ArrayList<>();

            while (autorCursor.moveToNext()) {
                long idAutor = autorCursor.getLong(autorCursor.getColumnIndex("_id"));
                String ime = autorCursor.getString(autorCursor.getColumnIndex("ime"));
                Autor noviAutor = new Autor(ime, String.valueOf(idWeb));
                autori.add(noviAutor);
            }
            autorCursor.close();

            URL urlSlika = null;
            try {
                urlSlika = new URL(slika);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            boolean obojena = (pregledana == 1 ? true : false);
            Knjiga novaKnjiga = new Knjiga(String.valueOf(idWeb), naziv, autori, opis, datum, urlSlika, brojStranica);
            knjige.add(novaKnjiga);
        }
        knjigeCursor.close();

        return knjige;
    }

    public long dajIDAutora(String autor) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"_id"};
        String[] args = { autor };
        Cursor autoriCursor = db.query(AUTORI_TABLE, columns, "ime = ?", args, null, null, null);
        /*Cursor autoriCursor = db.rawQuery("select _id from " + AUTORI_TABLE +
        " where ime = " + "'" + autor + "';", null);*/
        autoriCursor.moveToFirst();
        return autoriCursor.getLong(autoriCursor.getColumnIndex("_id"));
    }

    public long dajIDKategorije(String kategorija) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"_id"};
        String[] args = { kategorija };
        Cursor kategorijeCursor = db.query(KATEGORIJE_TABLE, columns, "naziv = ?", args, null, null, null);
        /*Cursor kategorijeCursor = db.rawQuery("select _id from " + KATEGORIJE_TABLE +
                " where naziv = " + "'"  + kategorija + "';", null);*/
        kategorijeCursor.moveToFirst();
        return kategorijeCursor.getLong(kategorijeCursor.getColumnIndex("_id"));
    }

    public ArrayList<String> dajKategorije() {
        ArrayList<String> kategorije = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"naziv"};
        Cursor kategorijeCursor = db.query(KATEGORIJE_TABLE, columns, null, null, null, null, null);
        //Cursor kategorijeCursor = db.rawQuery("select naziv from " + KATEGORIJE_TABLE + ";", null);
        while (kategorijeCursor.moveToNext()) {
            kategorije.add(kategorijeCursor.getString(kategorijeCursor.getColumnIndex("naziv")));
        }
        kategorijeCursor.close();
        return kategorije;
    }

    public ArrayList<String> dajAutore() {
        ArrayList<String> autori = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"ime"};
        Cursor autoriCursor = db.query(AUTORI_TABLE, columns, null, null, null, null, null);
        //Cursor kategorijeCursor = db.rawQuery("select ime from " + AUTORI_TABLE + ";", null);
        while (autoriCursor.moveToNext()) {
            autori.add(autoriCursor.getString(autoriCursor.getColumnIndex("ime")));
        }
        autoriCursor.close();
        return autori;
    }

    public ArrayList<String> dajAutoreSaBrojemKnjiga() {
        ArrayList<String> autoriBrojKnjiga = new ArrayList<>();
        ArrayList<String> autori = dajAutore();
        for(int i = 0; i < autori.size(); i++) {
            long idAutora = dajIDAutora(autori.get(i));
            ArrayList<Knjiga> knjigeAutora = knjigeAutora(idAutora);
            autoriBrojKnjiga.add(autori.get(i) + "\n" + knjigeAutora.size());
        }
        return autoriBrojKnjiga;
    }

    public void obojiKnjigu(Knjiga knjiga) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("pregledana", 1);
        db.update(KNJIGE_TABLE, cv, "naziv = ?", new String[] { knjiga.getNaziv() });
        /*db.execSQL("update " + KNJIGE_TABLE + "\n" +
        "set pregledana = 1" + "\n" +
        "where naziv = '" + knjiga.getNaziv() + "';");*/
    }

    public boolean daLiJeObojena(Knjiga knjiga) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor knjigaCursor = db.query(KNJIGE_TABLE, new String[]{ "pregledana" }, "naziv = ?",
                new String[] { knjiga.getNaziv() }, null, null, null);
        /*Cursor knjigaCursor = db.rawQuery("select pregledana from " + KNJIGE_TABLE +
            " where naziv = '" + knjiga.getNaziv() + "';", null);*/
        knjigaCursor.moveToFirst();
        int obojena = knjigaCursor.getInt(knjigaCursor.getColumnIndex("pregledana"));
        return (obojena == 1 ? true : false);
    }

}
