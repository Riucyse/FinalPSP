package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;
import javafx.scene.image.ImageView;


public class ModeloUsuario {

    private String _id;

    private String name;

    private String password;

    private ImageView image;

    public ModeloUsuario(){
        this._id = "";
        this.name = "";
        this.password = "";
        this.image = null;
    }

    public ModeloUsuario(String nuevaId, String nuevoName, String nuevaPassword, ImageView nuevaImage){
        this._id = nuevaId;
        this.name = nuevoName;
        this.password = nuevaPassword;
        this.image = nuevaImage;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
