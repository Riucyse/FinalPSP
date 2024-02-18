package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

public class OutputRegister {
    //error, result, access
    @SerializedName("access")
    public String access;
    @SerializedName("error")
    public String error;
    @SerializedName("result")
    public String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String toString(){
        return "{\naccess: " + access + "\nresult: " + result + "\nerror: " + error + "\n}";
    }
}
