package hetikk;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import hetikk.bot.message.EventListener;
import hetikk.bot.utils.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class Application {

    public static Properties properties;
    private static Logger LOG;

    static {
        properties = Resources.loadProperties(Application.class, "/config.properties");

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        URL resource = EventListener.class.getResource("/log4j2.xml");
        LOG = LogManager.getLogger(EventListener.class.getName());
        try {
            context.setConfigLocation(resource.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (properties == null) {
            LOG.info("Не удалось считать файл \'config.properties\'");
            return;
        }

        HttpTransportClient client = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(client);

        int groupId = Integer.parseInt(properties.getProperty("vk.groupId"));
        String token = properties.getProperty("vk.token");
        GroupActor actor = new GroupActor(groupId, token);

        LOG.info("Бот успешно запущен");

        listening(vk, actor);
    }

    private static void listening(VkApiClient vk, GroupActor actor) {
        try {
            EventListener messagesHandler = new EventListener(vk, actor);
            messagesHandler.run();
        } catch (ApiException | ClientException e) {
            LOG.error(e);
            listening(vk, actor);
            LOG.info("Бот успешно перезапущен");
        }
    }

}
