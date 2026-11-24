package com.psychemap.ui.controller;

import com.psychemap.security.dto.UsuarioDTO;
import com.psychemap.service.IAtlasService;
import com.psychemap.service.IAuditService;
import com.psychemap.service.IAuthService;
import com.psychemap.service.db.DatabaseAtlasService;
import com.psychemap.service.db.DatabaseAuditService;
import com.psychemap.service.db.DatabaseAuthService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public class SceneManager {

    private static final IAuthService authService = new DatabaseAuthService();
    private static final IAtlasService atlasService = new DatabaseAtlasService();
    private static final IAuditService auditService = new DatabaseAuditService();

    public static IAuthService getAuthService() {
        return authService;
    }

    public static IAtlasService getAtlasService() {
        return atlasService;
    }

    public static IAuditService getAuditService() {
        return auditService;
    }

    private static UsuarioDTO usuarioActual;

    public static UsuarioDTO getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(UsuarioDTO u) {
        usuarioActual = u;
    }

    public static final String IP_LOCAL = "127.0.0.1";

    public static String nombreAmigableRol(String rol) {
        return switch (rol) {
            case UsuarioDTO.ROL_CLINICO -> "Psicólogo clínico";
            case UsuarioDTO.ROL_INVESTIGADOR -> "Investigador";
            case UsuarioDTO.ROL_ADMIN -> "Administrador";
            default -> rol;
        };
    }

    public static void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private static Stage stage;
    private static Scene scene;

    public static void iniciar(Stage ventana) {
        stage = ventana;
        stage.setTitle("PsycheMap");
        mostrarLogin();
        stage.show();
    }

    public static void mostrarLogin() {
        usuarioActual = null;
        cambiarPantalla("login.fxml", 960, 580);
    }

    public static void mostrarPrincipal() {
        cambiarPantalla("main_layout.fxml", 1200, 720);
    }

    private static void cambiarPantalla(String archivoFxml, double ancho, double alto) {
        try {
            URL url = SceneManager.class.getResource("/fxml/" + archivoFxml);
            if (url == null) {
                throw new IllegalStateException("No se encontró /fxml/" + archivoFxml);
            }
            Parent raiz = new FXMLLoader(url).load();

            if (scene == null) {
                scene = new Scene(raiz, ancho, alto);
                scene.getStylesheets().add(
                        SceneManager.class.getResource("/css/estilos.css").toExternalForm());
                stage.setScene(scene);
            } else {
                scene.setRoot(raiz);
                stage.setWidth(ancho);
                stage.setHeight(alto);
            }
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new UncheckedIOException("Error cargando " + archivoFxml, e);
        }
    }
}
