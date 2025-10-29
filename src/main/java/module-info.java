module com.example.cafeteriajavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cafeteriajavafx to javafx.fxml;
    exports com.example.cafeteriajavafx;
}