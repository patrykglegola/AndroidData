package pg.androiddane;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NEW_ELEMENT = -1;
    private SimpleCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFields();
    }

    private void setFields() {
        //inicjalizacja loadera:
        getLoaderManager().initLoader(0, null, this);
        //utworzenie mapowania między kolumnami tabeli a kolumnami wyświetlanej listy:
        String[] mapFrom = new String[]{DatabaseHelper.COL_PRODUCENT, DatabaseHelper.COL_MODEL};
        int[] mapTo = new int[]{R.id.producentText, R.id.modelEdit};
        //adapter wymaga, aby w wyniku zapytania znajdowała się kolumna _id
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.phone_list_row, null, mapFrom, mapTo, 0);
        setListAdapter(cursorAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DatabaseHelper.ID,
                DatabaseHelper.COL_PRODUCENT,
                DatabaseHelper.COL_MODEL};
        CursorLoader cursorLoader = new CursorLoader(this, PhonesProvider.URI_CONTENTS, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null)
;    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return super.onMenuItemSelected(featureId, item);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        addOrEditElement(id);
    }
    private void createNewElement() {
        addOrEditElement((long) NEW_ELEMENT);
    }

    private void addOrEditElement(long id) {
        Intent intent = new Intent(this, EditPhoneActivity.class);
        intent.putExtra(DatabaseHelper.ID, id);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //restart loadera, jako że dane mogły zostać zmodyfikowane:
        getLoaderManager().restartLoader(0, null, this);
    }


}