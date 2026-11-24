package com.psychemap.ui.controller;

import com.psychemap.atlas.dto.CriterioDTO;
import com.psychemap.atlas.dto.SintomaDTO;
import com.psychemap.atlas.dto.TrastornoDTO;
import com.psychemap.security.dto.UsuarioDTO;
import com.psychemap.service.IAtlasService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class AtlasViewController {

    @FXML private TextField tfBuscar;
    @FXML private Label lblConteo;
    @FXML private TableView<TrastornoDTO> tablaTrastornos;
    @FXML private TableColumn<TrastornoDTO, String> colCodigo;
    @FXML private TableColumn<TrastornoDTO, String> colNombre;
    @FXML private TableColumn<TrastornoDTO, String> colCategoria;

    @FXML private TextField tfFiltroSintomas;
    @FXML private Label lblMarcados;
    @FXML private VBox vbSintomasLista;
    @FXML private TableView<Compatibilidad> tablaCompat;
    @FXML private TableColumn<Compatibilidad, String> colCompCodigo;
    @FXML private TableColumn<Compatibilidad, String> colCompNombre;
    @FXML private TableColumn<Compatibilidad, Compatibilidad> colCompPct;

    @FXML private Label lblVacio;
    @FXML private ScrollPane spDetalle;
    @FXML private Label lblCodigo;
    @FXML private Label lblNombre;
    @FXML private Label lblInfo;
    @FXML private Button btnAgregarCriterio;
    @FXML private VBox vbCriterios;
    @FXML private VBox vbSintomas;

    private static class Compatibilidad {
        private final TrastornoDTO trastorno;
        private final int porcentaje;

        Compatibilidad(TrastornoDTO trastorno, int porcentaje) {
            this.trastorno = trastorno;
            this.porcentaje = porcentaje;
        }
    }

    private final IAtlasService atlas = SceneManager.getAtlasService();
    private final Set<String> marcados = new LinkedHashSet<>();
    private boolean puedeEditar;
    private TrastornoDTO actual;
    private boolean cargando;

    @FXML
    private void initialize() {
        UsuarioDTO u = SceneManager.getUsuarioActual();
        puedeEditar = UsuarioDTO.ROL_INVESTIGADOR.equals(u.getNombreRol()) && u.isPermisoEscritura();

        btnAgregarCriterio.setVisible(puedeEditar);
        btnAgregarCriterio.setManaged(puedeEditar);

        lblConteo.getStyleClass().addAll("badge", "badge-morado");
        lblMarcados.getStyleClass().addAll("badge", "badge-morado");

        colCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodigoClasificacion()));
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreTrastorno()));
        colCategoria.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategoriaNosologica()));
        tablaTrastornos.setPlaceholder(new Label("Sin coincidencias"));
        tablaTrastornos.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, elegido) -> seleccionar(elegido));
        tfBuscar.textProperty().addListener((obs, viejo, nuevo) -> cargarTabla(nuevo));

        colCompCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().trastorno.getCodigoClasificacion()));
        colCompNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().trastorno.getNombreTrastorno()));
        colCompPct.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue()));
        colCompPct.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Compatibilidad c, boolean vacia) {
                super.updateItem(c, vacia);
                if (vacia || c == null) {
                    setGraphic(null);
                    return;
                }
                ProgressBar barra = new ProgressBar(c.porcentaje / 100.0);
                barra.setPrefHeight(10);
                barra.setPrefWidth(70);
                barra.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(barra, Priority.ALWAYS);
                Label porcentaje = new Label(c.porcentaje + "%");
                porcentaje.getStyleClass().add("sintoma-nombre");
                HBox caja = new HBox(8, barra, porcentaje);
                caja.setAlignment(Pos.CENTER_LEFT);
                setGraphic(caja);
            }
        });
        tablaCompat.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, fila) -> { if (fila != null) seleccionar(fila.trastorno); });
        tfFiltroSintomas.textProperty().addListener((obs, viejo, nuevo) -> construirListaSintomas(nuevo));

        cargarTabla("");
        construirListaSintomas("");
        recargarCompatibilidad();
    }

    private void seleccionar(TrastornoDTO t) {
        if (t == null) return;
        mostrarDetalle(t);
        if (!cargando) {
            UsuarioDTO u = SceneManager.getUsuarioActual();
            SceneManager.getAuditService().registrar(u.getNombreUsuario(), SceneManager.IP_LOCAL,
                    "Atlas", "CONSULTA", "Trastorno " + t.getCodigoClasificacion());
        }
    }

    private void cargarTabla(String texto) {
        int idPrevio = (actual != null) ? actual.getId() : -1;
        List<TrastornoDTO> resultado = atlas.buscar(texto);

        cargando = true;
        tablaTrastornos.setItems(FXCollections.observableArrayList(resultado));
        for (TrastornoDTO t : resultado) {
            if (t.getId() == idPrevio) {
                tablaTrastornos.getSelectionModel().select(t);
            }
        }
        cargando = false;

        lblConteo.setText(resultado.size() + " de " + atlas.listarTrastornos().size());
    }

    private void construirListaSintomas(String filtro) {
        vbSintomasLista.getChildren().clear();
        String f = (filtro == null) ? "" : filtro.trim().toLowerCase();

        Map<String, Set<String>> porDominio = new TreeMap<>();
        for (TrastornoDTO t : atlas.listarTrastornos()) {
            for (SintomaDTO s : t.getSintomas()) {
                porDominio.computeIfAbsent(s.getDominioAfectado(), k -> new TreeSet<>()).add(s.getNombreSintoma());
            }
        }

        for (Map.Entry<String, Set<String>> grupo : porDominio.entrySet()) {
            List<String> visibles = new ArrayList<>();
            for (String nombre : grupo.getValue()) {
                if (nombre.toLowerCase().contains(f)) visibles.add(nombre);
            }
            if (visibles.isEmpty()) continue;

            Label titulo = new Label(grupo.getKey().toUpperCase());
            titulo.setStyle("-fx-text-fill: " + colorDominio(grupo.getKey())
                    + "; -fx-font-weight: bold; -fx-font-size: 11.5px;");
            VBox.setMargin(titulo, new Insets(8, 0, 2, 0));
            vbSintomasLista.getChildren().add(titulo);

            for (String nombre : visibles) {
                CheckBox casilla = new CheckBox(nombre);
                casilla.setSelected(marcados.contains(nombre));
                casilla.setOnAction(e -> {
                    if (casilla.isSelected()) marcados.add(nombre);
                    else marcados.remove(nombre);
                    recargarCompatibilidad();
                });
                vbSintomasLista.getChildren().add(casilla);
            }
        }
    }

    private void recargarCompatibilidad() {
        List<Compatibilidad> filas = new ArrayList<>();
        for (TrastornoDTO t : atlas.listarTrastornos()) {
            if (t.getSintomas().isEmpty()) continue;
            int coinciden = 0;
            for (SintomaDTO s : t.getSintomas()) {
                if (marcados.contains(s.getNombreSintoma())) coinciden++;
            }
            if (coinciden > 0) {
                filas.add(new Compatibilidad(t, (int) Math.round(coinciden * 100.0 / t.getSintomas().size())));
            }
        }
        filas.sort((a, b) -> b.porcentaje - a.porcentaje);

        int idPrevio = (actual != null) ? actual.getId() : -1;
        cargando = true;
        tablaCompat.setItems(FXCollections.observableArrayList(filas));
        for (Compatibilidad c : filas) {
            if (c.trastorno.getId() == idPrevio) tablaCompat.getSelectionModel().select(c);
        }
        cargando = false;

        lblMarcados.setText(marcados.size() + (marcados.size() == 1 ? " marcado" : " marcados"));
        tablaCompat.setPlaceholder(new Label(marcados.isEmpty()
                ? "Marca síntomas para calcular la compatibilidad." : "Sin coincidencias"));
    }

    @FXML
    private void onLimpiarSintomas() {
        marcados.clear();
        construirListaSintomas(tfFiltroSintomas.getText());
        recargarCompatibilidad();
    }

    private String colorDominio(String dominio) {
        return switch (dominio) {
            case "Atencional" -> "#3b82f6";
            case "Conductual" -> "#e11d48";
            case "Afectivo" -> "#173a7a";
            case "Somático" -> "#059669";
            default -> "#64748b";
        };
    }

    private void mostrarDetalle(TrastornoDTO t) {
        actual = t;
        lblVacio.setVisible(false);
        lblVacio.setManaged(false);
        spDetalle.setVisible(true);
        spDetalle.setManaged(true);

        lblCodigo.setText(t.getCodigoClasificacion());
        lblNombre.setText(t.getNombreTrastorno());
        lblInfo.setText(String.format("Categoría: %s  ·  Edad de inicio promedio: %.1f años  ·  Prevalencia estimada: %.1f %%",
                t.getCategoriaNosologica(), t.getEdadInicioPromedio(), t.getPrevalenciaEstimada()));

        vbCriterios.getChildren().clear();
        for (CriterioDTO c : t.getCriterios()) {
            vbCriterios.getChildren().add(tarjetaCriterio(t, c));
        }
        vbSintomas.getChildren().clear();
        for (SintomaDTO s : t.getSintomas()) {
            vbSintomas.getChildren().add(tarjetaSintoma(s));
        }
    }

    private Node tarjetaCriterio(TrastornoDTO t, CriterioDTO c) {
        Label letra = new Label(c.getIdentificadorCriterio());
        letra.getStyleClass().add("criterio-letra");

        Label descripcion = new Label(c.getDescripcionClinica());
        descripcion.setWrapText(true);

        String meta = c.getTipoCriterio();
        if (c.getDuracionMinimaMeses() > 0) meta += "  ·  Duración mínima: " + c.getDuracionMinimaMeses() + " meses";
        if (c.getUmbralSintomasRequerido() > 0) meta += "  ·  Umbral: " + c.getUmbralSintomasRequerido() + " síntomas";
        Label detalle = new Label(meta);
        detalle.setWrapText(true);
        detalle.getStyleClass().add("texto-gris");

        VBox texto = new VBox(4, descripcion, detalle);
        HBox.setHgrow(texto, Priority.ALWAYS);

        HBox fila = new HBox(12, letra, texto);
        fila.setAlignment(Pos.TOP_LEFT);
        fila.getStyleClass().add("criterio-fila");
        if ("Exclusion".equalsIgnoreCase(c.getTipoCriterio())) fila.getStyleClass().add("criterio-exclusion");
        if ("Temporal".equalsIgnoreCase(c.getTipoCriterio())) fila.getStyleClass().add("criterio-temporal");

        if (puedeEditar) {
            Button editar = new Button("Editar");
            editar.getStyleClass().add("boton-suave");
            editar.setMinWidth(Region.USE_PREF_SIZE);
            editar.setOnAction(e -> abrirDialogoCriterio(t, c));
            fila.getChildren().add(editar);
        }
        return fila;
    }

    private Node tarjetaSintoma(SintomaDTO s) {
        Label nombre = new Label(s.getNombreSintoma());
        nombre.getStyleClass().add("sintoma-nombre");

        Label dominio = new Label(s.getDominioAfectado());
        dominio.getStyleClass().addAll("badge", "badge-azul");

        HBox cabecera = new HBox(8, nombre, dominio);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        if (s.isEsCardinal()) {
            Label cardinal = new Label("Cardinal");
            cardinal.getStyleClass().addAll("badge", "badge-morado");
            cabecera.getChildren().add(cardinal);
        }

        Label descripcion = new Label(s.getDescripcionFenomenologica());
        descripcion.setWrapText(true);

        VBox fila = new VBox(4, cabecera, descripcion);
        fila.getStyleClass().add("sintoma-fila");
        return fila;
    }

    @FXML
    private void onAgregarCriterio() {
        if (actual == null) {
            SceneManager.alerta(Alert.AlertType.INFORMATION, "Atlas", "Primero selecciona un trastorno.");
            return;
        }
        abrirDialogoCriterio(actual, null);
    }

    private void abrirDialogoCriterio(TrastornoDTO t, CriterioDTO existente) {
        Dialog<CriterioDTO> dialogo = new Dialog<>();
        dialogo.setTitle(existente == null ? "Agregar criterio" : "Editar criterio");
        dialogo.setHeaderText(t.getCodigoClasificacion() + " · " + t.getNombreTrastorno());
        dialogo.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/estilos.css").toExternalForm());

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        TextField tfId = new TextField();
        tfId.setPromptText("A, B, C...");
        TextArea taDescripcion = new TextArea();
        taDescripcion.setWrapText(true);
        taDescripcion.setPrefRowCount(6);
        TextField tfDuracion = new TextField("0");
        TextField tfUmbral = new TextField("0");
        ComboBox<String> cmbTipo = new ComboBox<>(
                FXCollections.observableArrayList("Inclusion", "Exclusion", "Temporal"));
        cmbTipo.setValue("Inclusion");

        if (existente != null) {
            tfId.setText(existente.getIdentificadorCriterio());
            tfId.setDisable(true);
            taDescripcion.setText(existente.getDescripcionClinica());
            tfDuracion.setText(String.valueOf(existente.getDuracionMinimaMeses()));
            tfUmbral.setText(String.valueOf(existente.getUmbralSintomasRequerido()));
            cmbTipo.setValue(existente.getTipoCriterio());
        }

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.addRow(0, new Label("Identificador"), tfId);
        form.addRow(1, new Label("Descripción clínica"), taDescripcion);
        form.addRow(2, new Label("Duración mínima (meses)"), tfDuracion);
        form.addRow(3, new Label("Umbral de síntomas"), tfUmbral);
        form.addRow(4, new Label("Tipo"), cmbTipo);
        Label aviso = new Label("Si usas un identificador que ya existe, ese criterio se reemplaza. Usa 0 si no aplica.");
        aviso.getStyleClass().add("texto-gris");
        aviso.setWrapText(true);
        form.add(aviso, 0, 5, 2, 1);
        dialogo.getDialogPane().setContent(form);

        Button botonOk = (Button) dialogo.getDialogPane().lookupButton(btnGuardar);
        botonOk.addEventFilter(ActionEvent.ACTION, ev -> {
            String error = null;
            if (tfId.getText().isBlank()) {
                error = "Indica el identificador del criterio (A, B, C...).";
            } else if (taDescripcion.getText().isBlank()) {
                error = "Escribe la descripción clínica.";
            } else if (entero(tfDuracion.getText()) < 0 || entero(tfUmbral.getText()) < 0) {
                error = "Duración y umbral deben ser números enteros (0 si no aplica).";
            }
            if (error != null) {
                ev.consume();
                SceneManager.alerta(Alert.AlertType.WARNING, "Datos incompletos", error);
            }
        });

        dialogo.setResultConverter(boton -> boton == btnGuardar
                ? new CriterioDTO(tfId.getText().trim(), taDescripcion.getText().trim(),
                        entero(tfDuracion.getText()), entero(tfUmbral.getText()), cmbTipo.getValue())
                : null);

        dialogo.showAndWait().ifPresent(criterio -> {
            atlas.guardarCriterio(t.getId(), criterio);

            UsuarioDTO u = SceneManager.getUsuarioActual();
            SceneManager.getAuditService().registrar(u.getNombreUsuario(), SceneManager.IP_LOCAL, "Atlas",
                    "MODIFICACION", "Criterio " + criterio.getIdentificadorCriterio() + " de " + t.getCodigoClasificacion());

            refrescarActual();
        });
    }

    private void refrescarActual() {
        int id = actual.getId();
        cargarTabla(tfBuscar.getText());
        recargarCompatibilidad();
        for (TrastornoDTO t : atlas.listarTrastornos()) {
            if (t.getId() == id) mostrarDetalle(t);
        }
    }

    private int entero(String texto) {
        try {
            int n = Integer.parseInt(texto.trim());
            return n >= 0 ? n : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
