package riucyse.pspfinal;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import riucyse.pspfinal.Clases.*;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MensajeriaController {

    // https://www.youtube.com/watch?v=MsgiJdf5njc
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
    public Button botonEnviarImagen;
    private final String SERVIDOR_URL = "http://127.0.0.1:8080/ejercicioFinal";
    private final Gson gson = new Gson();
    private String imageBase64AEnviar;

    public void initialize() {
        columnaTextoMensaje.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, String >("message"));
        columnaImagenMensaje.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, ImageView>("image"));
        columnaFechaEnviadaMensaje.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, String>("sent"));
        columnaEnviador.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, String>("from"));
        columnaFotoPerfilEnviador.setCellValueFactory(new PropertyValueFactory<ModeloMensaje, ImageView>("profileImage"));

        columnaFotoAvatar.setCellValueFactory(new PropertyValueFactory<ModeloUsuario, ImageView>("image"));
        columnaNombre.setCellValueFactory(new PropertyValueFactory<ModeloUsuario, String>("name"));

        ServiceUtils.setToken(datosUsuario.getToken());
        textoNombreUsuario.setText(datosUsuario.getName());
        String obtenerImagenPerfil = SERVIDOR_URL + "/profilePicture/" + datosUsuario.getName();
        String json = ServiceUtils.getResponse(obtenerImagenPerfil, null, "GET");
        OutputGetImagen intentoDeImagen = gson.fromJson(json, OutputGetImagen.class);
        ImageView nuevaImagen = base64AImagen(intentoDeImagen.getImage());
        Image imagen = nuevaImagen.getImage();
        imageviewImagenPerfil.setImage(imagen);

        imageBase64AEnviar = null;
        actualizarListaMensajes();
        actualizarUsuarios();
    }

    public void PulsarCambiarImagen(ActionEvent actionEvent) {
        try {
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

    public void PulsarRefrescar(ActionEvent actionEvent) {
        actualizarListaMensajes();
        actualizarUsuarios();
    }

    public void PulsarBorrarMensaje(ActionEvent actionEvent) {
        try{
            ModeloMensaje mensajeSeleccionado = tableviewMensajes.getSelectionModel().getSelectedItem();
            String url = SERVIDOR_URL + "/messages/" + mensajeSeleccionado.get_id();
            ServiceUtils.getResponse(url, null, "DELETE");
            actualizarListaMensajes();
        } catch(NullPointerException e) {
            System.out.println("No selecciono mensaje a borrar");
        }
    }

    public void PulsarSeleccionarImagen(ActionEvent actionEvent) {
        try{
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

    public void PulsarEnviarMensaje(ActionEvent actionEvent) {
        try{
            ModeloUsuario usuario = tableviewUsuarios.getSelectionModel().getSelectedItem();
            System.out.println(usuario.get_id());
            if(!(textoMensajeAEnviar.getText().isEmpty())){
                InputMessage mensaje = new InputMessage(textoMensajeAEnviar.getText(), imageBase64AEnviar);
                String json = gson.toJson(mensaje, InputMessage.class);
                String url = SERVIDOR_URL + "/messages/" + usuario.get_id();
                ServiceUtils.getResponse(url, json, "POST");
            } else{
                System.out.println("Intenta enviar un mensaje vacio");
            }
        } catch(NullPointerException e){
            System.out.println("No selecciono usuario para enviar el mensaje");
        }
    }

    private ImageView base64AImagen(String imagenBase64) {
        try {
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
