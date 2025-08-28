package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.QuarterlyRevenueReportRequest;
import il.cshaifasweng.OCSFMediatorExample.entities.QuarterlyRevenueReportResponse;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportsController {

    @FXML
    private ComboBox<String> reportTypeComboBox;
    @FXML
    private DatePicker dpFrom;
    @FXML
    private DatePicker dpTo;
    @FXML
    private Button btnGenerate;
    @FXML
    private TableView<RevenueRow> tblRevenue;
    @FXML
    private TableColumn<RevenueRow, Integer> colYear;
    @FXML
    private TableColumn<RevenueRow, Integer> colQuarter;
    @FXML
    private TableColumn<RevenueRow, Double> colRevenue;

    private final ObservableList<RevenueRow> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colYear.setCellValueFactory(c -> c.getValue().yearProperty().asObject());
        colQuarter.setCellValueFactory(c -> c.getValue().quarterProperty().asObject());
        colRevenue.setCellValueFactory(c -> c.getValue().revenueProperty().asObject());
        tblRevenue.setItems(data);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @FXML
    private void onGenerate() {
        LocalDate fromLd = dpFrom.getValue();
        LocalDate toLd = dpTo.getValue();
        if (fromLd == null || toLd == null) {
            showAlert("Please select a date range (from and to).");
            return;
        }

        Date from = Date.from(fromLd.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date to = Date.from(toLd.atStartOfDay(ZoneId.systemDefault()).toInstant());

        try {
            QuarterlyRevenueReportRequest req = new QuarterlyRevenueReportRequest(from, to, 0);

            SimpleClient.getClient().sendToServer(req);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error sending request to the server.");
        }
    }

    @Subscribe
    public void onQuarterlyRevenueReportResponse(QuarterlyRevenueReportResponse resp) {
        Platform.runLater(() -> {
            List<RevenueRow> rows = new ArrayList<>();
            for (QuarterlyRevenueReportResponse.Row r : resp.getRows()) {
                rows.add(new RevenueRow(r.getYear(), r.getQuarter(), r.getRevenue()));
            }
            data.setAll(rows);
        });
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            a.showAndWait();
        });
    }
}