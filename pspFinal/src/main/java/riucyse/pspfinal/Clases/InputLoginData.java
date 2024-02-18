package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class InputLoginData {

    @SerializedName("name")
    public String user;
    @SerializedName("password")
    public String password;

    public InputLoginData(String nuevoUser, String nuevaPassword){
        this.user = nuevoUser;
        this.password = nuevaPassword;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
