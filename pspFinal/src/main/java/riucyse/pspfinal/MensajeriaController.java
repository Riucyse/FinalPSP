package riucyse.pspfinal;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import riucyse.pspfinal.Clases.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MensajeriaController {

    private final DatosUsuario datosUsuario = DatosUsuario.obtenerInstancia();
    public Text textoNombreUsuario;
    public Button botonCambiarImagen;
    public ImageView imageviewImagenPerfil;
    public Button botonRefrescarMensajes;
    public TableView<ModeloMensaje> tableviewMensajes;
    public TableColumn<ModeloMensaje, String> columnaTextoMensaje;
    public TableColumn<ModeloMensaje, ImageView> columnaImagenMensaje;
    public TableColumn<ModeloMensaje, String> columnaFechaEnviadaMensaje;
    public TableColumn<ModeloMensaje, String> columnaEnviador;
    public TableColumn<ModeloMensaje, ImageView> columnaFotoPerfilEnviador;
    public Button botonBorrarMensaje;
    public TableView<ModeloUsuario> tableviewUsuarios;
    public TableColumn<ModeloUsuario, ImageView> columnaFotoAvatar;
    public TableColumn<ModeloUsuario, String> columnaNombre;
    public ImageView imageviewFotoAEnviar;
    public TextField textoMensajeAEnviar;
    public Button botonSeleccionarImagen;
    private final String SERVIDOR_URL = "http://127.0.0.1:8080/ejercicioFinal";
    private final Gson gson = new Gson();
    private String imageBase64AEnviar;

    public void initialize() {

        /* Al iniciar esta vista, primero ajusta los valores que van a entrar dentro de los dos TableView, y sus
           respectivos TableColumns uno a uno, despues de esto, hace que ambos TableView se ajusten al tamaño actual de
           la pantalla.
           Despues de esto, añade al ServiceUtils el token del registro almacenado en los datos de usuario de la clase
           estatica, al igual que ajusta el nombre del usuario de la misma forma, y recoge la imagen que tiene, y pide
           la imagen en Base64 al servidor a traves del metodo GET, almacenanndolo en una clase modelo de la respuesta
           del servidor en JSON, llama al metodo para transformar una imagen en Base64 a Imageview, obtiene la imagen
           del ImageView y la coloca a la imagen de perfil del usuario.
           Una vez t0do colocado, inicializa la variable del String de la imagen en Base64 para enviar al servidor con
           un String vacio, y actualiza la lista de mensajes y usuarios llamando a sus respectivos metodos. */
        columnaTextoMensaje.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, String >("message"));
        columnaImagenMensaje.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, ImageView>("image"));
        columnaFechaEnviadaMensaje.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, String>("sent"));
        columnaEnviador.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, String>("from"));
        columnaFotoPerfilEnviador.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, ImageView>("profileImage"));
        columnaFotoAvatar.setCellValueFactory(new PropertyValueFactory<ModeloUsuario, ImageView>("image"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<ModeloUsuario, String>("name"));
        tableviewMensajes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableviewUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ServiceUtils.setToken(datosUsuario.getToken());
        textoNombreUsuario.setText(datosUsuario.getName());
        String obtenerImagenPerfil = SERVIDOR_URL + "/profilePicture/" + datosUsuario.getName();
        String json = ServiceUtils.getResponse(obtenerImagenPerfil, null, "GET");
        OutputGetImagen intentoDeImagen = gson.fromJson(json, OutputGetImagen.class);
        ImageView nuevaImagen = base64AImagen(intentoDeImagen.getImage());
        Image imagen = nuevaImagen.getImage();
        imageviewImagenPerfil.setImage(imagen);

        imageBase64AEnviar = "";
        actualizarListaMensajes();
        actualizarUsuarios();
    }

    // Este metodo se activa cuando pulsas al boton "Cambiar imagen" de la imagen de perfil
    public void PulsarCambiarImagen(ActionEvent actionEvent) {
        try {

            /* Abre un FileChooser, cambiando el titulo y haciendo que solo abra PNGs y JPGs, y muestra el FileChooser,
               guardando la imagen seleccionada en una variable.
               Despues de esto, crea un FileInputStream de la imagen seleccionada y un array de Bytes de la longitud de
               la imagen, y le aplica todos los Bytes al FileInputStream.
               Despues de esto, cambia la imagen de perfil de la vista por la imagen seleccionada
               Crea un nuevo String donde almacenara la imagen en Base64 que se codifica en el momento.
               Despues, crea un nuevo modelo de JSON para poder enviar la imagen codificada en Base64 en JSON al
               servidor, y le envia el cambio de imagen al servidor con el metodo PUT */
            FileChooser fc = new FileChooser();
            fc.setTitle("Select your profile photo");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg"));
            File imagen = fc.showOpenDialog(new Stage());

            FileInputStream fileInputStream = new FileInputStream(imagen);
            byte[] bytes = new byte[(int) imagen.length()];
            fileInputStream.read(bytes);

            imageviewImagenPerfil.setImage(new Image("file:" + imagen.getAbsolutePath()));

            String nuevaImagenBase64 = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
            InputCambiarImagen inputCambiarImagen = new InputCambiarImagen(nuevaImagenBase64);
            String json = gson.toJson(inputCambiarImagen);
            String url = SERVIDOR_URL + "/users";
            ServiceUtils.getResponse(url, json, "PUT");
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Este metodo se activa cuando pulsas el boton "Refresh" del menu de mensajes
    public void PulsarRefrescar(ActionEvent actionEvent) {

        // Llama a los dos metodos para actualizar la lista de mensajes y la lista de usuarios
        actualizarListaMensajes();
        actualizarUsuarios();
    }

    // Este metodo se activa cuando pulsas el boton "Delete message" del menu de mensajes
    public void PulsarBorrarMensaje(ActionEvent actionEvent) {
        try{

            /* Obtiene el objeto del mensaje que se esta seleccionando, y recoge su ID, para hacer una peticion "DELETE"
               al servidor sobre la id del mensaje. Despues de esto, se actualiza la lista de mensajes
               Si no ha seleccionado ningun mensaje, le salta un alert indicando que ha de seleccionar un mensaje antes
               de poder borrarlo */
            ModeloMensaje mensajeSeleccionado = tableviewMensajes.getSelectionModel().getSelectedItem();
            String url = SERVIDOR_URL + "/messages/" + mensajeSeleccionado.get_id();
            ServiceUtils.getResponse(url, null, "DELETE");
            actualizarListaMensajes();
        } catch(NullPointerException e) {
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setHeaderText("Information");
            error.setContentText("You have to select a message to delete it");
            error.showAndWait();
        }
    }

    // Este metodo se activa cuando pulsas el boton de "Select image" en el menu de enviar mensaje
    public void PulsarSeleccionarImagen(ActionEvent actionEvent) {
        try{

            /* Abre un nuevo FileChooser, poniendo su titulo y haciendo que solo pueda abrir PNGs y JPGs, despues de
               esto guarda el archivo en un objeto File.
               Despues, crea un FileInputStream con la imagen seleccionada, se crea un array de Bytes y se rellenan con
               los bytes de la imagen.
               A continuacion, se codifica el array de Bytes en Base64 y se guarda en la variable de la imagen en Base64
               a enviar.
               Por ultimo, cambia la imagen del ImageView de la imagen a enseñar que se va a enviar por la imagen
               seleccionada */
            FileChooser fc = new FileChooser();
            fc.setTitle("Select your photo");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg"));
            File imagen = fc.showOpenDialog(new Stage());

            FileInputStream fileInputStream = new FileInputStream(imagen);
            byte[] bytes = new byte[(int)imagen.length()];
            fileInputStream.read(bytes);
            imageBase64AEnviar = new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
            imageviewFotoAEnviar.setImage(new Image("file:" + imagen.getAbsolutePath()));
        } catch(Exception e){
            System.out.println(e);
        }
    }

    // Este metodo se activa cuando pulsas el boton "Send message" del menu de enviar mensaje
    public void PulsarEnviarMensaje(ActionEvent actionEvent) {
        try{

            /* Primero, revisa si el usuario ha seleccionado otro usuario al que enviar el mensaje, si no lo ha hecho,
               saltara un Alert informando que ha de seleccionar un usuario al que enviar el mensaje.
               Despues de esto revisara si ha escrito un mensaje, y si no, saltara otro Alert informando que ha de
               escribir un mensaje para poder enviarlo.
               Una vez tiene seleccionado el usuario y escrito el mensaje, crea un nuevo objeto modelo de JSON donde
               guardara el mensaje y la imagen, y entonces revisa si hay una imagen. Si la hay, creara el objeto con el
               texto y la imagen en Base64, si no hay imagen, el campo de imagen sera NULL.
               A continuacion obtendremos el mensaje en JSON y usaremos el metodo "POST" para enviar el mensaje.
               Por ultimo, limpia el TextField del texto del mensaje, quita la imagen y limpia el String donde iba a
               estar la imagen en Base64 */
            ModeloUsuario usuario = tableviewUsuarios.getSelectionModel().getSelectedItem();
            if(!(textoMensajeAEnviar.getText().isEmpty())){
                InputMessage mensaje;
                if(imageBase64AEnviar.isEmpty()){
                    mensaje = new InputMessage(textoMensajeAEnviar.getText(), null);
                } else{
                    mensaje = new InputMessage(textoMensajeAEnviar.getText(), imageBase64AEnviar);
                }
                String json = gson.toJson(mensaje, InputMessage.class);
                String url = SERVIDOR_URL + "/messages/" + usuario.get_id();
                ServiceUtils.getResponse(url, json, "POST");
                textoMensajeAEnviar.setText("");
                imageBase64AEnviar = "";
                imageviewFotoAEnviar.setImage(null);
            } else{
                Alert error = new Alert(Alert.AlertType.INFORMATION);
                error.setHeaderText("Information");
                error.setContentText("You have to write a message before sending it");
                error.showAndWait();
            }
        } catch(NullPointerException e){
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setHeaderText("Information");
            error.setContentText("You have to select a user to send the message");
            error.showAndWait();
        }
    }

    private ImageView base64AImagen(String imagenBase64) {
        try {

            /* Este metodo recibe un String, que debe ser una imagen en Base64.
               Crea un nuevo array de Bytes, los cuales rellena con los Bytes decodificados del String de la imagen en
               Base64, con el cual crea un nuevo BufferedImage con los Bytes.
               Entonces crea una nueva WritableImage con las dimensiones de la BufferedImage, y creamos una ultima clase
               PixelWriter, la cual es la WritableImage que le pasa el PixelWriter, el cual le permitira cambiar la
               imagen (ahora mismo, vacia).
               Una vez tenemos las herramientas preparadas, creamos dos bucles; Uno que recorre cada fila, que dentro
               tiene otro bucle que recorre cada columna, y pinta cada pixel de la imagen vacia con cada color que
               tiene la BufferedImage.
               Una vez hecho esto, creamos una nueva ImageView con la WritableImage, y la devolvemos
               Si durante el proceso sucede cualquier error, se devolvera NULL */
            byte[] byteImagen = Base64.getDecoder().decode(imagenBase64);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteImagen));
            WritableImage writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            PixelWriter pixelWriter = writableImage.getPixelWriter();
            for (int x = 0; x < bufferedImage.getWidth(); x += 1) {
                for (int y = 0; y < bufferedImage.getHeight(); y += 1) {
                    pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }
            ImageView nuevaImageView = new ImageView(writableImage);
            return nuevaImageView;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    private void actualizarListaMensajes() {

        /* Primero limpio los objetos que hay en la TableView, para pedirle los mensajes al servidor a través del
           metodo "GET", los cuales los guardare en un objeto modelo del JSON, para hacer un List de los mensajes
           individuales que ha traido el servidor.
           A continuacion, a cada mensaje individual de la List donde se almacenan obtengo los datos del propio mensaje,
           de quien lo envio, de que fecha, la imagen del enviador y la imagen adjunta si hay.
           Para las imagenes, lo que se obtiene es el nombre del archivo en el servidor. Aprovechando este nombre, uso
           el metodo "GET" del servidor para pedirle la imagen en Base64, recibiendo el JSON en un String, que paso
           previamente a su clase modelo, para obtener el String de la imagen en Base64, y pasarla por el metodo que me
           devolvera el ImageView, para añadirla al objeto.
           En el caso de la imagen adjunta, antes revisara si hay una. Si no la hay, pondra un NULL a la imagen adjunta,
           haciendo que no cargue ninguna imagen en el TableView
           Despues de t0do esto, añado el objeto al TablewView */
        tableviewMensajes.getItems().clear();
        String url = SERVIDOR_URL + "/messages";
        String json = ServiceUtils.getResponse(url, null, "GET");
        OutputGetMessages outputGetMessages = gson.fromJson(json, OutputGetMessages.class);
        List<OutputGetIndividualMessage> listaMensajes = outputGetMessages.getMessages();
        listaMensajes.forEach(OutputGetIndividualMessage -> {
            ImageView imagenDeMensaje = null;
            ImageView imagenDePerfilEnviador = null;
            if (OutputGetIndividualMessage.getImage() != null) {
                String url2 = SERVIDOR_URL + "/messagePicture/" + OutputGetIndividualMessage.getImage();
                String jsonImagen = ServiceUtils.getResponse(url2, null, "GET");
                OutputGetImagen outputGetImagen = gson.fromJson(jsonImagen, OutputGetImagen.class);
                imagenDeMensaje = base64AImagen(outputGetImagen.getImage());
                imagenDeMensaje.setFitHeight(30);
                imagenDeMensaje.setPreserveRatio(true);
            }
            String url3 = SERVIDOR_URL + "/profilePicture/" + OutputGetIndividualMessage.getFrom();
            String jsonImagenPerfil = ServiceUtils.getResponse(url3, null, "GET");
            OutputGetImagen outputGetImagen2 = gson.fromJson(jsonImagenPerfil, OutputGetImagen.class);
            imagenDePerfilEnviador = base64AImagen(outputGetImagen2.getImage());
            imagenDePerfilEnviador.setFitHeight(30);
            imagenDePerfilEnviador.setPreserveRatio(true);
            ModeloMensaje mensajeNuevo = new ModeloMensaje(OutputGetIndividualMessage.getFrom(),
                    OutputGetIndividualMessage.getMessage(), imagenDeMensaje, OutputGetIndividualMessage.getSent(),
                    imagenDePerfilEnviador, OutputGetIndividualMessage.get_id());
            tableviewMensajes.getItems().add(mensajeNuevo);
        });
    }

    private void actualizarUsuarios(){

        /* Primero limpia la lista de usuarios, para despues, recibir el objeto JSON de la peticion "GET" al servidor
           de la lista de usuarios que hay en la base de datos.
           Despues de esto, pasa el String del JSON a su clase modelo, para poder recibir la lista de los usuarios
           registrados en la base de datos en un List.
           A continuacion, por cada usuario individual de la List se busca su imagen de perfil con el metodo "GET",
           recibiendo el JSON en un String, que sera previamente pasado a su clase modelo para poder extraer la imagen
           en Base64, y asi llamar al metodo que transforma el String de Base64 en una ImageView, y poder añadirla junto
           al nombre de usuario al objeto usuario individual, que posteriormente se añade a la TableView */
        tableviewUsuarios.getItems().clear();
        String url = SERVIDOR_URL + "/users";
        String json = ServiceUtils.getResponse(url, null, "GET");
        OutputGetUsers outputGetUsers = gson.fromJson(json, OutputGetUsers.class);
        List<OutputGetIndividualUser> listaUsuarios = outputGetUsers.getUsers();
        listaUsuarios.forEach(OutputGetIndividualUser -> {
            ImageView imagenDeUsuario = null;
            String url2 = SERVIDOR_URL + "/messagePicture/" + OutputGetIndividualUser.getImage();
            String jsonImagenPerfil = ServiceUtils.getResponse(url2, null, "GET");
            OutputGetImagen outputGetImagen = gson.fromJson(jsonImagenPerfil, OutputGetImagen.class);
            imagenDeUsuario = base64AImagen(outputGetImagen.getImage());
            imagenDeUsuario.setFitHeight(30);
            imagenDeUsuario.setPreserveRatio(true);
            ModeloUsuario nuevoUsuario = new ModeloUsuario(OutputGetIndividualUser.get_id(), OutputGetIndividualUser.getName(),
                    OutputGetIndividualUser.getPassword(), imagenDeUsuario);
            tableviewUsuarios.getItems().add(nuevoUsuario);
        });
    }
}