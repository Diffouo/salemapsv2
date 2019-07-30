package cm.pade.formapps;

/**
 * Created by Sumbang on 16/09/2016.
 */
public class Form {

    private String name;
    private String adresse;
    private String phone;
    private String email;
    private String type;
    private String longitude;
    private String latitude;

    public Form(){

        this.name = ""; this.adresse = ""; this.phone = ""; this.email = ""; this.type = "";

        this.longitude = ""; this.latitude = "";
    }

    public Form(String name,String adresse, String phone, String email, String type, String longitude, String latitude){

        this.name = name; this.adresse = adresse; this.phone = phone; this.email = email; this.type = type;

        this.longitude = longitude; this.latitude = latitude;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getEmail() {
        return email;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getType() {
        return type;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setType(String type) {
        this.type = type;
    }

}
