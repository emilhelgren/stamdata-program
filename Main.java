import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    Stage window;
    Scene scene1, scene2;
    Label tester;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Test app");

        window.setOnCloseRequest(e -> {
            e.consume(); // Vi fortæller systemet at vi nok skal ordne den close event der er kaldt, så det skal ikke gøre mere medmindre vi fortæller det
            closeProgram();
        });


        //Layout
        GridPane layout = new GridPane();
        createMenuBar(layout);
        createInitialContent(layout);


        scene1 = new Scene(layout);
        window.setScene(scene1);
        window.show();

    }

    private void closeProgram() {
        Boolean answer = ConfirmBox.display("Title", "Er du sikke på du vil lukke?");
        if (answer) {
            window.close();
        }
    }


    private void createMenuBar(GridPane layout) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(e -> System.out.println("Åben ny fil"));
        MenuItem openFile = new MenuItem("Open...");
        MenuItem openRecentFile = new MenuItem("Open Recent");
        MenuItem closeFile = new MenuItem("Close File");
        MenuItem saveFile = new MenuItem("Save File");
        MenuItem saveFileAs = new MenuItem("Save As...");
        fileMenu.getItems().addAll(newFile, openFile, openRecentFile, closeFile, saveFile, saveFileAs);

        Menu editMenu = new Menu("Edit");

        Menu viewMenu = new Menu("View");

        Menu helpMenu = new Menu("Help");

        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);

        GridPane.setConstraints(menuBar, 0, 0);
        layout.getChildren().add(menuBar);

    }

    private VBox inputField(GridPane layout, String text, int col, int row) {
        // Måske lav et placeholder-parameter
        VBox sublayout = new VBox();
        Label label = new Label(text);
        TextField textField = new TextField();
        sublayout.getChildren().addAll(label, textField);
        GridPane.setConstraints(sublayout, col, row);
        layout.getChildren().add(sublayout);

        return sublayout;
    }

    private VBox adressField(GridPane layout, String text, int col, int row) {
        VBox sublayout = new VBox();
        Label label = new Label(text);

        HBox subsublayout = new HBox();
        TextField vejInput = new TextField();
        TextField byInput = new TextField();
        vejInput.setPromptText("Vej og husnummer");
        byInput.setPromptText("Postnummer");

        subsublayout.getChildren().addAll(vejInput, byInput);

        sublayout.getChildren().addAll(label, subsublayout);
        GridPane.setConstraints(sublayout, col, row);
        layout.getChildren().add(sublayout);

        return sublayout;

    }

    private VBox createComboBox(GridPane layout, ObservableList<String> elements, int col, int row, Boolean selectFirst, String text) {
        ComboBox comboBox = new ComboBox(elements);
        if (selectFirst) {
            comboBox.getSelectionModel().selectFirst();
        }
        Label label = new Label(text);
        VBox sublayout = new VBox();
        sublayout.getChildren().addAll(label, comboBox);
        GridPane.setConstraints(sublayout, col, row);
        layout.getChildren().add(sublayout);
        return sublayout;
    }


    private void createInitialContent(GridPane layout) {
        // LAD VÆRE MED AT initiate ting her, vi skal jo kunne access dem uden for funktionen

        layout.setVgap(10);
        layout.setPadding(new Insets(5, 5, 5, 5));

        ObservableList<String> typerList = FXCollections.observableArrayList("HYBRID", "STANDARD", "AC-BATTERI");
        VBox typeBox = createComboBox(layout, typerList, 0, 1, true, "Anlægstype");



        VBox navnInput = inputField(layout, "Navn på anlægsejer:", 0, 2);
        VBox tlfInput = inputField(layout, "Telefonnummer på anlægsejer:", 0, 3);
        VBox emailInput = inputField(layout, "Email på anlægsejer:", 0, 4);
        VBox adresseInput = adressField(layout, "Anlægsadresse:", 0, 5);
        VBox fdagInput = inputField(layout, "Fødselsdag/cvr på anlægsejer:", 0, 6);
        VBox instInput = inputField(layout, "Installationsnummer/Aftagenummer:", 0, 7);

        ObservableList<String> panelList = FXCollections.observableArrayList("LONGI", "AXITECH", "TALESUN");
        VBox panelBox = createComboBox(layout, panelList, 0, 9, false, "Paneltype");
        VBox panelInput = inputField(layout, "Antal paneler:", 0, 10);

        ObservableList<String> inverterList = FXCollections.observableArrayList("HUAWEI", "FRONIUS", "GROWATT", "SONNEN");
        VBox inverterBox = createComboBox(layout, inverterList, 0, 11, false, "Inverter");


        ObservableList<String> batList = FXCollections.observableArrayList();
        VBox batBox = createComboBox(layout, batList, 0, 12, false, "Batteritillæg");

        Node inverterCombo = inverterBox.getChildren().get(1);
        if (inverterCombo instanceof ComboBox) {
            ((ComboBox) inverterCombo).getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                batList.clear();
                if (newValue == "GROWATT") {
                    batList.addAll("0.0", "6.5", "13.0");
                } else {
                    batList.addAll("0.0", "2.5", "5.0", "7.5", "10.0");
                }
            });
        }

        Node typeCombo = typeBox.getChildren().get(1);
        if (typeCombo instanceof ComboBox) {
            ((ComboBox) typeCombo).getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
                if (newValue == "STANDARD") {
                    layout.getChildren().remove(batBox);
                } else {
                    if (!(layout.getChildren().contains(batBox))) {
                        // Hvis den ikke ! indeholder
                        layout.getChildren().add(batBox);
                    }

                }
            });
        }





        tester = new Label("");

        // Til at access input field
        Button button = new Button("Set label");
        button.setOnAction(e -> {
            Node input = navnInput.getChildren().get(1);
            if (input instanceof TextField) {
                tester.setText(((TextField) input).getText());
            }
        });

        GridPane.setConstraints(tester, 0, 20);
        GridPane.setConstraints(button, 0, 21);
        layout.getChildren().addAll(tester, button);


    }

}



