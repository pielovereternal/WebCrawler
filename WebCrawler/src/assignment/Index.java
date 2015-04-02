package assignment;

import java.io.*;
import java.net.URL;

public class Index implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Index load(URL url) throws IOException, ClassNotFoundException {
        InputStream fin = null;
        ObjectInputStream oin = null;
        Index result = null;
        try {
            fin = url.openStream();
            oin = new ObjectInputStream(fin);
            result = (Index)oin.readObject();
        } finally {
            try {
                if (oin != null)
                    oin.close();
                else if (fin != null)
                    fin.close();
            } catch (IOException e) {
            }
        }
        return result;
    }

    public void save(String filename) throws IOException {
        FileOutputStream fout = null;
        ObjectOutputStream oout = null;
        try {
            fout = new FileOutputStream(filename);
            oout = new ObjectOutputStream(fout);
            oout.writeObject(this);
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (oout != null)
                    oout.close();
                else if (fout != null)
                    fout.close();
            } catch (IOException e) {
            }
        }
    }
}
