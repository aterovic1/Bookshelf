package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class KnjigePoznanika extends IntentService {

    public static int STATUS_START = 0;
    public static int STATUS_FINISH = 1;
    public static int STATUS_ERROR = 2;

    ArrayList<Knjiga> knjige = new ArrayList<Knjiga>();

    public KnjigePoznanika() {
        super(null);
    }

    public KnjigePoznanika(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String idKorisnika = intent.getStringExtra("idKorisnika");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        receiver.send(STATUS_START, Bundle.EMPTY);
        Bundle bundle = new Bundle();
        ArrayList<String> bookshelves_ID = new ArrayList<>();

        try {
            bookshelves_ID = dajBookshelfIDoveZaUsera(idKorisnika);
        } catch (IOException e) {
            e.printStackTrace();
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
        } catch (JSONException e) {
            e.printStackTrace();
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
        }

        //https://www.googleapis.com/books/v1/users/117029538926736556454/bookshelves/1001/volumes
        String query = null;
        try {
            query = URLEncoder.encode(idKorisnika, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            for (int k = 0; k < bookshelves_ID.size(); k++) {
                String url1 = "https://www.googleapis.com/books/v1/users/" + query + "/bookshelves/"
                        + bookshelves_ID.get(k) + "/volumes";
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String rezultat = convertStreamToString(in);
                JSONObject jo = new JSONObject(rezultat);
                JSONArray items = jo.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject knjiga = items.getJSONObject(i); //id, naziv, autori, opis, datumObj, slika, brStranica

                    String id = null;
                    if (knjiga.has("id"))
                        id = knjiga.getString("id");

                    JSONObject volInfo = null;
                    String naziv = "";
                    ArrayList<Autor> autori = new ArrayList<Autor>();
                    JSONArray aut = null;
                    String opis = "";
                    int brStranica = 0;
                    String datum = "";
                    JSONObject slike = null;
                    URL slika = new URL("https://vignette.wikia.nocookie.net/superfriends/images/a/a5/No_Photo_Available.jpg/revision/latest?cb=20090329133959");

                    if (knjiga.has("volumeInfo")) {
                        volInfo = knjiga.getJSONObject("volumeInfo");
                        if (volInfo.has("title"))
                            naziv = volInfo.getString("title");
                        if (volInfo.has("authors")) {
                            aut = volInfo.getJSONArray("authors");
                            for (int j = 0; j < aut.length(); j++) {
                                Autor author = new Autor(aut.getString(j), id);
                                autori.add(author);
                            }
                        }
                        if (volInfo.has("description"))
                            opis = volInfo.getString("description");
                        if (volInfo.has("pageCount"))
                            brStranica = volInfo.getInt("pageCount");
                        if (volInfo.has("publishedDate"))
                            datum = volInfo.getString("publishedDate");
                        if (volInfo.has("imageLinks")) {
                            slike = volInfo.getJSONObject("imageLinks");
                            if (slike.has("thumbnail"))
                                slika = new URL(slike.getString("thumbnail"));
                        }
                    }

                    Knjiga book = new Knjiga(id, naziv, autori, opis, datum, slika, brStranica);
                    knjige.add(book);
                }
            }

            bundle.putParcelableArrayList("listaKnjiga", knjige);
            receiver.send(STATUS_FINISH, bundle);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
        } catch (IOException e) {
            e.printStackTrace();
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
        } catch (JSONException e) {
            e.printStackTrace();
            receiver.send(STATUS_ERROR, Bundle.EMPTY);
        }
    }

    private ArrayList<String> dajBookshelfIDoveZaUsera(String idKorisnika) throws IOException, JSONException {
        ArrayList<String> bookshelfID = new ArrayList<>();
        String query = null;
        try {
            query = URLEncoder.encode(idKorisnika, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url1 = "https://www.googleapis.com/books/v1/users/".toString() + query + "/bookshelves".toString();
        URL url = new URL(url1);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        String rezultat = convertStreamToString(in);
        JSONObject jo = new JSONObject(rezultat);
        JSONArray items = jo.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject bookshelf = items.getJSONObject(i);
            bookshelfID.add(bookshelf.getString("id"));
        }
        return bookshelfID;
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        }
        catch (IOException e) {

        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {

            }
        }
        return sb.toString();
    }
}

