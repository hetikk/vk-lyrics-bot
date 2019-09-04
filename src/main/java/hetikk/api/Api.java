package hetikk.api;

import hetikk.bot.music.Genius;
import hetikk.network.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Api {

    private static Logger LOG = LogManager.getLogger(Genius.class.getName());

    private String token;
    private String host;

    public Api(Service service, String token) {
        switch (service) {
            case VK:
                host = Service.VK.getHost();
                break;

            case GENIUS:
                host = Service.GENIUS.getHost();
                break;
        }

        this.token = token;
    }

    public String query(String method, QueryParams params) throws IOException {
        StringBuilder queryBuilder = new StringBuilder();
        QueryKeys[] keys = new QueryKeys[params.size()];
        params.keySet().toArray(keys);
        String[] values = new String[params.size()];
        params.values().toArray(values);

        queryBuilder.append(host);
        queryBuilder.append(method);
        queryBuilder.append("?access_token=").append(token);
        for (int i = 0; i < params.size(); i++) {
            queryBuilder.append("&");
            queryBuilder.append(keys[i].getMethod());
            queryBuilder.append("=");
            queryBuilder.append(values[i]);
        }

        return Connection.getRequestResponse(queryBuilder.toString().replace(" ", "%20"));
    }

    public String query(String method, String params) throws IOException {
        String queryBuilder = host + method + "?access_token=" + token + "&" + params;
        LOG.info("Genius: " + queryBuilder);
        return Connection.getRequestResponse(queryBuilder);
    }

}
