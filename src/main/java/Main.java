import models.FirstTaskModel_avg;
import models.PassengerModel;
import models.SecondTask;
import models.ThirdTask;
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
        task1();
        task2();
        task3();
    }

    public static void task1(){
        var dbWorker = SqliteDBWorker.getInstance();
        var sqlConstructor = new QueryConstructor();
        var query = sqlConstructor
                .select(new String[]{"sex", "embarked", "avg(fare) AS avg"}, tableName)
                .where("embarked is not null")
                .groupBy(new String[]{"embarked", "sex"})
                .buildQuery();
        var variantWithAvg = dbWorker.executeSqlQuery(query, new FirstTaskModel_avg()).stream()
                .map(model -> {
                    var fmodel = (FirstTaskModel_avg) model;
                    fmodel.embarked = fmodel.embarked.equals("S")
                            ? "Southampton"
                            : fmodel.embarked.equals("C")
                                ? "Cherbourg"
                                : "Queenstown";
                    return fmodel;
                })
                .toList();

        var dataset = new DefaultCategoryDataset();
        for (var model : variantWithAvg){
            dataset.addValue(
                    model.avg,
                    model.embarked == null ? "null" : model.embarked,
                    model.sex == null ? "null" : model.sex
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
            ChartUtils.saveChartAsPNG(new File("task_1_avg.png"), barChart, 640, 480);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void task2(){
        var dbWorker = SqliteDBWorker.getInstance();
        var sqlConstructor = new QueryConstructor();
        var query = sqlConstructor
                .select(new String[]{"max(fare) - min(fare) as 'diff'"}, tableName)
                .where("sex = 'female' and age between 15 and 30")
                .buildQuery();
        var result = dbWorker.executeSqlQuery(query, new SecondTask());
        var model = (SecondTask) result.get(0);
        System.out.format("Разница между максимальной и минимальной ценой билета у женщин от 15 до 30 лет: %f\n", model.diff);
    }

    public static void task3(){
        var dbWorker = SqliteDBWorker.getInstance();
        var sqlConstructor = new QueryConstructor();
        var query = sqlConstructor
                .select(new String[]{"ticket"}, tableName)
                .where("(sex = 'male' and age between 45 and 60) or (sex = 'female' and age between 20 and 25)")
                .buildQuery();
        var result = dbWorker.executeSqlQuery(query, new ThirdTask());
        System.out.println("Список билетов, мужчин в возрасте от 45 до 60 и женщин от 20 до 25.");
        for (var m : result){
            var model = (ThirdTask) m;
            System.out.println(model.ticket);
        }
    }
}
