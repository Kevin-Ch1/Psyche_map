package com.psychemap.ui.controller;

import com.psychemap.security.dto.UsuarioDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;

public class MainLayoutController {

    @FXML private Button btnAtlas;
    @FXML private Button btnUsuarios;
    @FXML private Button btnBitacora;
    @FXML private Label lblNotaRol;
    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private Label lblNombre;
    @FXML private Label lblRol;
    @FXML private Label lblAvatar;
    @FXML private StackPane contenido;

    private Button botonActivo;

    @FXML
    private void initialize() {
        UsuarioDTO u = SceneManager.getUsuarioActual();

        lblNombre.setText(u.getNombreUsuario());
        lblRol.setText(SceneManager.nombreAmigableRol(u.getNombreRol()));
        lblAvatar.setText(u.getNombreUsuario().substring(0, 2).toUpperCase());

        mostrar(btnAtlas, false);
        mostrar(btnUsuarios, false);
        mostrar(btnBitacora, false);

        switch (u.getNombreRol()) {
            case UsuarioDTO.ROL_CLINICO -> {
                mostrar(btnAtlas, true);
                lblNotaRol.setText("Acceso clínico al atlas nosológico. Sin auditoría ni configuración.");
                onAtlas();
            }
            case UsuarioDTO.ROL_INVESTIGADOR -> {
                mostrar(btnAtlas, true);
                lblNotaRol.setText("Lectura del atlas. Editar criterios requiere permiso de escritura.");
                onAtlas();
            }
            case UsuarioDTO.ROL_ADMIN -> {
                mostrar(btnUsuarios, true);
                mostrar(btnBitacora, true);
                lblNotaRol.setText("Solo gestión técnica. Sin acceso a datos clínicos.");
                onUsuarios();
            }
            default -> lblNotaRol.setText("Rol sin pantallas asignadas.");
        }
    }

    @FXML private void onAtlas() {
        abrirVista(btnAtlas, "atlas_view.fxml",
                "Atlas nosológico", "Lectura de criterios y síntomas");
    }

    @FXML private void onUsuarios() {
        abrirVista(btnUsuarios, "admin_users.fxml",
                "Gestión de usuarios", "Cuentas y permisos");
    }

    @FXML private void onBitacora() {
        abrirVista(btnBitacora, "admin_audit.fxml",
                "Bitácora de auditoría", "Registro de accesos y cambios");
    }

    @FXML private void onCerrarSesion() {
        UsuarioDTO u = SceneManager.getUsuarioActual();
        SceneManager.getAuditService().registrar(u.getNombreUsuario(), SceneManager.IP_LOCAL,
                "Acceso", "INICIO_SESION", "Cierre de sesión");
        SceneManager.mostrarLogin();
    }

    private void abrirVista(Button boton, String archivoFxml, String titulo, String subtitulo) {
        if (botonActivo != null) {
            botonActivo.getStyleClass().remove("nav-boton-activo");
        }
        botonActivo = boton;
        boton.getStyleClass().add("nav-boton-activo");

        lblTitulo.setText(titulo);
        lblSubtitulo.setText(subtitulo);

        contenido.getChildren().setAll(cargarNodo(archivoFxml));
    }

    private Node cargarNodo(String archivoFxml) {
        URL url = getClass().getResource("/fxml/" + archivoFxml);
        if (url == null) {
            return aviso("Pantalla pendiente de crear: " + archivoFxml);
        }
        try {
            return new FXMLLoader(url).load();
        } catch (Exception e) {
            e.printStackTrace();
            return aviso("Error al cargar " + archivoFxml + ": " + e.getMessage());
        }
    }

    private Node aviso(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("texto-gris");
        VBox caja = new VBox(etiqueta);
        caja.getStyleClass().add("card");
        return caja;
    }

    private void mostrar(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }
}
