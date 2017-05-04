package pg.androiddane;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

public class PhonesProvider extends ContentProvider {

    //pole przechowujące referencję do obiektu pomocnika bazy:
    private DatabaseHelper mDatabaseHelper;
    //identyfikator dostawcy - pozwala na odróżnienie naszego dostawcy od innych:
    private static final String AUTHORITY = "pg.androiddane.myProvider";
    //stała - aby nie trzeba było wpisywać tekstu samodzielnie:
    public static final Uri URI_CONTENTS = Uri.parse("content://" + AUTHORITY
                                        + "/" + DatabaseHelper.TABLE_NAME);
    //stałe pozwalające zidentyfikować rodzaj rozpoznanego URI:
    private static final int WHOLE_TABLE = 1;
    private static final int SELECTED_ROW = 2;
    //UriMatcher z pustym korzeniem drzewa URI (NO_MATCH):
    private static final UriMatcher uriMatch = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //dodanie rozpoznawanych URI:
        uriMatch.addURI(AUTHORITY, DatabaseHelper.TABLE_NAME, WHOLE_TABLE);
        uriMatch.addURI(AUTHORITY, DatabaseHelper.TABLE_NAME + "/#", SELECTED_ROW);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        //sprawdzenie typu Uri i zapisanie wyniku
        int typeOfUri = uriMatch.match(uri);

        //otwieranie magazynu - bazy danych telefonów:
        SQLiteDatabase phonesDB = mDatabaseHelper.getWritableDatabase();

        Cursor phonesCursor = null;
        switch (typeOfUri) {
            //gdy oczytujemy dane z całej tabeli, przekazujemy parametry z wywołania metody query()
            // dostawcy do metody query() bazy
            case WHOLE_TABLE:
                phonesCursor = phonesDB.query(false, DatabaseHelper.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder, null, null);
                break;
            //gdy odczytujemy dane jednego wiersza, do argumentu selection dodajemy wartość klucza,
            // który określa żądany wiersz. Wykorzystujemy do tego metodę pomocniczą
            // addIdToSelection()
            case SELECTED_ROW:
                phonesCursor = phonesDB.query(false, DatabaseHelper.TABLE_NAME, projection,
                        addIdToSelection(selection, uri),
                        selectionArgs, null, null, sortOrder, null, null);
                break;
            //jeśli URI nie zostanie rozpoznane, zgłaszany jest wyjątek
            default:
                throwUnknownUriException(uri);
        }
        //monitorowanie zmiany danych URI:
        phonesCursor.setNotificationUri(
                getContext().getContentResolver(), uri);

        return phonesCursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int typeOfUri = uriMatch.match(uri);
        SQLiteDatabase phonesDB = mDatabaseHelper.getWritableDatabase();
        long addedRowId = 0;
        switch (typeOfUri) {
            case WHOLE_TABLE:
                phonesDB.insert(DatabaseHelper.TABLE_NAME, null, values);
                break;
            default:
                throwUnknownUriException(uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(DatabaseHelper.TABLE_NAME + "/" + addedRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int typeOfUri = uriMatch.match(uri);
        SQLiteDatabase phonesDB = mDatabaseHelper.getWritableDatabase();
        int amountOfDeletedRows = 0;
        switch (typeOfUri) {
            case WHOLE_TABLE:
                amountOfDeletedRows = phonesDB.delete(DatabaseHelper.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case SELECTED_ROW:
                amountOfDeletedRows = phonesDB.delete(DatabaseHelper.TABLE_NAME,
                        DatabaseHelper.ID + "=?", new String[]{uri.getPathSegments().get(1)});
                break;
            default:
                throwUnknownUriException(uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return amountOfDeletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int typeOfUri = uriMatch.match(uri);
        SQLiteDatabase phonesDB = mDatabaseHelper.getWritableDatabase();

        int amountOfUpdated = 0;
        switch (typeOfUri) {
            case WHOLE_TABLE:
                amountOfUpdated = phonesDB.update(DatabaseHelper.TABLE_NAME,
                        values, selection, selectionArgs);
                break;
            case SELECTED_ROW:
                amountOfUpdated = phonesDB.update(DatabaseHelper.TABLE_NAME,
                        values, addIdToSelection(selection, uri), selectionArgs);
                break;
            default:
                throwUnknownUriException(uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return amountOfUpdated;
    }

    private void throwUnknownUriException(@NonNull Uri uri) {
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }


    private String addIdToSelection(String selection, Uri uri) {
        if (selection != null && !selection.equals(""))
            selection = selection + " and " + DatabaseHelper.ID + "=" + uri.getLastPathSegment();
        else
            selection = DatabaseHelper.ID + "=" + uri.getLastPathSegment();
        return selection;
    }
}
