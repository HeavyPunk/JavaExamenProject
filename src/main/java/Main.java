import models.PassengerModel;
import utils.CSVFileParser;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var fileParser = new CSVFileParser(",");
        fileParser.parseFile(Path.of("/home/blackpoint/IdeaProjects/Ulearn/11/ExamProject/src/main/resources/Пассажиры Титаника.csv"));
        var models = fileParser.buildModels(new PassengerModel());
    }
}
