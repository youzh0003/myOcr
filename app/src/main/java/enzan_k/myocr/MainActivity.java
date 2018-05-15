package enzan_k.myocr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import enzan_k.myocr.models.ScannedText;
import enzan_k.myocr.models.ScannedTextAdapter;

public class MainActivity extends AppCompatActivity {

    private CompoundButton compoundBtnAutoFocus,compoundBtnUseFlash;
    private TextView tvStatusMessage,tvTextValue;
    private Button btnReadText, btnSaveText;
    private ListView listViewSavedText;
    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";

    DatabaseReference databaseScannedText;

    private List<String> SavedDataList;
    private List<ScannedText> scannedTextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SavedDataList=new ArrayList<>();
        scannedTextList=new ArrayList<ScannedText>();
        btnSaveText=findViewById(R.id.button_saveText);

        tvStatusMessage = findViewById(R.id.status_message);
        tvTextValue = findViewById(R.id.text_value);
        compoundBtnAutoFocus = findViewById(R.id.auto_focus);
        compoundBtnUseFlash = findViewById(R.id.use_flash);
        btnReadText = findViewById(R.id.read_text);
        listViewSavedText=findViewById(R.id.listView_SavedTextList);

        try{

            databaseScannedText= FirebaseDatabase.getInstance().getReference("ScannedTexts");}
        catch (Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            tvTextValue.setText(ex.getMessage());
        }

        btnSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addScannedText();
            }
        });
        btnReadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // launch Ocr capture activity.
                Intent intent = new Intent(getApplicationContext(), OcrCaptureActivity.class);
                intent.putExtra(OcrCaptureActivity.AutoFocus, compoundBtnAutoFocus.isChecked());
                intent.putExtra(OcrCaptureActivity.UseFlash, compoundBtnUseFlash.isChecked());
                startActivityForResult(intent, RC_OCR_CAPTURE);
            }
        });

        listViewSavedText.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ScannedText scannedText=scannedTextList.get(position);
                showUpdateDialog(scannedText.getTextId(),scannedText.getTextContent());
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseScannedText.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                        scannedTextList.clear();

                    for (DataSnapshot savedDataSnapshot : dataSnapshot.getChildren()) {

                        ScannedText scannedText=savedDataSnapshot.getValue(ScannedText.class);
                        scannedTextList.add(scannedText);
                    }
                    ScannedTextAdapter adapter=new ScannedTextAdapter(MainActivity.this,scannedTextList);
                    listViewSavedText.setAdapter(adapter);
                }
                catch (Exception ex){
                    tvTextValue.setText(ex.getMessage());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);
                    tvStatusMessage.setText("Read text successfully");
                    tvTextValue.setText(text);
                    Log.d(TAG, "Text read: " + text);
                } else {
                    tvStatusMessage.setText("No text detected");
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                tvStatusMessage.setText(String.format("Error read text",
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void addScannedText(){
        String text=tvTextValue.getText().toString().trim();
        if (!TextUtils.isEmpty(text)){
            String id=databaseScannedText.push().getKey();
            ScannedText scannedText=new ScannedText(id,text);
            databaseScannedText.child(id).setValue(scannedText);
            Toast.makeText(this, "Text added to database!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Empty text!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateDialog(final String id,  String name){
        final AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.update_dialog,null);

        dialogBuilder.setView(dialogView);
        final EditText editText_textForUpdate=dialogView.findViewById(R.id.editText_updateDialog);
        final Button button_update=dialogView.findViewById(R.id.button_update);
        final Button button_cancel=dialogView.findViewById(R.id.button_Cancel);

        dialogBuilder.setTitle("Updating text: "+name);
        final AlertDialog alertDialog=dialogBuilder.create();
        alertDialog.show();

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=editText_textForUpdate.getText().toString().trim();
                if(!TextUtils.isEmpty(text)){
                    updateScannedText(id,text);
                    alertDialog.dismiss();
                }else{
                    Toast.makeText(MainActivity.this, "Empty Text!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private boolean updateScannedText(String id,String text) {
        DatabaseReference dR=FirebaseDatabase.getInstance().getReference("ScannedTexts").child(id);
        
        ScannedText scannedText=new ScannedText(id,text);
        
        dR.setValue(scannedText);
        Toast.makeText(this, "Text updated sucessfully", Toast.LENGTH_SHORT).show();
        return true;
    }

} //end of activity
