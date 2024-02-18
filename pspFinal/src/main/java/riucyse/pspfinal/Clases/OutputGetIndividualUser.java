package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class OutputGetIndividualUser {

    @SerializedName("_id")
    public String _id;
    @SerializedName("name")
    public String name;
    @SerializedName("password")
    public String password;
    @SerializedName("image")
    public String image;

    public OutputGetIndividualUser(String nuevoId, String nuevoName, String nuevaPassword, String nuevaImage){
        this._id = nuevoId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
