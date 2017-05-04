package pg.androiddane;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Aktywność obsługująca dodawanie nowych telefonów do bazy, edycję informacji
 * o istnięjących w bazie telefonac oraz dprzejście na stronę internetową o danym telefonie
 */

public class AddOrEditPhoneActivity extends Activity {

    private static final int NEW_ROW = -1;

    private long rowId;
    private EditText producerET;
    private EditText modelET;
    private EditText androidVersionET;
    private EditText urlET;
    private Button saveButton;
    private Button cancelButton;
    private Button wwwButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_phone);
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
        wwwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wwwButtonClick();
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

    //obsługa przycisku "Zapisz":
    private void saveButtonClick() {
        if(isAnyTextFieldEmpty()) {
            //jeśli któreś z pól jest puste, wyświetlany jest stosowny komunikat
            Toast.makeText(this, getString(R.string.fill_fields_toast), Toast.LENGTH_SHORT).show();
        }
        //jeśli wersja androida ma niepoprawny format, wyświetlany jest stosowny komunikat
        else if (!androidVersionIsValid()) {
            Toast.makeText(this, getString(R.string.android_version_not_valid_toast),
                    Toast.LENGTH_SHORT).show();
        }
        else if (!urlIsValid()) {
            //jeśli adres url ma niepoprawny format, wyświetlany jest stosowny komunikat
            Toast.makeText(this, getString(R.string.url_is_not_valid_toast),
                    Toast.LENGTH_SHORT).show();
        }
        else { ///jeśli pola nie są puste, zapisujemy dane do bazy:
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COL_PRODUCER, producerET.getText().toString());
            values.put(DatabaseHelper.COL_MODEL, modelET.getText().toString());
            values.put(DatabaseHelper.COL_ANDROID_VERSION, androidVersionET.getText().toString());
            values.put(DatabaseHelper.COL_WWW, urlET.getText().toString());

            if (rowId == NEW_ROW) {
                //dodajemy nowy wiersz do tabeli w bazie
                Uri newRowUri = getContentResolver().insert(PhonesProvider.URI_CONTENTS, values);
                rowId = Integer.parseInt(newRowUri.getLastPathSegment());
            } else {
                //edytujemy istniejący wiersz tabeli w bazie
                int changedRows = getContentResolver().update(ContentUris.withAppendedId(
                        PhonesProvider.URI_CONTENTS, rowId), values, null, null);
            }
            setResult(RESULT_OK); //ustawienie wyniku działania aktywności jako OK
            finish(); // zakończenie aktywności
        }
    }

    //obsługa przycisku "Anuluj":
    private void cancelButtonClick() {
        setResult(RESULT_CANCELED); //ustawienie wyniku działania aktywności jako anulowany
        finish(); // zakończenie aktywności
    }

    //obsługa przycisku "WWW":
    private void wwwButtonClick(){
        if (!urlIsValid()) {
            //jeśli adres url ma niepoprawny format, wyświetlany jest stosowny komunikat
            Toast.makeText(this, getString(R.string.url_is_not_valid_toast),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            //wywołanie aktywności przeglądarki i przejście pod adres url wyświetlany w oknie "WWW"
            Intent webBrowserIntent = new Intent("android.intent.action.VIEW",
                    Uri.parse(urlET.getText().toString()));
            startActivity(webBrowserIntent);
        }
    }

    //metoda sprawdzająca czy adres url zaczyna się od "http://" lub "https://"
    private boolean urlIsValid() {
        String url = urlET.getText().toString();
        return ( url.startsWith("http://") || url.startsWith("https://") );
    }

    //metoda sprawdzająca poprawność formatu wprowadzonej wersji androida:
    private boolean androidVersionIsValid() {
        Pattern pattern = Pattern.compile( "[1-9]{1}(\\.[0-9]){1,2}" );
        Matcher matcher = pattern.matcher(androidVersionET.getText().toString());
        return matcher.matches();
    }

    //sprawdzenie czy jakiekolwiek pole jest puste
    private boolean isAnyTextFieldEmpty() {
        return ( producerET.getText().toString().isEmpty()
        || modelET.getText().toString().isEmpty()
        || androidVersionET.getText().toString().isEmpty()
        || urlET.getText().toString().isEmpty() );
    }


    private void fillFields() {
        //tablica określająca które kolumny chcemy pobrać z dostawcy:
        String projection[] = { DatabaseHelper.COL_PRODUCER, DatabaseHelper.COL_MODEL,
                            DatabaseHelper.COL_ANDROID_VERSION, DatabaseHelper.COL_WWW};
        //odpytanie dostawcy treści i zwrócenie kursora:
        Cursor phonesCursor = getContentResolver().query(
                ContentUris.withAppendedId(PhonesProvider.URI_CONTENTS, rowId),
                projection, null, null, null);
        //przestawienie kursora na pierwszy element
        phonesCursor.moveToFirst();
        //odczytanie wartości od dostawcy i wpisanie do pól tekstowych:
        producerET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_PRODUCER)));
        modelET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_MODEL)));
        androidVersionET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_ANDROID_VERSION)));
        urlET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_WWW)));
        //zamknięcie kursora:
        phonesCursor.close();

    }

    //metoda pzydzielająca polom odpowiednie elementy XML
    private void findAllViews() {
        producerET = (EditText) findViewById(R.id.producentEdit);
        modelET = (EditText) findViewById(R.id.modelEdit);
        androidVersionET = (EditText) findViewById(R.id.androidVersionEdit);
        urlET = (EditText) findViewById(R.id.wwwEdit);
        saveButton = (Button) findViewById(R.id.activity_edit_phone_save_button);
        cancelButton = (Button) findViewById(R.id.activity_edit_phone_cancel_button);
        wwwButton = (Button) findViewById(R.id.activity_edit_phone_www_button);
    }
}
