package abstractions;

import java.util.List;

public interface DBWorker {
    void configureDB(String tableName, Model baseModel, String primaryKeyField);
    void addModel(String tableName, Model model);
    void addModels(String tableName, List<Model> models);
    List<Model> getAllModels(String wherePredicate, Model baseModel);
    List<Model> executeSqlQuery(String sqlQuery, Model baseModel);
}
