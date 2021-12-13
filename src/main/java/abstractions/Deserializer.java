package abstractions;

public interface Deserializer {
    Model buildNewModel(Model baseModel, String[] titles, String[] data);
}
