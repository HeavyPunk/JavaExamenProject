package utils;

import models.CSVModel;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVReader {
    public CSVModel read(Path pathToCsv, String delimiter){
        try {
            var scanner = new Scanner(pathToCsv.toFile());
            var titles = scanner.nextLine().split(delimiter);
            var allAttributes = new ArrayList<String[]>();
            while (scanner.hasNextLine())
            {
                var line = scanner.nextLine();
                var objectAttributes = parseLine(line, delimiter.charAt(0));
                allAttributes.add(objectAttributes);
            }
            scanner.close();
            var model = new CSVModel();
            model.titles = titles;
            model.data = allAttributes.toArray(String[][]::new);
            return model;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] parseLine(String line, char delimiter){
        var result = new ArrayList<String>();
        var attributeBuilder = new StringBuilder();
        for (var c = 0; c < line.length(); c++){
            if (line.charAt(c) == delimiter) {
                result.add(attributeBuilder.toString());
                attributeBuilder.delete(0, attributeBuilder.length());
            }
            else if (line.charAt(c) == '\"'){
                c++;
                var row = new StringBuilder();
                for (; line.charAt(c) != '\"'; c++){
                    row.append(line.charAt(c));
                }
                attributeBuilder.append(row);
            }else {
                attributeBuilder.append(line.charAt(c));
            }
        }
        result.add(attributeBuilder.toString());
        return result.toArray(String[]::new);
    }
}
