package cm.pade.formapps;

/**
 * Created by christian.sumbang on 14/02/2017.
 */

public class CategorieUser {

    private int id;
    private String code;
    private String name;

    public CategorieUser(){}

    public CategorieUser(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }


}
