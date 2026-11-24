package com.psychemap.ui.controller;

import com.psychemap.security.dto.UsuarioDTO;
import com.psychemap.service.AuthException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField tfUsuario;
    @FXML private PasswordField pfClave;

    private static final String IP_LOCAL = "127.0.0.1";

    @FXML
    private void onEntrar() {
        String nombre = tfUsuario.getText();
        String clave = pfClave.getText();

        try {
            UsuarioDTO usuario = SceneManager.getAuthService().login(nombre, clave);

            SceneManager.setUsuarioActual(usuario);
            SceneManager.getAuditService().registrar(usuario.getNombreUsuario(), IP_LOCAL,
                    "Acceso", "INICIO_SESION", "Inicio de sesión correcto");

            pfClave.clear();
            SceneManager.mostrarPrincipal();

        } catch (AuthException e) {
            if (nombre != null && !nombre.isBlank()) {
                SceneManager.getAuditService().registrar(nombre.trim(), IP_LOCAL,
                    "Acceso", "ACCESO_DENEGADO", "Intento de inicio de sesión fallido");
            }
            pfClave.clear();
            mostrarAlerta(e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("No se pudo iniciar sesión");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
