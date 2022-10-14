module org.cnpem.fitotron {

    requires javafx.controls;
    requires javafx.fxml;
    requires commons.math3;

    opens org.cnpem.fitotron to javafx.fxml;
    exports org.cnpem.fitotron;

    opens org.cnpem.fitotron.combos to javafx.fxml;
    exports org.cnpem.fitotron.combos;

    opens org.cnpem.fitotron.file to javafx.fxml;
    exports org.cnpem.fitotron.file;

    opens org.cnpem.fitotron.chart to javafx.fxml;
    exports org.cnpem.fitotron.chart;

    opens org.cnpem.fitotron.list to javafx.fxml;
    exports org.cnpem.fitotron.list;

    opens org.cnpem.fitotron.table to javafx.fxml;
    exports org.cnpem.fitotron.table;
    exports org.cnpem.fitotron.measurement;
    opens org.cnpem.fitotron.measurement to javafx.fxml;
}