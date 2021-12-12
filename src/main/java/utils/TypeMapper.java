package utils;

import java.util.HashMap;

public class TypeMapper {
    private HashMap<String, Mapper> availableMappers = new HashMap<>();

    public TypeMapper(){
        availableMappers.put("int", input -> mapToInt(input, -1));
        availableMappers.put("float", input -> mapToFloat(input, -1));
        availableMappers.put("boolean", input -> mapToBool(input, false));
        availableMappers.put("string", input -> input);
    }

    public Object mapToNeeded(Class neededType, String input){
        var name = neededType.getSimpleName();
        if (!availableMappers.containsKey(name.toLowerCase()))
            throw new RuntimeException(String.format("Cannot cast to %s object %s", name, input));
        return availableMappers.get(name.toLowerCase()).map(input);
    }

    public int mapToInt(String input, int defaultValue){
        try {
            var res = Integer.parseInt(input);
            return res;
        }catch (RuntimeException e){
            return defaultValue;
        }
    }

    public float mapToFloat(String input, int defaultValue){
        try {
            var res = Float.parseFloat(input);
            return res;
        }catch (RuntimeException e){
            return defaultValue;
        }
    }

    public boolean mapToBool(String input, boolean defaultValue){
        try {
            var asInt = Integer.parseInt(input);
            return asInt != 0;
        }catch (RuntimeException ignored){
        }
        try {
            var asStrBool = Boolean.parseBoolean(input);
            return asStrBool;
        }catch (RuntimeException e){
            return defaultValue;
        }
    }

    @FunctionalInterface
    private interface Mapper{
        Object map(String input);
    }
}
