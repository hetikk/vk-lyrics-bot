package hetikk.bot.music;

import hetikk.network.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SongTextWorker {

    private static final String HOST = "https://songtext.ru";
    private static final String METHOD = "/search?q=";

    public static void main(String[] args) {

        String s = "Sandstorm (Radio Edit) (Radio Edit)";
        System.out.println(correct(s));

    }

    private static String correct(String s) {
        if (!s.contains("("))
            return s;

        StringBuilder builder = new StringBuilder(s);
        System.out.println(builder);
        while (builder.indexOf("(") > -1 && (builder.indexOf(")") > -1)) {
            int st = builder.indexOf("(");
            int end = builder.indexOf(")") + 1;
            builder.delete(st, end);
        }

        System.out.println(builder);
        int i = 0;
        while (i < builder.length() - 2) {
            if (builder.charAt(i) == ' ' && builder.charAt(i + 1) == ' ') {
                i++;
                while (builder.charAt(i) == ' ')
                    builder.deleteCharAt(i);
            }
            i++;
        }

        return builder.toString();
    }

    public String search(String title, String artist) {
        //String query = (HOST + METHOD + title + " " + artist).toLowerCase().replace(" ", "-");
        String query = null;
        try {
            query = HOST + METHOD + URLEncoder.encode((title + " " + artist)
                    .toLowerCase()
                    .replace(" ", "-"), "utf-8");
            System.out.println(query);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println(query);

        try {
            Document document = Jsoup.parse(Connection.getRequestResponse(query));
            Document songTable = Jsoup.parse(document.getElementsByClass("song-list").html());
            Elements songList = songTable.getElementsByTag("li");
            boolean isFound = false;
            StringBuilder builder = new StringBuilder();
            for (Element song : songList) {
                String lyricsLink = HOST + song.child(0).attr("href");
                String aTitle = song.child(0).toString();
                aTitle = aTitle.substring(aTitle.indexOf(">") + 1, aTitle.indexOf("<", 1));
                String aArtist = song.child(0).child(0).text();

                if (title.equals(aTitle) && artist.equals(aArtist)) {
                    isFound = true;
                    builder.append("Название: ").append(aTitle).append("\n");
                    builder.append("Исполнитель: ").append(aArtist).append("\n");
                    builder.append("Текст:").append("\n");

                    Document lyricsDocument = Jsoup.parse(Connection.getRequestResponse(lyricsLink));
                    Elements lyricsBlock = lyricsDocument.getElementsByClass("lyrics").first().getElementsByTag("p");
                    for (Element element : lyricsBlock) {
                        String paragraph = element.toString().substring(3, element.toString().length() - 4);
                        String[] rows = paragraph.split("<br>");
                        for (String row : rows) {
                            builder.append(row).append("\n");
                        }
                    }

                    break;
                }
            }

            return isFound ? builder.toString() : null;
        } catch (IOException e) {
            return null;
        }
    }

}
