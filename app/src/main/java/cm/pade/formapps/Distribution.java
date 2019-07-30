package cm.pade.formapps;

/**
 * Created by Sumbang on 30/10/2016.
 */

public class Distribution {

    private Produit produit;
    private int qte;
    private int price;
    private int unite;

    public Distribution(){}

    public int getPrice() {
        return price;
    }

    public int getQte() {
        return qte;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public void setQte(int qte) {
        this.qte = qte;
    }

    public int getUnite() {
        return unite;
    }

    public void setUnite(int unite) {
        this.unite = unite;
    }
}
