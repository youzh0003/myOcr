package enzan_k.myocr.models;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import enzan_k.myocr.R;

/**
 * Created by Enzan on 11/21/2017.
 */

public class ScannedTextAdapter extends ArrayAdapter<ScannedText> {

    private Activity context;
    private List<ScannedText> scannedTextList;



    public ScannedTextAdapter(@NonNull Activity context, @NonNull List<ScannedText> objects) {
        super(context, R.layout.list_layout,objects);
        this.context=context;
        this.scannedTextList=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        View listViewItems=inflater.inflate(R.layout.list_layout,null,true);

        TextView textView=listViewItems.findViewById(R.id.textView_list_layout_item);

        ScannedText scannedText=scannedTextList.get(position);

        textView.setText(scannedText.getTextContent());

        return listViewItems;
    }
}
