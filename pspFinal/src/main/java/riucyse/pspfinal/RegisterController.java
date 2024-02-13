package riucyse.pspfinal;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class RegisterController {


    public TextField campoUsuario;
    public TextField campoPassword;
    public TextField campoConfirmarPassword;
    public Button botonCancelar;
    public Button botonRegister;
    public Button botonSeleccionarImagen;
    public ImageView imagenPerfil;

    public void pulsarCancelar(ActionEvent actionEvent) {
        try{
            Stage estaStage = (Stage) botonCancelar.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load(), 320, 240);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Login");
            estaStage.close();
            stage.show();
        } catch(Exception e){
            System.out.println(e);
        }

    }

    public void pulsarRegistrar(ActionEvent actionEvent) {
    }

    public void pulsarSeleccionarImagen(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select your profile photo");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg"));
        fc.showOpenDialog(new Stage());
    }
}
