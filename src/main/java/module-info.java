module com.example.mine_sweeper {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mine_sweeper to javafx.fxml;
    exports com.example.mine_sweeper;
}