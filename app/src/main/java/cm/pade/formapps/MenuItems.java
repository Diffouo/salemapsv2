package cm.pade.formapps;

/**
 * Created by Sumbang on 21/09/2016.
 */

public class MenuItems {

    public int Icone;
    public String Label;

    public MenuItems(){ this.Icone=0; this.Label=""; }

    public MenuItems(int icone,String label){ this.Icone = icone; this.Label = label; }

    public int getIcone() {
        return Icone;
    }

    public String getLabel() {
        return Label;
    }

    public void setIcone(int icone) {
        this.Icone = icone;
    }

    public void setLabel(String label) {
        this.Label = label;
    }
}