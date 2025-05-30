package com.example.sto.controller;

import javafx.fxml.FXML;


import com.example.sto.dao.*;
import com.example.sto.entity.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private TextField workerNameField;
    @FXML private TextField workerPaymentField;
    @FXML private TableView<Worker> workerTable;
    @FXML private TableColumn<Worker, String> workerNameColumn;
    @FXML private TableColumn<Worker, BigDecimal> workerPaymentColumn;

    @FXML private TextField detailTitleField;
    @FXML private TextField detailPriceField;
    @FXML private TextField detailQuantityField;
    @FXML private TableView<Detail> detailTable;
    @FXML private TableColumn<Detail, String> detailTitleColumn;
    @FXML private TableColumn<Detail, BigDecimal> detailPriceColumn;
    @FXML private TableColumn<Detail, Integer> detailQuantityColumn;

    @FXML private TextField autoNameField;
    @FXML private TextField autoOwnerField;
    @FXML private TableView<Auto> autoTable;
    @FXML private TableColumn<Auto, String> autoNameColumn;
    @FXML private TableColumn<Auto, String> autoOwnerColumn;

    @FXML private ComboBox<Worker> reportWorkerCombo;
    @FXML private ComboBox<Auto> reportAutoCombo;
    @FXML private TextField reportHoursField;
    @FXML private ListView<Detail> availableDetailsList;
    @FXML private ListView<Detail> selectedDetailsList;
    @FXML private TableView<RepairReport> reportTable;
    @FXML private TableColumn<RepairReport, LocalDateTime> reportDateColumn;
    @FXML private TableColumn<RepairReport, String> reportWorkerColumn;
    @FXML private TableColumn<RepairReport, String> reportAutoColumn;
    @FXML private TableColumn<RepairReport, BigDecimal> reportTotalColumn;
    @FXML private TableColumn<RepairReport, Void> reportStatusColumn;


    private WorkerDAO workerDAO = new WorkerDAO();
    private DetailDAO detailDAO = new DetailDAO();
    private AutoDAO autoDAO = new AutoDAO();
    private RepairReportDAO reportDAO = new RepairReportDAO();


    private ObservableList<Worker> workers = FXCollections.observableArrayList();
    private ObservableList<Detail> details = FXCollections.observableArrayList();
    private ObservableList<Auto> autos = FXCollections.observableArrayList();
    private ObservableList<RepairReport> reports = FXCollections.observableArrayList();
    private ObservableList<Detail> selectedDetails = FXCollections.observableArrayList();

    // Status progression array
    private final String[] statusProgression = {"created", "in progress", "done", "closed"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        loadData();
        setupComboBoxes();
    }

    private void initializeTableColumns() {
        workerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        workerPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentPerHour"));

        detailTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        detailPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        detailQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        autoNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        autoOwnerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));

        reportDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        reportWorkerColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getWorker() != null ? cellData.getValue().getWorker().getName() : ""));
        reportAutoColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getAuto() != null ? cellData.getValue().getAuto().getName() : ""));
        reportTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Setup status column with custom cell factory
        setupStatusColumn();
    }

    private void setupStatusColumn() {
        reportStatusColumn.setCellFactory(param -> new TableCell<RepairReport, Void>() {
            private final HBox container = new HBox();
            private final Label statusLabel = new Label();
            private final Button nextButton = new Button("→");

            {
                container.setAlignment(Pos.CENTER);
                container.setSpacing(10);
                statusLabel.setStyle("-fx-font-weight: bold;");
                nextButton.setStyle("-fx-font-size: 12px; -fx-min-width: 30px;");

                nextButton.setOnAction(event -> {
                    RepairReport report = getTableView().getItems().get(getIndex());
                    updateReportStatus(report);
                });

                container.getChildren().addAll(statusLabel, nextButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    RepairReport report = getTableView().getItems().get(getIndex());
                    String status = report.getStatus() != null ? report.getStatus() : "created";
                    statusLabel.setText(status);

//
//                    switch (status) {
//                        case "created":
//                            statusLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
//                            break;
//                        case "in progress":
//                            statusLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
//                            break;
//                        case "done":
//                            statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
//                            break;
//                        case "closed":
//                            statusLabel.setStyle("-fx-text-fill: #9E9E9E; -fx-font-weight: bold;");
//                            nextButton.setDisable(true);
//                            break;
//                    }

                    if (status.equals("closed")){
                        nextButton.setDisable(true);
                    }
                    if (!"closed".equals(status)) {
                        nextButton.setDisable(false);
                    }

                    setGraphic(container);
                }
            }
        });
    }

    private void updateReportStatus(RepairReport report) {
        try {
            String currentStatus = report.getStatus() != null ? report.getStatus() : "created";
            String nextStatus = getNextStatus(currentStatus);

            report.setStatus(nextStatus);
            reportDAO.update(report); // Assuming you have an update method in your DAO

            // Refresh the table to show updated status
            reportTable.refresh();

        } catch (Exception e) {
            showAlert("Error", "Failed to update report status: " + e.getMessage());
        }
    }

    private String getNextStatus(String currentStatus) {
        for (int i = 0; i < statusProgression.length; i++) {
            if (statusProgression[i].equals(currentStatus)) {
                if (i < statusProgression.length - 1) {
                    return statusProgression[i + 1];
                } else {
                    return currentStatus; // Already at final status
                }
            }
        }
        return "created"; // Default fallback
    }

    private void loadData() {
        try {
            workers.setAll(workerDAO.findAll());
            details.setAll(detailDAO.findAll());
            autos.setAll(autoDAO.findAll());
            reports.setAll(reportDAO.findAll());

            workerTable.setItems(workers);
            detailTable.setItems(details);
            autoTable.setItems(autos);
            reportTable.setItems(reports);
            availableDetailsList.setItems(details);
            selectedDetailsList.setItems(selectedDetails);
        } catch (Exception e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }
    }

    private void setupComboBoxes() {
        reportWorkerCombo.setItems(workers);
        reportAutoCombo.setItems(autos);

        reportWorkerCombo.setCellFactory(param -> new ListCell<Worker>() {
            @Override
            protected void updateItem(Worker item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        reportAutoCombo.setCellFactory(param -> new ListCell<Auto>() {
            @Override
            protected void updateItem(Auto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getOwnerName() + ")");
            }
        });

        reportWorkerCombo.setButtonCell(new ListCell<Worker>() {
            @Override
            protected void updateItem(Worker item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        reportAutoCombo.setButtonCell(new ListCell<Auto>() {
            @Override
            protected void updateItem(Auto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " (" + item.getOwnerName() + ")");
            }
        });
    }

    @FXML
    private void addWorker() {
        try {
            String name = workerNameField.getText().trim();
            String paymentStr = workerPaymentField.getText().trim();

            if (name.isEmpty() || paymentStr.isEmpty()) {
                showAlert("Error", "Please fill all worker fields");
                return;
            }

            BigDecimal payment = new BigDecimal(paymentStr);
            Worker worker = new Worker(null, name, payment);
            workerDAO.save(worker);

            clearWorkerFields();
            loadData();
            showAlert("Success", "Worker added successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid payment amount");
        } catch (Exception e) {
            showAlert("Error", "Failed to add worker: " + e.getMessage());
        }
    }

    @FXML
    private void addDetail() {
        try {
            String title = detailTitleField.getText().trim();
            String priceStr = detailPriceField.getText().trim();
            String quantityStr = detailQuantityField.getText().trim();

            if (title.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
                showAlert("Error", "Please fill all detail fields");
                return;
            }

            BigDecimal price = new BigDecimal(priceStr);
            Integer quantity = Integer.parseInt(quantityStr);
            Detail detail = new Detail(null, title, price, quantity);
            detailDAO.save(detail);

            clearDetailFields();
            loadData();
            showAlert("Success", "Detail added successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid price or quantity");
        } catch (Exception e) {
            showAlert("Error", "Failed to add detail: " + e.getMessage());
        }
    }

    @FXML
    private void addAuto() {
        try {
            String name = autoNameField.getText().trim();
            String owner = autoOwnerField.getText().trim();

            if (name.isEmpty() || owner.isEmpty()) {
                showAlert("Error", "Please fill all auto fields");
                return;
            }

            Auto auto = new Auto(null, name, owner);
            autoDAO.save(auto);

            clearAutoFields();
            loadData();
            showAlert("Success", "Auto added successfully");
        } catch (Exception e) {
            showAlert("Error", "Failed to add auto: " + e.getMessage());
        }
    }

    @FXML
    private void addDetailToReport() {
        Detail selected = availableDetailsList.getSelectionModel().getSelectedItem();
        if (selected != null && !selectedDetails.contains(selected)) {
            selectedDetails.add(selected);
        }
    }

    @FXML
    private void removeDetailFromReport() {
        Detail selected = selectedDetailsList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedDetails.remove(selected);
        }
    }

    @FXML
    private void createReport() {
        try {
            Worker worker = reportWorkerCombo.getValue();
            Auto auto = reportAutoCombo.getValue();
            String hoursStr = reportHoursField.getText().trim();

            if (worker == null || auto == null || hoursStr.isEmpty() || selectedDetails.isEmpty()) {
                showAlert("Error", "Please fill all report fields and select at least one detail");
                return;
            }

            BigDecimal hours = new BigDecimal(hoursStr);

            RepairReport report = RepairReport.builder()
                    .date(LocalDateTime.now().toString())
                    .worker(worker)
                    .auto(auto)
                    .details(new ArrayList<>(selectedDetails))
                    .workedHours(hours)
                    .status("created") // Set default status
                    .build();

            report.setTotalCost(report.calculateTotalCost());
            reportDAO.save(report);

            clearReportFields();
            loadData();
            showAlert("Success", "Report created successfully. Total cost: $" + report.getTotalCost());
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid hours amount");
        } catch (Exception e) {
            showAlert("Error", "Failed to create report: " + e.getMessage());
        }
    }

    private void clearWorkerFields() {
        workerNameField.clear();
        workerPaymentField.clear();
    }

    private void clearDetailFields() {
        detailTitleField.clear();
        detailPriceField.clear();
        detailQuantityField.clear();
    }

    private void clearAutoFields() {
        autoNameField.clear();
        autoOwnerField.clear();
    }

    private void clearReportFields() {
        reportWorkerCombo.setValue(null);
        reportAutoCombo.setValue(null);
        reportHoursField.clear();
        selectedDetails.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}