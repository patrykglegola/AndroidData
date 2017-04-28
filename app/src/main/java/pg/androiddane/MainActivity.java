package pg.androiddane;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends ListActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFields();
    }

    private void setFields() {
        //inicjalizowanie loadera:
        getLoaderManager().initLoader(0,null,null);
        //utworzenie mapowania między kolumnami tabeli a kolumnami wyświetlanej listy:
        String[] mapFrom = new String[] { DatabaseHelper.COL_PRODUCENT, DatabaseHelper.COL_MODEL};
        int [] mapTo = new int[] {R.id.producentText, R.id.modelEdit};
        //adapter wymaga, aby w wyniku zapytania znajdowała się kolumna _id
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.phone_list_row, null, mapFrom, mapTo, 0);
        setListAdapter(cursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { DatabaseHelper.ID,
                DatabaseHelper.COL_PRODUCENT,
                DatabaseHelper.COL_MODEL};
        CursorLoader cursorLoader = new CursorLoader(this, PhonesProvider.URI_CONTENTS, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

}
