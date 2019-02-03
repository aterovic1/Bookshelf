package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.os.AsyncTask;

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

public class DohvatiNajnovije extends AsyncTask<String, Integer, Void> {

    public interface IDohvatiNajnovijeDone {
        public void onNajnovijeDone(ArrayList<Knjiga> knjige);
    }

    ArrayList<Knjiga> knjige = new ArrayList<Knjiga>();
    private IDohvatiNajnovijeDone pozivatelj;
    public DohvatiNajnovije(IDohvatiNajnovijeDone pozivatelj) {
        this.pozivatelj = pozivatelj;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String query = null;
        try {
            query = URLEncoder.encode(strings[0], "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url1 = "https://www.googleapis.com/books/v1/volumes?q=inauthor:".toString() + query + "&orderBy=newest&maxResults=5".toString();
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String rezultat = convertStreamToString(in);
            JSONObject jo = new JSONObject(rezultat);
            JSONArray items = jo.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject knjiga = items.getJSONObject(i); //id, naziv, autori, opis, datumObj, slika, brStranica

                String id = null;
                if(knjiga.has("id"))
                    id = knjiga.getString("id");

                JSONObject volInfo = null;
                String naziv = "";
                ArrayList<Autor> autori = new ArrayList<Autor>();
                JSONArray aut = null;
                String opis = "";
                int brStranica = 0;
                String datum = "";
                JSONObject slike = null;
                URL slika = new URL("http://www.bsmc.net.au/wp-content/uploads/No-image-available.jpg");

                if(knjiga.has("volumeInfo")) {
                    volInfo = knjiga.getJSONObject("volumeInfo");
                    if(volInfo.has("title"))
                        naziv = volInfo.getString("title");
                    if(volInfo.has("authors")) {
                        aut = volInfo.getJSONArray("authors");
                        for (int j = 0; j < aut.length(); j++) {
                            Autor author = new Autor(aut.getString(j), id);
                            autori.add(author);
                        }
                    }
                    if(volInfo.has("description"))
                        opis = volInfo.getString("description");
                    if(volInfo.has("pageCount"))
                        brStranica = volInfo.getInt("pageCount");
                    if(volInfo.has("publishedDate"))
                        datum = volInfo.getString("publishedDate");
                    if(volInfo.has("imageLinks")) {
                        slike = volInfo.getJSONObject("imageLinks");
                        if(slike.has("thumbnail"))
                            slika = new URL(slike.getString("thumbnail"));
                    }
                }

                Knjiga k = new Knjiga(id, naziv, autori, opis, datum, slika, brStranica);
                knjige.add(k);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pozivatelj.onNajnovijeDone(knjige);
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
