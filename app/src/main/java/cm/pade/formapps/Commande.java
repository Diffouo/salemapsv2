package cm.pade.formapps;

/**
 * Created by Sumbang on 25/10/2016.
 */

public class Commande {

    private Produit produit;
    private int qte;
    private int type;

    public Commande(){

    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Produit getProduit() {
        return produit;
    }

    public int getQte() {
        return qte;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
