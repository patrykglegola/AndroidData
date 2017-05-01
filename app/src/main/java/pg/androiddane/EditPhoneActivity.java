package pg.androiddane;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditPhoneActivity extends AppCompatActivity {

    private static final int NEW_ROW = -1;

    private long rowId;
    private EditText producentET;
    private EditText modelET;
    private EditText androidVersionET;
    private EditText wwwET;
    private Button saveButton;
    private Button cancelButton;
    private Button wwwButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);
        findAllViews();
        rowId = NEW_ROW;
        if(savedInstanceState != null) {
            rowId = savedInstanceState.getLong((DatabaseHelper.ID));
        }
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null)
                rowId = bundle.getLong(DatabaseHelper.ID);
        }
        if (rowId != NEW_ROW)
            //jeśli edytujemy istniejący wiersz tabei telefonów, wywołujemy metodę wypełniającą
            // pola tekstowe wg danych z tego wiersza:
            fillFields();

        //listenery przycisków:
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonClick();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelButtonClick();
            }
        });
    }

    //nadpisanie metody onSaveInstanceState, aby zabezpieczyć aplikację przed utratą identyfikatora
    // id edytowanego wiersza tabeli z bazy w przypadku zresetowania aktywności:
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DatabaseHelper.ID, rowId);
    }

    //obsługa przycisku "zapisz":
    private void saveButtonClick() {
        if(areTextFieldsNotEmpty()) { //jeśli pola nie są puste, zapisujemy dane do bazy:
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_PRODUCENT, producentET.getText().toString());
            values.put(DatabaseHelper.COL_MODEL, modelET.getText().toString());
            values.put(DatabaseHelper.COL_ANDROID_VERSION, androidVersionET.getText().toString());
            values.put(DatabaseHelper.COL_WWW, wwwET.getText().toString());
            if(rowId == NEW_ROW) {
                Uri newRowUri = getContentResolver().insert(PhonesProvider.URI_CONTENTS, values);
                rowId = Integer.parseInt(newRowUri.getLastPathSegment());
            }
            else {
                int changedRows = getContentResolver().update(ContentUris.withAppendedId(
                        PhonesProvider.URI_CONTENTS, rowId), values, null, null);
            }
            setResult(RESULT_OK); //ustawienie wyniku działania aktywności jako OK
            finish(); // zakończenie aktywności
        }
        else //jeśli któreś z pól jest puste, wyświetlany jest stosowny komunikat
            Toast.makeText(this, getString(R.string.fill_fields_toast), Toast.LENGTH_SHORT).show();
    }

    //obsługa przycisku "anuluj":
    private void cancelButtonClick() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean areTextFieldsNotEmpty() {
        return !(producentET.getText().toString().isEmpty()
        || modelET.getText().toString().isEmpty()
        || androidVersionET.getText().toString().isEmpty()
        || wwwET.getText().toString().isEmpty() );
    }

    private void fillFields() {
        //tablica określająca które kolumny chcemy pobrać z dostawcy:
        String projection[] = { DatabaseHelper.COL_PRODUCENT, DatabaseHelper.COL_MODEL,
                            DatabaseHelper.COL_ANDROID_VERSION, DatabaseHelper.COL_WWW};
        //odpytanie dostawcy treści i zwrócenie kursora:
        Cursor phonesCursor = getContentResolver().query(
                ContentUris.withAppendedId(PhonesProvider.URI_CONTENTS, rowId),
                projection, null, null, null);
        //przestawienie kursora na pierwszy element
        phonesCursor.moveToFirst();
        //odczytanie wartości od dostawcy i wpisanie do pól tekstowych:
        producentET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_PRODUCENT)));
        modelET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_MODEL)));
        androidVersionET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_ANDROID_VERSION)));
        wwwET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_WWW)));
        //zamknięcie kursora:
        phonesCursor.close();

    }

    //metoda pzydzielająca polom odpowiednie elementy XML
    private void findAllViews() {
        producentET = (EditText) findViewById(R.id.producentEdit);
        modelET = (EditText) findViewById(R.id.modelEdit);
        androidVersionET = (EditText) findViewById(R.id.androidVersionEdit);
        wwwET = (EditText) findViewById(R.id.wwwEdit);
        saveButton = (Button) findViewById(R.id.activity_edit_phone_save_button);
        cancelButton = (Button) findViewById(R.id.activity_edit_phone_cancel_button);
        wwwButton = (Button) findViewById(R.id.activity_edit_phone_www_button);
    }
}
