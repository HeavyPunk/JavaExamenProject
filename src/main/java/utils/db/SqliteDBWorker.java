package utils.db;

import abstractions.DBWorker;
import abstractions.Model;
import configuration.SettingsReader;
import models.Settings;
import org.sqlite.JDBC;
import utils.ModelBuilder;
import utils.TypeMapper;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqliteDBWorker implements DBWorker {
    private static Settings appSettings = new SettingsReader().getAppSettings();
    private static SqliteDBWorker instance;
    private Connection connection;
    private QueryConstructor queryConstructor = new QueryConstructor();
    private DBTypeMapper DBtypeMapper = new DBTypeMapper();
    private TypeMapper typeMapper = new TypeMapper();
    private ModelBuilder modelBuilder = new ModelBuilder();

    private SqliteDBWorker(){
        try {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(appSettings.DB_CONNECTION_PATH);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SqliteDBWorker getInstance(){
        if (instance == null)
            instance = new SqliteDBWorker();
        return instance;
    }

    @Override
    public void configureDB(String tableName, Model baseModel, String primaryKeyField) {
        var type = baseModel.getClass();
        var fields = type.getDeclaredFields();
        var fieldsWithMeta = Arrays.stream(fields)
                .map(f -> new String[]{f.getName(), this.DBtypeMapper.map(f.getType().getSimpleName())})
                .toList();
        var selectAllQuery = queryConstructor
                .select(new String[]{"*"}, tableName)
                .buildQuery();
        var createTableQuery = queryConstructor.createTable(tableName, fieldsWithMeta, primaryKeyField);
        if (!executeQueryWithoutResult(selectAllQuery, System.out)){
            executeQueryWithoutResult(createTableQuery, System.out);
        }
    }

    @Override
    public void addModel(String tableName, Model model) {
        var fields = model.getClass().getDeclaredFields();
        var fieldsNames = Arrays.stream(fields).map(Field::getName).toArray(String[]::new);
        var values = Arrays.stream(fields)
                .map(f -> {
                    try {
                        return typeMapper.mapToString(f.get(model));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .toArray(String[]::new);
        var valuesCollectionList = new ArrayList<String[]>();
        valuesCollectionList.add(values);
        var insertQuery = queryConstructor.insert(tableName, fieldsNames, valuesCollectionList);
        executeQueryWithoutResult(insertQuery, System.out);
    }

    @Override
    public void addModels(String tableName, List<Model> models) {
        var fields = models.get(0).getClass().getDeclaredFields();
        var fieldsName = Arrays.stream(fields).map(Field::getName).toArray(String[]::new);
        var valuesCollectionList = new ArrayList<String[]>();
        for(var model : models){
            var values = Arrays.stream(fields)
                    .map(f -> {
                        try {
                            return typeMapper.mapToString(f.get(model));
                        }catch (IllegalAccessException e){
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .toArray(String[]::new);
            valuesCollectionList.add(values);
        }
        var insertQuery = queryConstructor.insert(tableName, fieldsName, valuesCollectionList);
        executeQueryWithoutResult(insertQuery, System.out);
    }

    @Override
    public List<Model> getAllModels(String wherePredicate, Model baseModel) {
        var fields = baseModel.getClass().getDeclaredFields();
        var selectQuery = queryConstructor
                .select(new String[]{"*"}, "Passengers")
                .where(wherePredicate)
                .buildQuery();
        try(var statement = this.connection.createStatement()){
            var queryResult = statement.executeQuery(selectQuery);
            return buildModelsFromResultSet(queryResult, baseModel);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Model> executeSqlQuery(String sqlQuery, Model baseModel) {
        try (var statement = this.connection.createStatement()){
            if (baseModel == null) {
                statement.execute(sqlQuery);
                return null;
            }
            else{
                var resultSet = statement.executeQuery(sqlQuery);
                return buildModelsFromResultSet(resultSet, baseModel);
            }
        }catch (SQLException e){

        }
        return null;
    }

    private ArrayList<Model> buildModelsFromResultSet(ResultSet queryResult, Model baseModel){
        var fields = baseModel.getClass().getDeclaredFields();
        var resultList = new ArrayList<String[]>();
        try {
            while (queryResult.next()) {
                var res = new ArrayList<String>();
                for (var f : fields) {
                    try{
                        var value = queryResult.getString(f.getName());
                        res.add(value);
                    }catch (SQLException ignored){
                        res.add(null);
                    }
                }
                resultList.add(res.toArray(String[]::new));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        var allModels = new ArrayList<Model>();
        for (var modelData : resultList) {
            var model = modelBuilder.buildNewModel(baseModel, Arrays.stream(fields).map(Field::getName).toArray(String[]::new), modelData);
            allModels.add(model);
        }
        return allModels;
    }

    private boolean executeQueryWithoutResult(String query, PrintStream errorsOut){
        try(var statement = this.connection.createStatement()){
            statement.execute(query);
            return true;
        }
        catch (SQLException e) {
            errorsOut.println(e.getMessage()); //TODO: втащить logger
            errorsOut.println(query);
            return false;
        }
    }

}
