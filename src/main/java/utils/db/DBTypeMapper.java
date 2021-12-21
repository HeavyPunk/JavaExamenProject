package utils.db;

import java.util.HashMap;

public class DBTypeMapper {
    private HashMap<String, String> mappers;
    public DBTypeMapper(){
        mappers = new HashMap<>(); //TODO:сделать настраиваемый/адаптивный маппер
        mappers.put("int", "int");
        mappers.put("string", "varchar(255)");
        mappers.put("boolean", "int");
        mappers.put("float", "decimal(6, 3)");
    }
    public String map(String javaType){
        if(mappers.containsKey(javaType.toLowerCase()))
            return mappers.get(javaType.toLowerCase());
        throw new RuntimeException(String.format("Not found SQL type for %s", javaType));
    }
}
