package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OutputGetUsers {

    @SerializedName("access")
    public String access;
    @SerializedName("users")
    public List<OutputGetIndividualUser> users;

    public OutputGetUsers(String nuevoAccess, List<OutputGetIndividualUser> nuevosUsuarios){
        this.access = nuevoAccess;
        this.users = nuevosUsuarios;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public List<OutputGetIndividualUser> getUsers() {
        return users;
    }

    public void setUsers(List<OutputGetIndividualUser> users) {
        this.users = users;
    }
}