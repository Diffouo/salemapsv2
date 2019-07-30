package cm.pade.formapps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Sumbang on 20/09/2016.
 */
public class Request {

    private static final int VERSION_BDD=2;

    private static final String NOM_BDD="okfood.db";

    private static final String TABLE_USER="users";

    private static final String U_ID="u_id";

    private static final int NUM_ID=0;

    private static final String U_NAME="u_name";

    private static final int NUM_NAME=1;

    private static final String U_STATUS="u_status";

    private static final int NUM_STATUS=2;

    private static final String U_ROLE="u_role";

    private static final int NUM_ROLE=3;


    private static final String TABLE_POINT="points";

    private static final String P_ID="p_id";

    private static final int NUM_PID=0;

    private static final String P_NAME="p_name";

    private static final int NUM_PNAME=1;

    private static final String P_ADRESS="p_adress";

    private static final int NUM_PADRESS=2;

    private static final String P_PHONE="p_phone";

    private static final int NUM_PPHONE=3;

    private static final String P_EMAIL="p_email";

    private static final int NUM_PEMAIL=4;

    private static final String P_TYPE="p_type";

    private static final int NUM_PTYPE=5;

    private static final String P_XLOCATION="p_xlocation";

    private static final int NUM_PX=6;

    private static final String P_YLOCATION="p_ylocation";

    private static final int NUM_PY=7;



    private SQLiteDatabase bdd;

    private DataBase ma_bdd;

    public Request(Context context){

        // on cr�e la bdd et ses tables
        ma_bdd=new DataBase(context, NOM_BDD, null, VERSION_BDD);
    }


    public void open(){

        // ouverture de la BDD en �criture
        bdd=ma_bdd.getWritableDatabase();
    }


    public void close(){

        // on ferme l'acc�s � la BDD
        bdd.close();
    }


    public SQLiteDatabase getBdd(){

        return bdd;
    }

    public long insertUser(User u){

        // cr�ation d'un content values

        ContentValues values=new ContentValues();

        //values.put(W_ID,bourse.getID());

        values.put(U_ID, u.getIduser());

        values.put(U_NAME,u.getUsername());

        values.put(U_STATUS,u.getStatus());

        values.put(U_ROLE,u.getRole());

        return bdd.insert(TABLE_USER,null,values);

    }


    public long insertPoint(Point p){

        // cr�ation d'un content values

        ContentValues values=new ContentValues();

        values.put(P_NAME, p.getNom());

        values.put(P_ADRESS, p.getAdresse());

        values.put(P_PHONE, p.getPhone());

        values.put(P_EMAIL, p.getEmail());

        values.put(P_TYPE, p.getType());

        values.put(P_XLOCATION, p.getXlocation());

        values.put(P_YLOCATION, p.getYlocation());

        return bdd.insert(TABLE_POINT,null,values);

    }


    public long updateUser(User u,int id){

        ContentValues values=new ContentValues();

        values.put(U_ID, u.getIduser());

        values.put(U_NAME,u.getUsername());

        values.put(U_STATUS,u.getStatus());

        values.put(U_ROLE,u.getRole());

        return bdd.update(TABLE_USER,values, U_ID+"="+id,null);

    }

    public long updatePoint(Point p,int id){

        ContentValues values=new ContentValues();

        values.put(P_NAME, p.getNom());

        values.put(P_ADRESS, p.getAdresse());

        values.put(P_PHONE, p.getPhone());

        values.put(P_EMAIL, p.getEmail());

        values.put(P_TYPE, p.getType());

        values.put(P_XLOCATION, p.getXlocation());

        values.put(P_YLOCATION, p.getYlocation());

        return bdd.update(TABLE_POINT,values, P_ID+"="+id,null);

    }

    public int RemoveUser(int id){

        return bdd.delete(TABLE_USER, U_ID+"="+id,null);
    }

    public int RemovePoint(int id){

        return bdd.delete(TABLE_POINT, P_ID+"="+id,null);
    }

    public int RemoveAllUser(){

        return bdd.delete(TABLE_USER,null,null);
    }

    public int RemoveAllPoint(){

        return bdd.delete(TABLE_POINT,null,null);
    }

    public User getUser(int id){

        Cursor c=bdd.query(TABLE_USER, new String[]{U_ID,U_NAME,U_STATUS, U_ROLE}, U_ID+"="+id, null,null,null,null);

        return cursorUser(c);

    }

    public User cursorUser(Cursor c){

        if(c.getCount()==0){

            return null;

        }

        else { c.moveToFirst();

            User u = new User();

            u.setIduser(c.getInt(NUM_ID));

            u.setUsername(c.getString(NUM_NAME));

            u.setStatus(c.getInt(NUM_STATUS));

            u.setRole(c.getString(NUM_ROLE));

            c.close();

            return u;

        }

    }


    public Point getPoint(int id){

        Cursor c=bdd.query(TABLE_POINT, new String[]{P_ID,P_NAME,P_ADRESS,P_PHONE,P_EMAIL,P_TYPE,P_XLOCATION,P_YLOCATION}, P_ID+"="+id, null,null,null,null);

        return cursorPoint(c);

    }

    public Point cursorPoint(Cursor c){

        if(c.getCount()==0){

            return null;

        }

        else { c.moveToFirst();

            Point p = new Point();

            p.setNom(c.getString(NUM_PNAME));

            p.setAdresse(c.getString(NUM_PADRESS));

            p.setPhone(c.getString(NUM_PPHONE));

            p.setEmail(c.getString(NUM_PEMAIL));

            p.setType(c.getInt(NUM_PTYPE));

            p.setXlocation(c.getString(NUM_PX));

            p.setYlocation(c.getString(NUM_PY));

            p.setId(c.getInt(NUM_PID));

            c.close();

            return p;

        }

    }


    public ArrayList<User> getAllUser(){

        Cursor c = bdd.query(TABLE_USER, new String[]{U_ID,U_NAME,U_STATUS, U_ROLE}, null, null,null,null,null);

        return cursorUserAll(c);

    }

    public ArrayList<User> cursorUserAll(Cursor c){

        ArrayList<User> list=new ArrayList<User>();

        if(c.getCount()==0){

            return null;

        }

        else {

            // d�pla�ons le curseur sur le premier enregistrement

            c.moveToFirst();

            // tant que le curseur pourra se d�placer sur les autres �l�ments, remplir la liste

            while(c.isAfterLast()==false){

                User u = new User();

                u.setIduser(c.getInt(NUM_ID));

                u.setUsername(c.getString(NUM_NAME));

                u.setStatus(c.getInt(NUM_STATUS));

                u.setRole(c.getString(NUM_ROLE));

                list.add(u);

                c.moveToNext();

            }

            c.close();

            return list;

        }

    }


    public ArrayList<Point> getAllPoint(){

        Cursor c = bdd.query(TABLE_POINT, new String[]{P_ID,P_NAME,P_ADRESS,P_PHONE,P_EMAIL,P_TYPE,P_XLOCATION,P_YLOCATION}, null, null,null,null,null);

        return cursorPointAll(c);

    }

    public ArrayList<Point> cursorPointAll(Cursor c){

        ArrayList<Point> list=new ArrayList<Point>();

        if(c.getCount()==0){

            return null;

        }

        else {

            // d�pla�ons le curseur sur le premier enregistrement

            c.moveToFirst();

            // tant que le curseur pourra se d�placer sur les autres �l�ments, remplir la liste

            while(c.isAfterLast()==false){

                Point p = new Point();

                p.setNom(c.getString(NUM_PNAME));

                p.setAdresse(c.getString(NUM_PADRESS));

                p.setPhone(c.getString(NUM_PPHONE));

                p.setEmail(c.getString(NUM_PEMAIL));

                p.setType(c.getInt(NUM_PTYPE));

                p.setXlocation(c.getString(NUM_PX));

                p.setYlocation(c.getString(NUM_PY));

                p.setId(c.getInt(NUM_PID));

                list.add(p);

                c.moveToNext();

            }

            c.close();

            return list;

        }

    }


}
