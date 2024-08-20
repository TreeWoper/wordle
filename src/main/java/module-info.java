module com.example.wordle {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens mainApp to javafx.fxml;
    exports mainApp;
}