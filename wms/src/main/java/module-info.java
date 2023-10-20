module bg.tuvarna.sit.wms {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;

  opens bg.tuvarna.sit.wms to javafx.fxml;
  exports bg.tuvarna.sit.wms;
}