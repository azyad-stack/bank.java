module com.example.myjavafxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome; // for icons
    requires java.sql;
    requires java.naming; // Required for SQLite JDBC
    requires org.xerial.sqlitejdbc; // SQLite JDBC
    requires com.dlsc.formsfx;
    requires javafx.base;

    // Open packages to javafx.fxml for reflection (FXML injection)
    opens com.example.myjavafxapp to javafx.fxml;
    opens com.example.myjavafxapp.Controllers to javafx.fxml;
    opens com.example.myjavafxapp.Controllers.Admin to javafx.fxml;
    opens com.example.myjavafxapp.Controllers.User to javafx.fxml;

    // Export packages for public access
    exports com.example.myjavafxapp;
    exports com.example.myjavafxapp.Controllers;
    exports com.example.myjavafxapp.Controllers.Admin;
    exports com.example.myjavafxapp.Controllers.User;
    exports com.example.myjavafxapp.Models;
    exports com.example.myjavafxapp.Views;
}