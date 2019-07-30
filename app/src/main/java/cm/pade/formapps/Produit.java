package cm.pade.formapps;

/**
 * Created by Sumbang on 27/09/2016.
 */

public class Produit {

    private int id;
    private String name;
    private int status;
    boolean selected = false;

    public Produit(){
        this.id = 0; this.name = ""; this.status = 0; this.selected = false;
    }

    public Produit(int id, String name, int status, boolean selected){
        this.id = id; this.status = status; this.name = name;
        this.selected = selected;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
