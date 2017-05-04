package pg.androiddane;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

/**
 * Klasa przechowująca dane o bazie, nazwach tabel i kolumn
 * oraz polecenia do tworzenia i usuwania tabeli
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public final static int DB_VERSION = 1;
    //zdefiniowanie nazwy bazy, tabeli i kolumn:
    public final static String ID = "_id";
    public final static String DB_NAME = "PhonesDB";
    public final static String TABLE_NAME = "Phones";
    public final static String COL_PRODUCER = "producer";
    public final static String COL_MODEL = "model";
    public final static String COL_ANDROID_VERSION = "android_version";
    public final static String COL_WWW = "www";
    //zdefiniowanie polecenia tworzenia tabeli
    public final static String DB_create = "CREATE TABLE " + TABLE_NAME + "("+
            ID +" integer primary key autoincrement, " +
            COL_PRODUCER + " text not null, " +
            COL_MODEL + " text not null, " +
            COL_ANDROID_VERSION + " text not null, " +
            COL_WWW + " text);";
    //zdefiniowanie polecenia usuwania tabeli:
    private static final String DB_delete = "DROP TABLE IF EXIST "+TABLE_NAME;

    public DatabaseHelper(Context context) {

        super(context,DB_NAME,null,DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //tworzenie bazy
        db.execSQL(DB_create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //aktualizacja bazy
        db.execSQL(DB_delete);
        onCreate(db);
    }
}
