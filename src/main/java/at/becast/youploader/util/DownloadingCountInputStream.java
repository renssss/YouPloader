package at.becast.youploader.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.CountingOutputStream;

public class DownloadingCountInputStream extends CountingOutputStream {

    private ActionListener listener = null;

    public DownloadingCountInputStream(OutputStream out) {
        super(out);
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    @Override
    protected void afterWrite(int n) throws IOException {
        super.afterWrite(n);
        if (listener != null) {
            listener.actionPerformed(new ActionEvent(this, 0, null));
        }
    }

}