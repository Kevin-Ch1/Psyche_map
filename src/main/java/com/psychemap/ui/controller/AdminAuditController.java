package com.psychemap.ui.controller;

import com.psychemap.audit.dto.AuditLogDTO;
import com.psychemap.security.dto.UsuarioDTO;
import com.psychemap.service.IAuditService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminAuditController {

    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    @FXML
    private ComboBox<String> cmbUsuario;
    @FXML
    private ComboBox<String> cmbAccion;
    @FXML
    private TableView<AuditLogDTO> tablaAuditoria;
    @FXML
    private TableColumn<AuditLogDTO, String> colFecha;
    @FXML
    private TableColumn<AuditLogDTO, String> colUsuario;
    @FXML
    private TableColumn<AuditLogDTO, String> colIp;
    @FXML
    private TableColumn<AuditLogDTO, String> colModulo;
    @FXML
    private TableColumn<AuditLogDTO, String> colAccion;
    @FXML
    private TableColumn<AuditLogDTO, String> colEntidad;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final IAuditService auditoria = SceneManager.getAuditService();

    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getFechaHoraExacta().format(FORMATO_FECHA)));
        colUsuario.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreUsuario()));
        colIp.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDireccionIp()));
        colModulo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getModuloConsultado()));
        colAccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAccionRealizada()));
        colEntidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEntidadAfectada()));

        colAccion.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String accion, boolean vacia) {
                super.updateItem(accion, vacia);
                if (vacia || accion == null) {
                    setGraphic(null);
                    return;
                }
                Label insignia = new Label(accion);
                insignia.getStyleClass().addAll("badge", claseInsignia(accion));
                setGraphic(insignia);
            }
        });

        tablaAuditoria.setPlaceholder(new Label("Sin resultados"));

        cmbUsuario.getItems().add("Todos");
        cmbUsuario.getItems().addAll(auditoria.listarRegistros().stream()
                .map(AuditLogDTO::getNombreUsuario).distinct().sorted().toList());
        cmbUsuario.setValue("Todos");

        cmbAccion.getItems().addAll("Todas", "INICIO_SESION", "CONSULTA", "MODIFICACION",
                "EXPORTACION_REPORTE", "BAJA_LOGICA", "ACCESO_DENEGADO");
        cmbAccion.setValue("Todas");

        onFiltrar();
    }

    @FXML
    private void onFiltrar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        String usuario = cmbUsuario.getValue();
        String accion = cmbAccion.getValue();

        List<AuditLogDTO> resultado = auditoria.listarRegistros().stream().filter(r -> {
            LocalDate dia = r.getFechaHoraExacta().toLocalDate();
            if (desde != null && dia.isBefore(desde))
                return false;
            if (hasta != null && dia.isAfter(hasta))
                return false;
            if (!"Todos".equals(usuario) && !r.getNombreUsuario().equals(usuario))
                return false;
            if (!"Todas".equals(accion) && !r.getAccionRealizada().equals(accion))
                return false;
            return true;
        }).toList();

        tablaAuditoria.setItems(FXCollections.observableArrayList(resultado));
    }

    @FXML
    private void onIntentarExpediente() {
        UsuarioDTO u = SceneManager.getUsuarioActual();
        auditoria.registrar(u.getNombreUsuario(), SceneManager.IP_LOCAL, "Pacientes",
                "ACCESO_DENEGADO", "Intento de abrir expediente clínico (rol Administrador)");
        SceneManager.alerta(Alert.AlertType.WARNING, "Acceso denegado",
                "El Administrador no puede ver expedientes clínicos. El intento quedó registrado.");
        onFiltrar();
    }

    private String claseInsignia(String accion) {
        return switch (accion) {
            case "CONSULTA" -> "badge-azul";
            case "EXPORTACION_REPORTE" -> "badge-verde";
            case "INICIO_SESION" -> "badge-morado";
            default -> "badge-carmin";
        };
    }
}
