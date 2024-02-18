package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class InputMessage {

    @SerializedName("message")
    public String message;
    @SerializedName("image")
    public String image;

    public InputMessage(String nuevoMessage, String nuevaImageEnBase64){
        this.message = nuevoMessage;
        this.image = nuevaImageEnBase64;
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
}
