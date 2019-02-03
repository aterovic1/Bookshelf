package ba.unsa.etf.rma.amar_terovic.rma_spirala;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class KnjigaResultReceiver extends ResultReceiver {

    private KnjigaResultReceiver.Receiver mReceiver;

    public KnjigaResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(KnjigaResultReceiver.Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        public void onReceiveKnjiga(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null) {
            mReceiver.onReceiveKnjiga(resultCode, resultData);
        }
    }

}
