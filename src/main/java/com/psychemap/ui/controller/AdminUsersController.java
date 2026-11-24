package com.psychemap.ui.controller;

import com.psychemap.security.dto.UsuarioDTO;
import com.psychemap.service.IAuthService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.StringConverter;

public class AdminUsersController {

    @FXML private TextField tfUsuario;
    @FXML private TextField tfCorreo;
    @FXML private PasswordField pfClave;
    @FXML private ComboBox<String> cmbRol;
    @FXML private CheckBox chkLectura;
    @FXML private CheckBox chkEscritura;
    @FXML private CheckBox chkExportacion;
    @FXML private TableView<UsuarioDTO> tablaUsuarios;
    @FXML private TableColumn<UsuarioDTO, String> colUsuario;
    @FXML private TableColumn<UsuarioDTO, String> colCorreo;
    @FXML private TableColumn<UsuarioDTO, String> colRol;
    @FXML private TableColumn<UsuarioDTO, String> colEstado;
    @FXML private TableColumn<UsuarioDTO, Void> colAcciones;

    private final IAuthService auth = SceneManager.getAuthService();

    @FXML
    private void initialize() {
        cmbRol.getItems().addAll(UsuarioDTO.ROL_CLINICO, UsuarioDTO.ROL_INVESTIGADOR, UsuarioDTO.ROL_ADMIN);
        cmbRol.setConverter(new StringConverter<>() {
            @Override public String toString(String rol) {
                return rol == null ? "" : SceneManager.nombreAmigableRol(rol);
            }
            @Override public String fromString(String texto) { return texto; }
        });
        cmbRol.setValue(UsuarioDTO.ROL_CLINICO);
        chkLectura.setSelected(true);

        colUsuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreUsuario()));
        colCorreo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCorreoElectronico()));
        colRol.setCellValueFactory(d -> new SimpleStringProperty(SceneManager.nombreAmigableRol(d.getValue().getNombreRol())));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isCuentaBloqueada() ? "Baja lógica" : "Activo"));

        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean vacia) {
                super.updateItem(estado, vacia);
                if (vacia || estado == null) {
                    setGraphic(null);
                    return;
                }
                Label insignia = new Label(estado);
                insignia.getStyleClass().addAll("badge", "Activo".equals(estado) ? "badge-verde" : "badge-carmin");
                setGraphic(insignia);
            }
        });

        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void sinDato, boolean vacia) {
                super.updateItem(sinDato, vacia);
                UsuarioDTO u = (vacia || getTableRow() == null) ? null : getTableRow().getItem();
                setGraphic(u == null ? null : botonesDe(u));
            }
        });

        tablaUsuarios.setPlaceholder(new Label("Sin cuentas"));
        recargarTabla();
    }

    private HBox botonesDe(UsuarioDTO u) {
        boolean bloqueada = u.isCuentaBloqueada();

        Button btnEstado = new Button(bloqueada ? "Reactivar" : "Suspender");
        btnEstado.getStyleClass().addAll("boton-chico", bloqueada ? "boton-verde" : "boton-peligro");
        boolean esMiCuenta = u.getNombreUsuario().equals(SceneManager.getUsuarioActual().getNombreUsuario());
        btnEstado.setDisable(esMiCuenta);
        btnEstado.setOnAction(e -> cambiarEstado(u));

        Button btnReiniciar = new Button("Reiniciar acceso");
        btnReiniciar.getStyleClass().addAll("boton-chico", "boton-suave");
        btnReiniciar.setOnAction(e -> reiniciarAcceso(u));

        btnEstado.setMinWidth(Region.USE_PREF_SIZE);
        btnReiniciar.setMinWidth(Region.USE_PREF_SIZE);

        HBox caja = new HBox(8, btnEstado, btnReiniciar);
        caja.setAlignment(Pos.CENTER_LEFT);
        return caja;
    }

    private void cambiarEstado(UsuarioDTO u) {
        boolean bloquear = !u.isCuentaBloqueada();
        auth.cambiarBloqueo(u.getId(), bloquear);
        registrar(bloquear ? "BAJA_LOGICA" : "MODIFICACION",
                (bloquear ? "Suspendió" : "Reactivó") + " cuenta " + u.getNombreUsuario());
        recargarTabla();
    }

    private void reiniciarAcceso(UsuarioDTO u) {
        auth.reiniciarAcceso(u.getId());
        registrar("MODIFICACION", "Reinició acceso de " + u.getNombreUsuario());
        SceneManager.alerta(Alert.AlertType.INFORMATION, "Acceso reiniciado",
                "Se envió un enlace de reinicio a " + u.getCorreoElectronico() + " (simulado).");
    }

    @FXML
    private void onCrear() {
        String usuario = tfUsuario.getText().trim();
        String correo = tfCorreo.getText().trim();
        String clave = pfClave.getText();

        if (usuario.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
            SceneManager.alerta(Alert.AlertType.WARNING, "Datos incompletos",
                    "Completa usuario, correo y contraseña inicial.");
            return;
        }
        if (!correo.contains("@")) {
            SceneManager.alerta(Alert.AlertType.WARNING, "Correo no válido",
                    "El correo electrónico debe contener '@'.");
            return;
        }

        UsuarioDTO nuevo = new UsuarioDTO(0, usuario, correo, false, cmbRol.getValue(),
                chkLectura.isSelected(), chkEscritura.isSelected(), chkExportacion.isSelected());

        try {
            auth.crearUsuario(nuevo, clave);
            registrar("MODIFICACION", "Creó cuenta: " + usuario);
            limpiarFormulario();
            recargarTabla();
            SceneManager.alerta(Alert.AlertType.INFORMATION, "Cuenta creada",
                    "La cuenta \"" + usuario + "\" fue creada correctamente.");
        } catch (IllegalArgumentException e) {
            SceneManager.alerta(Alert.AlertType.WARNING, "No se pudo crear la cuenta", e.getMessage());
        }
    }

    private void registrar(String accion, String entidad) {
        UsuarioDTO admin = SceneManager.getUsuarioActual();
        SceneManager.getAuditService().registrar(admin.getNombreUsuario(), SceneManager.IP_LOCAL,
                "Usuarios", accion, entidad);
    }

    private void recargarTabla() {
        tablaUsuarios.setItems(FXCollections.observableArrayList(auth.listarUsuarios()));
    }

    private void limpiarFormulario() {
        tfUsuario.clear();
        tfCorreo.clear();
        pfClave.clear();
        cmbRol.setValue(UsuarioDTO.ROL_CLINICO);
        chkLectura.setSelected(true);
        chkEscritura.setSelected(false);
        chkExportacion.setSelected(false);
    }
}
