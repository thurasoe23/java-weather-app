module org.example.weatherapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires org.json;


    opens org.example.weatherapp to javafx.fxml;
    exports org.example.weatherapp;
}