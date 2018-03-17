/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.protonmail.sarahszabo.knisleyformattool.core;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * The main panel controller.
 *
 * @author Sarah Szabo
 */
public class Main_PanelController implements Initializable {

    @FXML
    private TextField authorsTextField;
    @FXML
    private TextField titleTextFeild;
    @FXML
    private RadioButton journalRadioButton;
    @FXML
    private ToggleGroup OptionsGroup;
    @FXML
    private RadioButton bookRadioButton;
    @FXML
    private TextArea citationTextArea;
    @FXML
    private TextField dateTextField;
    @FXML
    private TextField urlTextField;
    @FXML
    private TextField publisherTextField;
    @FXML
    private TextField telephoneNumberTextField;
    @FXML
    private Label label;
    @FXML
    private TextField pageNumberTextField;
    @FXML
    private Button clearFields;
    @FXML
    private Button generateCitationButton;
    @FXML
    private Menu historyMenuItem;

    private final Application application;
    private CitationGenerator generator;
    private CitationDiskManager diskManager;

    /**
     * Creates a new controller with the specified application.
     *
     * @param application The host application
     */
    public Main_PanelController(Application application) {
        this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            this.journalRadioButton.requestFocus();
            this.generateCitationButton.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER), () -> {
                generateCitation(null);
            });
            this.generateCitationButton.getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.DELETE, KeyCodeCombination.CONTROL_DOWN), () -> {
                        clearFields(null);
                        clearTextBox(null);
                        showNotification("Fields Cleared");
                    });
        });
        try {
            this.diskManager = new CitationDiskManager();
        } catch (IOException ex) {
            Logger.getLogger(Main_PanelController.class.getName()).log(Level.SEVERE, null, ex);
            showErrorNotification("We were unable to create the file for storing citations, you should check your disk usage.");
        }
        this.historyMenuItem.getItems().addAll(this.diskManager.getLast10Citations().stream().map(string -> {
            MenuItem menu = new MenuItem(string);
            menu.setOnAction((event) -> {
                toClipBoardString(string);
                showNotification(string + " Copied to Clipboard!");
            });
            return menu;
        })
                .collect(Collectors.toList()));
    }

    @FXML
    private void generateCitation(ActionEvent event) {
        if (this.authorsTextField.getText().isEmpty() || this.titleTextFeild.getText().isEmpty()
                || this.dateTextField.getText().isEmpty() || this.urlTextField.getText().isEmpty()) {
            showNotification("One of the manditory text fields is empty!");
            return;
        }
        String[] authors = this.authorsTextField.getText().trim().split(",");
        CitationGenerator.GeneratorType type;
        if (this.bookRadioButton.isSelected()) {
            type = CitationGenerator.GeneratorType.Article;
        } else {
            type = CitationGenerator.GeneratorType.Book;
        }
        this.generator = new CitationGenerator(authors, this.titleTextFeild.getText(), this.dateTextField.getText(),
                this.urlTextField.getText(), this.publisherTextField.getText(), this.telephoneNumberTextField.getText(),
                this.pageNumberTextField.getText(), type);
        String generatorText = this.generator.generateKnisleyCitation();
        this.citationTextArea.setText(generatorText);
        toClipBoardString(generatorText);
        if (!this.historyMenuItem.getItems().stream().anyMatch((MenuItem t) -> {
            return t.getText().equals(generatorText);
        })) {
            MenuItem menu = new MenuItem(generatorText);
            menu.setOnAction((action) -> {
                toClipBoardString(generatorText);
                showNotification(generatorText + " Copied to Clipboard!");
            });
            this.historyMenuItem.getItems().add(menu);
        }
        showNotification("Citation Copied to Clipboard");
        try {
            this.diskManager.storeCitation(generatorText);
        } catch (IOException ex) {
            Logger.getLogger(Main_PanelController.class.getName()).log(Level.SEVERE, null, ex);
            showErrorNotification("We couldn't write your citation to the disk, it may be full");
        }
    }

    @FXML
    private void clearTextBox(ActionEvent event) {
        this.citationTextArea.clear();
    }

    @FXML
    private void clearFields(ActionEvent event) {
        this.authorsTextField.clear();
        this.titleTextFeild.clear();
        this.dateTextField.clear();
        this.urlTextField.clear();
        this.publisherTextField.clear();
        this.telephoneNumberTextField.clear();
        this.pageNumberTextField.clear();
    }

    @FXML
    private void displayTips(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tips & Tricks");
        alert.setHeaderText("Cool Things!");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setContentText("0) When using et al. don't include the period, the program will add it for you, if you"
                + " do, you will get 2 periods. This might be fixed in future releases.\n\n"
                + "1) Use the Enter Key to generate the citation without clicking the button.\n\n"
                + "2) Don't forget about the history function, it will remember your previous citations,"
                + " even when you close the window!\n\n"
                + "3) For the Author Field, enter information in this format: John, Jane, Nick");
        alert.initOwner(this.authorsTextField.getScene().getWindow());
        alert.showAndWait();
    }

    @FXML
    private void displayAboutPage(ActionEvent event) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            ImageView image = new ImageView(new Image(new FileInputStream(Paths.get("Pictures", "Profile Picture.jpg").toFile()), 800,
                    800, true, true));
            alert.setGraphic(image);
            alert.setTitle("About");
            alert.setHeaderText("Samuel Szabo: SBU Student/Developer");
            alert.setContentText("Provided free of charge for the betterment of mankind!\n\n"
                    + "If you like this, and want to support further development on it, and other projects like it,"
                    + "consider making a donation :D\n\n"
                    + "Version: α 0.5");
            alert.initOwner(this.dateTextField.getScene().getWindow());
            ButtonType patreonButton = new ButtonType("Visit my Patreon Page");
            alert.getButtonTypes().add(patreonButton);
            alert.showAndWait().filter(result -> result.equals(patreonButton)).ifPresent(result
                    -> this.application.getHostServices().showDocument("https://www.patreon.com/samuelszabo"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main_PanelController.class.getName()).log(Level.SEVERE, null, ex);
            showErrorNotification("Unable to find profile picture");
        }
    }

    @FXML
    private void copyBIO205Citation(ActionEvent event) {
        toClipBoardString("Garcia RS, Miyazaki JM, Spikes DA, and O’Neal MH. 2018. Fundamentals of Scientific Inquiry"
                + " in the Biological Sciences II. Stony Brook: Stony Brook University. <www.kuracloud.com> Accessed 2018 January.");
        showNotification("BIO 205 Lab Manual Citation Copied to Clipboard");
    }

    /**
     * Puts a String on the clipboard
     *
     * @param text The string
     */
    private static void toClipBoardString(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    private static void showNotification(String text) {
        Notifications.create().title(text).hideAfter(Duration.seconds(4)).showInformation();
    }

    private static void showErrorNotification(String text) {
        Notifications.create().title(text).hideAfter(Duration.seconds(4)).showError();
    }
}
