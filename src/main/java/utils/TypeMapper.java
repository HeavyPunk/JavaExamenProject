package utils;

import java.util.HashMap;

public class TypeMapper {
    private HashMap<String, FromStringMapper> fromStringMappers = new HashMap<>();
    private HashMap<String, ToStringMapper> toStringMappers = new HashMap<>();

    public TypeMapper(){
        fromStringMappers.put("int", input -> mapToInt(input, -1));
        fromStringMappers.put("float", input -> mapToFloat(input, -1));
        fromStringMappers.put("boolean", input -> mapToBool(input, false));
        fromStringMappers.put("string", input -> mapToString(input, null));

        toStringMappers.put("integer", input -> Integer.toString((int) input));
        toStringMappers.put("float", input -> Float.toString((float) input));
        toStringMappers.put("boolean", input -> Boolean.toString((boolean) input));
        toStringMappers.put("string", input -> String.format("%s", input));
    }

    public Object mapToNeeded(Class neededType, String input){
        var name = neededType.getSimpleName();
        if (!fromStringMappers.containsKey(name.toLowerCase()))
            throw new RuntimeException(String.format("Cannot cast to %s object %s", name, input));
        return fromStringMappers.get(name.toLowerCase()).map(input);
    }

    public String mapToString(Object obj){
        if (obj == null)
            return null;
        var typeName = obj.getClass().getSimpleName();
        if (!toStringMappers.containsKey(typeName.toLowerCase()))
            throw new RuntimeException(String.format("Cannot convert to string the type %s", typeName));
        return toStringMappers.get(typeName.toLowerCase()).map(obj);
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

    public String mapToString(String input, String defaultValue){
        return input == null || input.isEmpty() ? defaultValue : input;
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
    private interface FromStringMapper {
        Object map(String input);
    }

    @FunctionalInterface
    private interface ToStringMapper{
        String map(Object input);
    }
}
