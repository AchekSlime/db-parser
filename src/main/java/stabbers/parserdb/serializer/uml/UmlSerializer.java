package stabbers.parserdb.serializer.uml;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.SourceStringReader;
import stabbers.parserdb.entity.Database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UmlSerializer {

    public static void serialize(String filePathTxt, String filePathPng, Database db){
        serializeToTxt(filePathTxt, db);
        serializeToPngFromFile(filePathTxt, "png");
    }
    /**
     * Serialize database to png diagram
     * @param filePath path to result .png file
     * @param db requested database
     */
    public static void serializeToPng(String filePath, Database db){
        UmlConverter converter = new UmlConverter();
        try(BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath))){
            writePng(out, converter.getString(db));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void serializeToPngFromFile(String filePathTxt, String filePathPng){
        try {
            writePngToFile(new File(filePathTxt), new File(filePathPng));
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
        try(BufferedWriter writer = new BufferedWriter
                (new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))){
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

    private static void writePngToFile(File fileIn, File fileOut) throws IOException {
        SourceFileReader reader = new SourceFileReader(fileIn, null, "UTF-8");
        List<GeneratedImage> list = reader.getGeneratedImages();
        list.get(0).getPngFile();
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
