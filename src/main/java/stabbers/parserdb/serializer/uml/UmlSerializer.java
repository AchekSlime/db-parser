package stabbers.parserdb.serializer.uml;

import net.sourceforge.plantuml.SourceStringReader;
import stabbers.parserdb.entity.Database;

import java.io.*;

public class UmlSerializer {
    /**
     * Serialize database to png diagram
     * @param filePath path to result .png file
     * @param db requested database
     */
    public static void serializeToPng(String filePath, Database db){
        UmlConverter converter = new UmlConverter();
        try(OutputStream out = new FileOutputStream(filePath)){
            writePng(out, converter.getString(db));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serialize database to .txt file with uml syntax
     * @param filePath path to result .txt file
     * @param db requested database
     */
    public static void serializeToTxt(String filePath, Database db){
        UmlConverter converter = new UmlConverter();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            writeTxt(writer, converter.getString(db));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write uml string to .png file
     * @param out OutputStream for result .png file
     * @param uml string with uml syntax
     */
    private static void writePng(OutputStream out, String uml) throws IOException {
        SourceStringReader reader = new SourceStringReader(uml);
        reader.generateImage(out);
    }

    /**
     * Write uml string to .txt file
     * @param writer Writer impl. opened for result .txt file
     * @param uml string with uml syntax
     */
    private static void writeTxt(Writer writer, String uml) throws IOException {
        writer.write(uml);
    }

}
