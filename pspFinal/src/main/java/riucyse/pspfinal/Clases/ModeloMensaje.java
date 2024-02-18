package riucyse.pspfinal.Clases;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ModeloMensaje {

    private String from;
    private String message;
    private ImageView image;
    private String sent;
    private ImageView profileImage;
    private String _id;

    public ModeloMensaje(){
        this.from = "";
        this.message = "";
        this.image = null;
        this.sent = "";
        this.profileImage = null;
        this._id = "";
    }

    public ModeloMensaje(String nuevoFrom, String nuevoMensaje, ImageView nuevaImagen, String nuevoSent,
                         ImageView nuevoProfileImage, String nuevoId){
        this.from = nuevoFrom;
        this.message = nuevoMensaje;
        this.image = nuevaImagen;
        this.sent = nuevoSent;
        this.profileImage = nuevoProfileImage;
        this._id = nuevoId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
