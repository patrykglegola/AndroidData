package pg.androiddane;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{
    public final static int DB_VERSION = 1;
    public final static String ID = "_id";
    public final static String DB_NAME = "PhonesDB";
    public final static String TABLE_NAME = "Phones";
    public final static String COL_PRODUCENT = "producent";
    public final static String COL_MODEL = "model";
    public final static String COL_ANDROID_VERSION = "android_version";
    public final static String COL_WWW = "www";
    public final static String DB_create = "CREATE TABLE " + TABLE_NAME + "("+
            ID +" integer primary key autoincrement, " +
            COL_PRODUCENT + " text not null, " +
            COL_MODEL + " text not null, " +
            COL_ANDROID_VERSION + " text not null, " +
            COL_WWW + " text);";
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
