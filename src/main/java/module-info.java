module com.dbtech.system {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.dbtech.system to javafx.fxml;
    exports com.dbtech.system;

    // Επιτρέπει στο JavaFX να βλέπει τα Models (Room) για να γεμίζει τους πίνακες
    opens com.dbtech.system.models to javafx.base;

    // 2. Για να βλέπει το JavaFX τους Controllers και να φορτώνει το παράθυρο (ΑΥΤΟ ΣΟΥ ΛΕΙΠΕΙ)
    opens com.dbtech.system.controllers to javafx.fxml;
}