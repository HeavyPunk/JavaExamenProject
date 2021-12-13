package utils;

import abstractions.Deserializer;
import abstractions.FileParser;
import abstractions.Model;
import models.CSVModel;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVFileParser implements FileParser {
    private final String delimiter;
    private CSVModel rawData;
    private final CSVReader reader = new CSVReader();
    private final Deserializer modelBuilder = new ModelBuilder();

    public CSVFileParser(String delimiter){
        this.delimiter = delimiter;
    }

    @Override
    public void parseFile(Path pathToFile) {
        this.rawData = this.reader.read(pathToFile, this.delimiter);
    }

    @Override
    public List<Model> buildModels(Model baseModel) {
        var type = baseModel.getClass();
        var result = new ArrayList<Model>();
        for (var row : rawData.data){
            var model = modelBuilder.buildNewModel(baseModel, rawData.titles, row);
            result.add(model);
        }
        return result;
    }
}
