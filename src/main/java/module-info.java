module com.example.projekt_poc {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;


    opens com.example.projekt_poc to javafx.fxml;
    exports com.example.projekt_poc;
}