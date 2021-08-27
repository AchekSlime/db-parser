package stabbers.parserdb.serializer.uml;

import net.sourceforge.plantuml.SourceStringReader;
import stabbers.parserdb.entity.Database;

import java.io.*;

public class UmlSerializer {
    public static void serialize(String filePath, Database db){
        UmlConverter converter = new UmlConverter();
        try(OutputStream out = new FileOutputStream(filePath)){
            writeValue(out, converter.getString(db));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeValue(OutputStream out, String uml) throws IOException {
        SourceStringReader reader = new SourceStringReader(uml);
        reader.generateImage(out);
    }
}
