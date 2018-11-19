package provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mike on 9 Mar 2018.
 */

public class MySQLOpener extends SQLiteOpenHelper {
    MySQLOpener(Context context) {
        super(context, "/data/data/com.ultimatetoolsil.list/databases/location.db", null, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
