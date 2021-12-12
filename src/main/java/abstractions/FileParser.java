package abstractions;

import java.nio.file.Path;
import java.util.List;

public interface FileParser {
    void parseFile(Path pathToFile);
    List<Model> buildModels(Model baseModel);
}
