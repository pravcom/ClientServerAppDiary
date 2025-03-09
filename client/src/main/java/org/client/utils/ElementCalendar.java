package org.client.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.client.Client;
import org.client.controller.FieldViewController;
import org.client.controller.MainController;
import org.exchange.task.Task;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Stack;

public class ElementCalendar {
    private final double calendarWidth;
    private final double calendarHeight;
    private final double spacing;
    private final double padding;
    private final MainController mainController;

    private ElementCalendar (Builder builder){
        this.calendarWidth = builder.calendarWidth;
        this.calendarHeight = builder.calendarHeight;
        this.spacing = builder.spacing;
        this.padding = builder.padding;
        this.mainController = builder.mainController;

    }

    public Rectangle getRectangle() {
        // Создаем прямоугольник с границей
        Rectangle rectangle = new Rectangle(); // Размеры прямоугольника
        rectangle.setFill(Color.TRANSPARENT); // Прозрачный фон
        rectangle.setStroke(Color.BLACK); // Цвет границы
        rectangle.setStrokeWidth(2); // Толщина границы

        double rectangleWidth = (calendarWidth / 7 - spacing - padding); // Ширина прямоугольника
        rectangle.setWidth(rectangleWidth);
        double rectangleHeight = (calendarHeight / 6 - spacing - padding); // Высота прямоугольника
        rectangle.setHeight(rectangleHeight);

        return rectangle;
    }

    public StackPane getField(Node node1, Node node2) {
        // Помещаем Label и Rectangle в StackPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(node1, node2);
        return stackPane;
    }

    public void setOnMouseShowPopup(Node node, Popup popup) {
        // Обработчик события наведения курсора
        node.setOnMouseEntered(event -> {
            popup.show(node, event.getScreenX(), event.getScreenY() + 10);
        });
        // Обработчик события ухода курсора
        node.setOnMouseExited(event -> {
            popup.hide();
        });

    }

    public void clickOnRectangle(Node node, Day day) {
        node.setOnMouseClicked(event -> {
            Stage stage = new Stage();
            URL fxmlLocation = Client.class.getResource("/field-view.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
            try {
                Scene scene = new Scene(fxmlLoader.load(),600, 300);
                stage.setTitle("Окно");
                stage.setScene(scene);
                stage.show();

                FieldViewController fieldViewController = fxmlLoader.getController();
                fieldViewController.fillColumns();
                fieldViewController.setTaskList(day.getTasks());
                fieldViewController.setDateField(day.getDate());
                fieldViewController.setMainController(mainController);
                fieldViewController.hoursFieldValidation();
                mainController.setFieldViewController(fieldViewController);

                //При закрытии окна просим заного отрисовать календарь
                stage.setOnCloseRequest(windowEvent -> {
                    if (! day.getTasks().equals(fieldViewController.getTaskList())) {
                        int month = day.getDate().getMonth().getValue();
                        mainController.clearCalendar();
                        mainController.fillCalendar(month);
                        mainController.drawCalendar(month);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public Popup getPopup( List<Task> tasks ) {
        // Создаём Popup
        Popup popup = new Popup();
        if (tasks.isEmpty()) {
            return popup;
        }
        TableView tableView = new TableView();
        tableView.setPrefSize(400, 120);
        ObservableList<Task> data = FXCollections.observableArrayList();
        data.addAll(tasks);
        tableView.setItems(data);

        TableColumn<Task, String> projectColumn = new TableColumn<>("Проект");
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("project"));

        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Task, Float> hoursColumn = new TableColumn<>("Часы");
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("hoursSpent"));

        tableView.getColumns().addAll(projectColumn, descriptionColumn, hoursColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        popup.getContent().addAll(tableView);
        return popup;
    }

    public static class Builder {
        private double calendarWidth;
        private double calendarHeight;
        private double spacing;
        private double padding;
        private MainController mainController;

        public Builder setCalendarWidth(double calendarWidth) {
            this.calendarWidth = calendarWidth;
            return this;
        }

        public Builder setCalendarHeight(double calendarHeight) {
            this.calendarHeight = calendarHeight;
            return this;
        }

        public Builder setSpacing(double spacing) {
            this.spacing = spacing;
            return this;
        }

        public Builder setPadding(double padding) {
            this.padding = padding;
            return this;
        }

        public Builder setMainController(MainController mainController) {
            this.mainController = mainController;
            return this;
        }

        public ElementCalendar build() {
            return new ElementCalendar(this);
        }
    }


}
