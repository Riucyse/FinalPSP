module riucyse.pspfinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens riucyse.pspfinal to javafx.fxml;
    exports riucyse.pspfinal;
}