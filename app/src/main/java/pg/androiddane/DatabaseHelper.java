package pg.androiddane;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Patryk on 2017-04-13.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public final static int DB_VERSION = 1;
    public final static String ID = "_id";
    public final static String DB_NAME = "db_name";
    public final static String TABLE_NAME = "table_name";
    public final static String COL1 = "column_1_name";
    public final static String COL2 = "column_2_name";
    public final static String DB_create = "CREATE TABLE " + TABLE_NAME +
            "("+ID+" integer primary key autoincrement, " +
            COL1 + " text not null, " +
            COL2 + " text);";
    private static final String DB_delete = "DROP TABLE IF EXIST "+TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
