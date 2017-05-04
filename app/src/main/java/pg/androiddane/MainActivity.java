package pg.androiddane;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Główna aktywność aplikacji, lista wprowadzonych do bazy telefonów
 */

public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //-1 jest przekazywane jako id wiersza, kiedy chcemy dodać nowy element
    private static final int NEW_ELEMENT = -1;

    private SimpleCursorAdapter cursorAdapter;
    private ListView phonesList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activateMultiChoice(); //nadanie możliwości zaznaczania wielu elementów listy jednocześnie
        setUpAdapter(); //ustawienie i uruchomienie adaptera listy

    }

    //metoda umożliwiająca jednoczesne zaznaczanie elementów na liście:
    private void activateMultiChoice() {
        phonesList = (ListView) findViewById(android.R.id.list);
        phonesList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        phonesList.setMultiChoiceModeListener(
                new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                          long id, boolean checked) {

                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        //kiedy zaznaczymy element na liście, otwiera się menu kontekstowe
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.context_bar, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            //jeśli klikniemy ikonkę usunięcia na pasku akcji, wszystkie
                            //zaznaczone elementy listy są usuwane z bazy
                            case R.id.action_delete_phones:
                                deleteSelected();
                                return true;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                }
        );
    }

    //metoda obsługująca usunięcie wszystkich zaznaczonych elementów listy
    private void deleteSelected() {
        long selected[] = getListView().getCheckedItemIds();
        for (int i = 0; i < selected.length; ++i) {
            getContentResolver().delete(ContentUris.withAppendedId(PhonesProvider.URI_CONTENTS,
                    selected[i]), null, null);
        }
    }

    //metoda ustawiająca i uruchamiająca adapter listy:
    private void setUpAdapter() {
        //inicjalizacja loadera:
        getLoaderManager().initLoader(0, null, this);
        //utworzenie mapowania między kolumnami tabeli a kolumnami wyświetlanej listy:
        String[] mapFrom = new String[]{DatabaseHelper.COL_PRODUCER, DatabaseHelper.COL_MODEL};
        int[] mapTo = new int[]{R.id.producentText, R.id.modelText};
        //adapter wymaga, aby w wyniku zapytania znajdowała się kolumna _id
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.phone_list_row,
                null, mapFrom, mapTo, 0);
        setListAdapter(cursorAdapter);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {DatabaseHelper.ID,
                DatabaseHelper.COL_PRODUCER,
                DatabaseHelper.COL_MODEL};
        CursorLoader cursorLoader = new CursorLoader(this, PhonesProvider.URI_CONTENTS,
                projection, null, null, null);
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
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_phone_action:
                createNewElement();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);

    }

    //metoda wywoływana po naciśnięciu dowolnego elementu listy:
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        addOrEditElement(id);
    }
    private void createNewElement() {
        addOrEditElement((long) NEW_ELEMENT);
    }

    //metoda uruchamiająca nową aktywność w celu dodania lub edycji elementu listy:
    private void addOrEditElement(long id) {
        Intent intent = new Intent(this, AddOrEditPhoneActivity.class);
        intent.putExtra(DatabaseHelper.ID, id);
        startActivityForResult(intent, 0);
    }

    //metoda wywoływana po zamknięciu wywołanej aktywności:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //restart loadera, jako że dane mogły zostać zmodyfikowane:
        getLoaderManager().restartLoader(0, null, this);
    }


}