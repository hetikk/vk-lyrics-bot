package hetikk.api;

public enum QueryKeys {

    /**
     * VK Keys
     */
    VK_ACCESS_TOKEN("access_token"),
    VK_ADDRESS_IDS("address_ids"),
    VK_ALBUM_ID("album_id"),
    VK_ALBUM_IDS("album_ids"),
    VK_APP_ID("app_id"),
    VK_APP_IDS("app_ids"),
    VK_CHAT_ID("chat_id"),
    VK_CHAT_IDS("chat_ids"),
    VK_COMMENT_ID("comment_id"),
    VK_COPY_HISTORY_DEPTH("copy_history_depth"),
    VK_COUNT("count"),
    VK_DATE_FROM("date_from"),
    VK_DATE_TO("date_to"),
    VK_DEVICE_ID("device_id"),
    VK_DOMAIN("domain"),
    VK_EXTENDED("extended"),
    VK_FEED("feed"),
    VK_FEED_TYPE("feed_type"),
    VK_FIELDS("fields"),
    VK_FILTER("filter"),
    VK_FILTERS("filters"),
    VK_FRIENDS_COUNT("friends_count"),
    VK_GLOBAL("global"),
    VK_GROUP_ID("group_id"),
    VK_INTERVAL("interval"),
    VK_INTERVALS_COUNT("intervals_count"),
    VK_IS_BOARD("is_board"),
    VK_LAST_MESSAGE_ID("last_message_id"),
    VK_LATITUDE("latitude"),
    VK_LONGITUDE("longitude"),
    VK_MESSAGE_IDS("message_ids"),
    VK_NAME_CASE("name_case"),
    VK_NEED_COVERS("need_covers"),
    VK_NEED_HTML("need_html"),
    VK_NEED_LIKES("need_likes"),
    VK_NEED_SOURCE("need_source"),
    VK_NEED_SYSTEM("need_system"),
    VK_NOTE_IDS("note_ids"),
    VK_OFFSET("offset"),
    VK_ORDER("order"),
    VK_OWNER_ID("owner_id"),
    VK_PAGE_ID("page_id"),
    VK_PEER_IDS("peer_ids"),
    VK_PHOTO_IDS("photo_ids"),
    VK_PHOTO_SIZES("photo_sizes"),
    VK_PLATFORM("platform"),
    VK_POLL_ID("poll_id"),
    VK_POSTS("posts"),
    VK_POST_ID("post_id"),
    VK_PREVIEW("preview"),
    VK_PREVIEW_LENGTH("preview_length"),
    VK_RETURN_FRIENDS("return_friends"),
    VK_REV("rev"),
    VK_SITE_PREVIEW("site_preview"),
    VK_SORT("sort"),
    VK_START_COMMENT_ID("start_comment_id"),
    VK_STATS_GROUPS("stats_groups"),
    VK_THREAD_ITEMS_COUNT("thread_items_count"),
    VK_TIMESTAMP_FROM("timestamp_from"),
    VK_TIMESTAMP_TO("timestamp_to"),
    VK_TIME_OFFSET("time_offset"),
    VK_TITLE("title"),
    VK_TOKEN("token"),
    VK_TOPIC_ID("topic_id"),
    VK_TOPIC_IDS("topic_ids"),
    VK_TYPE("type"),
    VK_USER_ID("user_id"),
    VK_USER_IDS("user_ids"),
    VK_V("v"),
    VK_VIDEOS("videos"),

    /**
     * GENIUS Keys
     */
    GENIUS_AFTER_HTML("after_html"),
    GENIUS_ANNOTATION("annotation"),
    GENIUS_BEFORE_HTML("before_html"),
    GENIUS_BODY("body"),
    GENIUS_CANONICAL_URL("canonical_url"),
    GENIUS_CONTENT("content"),
    GENIUS_CONTEXT_FOR_DISPLAY("context_for_display"),
    GENIUS_CREATED_BY_ID("created_by_id"),
    GENIUS_CREATE_ANNOTATION("create_annotation"),
    GENIUS_DOM("dom"),
    GENIUS_FRAGMENT("fragment"),
    GENIUS_HREF("href"),
    GENIUS_HTML("html"),
    GENIUS_ID("id"),
    GENIUS_MANAGE_ANNOTATION("manage_annotation"),
    GENIUS_MARKDOWN("markdown"),
    GENIUS_ME("me"),
    GENIUS_OG_URL("og_url"),
    GENIUS_PAGE("page"),
    GENIUS_PER_PAGE("per_page"),
    GENIUS_PLAIN("plain"),
    GENIUS_POPULARITY("popularity"),
    GENIUS_Q("q"),
    GENIUS_RAW_ANNOTATABLE_URL("raw_annotatable_url"),
    GENIUS_REFERENT("referent"),
    GENIUS_SONG_ID("song_id"),
    GENIUS_SORT("sort"),
    GENIUS_TEXT_FORMAT("text_format"),
    GENIUS_TITLE("title"),
    GENIUS_VOTE("vote"),
    GENIUS_WEB_PAGE("web_page"),
    GENIUS_WEB_PAGE_ID("web_page_id");

    QueryKeys(String method) {
        this.method = method;
    }

    private String method;

    public String getMethod() {
        return method;
    }
}
