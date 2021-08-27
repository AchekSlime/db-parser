package stabbers.parserdb.serializer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import stabbers.parserdb.entity.Database;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class JsonSerializer
{
    /**
     * Static method that generates a JSON file
     * @param db the database to be sterilized
     */
    public static void serialize(String path, Database db) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        // Поток ввода для файла с именем "<db_name>_structure.json".
        try (BufferedWriter file = new BufferedWriter(new FileWriter(path))) {
            mapper.writeValue(file, db);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
