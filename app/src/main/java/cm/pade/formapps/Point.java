package cm.pade.formapps;

/**
 * Created by Sumbang on 21/09/2016.
 */
public class Point {

    private String nom;
    private String adresse;
    private String phone;
    private String email;
    private int type;
    private String xlocation;
    private String ylocation;
    private int id;

    public Point(){

        this.nom = ""; this.adresse = ""; this.phone = ""; this.email = ""; this.type = 0;

        this.xlocation = ""; this.ylocation = ""; this.id = 0;

    }

    public Point(String nom,String adresse,String phone,String email,int type,String xlocation,String ylocation,int id){

         this.nom = nom; this.adresse = adresse; this.phone = phone; this.email = email; this.type = type;

        this.xlocation = xlocation; this.ylocation = ylocation; this.id = id;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPhone() {
        return phone;
    }

    public int getType() {
        return type;
    }

    public String getXlocation() {
        return xlocation;
    }

    public String getYlocation() {
        return ylocation;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setXlocation(String xlocation) {
        this.xlocation = xlocation;
    }

    public void setYlocation(String ylocation) {
        this.ylocation = ylocation;
    }
}
