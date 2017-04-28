package pg.androiddane;

import android.content.ContentUris;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditPhoneActivity extends AppCompatActivity {

    private long rowId;
    private EditText editProducentET;
    private EditText editModelET;
    private EditText editAndroidVersionET;
    private EditText editWwwET;
    private Button saveButton;
    private Button cancelButton;
    private Button wwwButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);
        findAllViews();
        rowId = -1;
        if(savedInstanceState != null) {
            rowId = savedInstanceState.getLong((DatabaseHelper.ID));
        }
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null)
                rowId = bundle.getLong(DatabaseHelper.ID);
        }
        if (rowId != -1)
            fillFields();
        //obsługa przycisków


    }

    private void fillFields() {
        String projection[] = { DatabaseHelper.COL_PRODUCENT, DatabaseHelper.COL_MODEL,
                            DatabaseHelper.COL_ANDROID_VERSION, DatabaseHelper.COL_WWW};
        Cursor phonesCursor = getContentResolver().query(
                ContentUris.withAppendedId(PhonesProvider.URI_CONTENTS, rowId),
                projection, null, null, null);
        phonesCursor.moveToFirst();
        int columnIndex = phonesCursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCENT);
        String value = phonesCursor.getString(columnIndex);
        editProducentET.setText(value);
        editModelET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_MODEL)));
        editAndroidVersionET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_ANDROID_VERSION)));
        editWwwET.setText(phonesCursor.getString(phonesCursor.getColumnIndexOrThrow(
                DatabaseHelper.COL_WWW)));
        phonesCursor.close();

    }

    private void findAllViews() {
        editProducentET = (EditText) findViewById(R.id.producentEdit);
        editModelET = (EditText) findViewById(R.id.modelEdit);
        editAndroidVersionET = (EditText) findViewById(R.id.androidVersionEdit);
        editWwwET = (EditText) findViewById(R.id.wwwEdit);
        saveButton = (Button) findViewById(R.id.activity_edit_phone_save_button);
        cancelButton = (Button) findViewById(R.id.activity_edit_phone_cancel_button);
        wwwButton = (Button) findViewById(R.id.activity_edit_phone_www_button);
    }
}
