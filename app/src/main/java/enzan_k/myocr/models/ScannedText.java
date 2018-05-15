package enzan_k.myocr.models;

/**
 * Created by Enzan on 11/21/2017.
 */

public class ScannedText {
    private String textId;
    private String textContent;

    public ScannedText() {
    }

    public ScannedText(String textId, String textContent) {
        this.textId = textId;
        this.textContent = textContent;
    }

    public String getTextId() {
        return textId;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextId(String textId) {
        this.textId = textId;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}
