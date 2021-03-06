// HAK - Haris, Asad, Kyon

package App;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.prefs.Preferences;

public class Main extends Application {
    Stage window;
    Button closeButton, fileButton;
    private volatile Service<String> bgThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Clear isIndexing. This is done to ensure the program is not stuck in isIndexing state after it crashes or is force stopped.
        Preferences prefs = Preferences.userRoot().node("/LIRE-HAK/Store");
        prefs.putBoolean("isIndexing", false);

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("App.fxml")));
        window = primaryStage;
        window.setTitle("LIRE-HAK Application");

        window.setOnCloseRequest(event -> {
            event.consume();
            closeProgram();
        });

        window.setScene(new Scene(root, 1150, 600));
        window.setResizable(false);
        window.show();

        bgThread = new Service<String>() {
            @Override
            protected Task<String> createTask()  {
                return new Task<String>() {
                    @Override
                    protected void succeeded() {
                        super.succeeded();

                        //Check and notify of existing index.
                        Preferences prefs = Preferences.userRoot().node("/LIRE-HAK/Store");
                        if(prefs.get("indexingFilePath", "") != "")
                            DialogService.DisplayAlert("Note", "You had previously performed indexing. You can immediately\nproceed to querying with the previous index or perform a new\nindex if you wish.");
                    }

                    @Override
                    public String call() throws IOException, InterruptedException {
                        Thread.sleep(1200);
                        return null;
                    }
                };
            }
        };
        bgThread.start();
    }

    private void closeProgram() {
        Boolean answer = ConfirmBox.display("Sure you want to exit?", "Exit?");
        if (answer)
            window.close();
    }

    @FXML
    private void chooseFile() {
        DirectoryChooser dc = new DirectoryChooser();
        File directory = dc.showDialog(window);
        if (directory == null) System.out.println("No directory chosen");
        else System.out.println(directory.getAbsolutePath());
    }
}
