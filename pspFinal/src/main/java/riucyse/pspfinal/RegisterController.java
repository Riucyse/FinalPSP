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

        // Al iniciar esta vista, escondo el texto de error (dejandolo vacio) y le pongo color rojo
        textoInformacion.setText("");
        textoInformacion.setFill(Color.RED);
    }

    /* El metodo se activa cuando pulsas el boton "Cancelar"
       Solo arranca el metodo volverARegistro(), que vuelve a la pantalla anterior */
    public void pulsarCancelar(ActionEvent actionEvent) {
        volverARegistro();
    }

    // El metodo se activa cuando pulsas el boton "Registrar"
    public void pulsarRegistrar(ActionEvent actionEvent) {

        /* Comprueba que todos los campos de nombre, contraseña, confirmar contraseña e imagen esten seleccionados
           Si no estan seleccionados, activa el texto de error indicando que faltan campos por rellenar */
        if(!campoUsuario.getText().isEmpty() && !campoPassword.getText().isEmpty() &&
                !campoConfirmarPassword.getText().isEmpty() && !imagenBase64.isEmpty()){

            /* Comprueba que los campos de contraseña y confirmar contraseña coincidan
               Si no, activa el texto de error indicando que la contraseña confirmada no coincide */
            if(campoPassword.getText().equals(campoConfirmarPassword.getText())){

                /* Al estar todos los campos comprobados correctamente, oculta el texto de error, crea un nuevo objeto
                de modelo de JSON con la informacion, lo pasa a JSON y hace el post en el servidor, guardando la
                respuesta del servidor en una variable con el modelo de la respuesta del servidor */
                textoInformacion.setText("");
                InputRegisterData rd = new InputRegisterData(campoUsuario.getText(), campoPassword.getText(), imagenBase64);
                String jsonToServer = gson.toJson(rd);
                String json = ServiceUtils.getResponse(SERVIDOR_URL, jsonToServer, "POST");
                OutputRegister intentoDeRegistro = gson.fromJson(json, OutputRegister.class);

                /* Vale, a ver, que lo he hecho raro, pero me ha funcionado bien igualmente, me explico:
                   Los strings por defecto son null, y al intentar conseguir un string, el cual es null como valor,
                   da un error, eso significa que NO ha habido error, y al intentar comparar un null valor con un null
                   string, da error, yendose al catch como demostracion del registro completado */
                try{

                    // Si el registro fallo por algún motivo, te muestra un error con ello
                    if(!(intentoDeRegistro.getError().equals("null"))){
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setHeaderText("Register error");
                        error.setContentText(intentoDeRegistro.getError());
                        error.showAndWait();
                    }
                } catch(Exception e){

                    /* Si el registro se completo, te muestra un Alert de informacion y te devuelve a la
                       pantalla de login llamando al metodo volverARegistro */
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

    // El metodo se activa cuando pulsas el boton "Seleccionar imagen"
    public void pulsarSeleccionarImagen(ActionEvent actionEvent) {

        /* Creo un nuevo FileChooser, le pongo el titulo "Select your profile photo", indico que solo puede abrir PNGs
           y JPGs y muestro el FileChooser.
           Despues de seleccionar la imagen, la almaceno en una variable tipo File, y creo un FileInputStream con el,
           a parte tambien creo una variable array de bytes con la longitud de la imagen, pongo todos los bytes de la
           imagen en el FileInputStream y paso a la variable imagenBase64 (String) la codificacion en Base64 de todos
           los bytes de la imagen del FileInputStream.
           Despues de t0do esto, muestro en la variable del icono de la imagen de perfil la imagen seleccionada */
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

    /* Este metodo crea una nueva Stage de LoginController, cambiando el titulo y tamaños, abre la pantalla y
       cierra esta */
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