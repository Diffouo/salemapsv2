package cm.pade.formapps;

/**
 * Created by Sumbang on 19/10/2016.
 */

public class Customer {

    private int id;
    private String name;
    private String xlocation;
    private String ylocation;

    public Customer(){

        this.id = 0; this.name = ""; this.xlocation = ""; this.ylocation = "";
    }

    public Customer(int id, String name, String xlocation, String ylocation){

        this.id = id; this.name = name; this.xlocation = xlocation; this.ylocation = ylocation;
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

    public String getXlocation() {
        return xlocation;
    }

    public String getYlocation() {
        return ylocation;
    }

    public void setXlocation(String xlocation) {
        this.xlocation = xlocation;
    }

    public void setYlocation(String ylocation) {
        this.ylocation = ylocation;
    }
}
