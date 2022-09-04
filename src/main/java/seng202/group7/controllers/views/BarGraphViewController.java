package seng202.group7.controllers.views;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import seng202.group7.analyses.GraphUtil;
import seng202.group7.analyses.Tuple;
import seng202.group7.controllers.data.ControllerData;
import seng202.group7.data.CustomException;
import seng202.group7.data.DataAccessor;
import seng202.group7.data.Report;
import seng202.group7.view.MainScreen;
import java.util.List;
import java.util.Collections;

/**
 * Graph View Controller controls displays 7 different bar graphs based on user input from Graph Menu Controller.
 * @author Jack McCorkindale
 * @author Sam McMillan
 */
public class BarGraphViewController  {

    @FXML
    private BarChart<String, Integer> crimeChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;


    /**
     * Called from the Graph View controller when the scene is initialized, checks what input is currently
     * in the combo box from graph menu class with pseudo classes and loads the correct graph accordingly.
     *
     */

    public void prepareBarGraph(String option) {
        List<Report> data;
        try {
            data = DataAccessor.getInstance().getAll(ControllerData.getInstance().getCurrentList()); //Gets all the data from the current list
        } catch (CustomException e) {
            MainScreen.createWarnWin(e);
            return;
        }
        List<Tuple<String, Integer>> dataSet;
        switch (option) {
            case "Most Frequent Crime Types" -> {
                dataSet = GraphUtil.primaryFrequencyRank(data); //Ranks by crime type frequency
                Collections.reverse(dataSet); //Reverses the list so the tuples with the highest value are first
                generateBarGraph(dataSet, "Most Frequent Types of Crime", "Crime Types", "Number of Crime");
            }
            case "Most Dangerous Wards" -> {
                dataSet = GraphUtil.wardFrequencyRank(data); //Ranks by crime in each ward frequency
                Collections.reverse(dataSet); //Reverses the list so the tuples with the highest freq value are first
                generateBarGraph(dataSet, "Most Dangerous Wards", "Wards", "Number of Crime");
            }
            case "Most Dangerous Streets" -> {
                dataSet = GraphUtil.streetRank(data); //Ranks by sub string of block so crime in each street frequency
                Collections.reverse(dataSet); //Reverses the list so the tuples with the highest freq value are first
                generateBarGraph(dataSet, "Most Dangerous Streets", "Streets", "Number of Crime");
            }
            case "Most Dangerous Beats" -> {
                dataSet = GraphUtil.beatFrequencyRank(data); //Ranks by crime in each bea frequency
                Collections.reverse(dataSet); //Reverses the list so the tuples with the highest freq value are first
                generateBarGraph(dataSet, option, "Beats", "Number of Crime");
            }
            case "Less Frequent Crime Types" -> {
                dataSet = GraphUtil.primaryFrequencyRank(data); //Ranks by crime type frequency
                generateBarGraph(dataSet, option, "Crime Types", "Number of Crime");
            }
            case "Safest Wards" -> {
                dataSet = GraphUtil.wardFrequencyRank(data); //Ranks by crime in each ward frequency
                generateBarGraph(dataSet, option, "Wards", "Number of Crime");
            }
            case "Safest Beats" -> {
                dataSet = GraphUtil.beatFrequencyRank(data); //Ranks by crime in each bea frequency
                generateBarGraph(dataSet, option, "Beats", "Number of Crimes");
            }
        }
    }

    /**
     *Create the graph by inputting the values into the bar chart object.
     *
     * @param data      The List of String integer tuples from Rank class
     * @param title     The String title of the graph
     * @param xLabel    The x axis label of the graphS
     * @param yLabel    The y axis label of the graph
     */

    public void generateBarGraph(List<Tuple<String, Integer>> data, String title, String xLabel, String yLabel) {
        XYChart.Series<String, Integer> dataSet = new XYChart.Series<>();
        int i = 0;
        for (Tuple<String, Integer> tuple: data) {
            i++;
            if (i == 20) {
                break;
            }
            dataSet.getData().add(new XYChart.Data<> (String.valueOf(tuple.x), tuple.y));
        }
        this.crimeChart.setLegendVisible(false);
        this.crimeChart.getData().add(dataSet);
        this.crimeChart.setTitle(title);
        this.xAxis.setLabel(xLabel);
        this.yAxis.setLabel(yLabel);
    }
}