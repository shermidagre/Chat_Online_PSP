package com.example.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main class for the client application.
 * This class is responsible for launching the JavaFX application
 * and managing the lifecycle of the chat client.
 */
public class AppCliente extends Application {

    private ChatController controller;

    /**
     * This method is called when the application should start, and it is responsible for
     * creating the primary stage and scene of the application.
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * @throws IOException if the fxml file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppCliente.class.getResource("/com/example/chat/client/chat-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 400, 500);

        // Guardamos referencia al controller para cerrar conexión al salir
        controller = fxmlLoader.getController();

        stage.setTitle("Cliente Chat Socket");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method is called when the application should stop, and it is responsible for
     * performing any necessary cleanup, such as closing the socket connection.
     *
     * @throws Exception if an error occurs during stopping.
     */
    @Override
    public void stop() throws Exception {
        if (controller != null) {
            controller.cerrarConexion();
        }
        super.stop();
    }

    /**
     * The main entry point for the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}