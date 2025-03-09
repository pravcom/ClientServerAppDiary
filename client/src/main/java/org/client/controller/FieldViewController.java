package org.client.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;
import org.exchange.task.Task;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FieldViewController {
    public TableView tableView;
    public TextField dateField;
    public TextField projectField;
    public TextField descriptionField;
    public TextField hoursField;
    @Getter
    private final List<Task> taskList = new ArrayList<>();
    @Setter
    private MainController mainController;

    public void setTaskList(List<Task> taskList) {
        if (!taskList.isEmpty()) {
            this.taskList.clear();
            this.taskList.addAll(taskList);
            fillTable();
        }
    }

    public void fillColumns() {
        TableColumn<Task, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Task, LocalDate> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_key"));

        TableColumn<Task, String> projectColumn = new TableColumn<>("Проект");
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("project"));

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Task, Float> hoursColumn = new TableColumn<>("Часы");
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hoursSpent"));

        tableView.getColumns().addAll(idColumn, dateColumn, projectColumn, descriptionColumn, hoursColumn);
        //Устанавливаем выборку строк
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void fillTable() {
        ObservableList<Task> data = FXCollections.observableArrayList();
        data.addAll(taskList);
        tableView.setItems(data);
    }

    public void addEntry(ActionEvent actionEvent) {
        if (hoursField.getText().isEmpty()) {
            hoursField.setStyle("-fx-background-color: yellow;");
        } else {
            hoursField.setStyle("-fx-background-color: white;");
            Task task = new Task();

            task.setDate_key(Date.valueOf(dateField.getText()));
            task.setProject(projectField.getText());
            task.setDescription(descriptionField.getText());
            task.setHoursSpent(Float.valueOf(hoursField.getText()));

            mainController.addTasks(task);
            taskList.add(task);
            fillTable();
        }
    }

    public void setDateField(LocalDate date) {
        this.dateField.setText(date.toString());
    }

    /**
     * слушатель для отображения статуса валидации
     */
    public void hoursFieldValidation() {
        hoursField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")) {
                hoursField.setStyle("-fx-text-fill: green;");
            } else {
                hoursField.setStyle("-fx-text-fill: red;");
            }
        });
    }

    public void deleteEntry(ActionEvent actionEvent) {
        Task selectedTask = (Task) tableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            mainController.deleteTasks(selectedTask);
            taskList.remove(selectedTask);
            fillTable();
        }
    }
}
