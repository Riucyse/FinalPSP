package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class OutputGetIndividualMessage {

    @SerializedName("from")
    public String from;
    @SerializedName("to")
    public String to;
    @SerializedName("message")
    public String message;
    @SerializedName("image")
    public String image;
    @SerializedName("sent")
    public String sent;
    @SerializedName("_id")
    public String _id;

    public OutputGetIndividualMessage(String nuevoFrom, String nuevoTo, String nuevoMessage, String nuevaImage,
                                      String nuevoSent, String nuevoId){
        this.from = nuevoFrom;
        this.to = nuevoTo;
        this.message = nuevoMessage;
        this.image = nuevaImage;
        this.sent = nuevoSent;
        this._id = nuevoId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
