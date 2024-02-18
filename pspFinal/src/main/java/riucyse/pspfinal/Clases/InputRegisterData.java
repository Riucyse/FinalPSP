package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class InputRegisterData {

    @SerializedName("name")
    public String name = "null";
    @SerializedName("password")
    public String password = "null";
    @SerializedName("image")
    public String image = "null";

    public InputRegisterData(String nuevoName, String nuevaPassword, String nuevaImageEnBase64){
        this.name = nuevoName;
        this.password = nuevaPassword;
        this.image = nuevaImageEnBase64;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}