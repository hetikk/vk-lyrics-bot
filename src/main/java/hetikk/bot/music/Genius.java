package hetikk.bot.music;

import hetikk.api.Api;
import hetikk.api.QueryKeys;
import hetikk.api.Service;
import com.google.gson.*;
import hetikk.Application;
import hetikk.network.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Genius {

    private static Logger LOG = LogManager.getLogger(Genius.class.getName());

    private static String accessToken;
    private static Api api;

    private JsonArray hits;

    public Genius() {
        accessToken = Application.properties.getProperty("genius.token");
        api = new Api(Service.GENIUS, accessToken);
    }

    public Object search(String title, String artist) throws Exception {
        String search = (title + " " + artist).replace(" ", "%20");
        String response = api.query("search",
                QueryKeys.GENIUS_Q.getMethod() + "=" + search);

        String pattern = title + " by " + artist;
        List<Map<String, String>> mapList = parseJson(response);
        for (Map<String, String> map : mapList) {
            String full_title = map.get("full_title");
            if (customEqualsIgnoreCase(pattern, full_title)) {
                return createTrackData(map.get("lyrics_url"));
            }
        }

        return hits;
    }

    private boolean customEqualsIgnoreCase(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase().replace("\u200B", "");

        for (int i = 0; i < s1.length(); i++) {
            int chatIdx1 = s1.charAt(i);
            int chatIdx2 = s2.charAt(i);

            if (!((chatIdx1 == 32 || chatIdx1 == 160) && (chatIdx2 == 32 || chatIdx2 == 160))) {
                if (!(chatIdx1 == chatIdx2))
                    return false;
            }
        }

        return true;
    }

    private List<Map<String, String>> parseJson(String response) {
        JsonElement json = new JsonParser().parse(response);
        JsonObject jObject = json.getAsJsonObject();
        JsonObject resp = jObject.getAsJsonObject("response");
        JsonArray hits = resp.getAsJsonArray("hits");
        this.hits = hits;

        List<Map<String, String>> mapList = new ArrayList<>();
        for (JsonElement hit : hits) {
            JsonObject result = hit.getAsJsonObject().getAsJsonObject("result");
            Map<String, String> map = new HashMap<>();
            map.put("full_title", result.get("full_title").getAsString());
            map.put("title", result.get("title").getAsString());
            map.put("song_art_url", result.get("song_art_image_thumbnail_url").getAsString());
            map.put("lyrics_url", result.get("url").getAsString());

            mapList.add(map);
        }
//        JSONObject json = new JSONObject(response);
//        JSONObject resp = (JSONObject) json.get("response");
//        JSONArray hits = (JSONArray) resp.get("hits");
//
//        List<Map<String, String>> mapList = new ArrayList<>();
//
//        for (Object hit : hits) {
//            Map<String, String> map = new HashMap<>();
//            JSONObject obj = (JSONObject) hit;
//            JSONObject obj_result = (JSONObject) obj.get("result");
//
//            map.put("full_title", obj_result.get("full_title") + "");
//            map.put("title", obj_result.get("title").toString());
//            map.put("song_art_url", obj_result.get("song_art_image_thumbnail_url").toString());
//            map.put("lyrics_url", obj_result.get("url").toString());
//
//            mapList.add(map);
//        }
        return mapList;
    }

    @Deprecated
    private String createTrackData(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();

        String full_title = map.get("full_title");
        String title = map.get("title"); // берем названиеы

        builder.append("Название: ").append(title).append("\n");

        // получаем исполнителя
        int offset = (title + " by ").length();
        builder.append("Исполнитель: ").append(full_title.substring(offset)).append("\n");

        // получаем текст песни
        try {
            String response = Connection.getRequestResponse(map.get("lyrics_url"));
            Document doc = Jsoup.parse(response);
            Element lyricsBlock = doc.getElementsByClass("lyrics").get(0).child(0);
            String temp = lyricsBlock.toString();
            temp = temp.substring(3, temp.length() - 4); // удаляем из строки <p> и </p>

            String[] split = temp.split("<br>");
            StringBuilder lyricsBuilder = new StringBuilder();
            for (String s : split) {
                if (s.contains("<") ||  s.contains(">")) {
                    lyricsBuilder.append(Jsoup.parse(s).text()).append("\n");
                } else
                    lyricsBuilder.append(s).append("\n");
            }

            builder.append("Текст: ").append("\n").append(lyricsBuilder.toString());

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createTrackData(String trackUrl) {
        try {
            String response = Connection.getRequestResponse(trackUrl);
            Document doc = Jsoup.parse(response);
            StringBuilder builder = new StringBuilder();

            String title = doc.getElementsByClass("header_with_cover_art-primary_info-title").first().text();
            builder.append("Название: ").append(title).append("\n");

            String artist = doc.getElementsByClass("header_with_cover_art-primary_info-primary_artist").first().text();
            builder.append("Исполнитель: ").append(artist).append("\n");

            try {
                Elements metadata = doc.getElementsByClass("metadata_unit");
                Map<String, String> meta = new HashMap<>();
                for (Element element : metadata) {
                    String text = element.child(0).text().toLowerCase();
                    String val = element.child(1).text();
                    meta.put(text, val);
                }

                SimpleDateFormat srcFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                builder.append("Альбом: ").append(meta.get("album")).append("\n");
                builder.append("Дата выпуска: ").append(newFormat.format(srcFormat.parse(meta.get("release date")))).append("\n");
            } catch (Exception e) {
                LOG.error("Не удалось получить meta-данные: " + trackUrl, e);
            }

            Element lyricsBlock = doc.getElementsByClass("lyrics").get(0).child(0);
            String temp = lyricsBlock.toString();
            temp = temp.substring(3, temp.length() - 4); // удаляем из строки <p> и </p>

            String[] split = temp.split("<br>");
            StringBuilder lyricsBuilder = new StringBuilder();
            for (String s : split) {
                if (s.contains("<") ||  s.contains(">")) {
                    lyricsBuilder.append(Jsoup.parse(s).text()).append("\n");
                } else
                    lyricsBuilder.append(s).append("\n");
            }

            builder.append("Текст: ").append("\n").append(lyricsBuilder.toString());

            return  builder.toString();
        } catch (IOException e) {
            LOG.error(e);
            return "Не удалось получмить информацию";
        }
    }

    private String translate(String s) {
        switch (s.toLowerCase()) {
            case "produced by":
                return "";

            case "album":
                return "";

            case "written by":
                return "";

            case "vuitar":
                return "";

            case "vocals":
                return "";

            case "bass guitar, backing vocals":
                return "";

            case "drums by":
                return "";

            case "recorded at":
                return "";

            case "release date":
                return "";

            case "cover by":
                return "";

            case "performed":
                return "";

            case "Live As":
                return "live as";

            case "label":
                return "";

            case "bass":
                return "";

            case "recording engineer":
                return "";

            case "gong":
                return "";

            case "timpani":
                return "";

            case "equipment supervisor":
                return "";

            case "background vocals":
                return "";

            case "drums":
                return "";

            case "electric guitar":
                return "";

            case "piano":
                return "";

            case "lead vocals":
                return "";

            case "sampled in":
                return "";

            case "interpolated by":
                return "";

            case "remixed by":
                return "";
//
//            case "":
//                return "";
//
//            case "":
//                return "";
//
//            case "":
//                return "";
//
//            case "":
//                return "";
//
//            case "":
//                return "";
//
//            case "":
//                return "";

            default:
                return s;
        }
    }

}
