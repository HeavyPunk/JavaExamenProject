import models.PassengerModel;
import utils.csv.CSVFileParser;
import utils.db.SqliteDBWorker;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var fileParser = new CSVFileParser(",");
        fileParser.parseFile(Path.of("src/main/resources/Пассажиры Титаника.csv"));
        var models = fileParser.buildModels(new PassengerModel());
        var dbWorker = SqliteDBWorker.getInstance();
        dbWorker.configureDB("Passengers", new PassengerModel(), "PassengerId");
        dbWorker.addModels("Passengers", models);
        var dbModels = dbWorker.getAllModels(null, new PassengerModel());
    }
}
