package co.edu.uniquindio.poo.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage escenario) throws Exception {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("/view/Monedero.fxml"));
        Scene escena = new Scene(cargador.load());
        escena.getStylesheets().add(getClass().getResource("/estilos.css").toExternalForm());

        escenario.setTitle("Monedero Plus");
        escenario.setMinWidth(900);
        escenario.setMinHeight(650);
        escenario.setScene(escena);
        escenario.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}