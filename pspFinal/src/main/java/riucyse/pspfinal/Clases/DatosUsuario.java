package riucyse.pspfinal.Clases;

public class DatosUsuario {

    private static final DatosUsuario instancia = new DatosUsuario();
    private String name;
    private String token;
    private String image;

    private DatosUsuario(){}

    public static DatosUsuario obtenerInstancia(){
        return instancia;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String toString(){
        return "{\n    name: " + name + "\n    image: " + image + "\n    token: " + token + "\n}";
    }
}
