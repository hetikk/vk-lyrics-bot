package hetikk.bot.music;

import hetikk.network.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class AmalgamaLab {

    private static final String HOST = "https://www.amalgama-lab.com/songs/";

    public static void main(String[] args) {

        AmalgamaLab amalgamaLab = new AmalgamaLab();
        System.out.println(amalgamaLab.search("Северный цвет", "Король и Шут"));

    }

    public String search(String title, String artist) {
        String tempTitle = correct(title);
        String tempArtist = correct(artist);
;
        String query = HOST +
                tempArtist.charAt(0) + "/" +
                tempArtist + "/" +
                tempTitle + ".html";

        try {
            Document document = Jsoup.parse(Connection.getRequestResponse(query));
            Elements originalTextElement = document.getElementsByClass("original");
            originalTextElement.remove(0);
            StringBuilder originalText = new StringBuilder();
            for (Element row : originalTextElement) {
                originalText.append(row.text()).append("\n");
            }

            return "Название: " + title + "\n" +
                    "Исполнитель: " + artist + "\n" +
                    "Текст:" + "\n" +
                    originalText;
        } catch (IOException e) {
            return null;
        }
    }

    private String correct(String s) {
        String newS = s.toLowerCase()
                .replace(" & ", "_and_")
                .replaceAll("[ .,!?\\-':()]", "_");

        StringBuilder builder = new StringBuilder(newS);
        if (builder.charAt(builder.length() - 1) == '_')
            builder.deleteCharAt(builder.length() - 1);

        int i = 0;
        while (i < builder.length() - 1) {
            if (builder.charAt(i) == '_' && builder.charAt(i + 1) == '_') {
                i++;
                while (builder.charAt(i) == '_')
                    builder.deleteCharAt(i);
            }
            i++;
        }

        return builder.toString();
    }

}
