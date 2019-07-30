package cm.pade.formapps;

/**
 * Created by Sumbang on 20/09/2016.
 */
public class User {

    private int iduser;
    private String username;
    private int status;
    private String role;

    public User(){

        this.iduser = 0; this.username = ""; this.status = 0;
    }

    public User(int iduser,String username,int status){

        this.iduser = iduser; this.username = username; this.status = status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public int getIduser() {
        return iduser;
    }

    public String getUsername() {
        return username;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
