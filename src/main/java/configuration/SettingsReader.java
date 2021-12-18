package configuration;

import models.Settings;
import utils.ModelBuilder;

//TODO: Сделать чтение настроек из файла "config.config"
public class SettingsReader {
    private ModelBuilder modelBuilder = new ModelBuilder();
    public Settings getAppSettings(){
        var settings = modelBuilder.buildNewModel(new Settings(), new String[]{"DB_CONNECTION_PATH"}, new String[]{"jdbc:sqlite:/home/blackpoint/IdeaProjects/Ulearn/11/ExamProject/src/main/resources/Titanic.db"});
        return (Settings) settings;
    }
}
