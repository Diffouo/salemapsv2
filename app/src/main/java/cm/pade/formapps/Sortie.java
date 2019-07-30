package cm.pade.formapps;

/**
 * Created by Sumbang on 10/11/2016.
 */

public class Sortie {

    private int id;
    private String date;
    private String montant;

    public Sortie(){

    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMontant() {
        return montant;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }
}
