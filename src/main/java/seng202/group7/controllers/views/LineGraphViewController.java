package seng202.group7.controllers.views;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import seng202.group7.analyses.CrimeFrequency;
import seng202.group7.analyses.GraphUtil;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.data.Report;
import seng202.group7.view.MainScreen;

import java.util.List;


/**
 * Line Graph View Controller displays crime over time graphs based on user input from Graph Menu Controller
 *
 * @author Sam
 */
public class LineGraphViewController  {

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private LineChart<String, Integer> overTimeChart;

    /**
     * Called from the Graph View Controller when the scene is initialized, gets data from the Data Accessor using the
     * provided query, gets the appropriate title and displays the crime over time graph.
     * @param query the string query provided by user input in Graph Menu Controller
     * @param choices ArrayList of strings provided by user input in Graph Menu Controller
     */
    public void prepareLineGraph(String query, List<String> choices) {
        List<Report> sortedData;
        try {
            sortedData = DataAccessor.getInstance().getData(query); //Gets the sorted data from the Database
        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
            System.out.println(query);
            e.printStackTrace();
            return;
        }
        List<CrimeFrequency> crimeOverTime = GraphUtil.crimeOverTime(sortedData);
        String title = GraphUtil.getTitle(choices); //Gets the title from graph util
        XYChart.Series<String, Integer> dataSet = new XYChart.Series<>();
        for (CrimeFrequency freq: crimeOverTime) {
            dataSet.getData().add(new XYChart.Data<> (freq.getDate(), freq.getFrequency())); //Adds each value into the chart
        }
        this.overTimeChart.setLegendVisible(false); //Adds the correct values to the view
        this.overTimeChart.getData().add(dataSet);
        this.overTimeChart.setTitle(title);
        this.xAxis.setLabel("Date");
        this.yAxis.setLabel("Number of Crime");
    }
}
