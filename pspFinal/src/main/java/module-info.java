module riucyse.pspfinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;


    opens riucyse.pspfinal to javafx.fxml;
    exports riucyse.pspfinal;
    exports riucyse.pspfinal.Clases;
    opens riucyse.pspfinal.Clases to javafx.fxml;
}