package utils.db;

public class Query {
    private StringBuilder builder = new StringBuilder();

    public Query(String queryStart){
        builder.append(queryStart.replace(";", ""));
    }

    public Query where(String predicate){ //TODO: переделать на FunctionalInterface
        if (predicate == null)
            return this;
        builder.append(String.format(" WHERE %s", predicate));
        return this;
    }

    public Query groupBy (String[] fields){

        builder.append(String.format(" GROUP BY %s", String.join(", ", fields)));
        return this;
    }

    public String buildQuery(){
        return builder.toString() + ';';
    }
}
