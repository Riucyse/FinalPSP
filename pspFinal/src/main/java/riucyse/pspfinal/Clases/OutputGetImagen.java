package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class OutputGetImagen {

    @SerializedName("access")
    public String access = "null";
    @SerializedName("image")
    public String image = "null";
    @SerializedName("error")
    public String error = "null";

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String toString(){
        return "{\n    access: " + access + "\n    image: " + image + "\n    error: " + error + "\n}";
    }
}
