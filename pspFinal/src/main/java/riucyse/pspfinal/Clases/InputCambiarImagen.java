package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class InputCambiarImagen {

    @SerializedName("image")
    public String image;

    public InputCambiarImagen(String nuevaImagen){
        this.image = nuevaImagen;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String toString(){
        return "{\n    image: " + image + "\n}";
    }
}
