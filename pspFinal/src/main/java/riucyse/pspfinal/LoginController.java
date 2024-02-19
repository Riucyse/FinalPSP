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

        // Al iniciar esta vista, oculta el texto de eror (dejandolo vacio) y pone su texto en rojo
        textoErrorCredenciales.setText("");
        textoErrorCredenciales.setFill(Color.RED);
    }

    // Este metodo se activa cuando pulsas el boton de "Login"
    public void pulsarLogin(ActionEvent actionEvent) {

        /* Crea un nuevo modelo de JSON donde guarda los valores dentro de los textField de usuario y contraseña, pasa
           el modelo a JSON y usa el metodo POST para hacer login con las credenciales en JSON, guardando la respuesta
           en un modelo de respuesta de servidor de login. Revisa si la respuesta del servidor es que el login ha sido
           admitido, en cuyo caso añade a la clase estatica de los datos del usuario tanto el nombre, como el token como
           la imagen. Despues de esto, usa el metodo de abrir la vista, abriendo la vista de mensajeria
           Si el login no fue correcto, añade el texto de error indicando que el usuario o contraseña no fueron
           correctos */
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
                textoErrorCredenciales.setText("Incorrect user or password");
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }

    // Se activa cuando pulsas el hipervinculo "Crear cuenta"
    public void pulsarCrearCuenta(ActionEvent actionEvent) {

        // Simplemente llama al metodo para abrir una nueva vista, indicando que abra la vista de registro
        abrirVista("register.fxml");
    }

    private void abrirVista(String vistaQueAbrir){
        try{

            /* Este metodo recibe un parametro refiriendose a que vista hay que abrir
               Guarda en una variable la Stage actual, y crea tres parametros; el nombre de la ventana, altura y anchura
               Ajusta estos parametros según que ventana vaya a abrir.
               Crea una nueva ventana, poniendo todos los parametros anteriores, cierra la actual y abre la nueva
               ventana */
            Stage estaStage = (Stage) botonLogin.getScene().getWindow();
            String tituloDeLaVentana = "";
            int altura = 100;
            int anchura = 100;
            if(vistaQueAbrir.equals("mensajeria.fxml")){
                altura = 700;
                anchura = 600;
                tituloDeLaVentana = "Messenger";
            } else if(vistaQueAbrir.equals("register.fxml")){
                altura = 600;
                anchura = 200;
                tituloDeLaVentana = "Register";
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(vistaQueAbrir));
            Scene scene = new Scene(loader.load(), altura, anchura);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(tituloDeLaVentana);
            estaStage.close();
            stage.show();
        } catch (Exception e){
            System.out.println(e);
        }

    }
}