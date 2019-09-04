package hetikk.bot.message;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.enums.UsersNameCase;
import com.vk.api.sdk.objects.groups.responses.GetMembersResponse;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.UserXtrCounters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageHandler {

    private VkApiClient vk;
    private GroupActor actor;

    public MessageHandler(VkApiClient client, GroupActor actor) {
        this.vk = client;
        this.actor = actor;
    }

    public Integer getSenderId(Message message) {
        try {
            String msg = message.toString();

            String key = "\"id\":";
            if (!msg.contains(key))
                return null;
            int msgId = Integer.parseInt(Objects.requireNonNull(getIntValueByKey(msg, key)));

            msg = vk.messages().getById(actor, msgId).groupId(actor.getGroupId()).execute().toString();
            key = "\"from_id\":";
            return Integer.parseInt(Objects.requireNonNull(getIntValueByKey(msg, key)));
        } catch (ApiException | ClientException | NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSenderInfoById(Integer id, UsersNameCase nameCase) {
        try {
            List<UserXtrCounters> execute = vk.users().get(actor)
                    .userIds(String.valueOf(id))
                    .fields(Fields.NICKNAME)
                    .nameCase(nameCase)
                    .execute();
            UserXtrCounters[] data = new UserXtrCounters[execute.size()];
            execute.toArray(data);

            String template = "%s %s (https://vk.com/id%d)";
            return String.format(template, data[0].getLastName(), data[0].getFirstName(), id);
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            String template = "https://vk.com/id%d";
            return String.format(template, id);
        }
    }

    public String getMessageText(Message message) {
        try {
            String msg = message.toString();

            String key = "\"id\":";
            if (!msg.contains(key))
                return null;
            int msgId = Integer.parseInt(Objects.requireNonNull(getIntValueByKey(msg, key)));

            msg = vk.messages().getById(actor, msgId).groupId(actor.getGroupId()).execute().toString();
            key = "\"text\":";

            if (!msg.contains(key))
                return null;

            int pos = msg.lastIndexOf(key) + key.length() + 1;
            return msg.substring(pos, msg.indexOf("\"", pos));
        } catch (ApiException | ClientException | NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> divideLongMessage(String message) {
        List<String> parts = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        String[] rows = message.split("\n");
        for (String row : rows) {
            if (builder.length() > 3800) {
                parts.add(builder.toString());
                builder.setLength(0);
            } else {
                builder.append(row).append("\n");
            }
        }

        if (builder.length() > 0)
            parts.add(builder.toString());

        return parts;
    }

    public Boolean isFollower(String groupId, Integer userId) {
        final int offset = 1000;
        try {
            GetMembersResponse members = vk.groups().getMembers(actor).groupId(groupId).count(offset).execute();
            Integer membersCount = members.getCount();
            List<Integer> membersIdsList = members.getItems();
            for (Integer memberId : membersIdsList) {
                if (memberId.equals(userId))
                    return true;
            }
            return false;
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getIntValueByKey(String src, String key) {
        if (!src.contains(key))
            return null;

        int pos = src.lastIndexOf(key) + key.length();
        return src.substring(pos, src.indexOf(",", pos));
    }

}
