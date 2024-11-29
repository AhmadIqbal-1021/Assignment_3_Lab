package com.example.lab_task_29_november;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

public class FormAppWithFindAndClose extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Left Pane - Form Fields
        GridPane formPane = new GridPane();
        formPane.setPadding(new Insets(10));
        formPane.setVgap(10);
        formPane.setHgap(10);

        // Labels and Input Fields
        Label fullNameLabel = new Label("FullName:");
        TextField fullNameField = new TextField();

        Label idLabel = new Label("ID:");
        TextField idField = new TextField();

        Label genderLabel = new Label("Gender:");
        ToggleGroup genderGroup = new ToggleGroup();
        RadioButton maleButton = new RadioButton("Male");
        RadioButton femaleButton = new RadioButton("Female");
        maleButton.setToggleGroup(genderGroup);
        femaleButton.setToggleGroup(genderGroup);
        HBox genderBox = new HBox(10, maleButton, femaleButton);

        Label provinceLabel = new Label("HomeProvince:");
        TextField provinceField = new TextField();

        Label dobLabel = new Label("DOB:");
        DatePicker dobPicker = new DatePicker();

        // Adding components to GridPane
        formPane.add(fullNameLabel, 0, 0);
        formPane.add(fullNameField, 1, 0);

        formPane.add(idLabel, 0, 1);
        formPane.add(idField, 1, 1);

        formPane.add(genderLabel, 0, 2);
        formPane.add(genderBox, 1, 2);

        formPane.add(provinceLabel, 0, 3);
        formPane.add(provinceField, 1, 3);

        formPane.add(dobLabel, 0, 4);
        formPane.add(dobPicker, 1, 4);

        // Right Pane - Buttons
        VBox buttonPane = new VBox(10);
        buttonPane.setPadding(new Insets(10));

        Button newButton = new Button("New");
        Button deleteButton = new Button("Delete");
        Button restoreButton = new Button("Restore");
        Button findPrevButton = new Button("Find Prev");
        Button findNextButton = new Button("Find Next");
        Button criteriaButton = new Button("Criteria");
        Button findButton = new Button("Find");
        Button closeButton = new Button("Close");

        // Disable non-functional buttons
        deleteButton.setDisable(true);
        restoreButton.setDisable(true);
        findPrevButton.setDisable(true);
        findNextButton.setDisable(true);
        criteriaButton.setDisable(true);

        buttonPane.getChildren().addAll(
                newButton, deleteButton, restoreButton,
                findPrevButton, findNextButton, criteriaButton, findButton, closeButton
        );

        // Save to File on New Button Click
        newButton.setOnAction(e -> {
            try {
                saveToFile(fullNameField, idField, genderGroup, provinceField, dobPicker);

                // Clear the fields for new entry
                fullNameField.clear();
                idField.clear();
                genderGroup.selectToggle(null);
                provinceField.clear();
                dobPicker.setValue(null);

                // Confirmation Alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Record saved successfully!");
                alert.showAndWait();
            } catch (IOException ex) {
                // Error Alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to save the record: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        // Find Record on Find Button Click
        findButton.setOnAction(e -> {
            // Prompt the user to enter an ID
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Find Record");
            dialog.setHeaderText("Enter the ID to search for:");
            dialog.setContentText("ID:");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                String idToFind = result.get().trim();
                try {
                    String record = findRecordById(idToFind);
                    if (record != null) {
                        // Display the record in an Alert dialog
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Record Found");
                        alert.setHeaderText("Record Details:");
                        alert.setContentText(record);
                        alert.showAndWait();
                    } else {
                        // Record not found
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Not Found");
                        alert.setHeaderText(null);
                        alert.setContentText("No record found with ID: " + idToFind);
                        alert.showAndWait();
                    }
                } catch (IOException ex) {
                    // Error Alert
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error occurred while searching: " + ex.getMessage());
                    alert.showAndWait();
                }
            }
        });

        // Close the application on Close button click
        closeButton.setOnAction(e -> primaryStage.close());

        // Main Layout
        BorderPane mainPane = new BorderPane();
        mainPane.setLeft(formPane);
        mainPane.setRight(buttonPane);

        // Scene and Stage
        Scene scene = new Scene(mainPane, 400, 300);
        primaryStage.setTitle("Form GUI with Find and Close");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Save the form data to a file
     */
    private void saveToFile(TextField fullNameField, TextField idField, ToggleGroup genderGroup, TextField provinceField, DatePicker dobPicker) throws IOException {
        String fullName = fullNameField.getText();
        String id = idField.getText();
        String gender = genderGroup.getSelectedToggle() == null ? "Not Specified" : ((RadioButton) genderGroup.getSelectedToggle()).getText();
        String province = provinceField.getText();
        String dob = dobPicker.getValue() == null ? "Not Specified" : dobPicker.getValue().toString();

        // Prepare data for saving
        String record = String.format("FullName: %s, ID: %s, Gender: %s, HomeProvince: %s, DOB: %s",
                fullName, id, gender, province, dob);

        // Append the data to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("records.txt", true))) {
            writer.write(record);
            writer.newLine();
        }
    }

    /**
     * Find a record by ID
     */
    private String findRecordById(String id) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("records.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ID: " + id)) {
                    return line;
                }
            }
        }
        return null; // Record not found
    }

    public static void main(String[] args) {
        launch(args);
    }
}
