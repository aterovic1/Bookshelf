package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FragmentPreporuci extends Fragment {

    String nazivKnjige;
    String autor;
    Map<String, String> kontakti = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.preporuci_fragment, container, false);

        final Button dPosalji = v.findViewById(R.id.dPosalji);
        final Spinner sKontakti = v.findViewById(R.id.sKontakti);
        final TextView tNazivAutor = v.findViewById(R.id.tNazivAutor);

        nazivKnjige = getArguments().getString("naziv");
        if(getArguments().getString("autor") != null)
            autor = getArguments().getString("autor");
        else autor = null;

        tNazivAutor.setText(nazivKnjige + ", " + autor);

        //dohvatiKontakte();
        getContacts();

        //imena kontakata za adapter
        ArrayList<String> kontaktiImena = new ArrayList<>();
        for(Object o: kontakti.keySet()) {
            kontaktiImena.add(o.toString());
        }

        //Sortiranje kontakata
        Collections.sort(kontaktiImena);

        //adapter za spinner sKontakti
        ArrayAdapter<String> kont;
        kont = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, kontaktiImena);
        kont.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sKontakti.setAdapter(kont);

        dPosalji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //posaljiEmail(sKontakti.getSelectedItem().toString());
                sendEmail(sKontakti.getSelectedItem().toString());
            }
        });

        return v;
    }

    private void getContacts() {
        try {

            ContentResolver cr = getActivity().getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {

                do {
                    // get the contact's information
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    // get the user's email address
                    String email = null;
                    Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (ce != null && ce.moveToFirst()) {
                        email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        ce.close();
                    }

                    // get the user's phone number
                    String phone = null;
                    if (hasPhone > 0) {
                        Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (cp != null && cp.moveToFirst()) {
                            phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            cp.close();
                        }
                    }

                    // if the user user has an email or phone then add it to contacts
                   kontakti.put(name, email);

                } while (cursor.moveToNext());

                // clean up cursor
                cursor.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void dohvatiKontakte(){

        Cursor mails = getActivity().getContentResolver().query
                (ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,null,null, null);
        while (mails.moveToNext())
        {
            String emails = mails.getString(mails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            Log.e("a",emails);
        }
        mails.close();

        Cursor cursor = null;
        try {
            cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            int imeID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int emailID = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            cursor.moveToFirst();
            do {
                kontakti.put(cursor.getString(imeID), cursor.getString(emailID));
            }
            while (cursor.moveToNext());
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("Error",e.getMessage());
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void sendEmail(String ime) {
        String subject = "Knjiga za čitanje";
        String text = "Zdravo " + ime + ",\n" + "Pročitaj knjigu " + nazivKnjige + " od " + autor + "!";
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        String aEmailList[] = { kontakti.get(ime) };
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(emailIntent);
    }

    private void posaljiEmail(String ime) {
        Log.i("Send email", "");
        String TO = kontakti.get(ime);
        if(autor == null)
            autor = "";
        Toast.makeText(getContext(), TO, Toast.LENGTH_LONG).show();
        Intent emailSlanje = new Intent(Intent.ACTION_SEND);//, Uri.fromParts("mailto",TO, null));
        String text = "Zdravo " + ime + ",\n" + "Pročitaj knjigu " + nazivKnjige + " od " + autor + "!";
        emailSlanje.setData(Uri.parse("mailto:"));
        emailSlanje.setType("text/plain");
        emailSlanje.putExtra(Intent.EXTRA_EMAIL, TO);
        emailSlanje.putExtra(Intent.EXTRA_SUBJECT, "Knjiga za čitanje");
        emailSlanje.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(Intent.createChooser(emailSlanje, "Send mail..."));
            getActivity().finish();
            Log.i("Finished sending email", "");
        }
        catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "There is no email client installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
