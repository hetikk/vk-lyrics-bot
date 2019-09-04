package hetikk.api;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {

//        String vkToken = VkAuthParams.getAccessToken("7065122", VkAuthParams.ALL, "89888745326", "h230512");
//
//        Api hetikk.api = new Api(Service.VK, vkToken);
//        QueryParams params = new QueryParams();
//        params.put(QueryKeys.VK_USER_IDS, "372746271");
//        params.put(QueryKeys.VK_V, "5.101");
//        String query = hetikk.api.query("users.get", params);
//        System.out.println(query);
        String s = "                   ";
        String s1 = "Holiday by Green Day";
        String s2 = "Holiday by Green Day";
//
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s1.equalsIgnoreCase(s2));

        int i = s1.charAt(7);
        int i1 = s1.charAt(10);
        int i2 = s1.charAt(16);

        System.out.println(i);
        System.out.println(i1);
        System.out.println(i2);

        int j = s2.charAt(7);
        int j1 = s2.charAt(10);
        int j2 = s2.charAt(16);

        System.out.println(j);
        System.out.println(j1);
        System.out.println(j2);
    }
}
