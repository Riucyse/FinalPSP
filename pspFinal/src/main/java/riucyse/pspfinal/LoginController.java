package riucyse.pspfinal;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.google.gson.Gson;
import riucyse.pspfinal.Clases.DatosUsuario;
import riucyse.pspfinal.Clases.InputLoginData;
import riucyse.pspfinal.Clases.OutputLogin;

public class LoginController {

    public TextField fieldUser;
    public Hyperlink hyperlinkCrearCuenta;
    public Button botonLogin;
    public TextField fieldPassword;
    public Text textoErrorCredenciales;
    private final Gson gson = new Gson();
    private final String SERVIDOR_URL = "http://127.0.0.1:8080/ejercicioFinal/login";
    private DatosUsuario datosUsuario = DatosUsuario.obtenerInstancia();


    public void initialize(){
        textoErrorCredenciales.setText("");
        textoErrorCredenciales.setFill(Color.RED);
    }

    public void pulsarLogin(ActionEvent actionEvent) {
        try{
            InputLoginData ld = new InputLoginData(fieldUser.getText(), fieldPassword.getText());
            String jsonToServer = gson.toJson(ld);
            String json = ServiceUtils.getResponse(SERVIDOR_URL, jsonToServer, "POST");
            OutputLogin intentoDeLogin = gson.fromJson(json, OutputLogin.class);
            if(intentoDeLogin.getAccess().equals("admitted")){
                datosUsuario.setName(intentoDeLogin.getName());
                datosUsuario.setToken(intentoDeLogin.getToken());
                datosUsuario.setImage(intentoDeLogin.getImage());
                abrirVista("mensajeria.fxml");
            } else{
                textoErrorCredenciales.setText("Usuario o contraseña incorrectos");
            }
            System.out.println(intentoDeLogin.access);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void pulsarCrearCuenta(ActionEvent actionEvent) {
        abrirVista("register.fxml");
    }

    private void abrirVista(String vistaQueAbrir){
        try{
            Stage estaStage = (Stage) botonLogin.getScene().getWindow();
            int altura = 100;
            int anchura = 100;
            if(vistaQueAbrir.equals("mensajeria.fxml")){
                altura = 700;
                anchura = 600;
            } else if(vistaQueAbrir.equals("register.fxml")){
                altura = 600;
                anchura = 200;
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(vistaQueAbrir));
            Scene scene = new Scene(loader.load(), altura, anchura);
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
