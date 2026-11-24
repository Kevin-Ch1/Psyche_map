module com.psychemap {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;

    exports com.psychemap;
    exports com.psychemap.config;
    exports com.psychemap.security.crypto;
    exports com.psychemap.security.dao;
    exports com.psychemap.security.dto;
    exports com.psychemap.security.exception;
    exports com.psychemap.security.model;
    exports com.psychemap.security.service;
    exports com.psychemap.audit.dao;
    exports com.psychemap.audit.dto;
    exports com.psychemap.audit.model;
    exports com.psychemap.audit.service;
    exports com.psychemap.atlas.dao;
    exports com.psychemap.atlas.dto;
    exports com.psychemap.atlas.model;
    exports com.psychemap.ui.controller;
    exports com.psychemap.service;
    exports com.psychemap.service.db;

    opens com.psychemap to javafx.fxml;
    opens com.psychemap.ui.controller to javafx.fxml;
    opens com.psychemap.atlas.dto to javafx.base;
    opens com.psychemap.security.dto to javafx.base;
    opens com.psychemap.audit.dto to javafx.base;
}
