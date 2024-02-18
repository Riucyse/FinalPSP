package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class OutputLogin {

    @SerializedName("access")
    public String access = "null";
    @SerializedName("name")
    public String name = "null";
    @SerializedName("token")
    public String token = "null";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String toString(){
        return "{\n    access: " + access + "\n    token: " + token + "\n    name: " + name + "\n    image: " + image +
                "\n    error: " + error + "\n}";
    }
}
