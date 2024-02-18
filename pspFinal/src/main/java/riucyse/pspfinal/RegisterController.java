package riucyse.pspfinal;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import riucyse.pspfinal.Clases.OutputRegister;
import riucyse.pspfinal.Clases.InputRegisterData;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RegisterController {


    public TextField campoUsuario;
    public TextField campoPassword;
    public TextField campoConfirmarPassword;
    public Button botonCancelar;
    public Button botonRegister;
    public Button botonSeleccionarImagen;
    public ImageView imagenPerfil;
    public Text textoInformacion;
    private String imagenBase64 = "";
    private final Gson gson = new Gson();
    private final String SERVIDOR_URL = "http://127.0.0.1:8080/ejercicioFinal/register";

    public void initialize(){
        textoInformacion.setText("");
        textoInformacion.setFill(Color.RED);
    }

    public void pulsarCancelar(ActionEvent actionEvent) {
        volverARegistro();
    }

    public void pulsarRegistrar(ActionEvent actionEvent) {
        if(!campoUsuario.getText().isEmpty() && !campoPassword.getText().isEmpty() &&
                !campoConfirmarPassword.getText().isEmpty() && !imagenBase64.isEmpty()){
            if(campoPassword.getText().equals(campoConfirmarPassword.getText())){
                textoInformacion.setText("");
                InputRegisterData rd = new InputRegisterData(campoUsuario.getText(), campoPassword.getText(), imagenBase64);
                String jsonToServer = gson.toJson(rd);
                String json = ServiceUtils.getResponse(SERVIDOR_URL, jsonToServer, "POST");
                OutputRegister intentoDeRegistro = gson.fromJson(json, OutputRegister.class);
                System.out.println(intentoDeRegistro);

                /* Vale, a ver, que lo he hecho raro, pero me ha funcionado bien igualmente, me explico:
                Los strings por defecto son null, y al intentar conseguir un string, el cual es null como valor, da un
                error, eso significa que NO ha habido error, y al intentar comparar un null valor con un null string,
                da error, yendose al catch como demostracion del registro completado */
                try{
                    if(!(intentoDeRegistro.getError().equals("null"))){
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setHeaderText("Register error");
                        error.setContentText(intentoDeRegistro.getError());
                        error.showAndWait();
                    }
                } catch(Exception e){
                    Alert informacion = new Alert(Alert.AlertType.INFORMATION);
                    informacion.setHeaderText("Register complete");
                    informacion.setContentText("User successfully created");
                    informacion.show();
                    volverARegistro();
                }

            } else{
                textoInformacion.setText("La confirmacion de contraseña no coincide");
            }
        } else{
            textoInformacion.setText("Faltan campos por rellenar");
        }
    }

    public void pulsarSeleccionarImagen(ActionEvent actionEvent) {
        try{
            FileChooser fc = new FileChooser();
            fc.setTitle("Select your profile photo");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg"));
            File imagen = fc.showOpenDialog(new Stage());

            FileInputStream fileInputStream = new FileInputStream(imagen);
            byte[] bytes = new byte[(int)imagen.length()];
            fileInputStream.read(bytes);
            imagenBase64 = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
            imagenPerfil.setImage(new Image("file:" + imagen.getAbsolutePath()));
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void volverARegistro(){
        try{
            Stage estaStage = (Stage) botonCancelar.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load(), 300, 150);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Login");
            estaStage.close();
            stage.show();
        } catch(Exception e){
            System.out.println(e);
        }
    }
}
