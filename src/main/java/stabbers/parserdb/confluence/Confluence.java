package stabbers.parserdb.confluence;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Confluence {
    // ToDo передавать все в конфигураторе
    private final String baseURL;// = "https://impsface.atlassian.net/wiki/rest/api/content/196616";
    private final String token;// = "Basic aW1wc2ZhY2VAeWFuZGV4LnJ1OkhXQk9sZXllWVlNV09VUHRQa1h4Qzg1Ng==";
    private final String path;// = "";

    public Confluence(String url, String token, String path){
        baseURL = url;
        this.token = token;
        this.path = path;
    }

    public void addImage() {
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("file", getImageAsBytes(path), ContentType.create("image/png"), path)
                .build();
        HttpPost httpPost = new HttpPost(baseURL + "/child/attachment");

        httpPost.addHeader("Authorization", token);
        httpPost.addHeader("X-Atlassian-Token", "nocheck");
        httpPost.setEntity(entity);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            System.out.println("[ADD_IMAGE] - " + response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getImageAsBytes(String path) {
        BufferedImage bImage = null;
        try {
            bImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, "png", bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public void update() {
        JSONObject page = getPage();
        String newContent = "<p>" + path + "</p><ac:image><ri:attachment ri:filename=\"" + path + "\" /></ac:image>";
        JSONObject updatedJson = handleJson(page, newContent);
        displayImage(updatedJson.toString());
    }

    // ToDo переделать на ApacheHTTPClient
    private JSONObject getPage() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(baseURL + "?expand=body.storage,version,space");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", token);
            // ToDo можно удалить заголовок?
            connection.setRequestProperty("Host", "impsface.atlassian.net");
            connection.connect();
            int status = connection.getResponseCode();
            System.out.println(status);

            // Получение response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            JSONObject json = new JSONObject(response.toString());
            System.out.println("[GET_PAGE] - " + json);
            FileWriter file = null;
            try {
                file = new FileWriter("json/oldPage.json");
                file.write(json.toString(4));
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    private JSONObject handleJson(JSONObject jsonPage, String newContent) {
        JSONObject updatedPage = new JSONObject();

        updatedPage.put("id", jsonPage.getInt("id"));
        updatedPage.put("type", jsonPage.getString("type"));
        updatedPage.put("title", jsonPage.getString("title"));

        JSONObject version = new JSONObject();
        version.put("number", jsonPage.getJSONObject("version").getInt("number") + 1);
        updatedPage.put("version", version);

        JSONObject space = new JSONObject();
        space.put("key", jsonPage.getJSONObject("space").getString("key"));
        updatedPage.put("space", space);

        JSONObject body = new JSONObject();
        body.put("storage", jsonPage.getJSONObject("body").getJSONObject("storage"));
        body.getJSONObject("storage").put("value", body.getJSONObject("storage").getString("value") + " " + newContent);
        updatedPage.put("body", body);

        FileWriter file = null;
        try {
            file = new FileWriter("json/updatedPage.json");
            file.write(updatedPage.toString(4));
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return updatedPage;
    }

    private void displayImage(String updatedJson) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        StringEntity requestEntity = null;
        try {
            requestEntity = new StringEntity(
                    updatedJson,
                    "application/json",
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpPut httpPut = new HttpPut(baseURL);
        httpPut.addHeader("Authorization", token);
        httpPut.addHeader("Content-Type", "application/json");
        httpPut.setEntity(requestEntity);

        try {
            HttpResponse response = httpClient.execute(httpPut);
            System.out.println("[DISPLAY] - " + response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
