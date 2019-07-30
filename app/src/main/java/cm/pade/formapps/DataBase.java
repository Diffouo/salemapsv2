package cm.pade.formapps;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

/**
 * Created by Sumbang on 20/09/2016.
 */
public class DataBase extends SQLiteOpenHelper {

    private static final String TABLE_USER="users";

    private static final String TABLE_POINT="points";


    private static final String U_ID="u_id";

    private static final String U_NAME="u_name";

    private static final String U_STATUS="u_status";

    private static final String U_ROLE="u_role";

    private static final String CREATE_U="CREATE TABLE "+TABLE_USER+" ("+U_ID+" INTEGER PRIMARY KEY , "+U_NAME+" TEXT NOT NULL, "+U_STATUS+" INTEGER NOT NULL,"+U_ROLE+" TEXT NOT NULL ); ";


    private static final String P_ID="p_id";

    private static final String P_NAME="p_name";

    private static final String P_ADRESS="p_adress";

    private static final String P_PHONE="p_phone";

    private static final String P_EMAIL="p_email";

    private static final String P_TYPE="p_type";

    private static final String P_XLOCATION="p_xlocation";

    private static final String P_YLOCATION="p_ylocation";

    private static final String CREATE_P="CREATE TABLE "+TABLE_POINT+" ("+P_ID+" INTEGER PRIMARY KEY AUTOINCREMENT , "+P_NAME+" TEXT NOT NULL, "+P_ADRESS+" TEXT NOT NULL, "+P_PHONE+" TEXT NOT NULL, "+P_EMAIL+" TEXT NOT NULL, "+P_TYPE+" INTEGER NOT NULL, "+P_XLOCATION+" TEXT NOT NULL, "+P_YLOCATION+" TEXT NOT NULL); ";



    public DataBase(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_U); db.execSQL(CREATE_P);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_POINT);
        onCreate(db);

    }


}
