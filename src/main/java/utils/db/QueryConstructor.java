package utils.db;

import java.util.Arrays;
import java.util.List;

public class QueryConstructor {
    public String select(String[] fields, String tableName, String wherePredicate){ //TODO: переделать wherePredicate на lambda
        var fieldsStr = String.join(", ", fields);
        return wherePredicate == null
                ? String.format("SELECT %s FROM %s", fieldsStr, tableName)
                : String.format("SELECT %s FROM %s WHERE %s", fieldsStr, tableName, wherePredicate);
    }

    public String insert(String tableName, String[] fields, List<String[]> values){
        var fieldsForQuery = Arrays.stream(fields).map(fieldName -> String.format("\'%s\'", fieldName)).toArray(String[]::new);
        var valuesForQuery = values.stream().map(
                v -> String.format("(%s)", String.join(", ", Arrays.stream(v).map(str -> {
                    if (str == null)
                        return null;
                    var sqlFormattedStr = str.replace("\'", "\'\'");
                    return String.format("\'%s\'", sqlFormattedStr);
                }).toList()))
        ).toList();
        return String.format("INSERT INTO %s(%s) VALUES %s;",
                tableName,
                String.join(", ", fieldsForQuery),
                String.join(", ", valuesForQuery)
        );
    }

    public String createTable(String tableName, List<String[]> fieldsMeta, String primaryKeyField){
        var fieldsFullMeta = fieldsMeta.stream()
                .map(f -> String.format("%s %s %s", f[0], f[1], f[0].equalsIgnoreCase(primaryKeyField) ? "primary key" : ""))
                .toList();
        var fields = String.join(", ", fieldsFullMeta);
        return String.format("CREATE TABLE %s (%s);", tableName, fields);
    }
}
