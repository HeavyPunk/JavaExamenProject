package utils;

import abstractions.FileParser;
import abstractions.Model;
import models.CSVModel;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVFileParser implements FileParser {
    private String delimiter;
    private CSVModel rawData;
    private CSVReader reader = new CSVReader();
    private TypeMapper typeMapper = new TypeMapper();

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
            var model = buildNewModel(baseModel, rawData.titles, row);
            result.add(model);
        }
        return result;
    }

    private Model buildNewModel(Model baseModel, String[] titles, String[] data){
        var instance = buildNewInstance(baseModel);
        assert instance != null;
        var type = instance.getClass();
        var fields = type.getDeclaredFields();
        for (var d = 0; d < data.length; d++){
            int finalD = d;
            try {
                var fieldContainer = Arrays.stream(fields)
                        .filter(f -> f.getName().equalsIgnoreCase(titles[finalD]))
                        .findFirst();
                if (fieldContainer.isEmpty())
                    continue;
                var field = fieldContainer.get();
                field.setAccessible(true);
                var value = typeMapper.mapToNeeded(fields[d].getType(), data[d]);
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private Model buildNewInstance(Model baseModel){
        var type = baseModel.getClass();
        try {
            return (Model) Class.forName(type.getName()).getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
