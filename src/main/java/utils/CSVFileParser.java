package utils;

import abstractions.FileParser;
import abstractions.Model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVFileParser implements FileParser {
    private ArrayList<ArrayList<String>> rowData;

    @Override
    public void parseFile(Path pathToFile) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<Model> buildModels(Model baseModel) {
        var type = baseModel.getClass();
        var fields = type.getDeclaredFields();
        //TODO: достроить модель
    }
}
