package com.example.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppCliente extends Application {

    private ChatController controller;

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

    @Override
    public void stop() throws Exception {
        if (controller != null) {
            controller.cerrarConexion();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}