package riucyse.pspfinal;

import com.google.gson.annotations.SerializedName;

public class OutputRechazado {

    @SerializedName("access")
    public String access;
    @SerializedName("error")
    public String error;

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String toString(){
        return "Acceso " + this.access + "\nError " + this.error;
    }
}
