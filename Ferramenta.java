import javax.swing.text.StyledEditorKit.BoldAction;

import com.google.gson.annotations.Since;

public class Ferramenta {

    @Since(1.1)
    private boolean Usato;
    private String id;
    private int N;

    Ferramenta(String id, int N, boolean Usato) {
        this.id = id;
        this.N = N;
        this.Usato = Usato;
    }

    public void updateID(String newID) {
        this.id = newID;
    }

    public void updateN(int newN) {
        this.N = newN;
    }

    public void updateusato(boolean newUsato) {
        this.Usato = newUsato;
    }

}
