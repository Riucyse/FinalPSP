package riucyse.pspfinal;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import com.google.gson.Gson;

public class LoginController {

    public TextField fieldUser;
    public Hyperlink hyperlinkCrearCuenta;
    public Button botonLogin;
    public TextField fieldPassword;

    public void pulsarLogin(ActionEvent actionEvent) {
        try{
            String json = ServiceUtils.getResponse("http://127.0.0.1:8081/ejercicioFinal/messages", null, "GET");
            Gson gson = new Gson();
            OutputRechazado a = gson.fromJson(json, OutputRechazado.class);
            System.out.println(a.toString());
            System.out.println(json);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void pulsarCrearCuenta(ActionEvent actionEvent) {
        try{
            Stage estaStage = (Stage) botonLogin.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("register.fxml"));
            Scene scene = new Scene(loader.load(), 500, 300);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Register");
            estaStage.close();
            stage.show();
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
