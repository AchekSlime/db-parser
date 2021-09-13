package stabbers.parserdb.serializer.uml;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import stabbers.parserdb.entity.Database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UmlSerializer {

    public UmlSerializer(){
    }

    public void serialize(String filePathTxt, String umlInitConfig, Database db){
        serializeToTxt(filePathTxt, umlInitConfig, db);
        serializeToPngFromFile(filePathTxt);
    }

    /**
     * Serialize database to .txt file with uml syntax
     * @param filePathTxt path to result .txt file
     * @param db requested database
     */
    private void serializeToTxt(String filePathTxt, String umlInitConfig, Database db){
        UmlConverter converter = new UmlConverter(umlInitConfig);
        try(BufferedWriter writer = new BufferedWriter
                (new OutputStreamWriter(new FileOutputStream(filePathTxt), StandardCharsets.UTF_8))){
            writeTxt(writer, converter.getString(db));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void serializeToPngFromFile(String filePathTxt){
        try {
            writePngFromFile(new File(filePathTxt));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write uml string to .png file
     * @param fileIn txt file with uml
     */
    private void writePngFromFile(File fileIn) throws IOException {
        SourceFileReader reader = new SourceFileReader(fileIn, null, "UTF-8");
        List<GeneratedImage> list = reader.getGeneratedImages();
        list.get(0).getPngFile();
    }

    /**
     * Write uml string to .txt file
     * @param writer Writer impl. opened for result .txt file
     * @param uml string with uml syntax
     */
    private void writeTxt(Writer writer, String uml) throws IOException {
        writer.write(uml);
    }

}
