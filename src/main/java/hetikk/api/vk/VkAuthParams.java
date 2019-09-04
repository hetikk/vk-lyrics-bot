package hetikk.api.vk;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Iurii Miedviediev
 *
 * @author DruidKuma
 * @version 1.0.0
 * @since 12/23/15
 */
public final class VkAuthParams {

    public static final String NOTIFY = "notify";
    public static final String FRIENDS = "friends";
    public static final String PHOTOS = "photos";
    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";
    public static final String STORIES = "stories";
    public static final String PAGES = "pages";
    public static final String STATUS = "status";
    public static final String NOTES = "notes";
    public static final String MESSAGES = "messages";
    public static final String WALL = "wall";
    public static final String ADS = "ads";
    public static final String OFFLINE = "offline";
    public static final String DOCS = "docs";
    public static final String GROUPS = "groups";
    public static final String NOTIFICATIONS = "notifications";
    public static final String STATS = "stats";
    public static final String EMAIL = "email";
    public static final String MARKET = "market";
    public static final String ALL = NOTIFY + "," + FRIENDS + "," + PHOTOS + "," + AUDIO + "," + VIDEO + "," + STORIES + "," + PAGES + "," + STATUS + "," + NOTES +
            MESSAGES + "," + WALL + "," + ADS + "," + OFFLINE + "," + DOCS + "," + GROUPS + "," + NOTIFICATIONS + "," + STATS + "," + EMAIL + "," + MARKET;

    private VkAuthParams() {
    }

    // URL-адрес входа
    private static final String VK_URL = "https://m.vk.com";

    // URL-адрес авторизации OAuth
    private static final String TOKEN_URL = "https://oauth.vk.com/oauth/authorize?redirect_uri=http://oauth.vk.com/blank.html&response_type=token&client_id=%s&scope=%s&display=wap&v=5.101";
    private static final String CODE_URL = "https://oauth.vk.com/oauth/authorize?client_id=%s&redirect_uri=https://oauth.vk.com/blank.html&scope=%s&display=wap&response_type=code";

    // Имя файла Cookie для разрешения недопустимого потока учетных данных
    private static final String CHECKED_PROPERTIES = "p";

    /**
     * Имитирует активность входа в браузер VK
     * <p>
     * TODO: обработка дополнительных проверок потока входа (например, captcha)
     *
     * @param login логин пользователя (телефон или электронная почта)
     *              пользователей парам пароль @
     * @throws IOException
     * @возврат коллекции извлеченных файлов cookie после успешного входа в систему
     */
    private static Map<String, String> login(String login, String password) throws IOException {
        Map<String, String> cookies;

        // получить страницу входа html
        Connection.Response connection = Jsoup.connect(VK_URL).execute();

        // получить URL-адрес действия входа
        String url = getFormActionFromPage(connection);

        // получение файлов cookie с первой страницы входа
        cookies = connection.cookies();

        // подготовка и отправка учетных данных вместе с полученными cookies в действие входа
        Map<String, String> data = new HashMap<String, String>();
        data.put("email", login);
        data.put("pass", password);
        connection = Jsoup.connect(url).cookies(cookies).data(data).execute();

        // получить куки после входа в систему
        cookies = connection.cookies();

        // проверьте поток недопустимых учетных данных
        if (!cookies.containsKey(CHECKED_PROPERTIES)) {
            throw new RuntimeException("Invalid login/password");
        }
        return cookies;
    }

    /**
     * Получает маркер доступа, действительный в течение 24 часов
     *
     * @param login логин пользователя (электронная почта или телефон)
     *              пользователей парам пароль @
     * @return VK access token
     * @throws IOException
     * @ param appId VK идентификатор приложения
     * прицелы @сферу парам с запятой разделитель
     */
    public static String getAccessToken(String appId, String scope, String login, String password) throws IOException {

        // войти и получить куки
        Map<String, String> loginCookies = login(login, password);

        // имитируйте поток авторизации oauth, предполагая, что пользователь вошел в систему
        Connection.Response response = Jsoup.connect(String.format(TOKEN_URL, appId, scope)).cookies(loginCookies).execute();

        /*
         * Если маркер доступа все еще не находится в расположении извлеченной страницы,
         * это означает, что приложение авторизуется в первый раз,
         * и пользователь должен предоставить доступ к нему, в соответствии с запрашиваемыми областями
         * (Имитирует нажатие на кнопку "Разрешить")
         */
        if (response.url().getRef() == null) {
            String grantAccessAction = getFormActionFromPage(response);
            response = Jsoup.connect(grantAccessAction).cookies(loginCookies).execute();
        }
        // url-адрес ответа ref содержит всю информацию о токене (время до истечения срока действия и идентификатор пользователя), нам нужно только само значение токена
        return response.url().getRef().split("&")[0].split("=")[1];
    }

    /**
     * Получает CODE, действительный в течение 1 часа
     *
     * @param login логин пользователя (электронная почта или телефон)
     *              пользователей парам пароль @
     * @return VK code
     * @throws IOException
     * @ param appId VK идентификатор приложения
     * прицелы @сферу парам с запятой разделитель
     */
    public static String getCode(String appId, String scope, String login, String password) throws IOException {

        // войти и получить куки
        Map<String, String> loginCookies = login(login, password);

        // имитируйте поток авторизации oauth, предполагая, что пользователь вошел в систему
        Connection.Response response = Jsoup.connect(String.format(CODE_URL, appId, scope)).cookies(loginCookies).execute();
        /*
         * Если маркер доступа все еще не находится в расположении извлеченной страницы,
         * это означает, что приложение авторизуется в первый раз,
         * и пользователь должен предоставить доступ к нему, в соответствии с запрашиваемыми областями
         * (Имитирует нажатие на кнопку "Разрешить")
         */
        if (response.url().getRef() == null) {
            String grantAccessAction = getFormActionFromPage(response);
            response = Jsoup.connect(grantAccessAction).cookies(loginCookies).execute();
        }

        // url-адрес ответа ref содержит всю информацию о токене (время до истечения срока действия и идентификатор пользователя), нам нужно только само значение токена
        String temp = response.url().getRef();
        return temp.substring(temp.lastIndexOf("=") + 1);
    }

    /**
     * Данный ответ с HTML страницы, найти элемент формы и извлекает его действие URL
     *
     * @param pageResponse ответ с HTML страницы
     * @throws IOException
     * @ return form action URL
     */
    private static String getFormActionFromPage(Connection.Response pageResponse) throws IOException {
        Document document = pageResponse.parse();
        Element form = document.getElementsByTag("form").get(0);
        return form.attr("action");
    }
}