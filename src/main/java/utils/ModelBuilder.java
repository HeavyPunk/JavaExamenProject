package utils;

import abstractions.Deserializer;
import abstractions.Model;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ModelBuilder implements Deserializer {
    private final TypeMapper typeMapper = new TypeMapper();

    @Override
    public Model buildNewModel(Model baseModel, String[] titles, String[] data){
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
