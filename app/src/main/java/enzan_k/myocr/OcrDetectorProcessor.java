package enzan_k.myocr;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

/**
 * vision.ocrreader.ui.camera.GraphicOverlay;
 * Created by Enzan on 11/9/2017.
 */

public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private static String allText;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        StringBuilder stringBuilder = new StringBuilder();
        allText="";
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();

        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                Log.d("Processor", "Text detected! " + item.getValue());
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n\n");
            }
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
            allText=stringBuilder.toString();
        }
    }
    public static String getAllText() {
        return allText;
    }
}
