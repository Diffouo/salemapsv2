package cm.pade.formapps;

/**
 * Created by Sumbang on 20/09/2016.
 */
public class SessionData {

    private int iduser; private String username; private int status;

    private String longdata; private String latdata; private String role;

    private static SessionData startSession;

    public static SessionData getInstance(){
        if(startSession == null) {
            startSession = new SessionData();
        }
        return startSession;
    }


    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLatdata() {
        return latdata;
    }

    public String getLongdata() {
        return longdata;
    }

    public void setLatdata(String latdata) {
        this.latdata = latdata;
    }

    public void setLongdata(String longdata) {
        this.longdata = longdata;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
