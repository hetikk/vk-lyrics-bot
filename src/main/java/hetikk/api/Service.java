package hetikk.api;

public enum Service {

    VK("https://api.vk.com/method/"),
    GENIUS("https://api.genius.com/");

    Service(String method) {
        this.host = method;
    }

    private String host;

    public String getHost() {
        return host;
    }

}
