import abstractions.DBWorker;
import models.FirstTaskModel_avg;
import models.FirstTaskModel_sum;
import models.PassengerModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import utils.csv.CSVFileParser;
import utils.db.QueryConstructor;
import utils.db.SqliteDBWorker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    private static final String tableName = "Passengers";

    public static void main(String[] args) {
        var fileParser = new CSVFileParser(",");
        fileParser.parseFile(Path.of("src/main/resources/Пассажиры Титаника.csv"));
        var models = fileParser.buildModels(new PassengerModel());
        var dbWorker = SqliteDBWorker.getInstance();
        dbWorker.configureDB(tableName, new PassengerModel(), "PassengerId");
        dbWorker.addModels(tableName, models);
        var dbModels = dbWorker.getAllModels(null, new PassengerModel());
        task1(dbWorker);
    }

    public static void task1(DBWorker dbWorker){
        var sqlConstructor = new QueryConstructor();
        var queryWithAvg = sqlConstructor
                .select(new String[]{"sex", "embarked", "avg(fare) AS avg"}, tableName)
                .groupBy(new String[]{"embarked", "sex"})
                .buildQuery();
        var variantWithAvg = dbWorker.executeSqlQuery(queryWithAvg, new FirstTaskModel_avg());

        var queryWithSum = sqlConstructor
                .select(new String[]{"sex", "embarked", "sum(fare) as sum"}, tableName)
                .groupBy(new String[]{"embarked", "sex"})
                .buildQuery();
        var variantWithSum = dbWorker.executeSqlQuery(queryWithSum, new FirstTaskModel_sum());
        var dataset = new DefaultCategoryDataset();
        for (var model : variantWithSum){
            var firstTaskModel_sum = (FirstTaskModel_sum) model;
            dataset.addValue(
                    firstTaskModel_sum.sum,
                    firstTaskModel_sum.embarked == null ? "null" : firstTaskModel_sum.embarked,
                    firstTaskModel_sum.sex == null ? "null" : firstTaskModel_sum.sex
            );
        }
        var barChart = ChartFactory.createBarChart(
                "Цена билетов пассажиров",
                "Группы пассажиров",
                "Цена",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        try {
            ChartUtils.saveChartAsPNG(new File("task_1_sum.png"), barChart, 640, 480);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataset = new DefaultCategoryDataset();
        for (var model : variantWithAvg){
            var firstTaskModel_avg = (FirstTaskModel_avg) model;
            dataset.addValue(
                    firstTaskModel_avg.avg,
                    firstTaskModel_avg.embarked == null ? "null" : firstTaskModel_avg.embarked,
                    firstTaskModel_avg.sex == null ? "null" : firstTaskModel_avg.sex
            );
        }
        barChart = ChartFactory.createBarChart(
                "Цена билетов пассажиров",
                "Группы пассажиров",
                "Цена",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        try {
            ChartUtils.saveChartAsPNG(new File("task_1_avg.png"), barChart, 640, 480);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
