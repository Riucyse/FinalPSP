package riucyse.pspfinal.Clases;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OutputGetMessages {
    @SerializedName("access")
    public String access;
    @SerializedName("messages")
    public List<OutputGetIndividualMessage> messages;

    public OutputGetMessages(String nuevoAccess, List<OutputGetIndividualMessage> nuevaListaDeMensajes){
        this.access = nuevoAccess;
        this.messages = nuevaListaDeMensajes;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public List<OutputGetIndividualMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<OutputGetIndividualMessage> messages) {
        this.messages = messages;
    }
}
