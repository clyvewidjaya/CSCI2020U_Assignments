package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {
    private TableView<TestFile> table;
    @Override
    public void start(Stage primaryStage) throws Exception{
        //Choose Directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory of Train and Test Folder");
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(primaryStage);
        //System.out.print(mainDirectory);
        FileOpener.startClass(mainDirectory);

        primaryStage.setTitle("SPAM MASTER");

        BorderPane layout = new BorderPane();

        table = new TableView<>();
        table.setItems(DataSource.getResult());

        TableColumn<TestFile,String> fileNameColumn = new TableColumn<>("File");
        fileNameColumn.setMinWidth(350);
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));

        TableColumn<TestFile,String> actualClassColumn = new TableColumn<>("Actual Class");
        actualClassColumn.setMinWidth(150);
        actualClassColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

        TableColumn<TestFile,String> actualClassCounted = new TableColumn<>("Spam Detector Categorized");
        actualClassCounted.setMinWidth(250);
        actualClassCounted.setCellValueFactory(new PropertyValueFactory<>("actualClassCounted"));

        TableColumn<TestFile,Double> spamProbColumn = new TableColumn<>("Spam Probability");
        spamProbColumn.setMinWidth(200);
        spamProbColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));

        table.getColumns().add(fileNameColumn);
        table.getColumns().add(actualClassColumn);
        table.getColumns().add(actualClassCounted);
        table.getColumns().add(spamProbColumn);


        GridPane percentage = new GridPane();
        percentage.add(new Label("Accuracy: " + FileOpener.accuracy),0,0);
        percentage.add(new Label("Precision: " + FileOpener.precision),0,1);

        layout.setCenter(table);
        layout.setBottom(percentage);

        Scene scene = new Scene(layout, 950,650);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
