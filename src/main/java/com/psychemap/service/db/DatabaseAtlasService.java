package com.psychemap.service.db;

import com.psychemap.atlas.dao.CriterioDAO;
import com.psychemap.atlas.dao.CriterioDAOImpl;
import com.psychemap.atlas.dao.SintomaDAO;
import com.psychemap.atlas.dao.SintomaDAOImpl;
import com.psychemap.atlas.dao.TrastornoDAO;
import com.psychemap.atlas.dao.TrastornoDAOImpl;
import com.psychemap.atlas.model.CriterioDiagnostico;
import com.psychemap.atlas.model.SintomaFenotipo;
import com.psychemap.atlas.model.TrastornoClinico;
import com.psychemap.config.DatabaseConnection;
import com.psychemap.atlas.dto.CriterioDTO;
import com.psychemap.atlas.dto.SintomaDTO;
import com.psychemap.atlas.dto.TrastornoDTO;
import com.psychemap.service.IAtlasService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class DatabaseAtlasService implements IAtlasService {

    private final TrastornoDAO trastornoDAO;
    private final CriterioDAO criterioDAO;
    private final SintomaDAO sintomaDAO;

    public DatabaseAtlasService() {
        this(new TrastornoDAOImpl(), new CriterioDAOImpl(), new SintomaDAOImpl());
    }

    public DatabaseAtlasService(TrastornoDAO trastornoDAO, CriterioDAO criterioDAO, SintomaDAO sintomaDAO) {
        this.trastornoDAO = trastornoDAO;
        this.criterioDAO = criterioDAO;
        this.sintomaDAO = sintomaDAO;
        inicializarAtlasSiVacio();
    }

    private void inicializarAtlasSiVacio() {
        try {
            List<TrastornoClinico> existentes = trastornoDAO.listarTodos();
            if (existentes.isEmpty()) {
                guardarTrastornoInicial("F90.0", "Trastorno por déficit de atención con hiperactividad",
                        "Neurodesarrollo", "7.0", "5.9");
                guardarTrastornoInicial("F41.1", "Trastorno de ansiedad generalizada",
                        "Ansiedad", "31.0", "3.1");
                guardarTrastornoInicial("F32.1", "Trastorno depresivo mayor (episodio moderado)",
                        "Afectivo", "27.0", "4.4");
                existentes = trastornoDAO.listarTodos();
            }

            sembrarCriteriosYSintomasSiVacio(existentes);

        } catch (Exception e) {
            System.err.println("[DB AtlasService] Nota al inicializar catálogo: " + e.getMessage());
        }
    }

    private void sembrarCriteriosYSintomasSiVacio(List<TrastornoClinico> trastornos) {
        for (TrastornoClinico t : trastornos) {
            try {
                List<CriterioDiagnostico> crits = criterioDAO.listarPorTrastornoId(t.getId());
                if (crits.isEmpty()) {
                    sembrarCriteriosParaTrastorno(t);
                }
                List<SintomaFenotipo> sints = sintomaDAO.buscarPorTrastornoId(t.getId());
                if (sints.isEmpty()) {
                    sembrarSintomasParaTrastorno(t);
                }
            } catch (Exception e) {
                System.err.println("[DB AtlasService] Error al sembrar criterios/síntomas para " + t.getCodigoClasificacion() + ": " + e.getMessage());
            }
        }
    }

    private void sembrarCriteriosParaTrastorno(TrastornoClinico t) throws Exception {
        String cod = t.getCodigoClasificacion();
        if ("F90.0".equalsIgnoreCase(cod)) {
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "A", "Patrón persistente de inatención que interfiere con el desarrollo.", 6, 6, "Inclusion", "DSM-5-TR"));
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "B", "Manifestaciones presentes antes de los 12 años.", 0, 0, "Temporal", "DSM-5-TR"));
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "C", "Aparición en dos o más contextos (hogar, escuela, laboral).", 0, 0, "Inclusion", "DSM-5-TR"));
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "E", "No se explica mejor por otro trastorno mental ni por sustancias.", 0, 0, "Exclusion", "DSM-5-TR"));
        } else if ("F41.1".equalsIgnoreCase(cod)) {
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "A", "Ansiedad y preocupación excesivas la mayoría de los días sobre diversos temas.", 6, 0, "Temporal", "DSM-5-TR"));
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "B", "Dificultad para controlar la preocupación constante.", 0, 0, "Inclusion", "DSM-5-TR"));
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "C", "Asociado a tres o más síntomas de tensión física o inquietud.", 0, 3, "Inclusion", "DSM-5-TR"));
        } else {
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "A", "Presencia de estado de ánimo deprimido o anhedonia casi todo el día.", 0, 5, "Inclusion", "DSM-5-TR"));
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "B", "Duración mínima de dos semanas continuas.", 0, 0, "Temporal", "DSM-5-TR"));
            criterioDAO.crear(new CriterioDiagnostico(null, t.getId(), "C", "Malestar clínicamente significativo o deterioro social/laboral.", 0, 0, "Inclusion", "DSM-5-TR"));
        }
    }

    private void sembrarSintomasParaTrastorno(TrastornoClinico t) throws Exception {
        String cod = t.getCodigoClasificacion();
        List<SintomaFenotipo> sintsParaAsociar = new ArrayList<>();
        if ("F90.0".equalsIgnoreCase(cod)) {
            sintsParaAsociar.add(insertarOObtenerSintoma("Dificultad para sostener la atención", "Se pierde en tareas prolongadas.", "Atencional", true));
            sintsParaAsociar.add(insertarOObtenerSintoma("Se distrae con facilidad", "Estímulos irrelevantes interrumpen la tarea.", "Atencional", true));
            sintsParaAsociar.add(insertarOObtenerSintoma("Inquietud motora", "Dificultad para permanecer sentado.", "Conductual", false));
            sintsParaAsociar.add(insertarOObtenerSintoma("Impulsividad", "Actúa antes de anticipar consecuencias.", "Conductual", false));
        } else if ("F41.1".equalsIgnoreCase(cod)) {
            sintsParaAsociar.add(insertarOObtenerSintoma("Preocupación crónica e incontrolable", "Rumiación sobre eventos cotidianos.", "Afectivo", true));
            sintsParaAsociar.add(insertarOObtenerSintoma("Tensión muscular", "Rigidez o dolor en cuello y hombros.", "Somático", false));
            sintsParaAsociar.add(insertarOObtenerSintoma("Fatiga fácil", "Cansancio desproporcionado al esfuerzo.", "Somático", false));
            sintsParaAsociar.add(insertarOObtenerSintoma("Irritabilidad", "Respuesta exasperada ante estímulos menores.", "Conductual", false));
        } else {
            sintsParaAsociar.add(insertarOObtenerSintoma("Tristeza profunda o vacío", "Desesperanza la mayor parte del tiempo.", "Afectivo", true));
            sintsParaAsociar.add(insertarOObtenerSintoma("Pérdida de interés o placer", "Anhedonia en actividades antes disfrutadas.", "Afectivo", true));
            sintsParaAsociar.add(insertarOObtenerSintoma("Insomnio o hipersomnia", "Alteración en los patrones circadianos de sueño.", "Somático", false));
        }

        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO TrastornoSintoma (trastorno_id, sintoma_id) VALUES (?, ?) ON CONFLICT DO NOTHING")) {
            for (SintomaFenotipo s : sintsParaAsociar) {
                if (s.getId() != null) {
                    ps.setInt(1, t.getId());
                    ps.setInt(2, s.getId());
                    ps.executeUpdate();
                }
            }
        }
    }

    private SintomaFenotipo insertarOObtenerSintoma(String nombre, String desc, String dominio, boolean cardinal) throws Exception {
        String selectSql = "SELECT id, nombre_sintoma, descripcion_fenomenologica, dominio_afectado, es_cardinal FROM SintomaFenotipo WHERE nombre_sintoma = ?";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SintomaFenotipo(rs.getInt("id"), rs.getString("nombre_sintoma"),
                            rs.getString("descripcion_fenomenologica"), rs.getString("dominio_afectado"), rs.getBoolean("es_cardinal"));
                }
            }
        }

        String insertSql = "INSERT INTO SintomaFenotipo (nombre_sintoma, descripcion_fenomenologica, dominio_afectado, es_cardinal) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnection.getInstance().getNewConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, nombre);
            ps.setString(2, desc);
            ps.setString(3, dominio);
            ps.setBoolean(4, cardinal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new SintomaFenotipo(rs.getInt("id"), nombre, desc, dominio, cardinal);
                }
            }
        }
        return new SintomaFenotipo(null, nombre, desc, dominio, cardinal);
    }

    private void guardarTrastornoInicial(String codigo, String nombre, String categoria, String edad, String prevalencia) {
        try {
            TrastornoClinico t = new TrastornoClinico(codigo, nombre, categoria);
            t.setEdadInicioPromedio(edad);
            t.setPrevalenciaEstimada(prevalencia);
            trastornoDAO.guardar(t);
        } catch (Exception e) {
            System.err.println("[DB AtlasService] Error al guardar trastorno inicial: " + e.getMessage());
        }
    }

    @Override
    public List<TrastornoDTO> listarTrastornos() {
        try {
            List<TrastornoClinico> listaDB = trastornoDAO.listarTodos();
            List<TrastornoDTO> resultado = new ArrayList<>();
            for (TrastornoClinico t : listaDB) {
                resultado.add(convertirADTO(t));
            }
            return resultado;
        } catch (Exception e) {
            System.err.println("[DB AtlasService] Error al listar de PostgreSQL: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<TrastornoDTO> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarTrastornos();
        }
        try {
            List<TrastornoClinico> filtrados = trastornoDAO.buscarPorTermino(texto);
            List<TrastornoDTO> resultado = new ArrayList<>();
            for (TrastornoClinico t : filtrados) {
                resultado.add(convertirADTO(t));
            }
            return resultado;
        } catch (Exception e) {
            System.err.println("[DB AtlasService] Error en búsqueda nosológica: " + e.getMessage());
            return listarTrastornos();
        }
    }

    @Override
    public void guardarCriterio(int idTrastorno, CriterioDTO criterio) {
        try {
            List<CriterioDiagnostico> existentes = criterioDAO.listarPorTrastornoId(idTrastorno);
            Optional<CriterioDiagnostico> coincidencia = existentes.stream()
                    .filter(c -> c.getIdentificadorCriterio().equalsIgnoreCase(criterio.getIdentificadorCriterio()))
                    .findFirst();

            if (coincidencia.isPresent()) {
                CriterioDiagnostico act = coincidencia.get();
                act.setDescripcionClinica(criterio.getDescripcionClinica());
                act.setDuracionMinimaMeses(criterio.getDuracionMinimaMeses());
                act.setUmbralSintomasRequerido(criterio.getUmbralSintomasRequerido());
                act.setTipoCriterio(criterio.getTipoCriterio());
                criterioDAO.actualizar(act);
            } else {
                CriterioDiagnostico nuevo = new CriterioDiagnostico(
                        null,
                        idTrastorno,
                        criterio.getIdentificadorCriterio(),
                        criterio.getDescripcionClinica(),
                        criterio.getDuracionMinimaMeses(),
                        criterio.getUmbralSintomasRequerido(),
                        criterio.getTipoCriterio(),
                        "DSM-5-TR"
                );
                criterioDAO.crear(nuevo);
            }
        } catch (Exception e) {
            System.err.println("[DatabaseAtlasService] Error al guardar criterio en DB: " + e.getMessage());
        }
    }

    private TrastornoDTO convertirADTO(TrastornoClinico t) {
        double edad = parsearDouble(t.getEdadInicioPromedio(), 25.0);
        double prev = parsearDouble(t.getPrevalenciaEstimada(), 3.5);

        TrastornoDTO dto = new TrastornoDTO(
                t.getId() != null ? t.getId() : 0,
                t.getCodigoClasificacion(),
                t.getNombreTrastorno(),
                t.getCategoriaNosologica(),
                edad,
                prev
        );

        try {
            List<CriterioDiagnostico> crits = criterioDAO.listarPorTrastornoId(dto.getId());
            for (CriterioDiagnostico c : crits) {
                dto.getCriterios().add(new CriterioDTO(
                        c.getIdentificadorCriterio(),
                        c.getDescripcionClinica(),
                        c.getDuracionMinimaMeses() != null ? c.getDuracionMinimaMeses() : 0,
                        c.getUmbralSintomasRequerido() != null ? c.getUmbralSintomasRequerido() : 0,
                        c.getTipoCriterio() != null ? c.getTipoCriterio() : "Inclusion"
                ));
            }

            List<SintomaFenotipo> sints = sintomaDAO.buscarPorTrastornoId(dto.getId());
            for (SintomaFenotipo s : sints) {
                dto.getSintomas().add(new SintomaDTO(
                        s.getNombreSintoma(),
                        s.getDescripcionFenomenologica(),
                        s.getDominioAfectado(),
                        s.isCardinal()
                ));
            }
        } catch (Exception e) {
            System.err.println("[DatabaseAtlasService] Error al cargar criterios/síntomas para " + dto.getId() + ": " + e.getMessage());
        }

        return dto;
    }

    private double parsearDouble(String val, double porDefecto) {
        if (val == null) return porDefecto;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
