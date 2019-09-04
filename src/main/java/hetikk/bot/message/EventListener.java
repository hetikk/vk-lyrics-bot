package hetikk.bot.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.callback.objects.group.CallbackGroupJoin;
import com.vk.api.sdk.callback.objects.group.CallbackGroupLeave;
import com.vk.api.sdk.objects.enums.UsersNameCase;
import com.vk.api.sdk.objects.messages.*;
import hetikk.bot.music.Genius;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EventListener extends CallbackApiLongPoll {

    private static final Logger LOG = LogManager.getLogger(EventListener.class.getName());
    private static final Genius GENIUS = new Genius();

    private static final String SUCCESSFULLY_FOUND = "Да \uD83D\uDC4D\uD83C\uDFFB";
    private static final String UNSUCCESSFULLY_FOUND = "Нет \uD83D\uDC4E\uD83C\uDFFB";
    private static final String GREETING_MESSAGE = "GREETING_MESSAGE";

    private VkApiClient vk;
    private GroupActor actor;
    private MessageHandler messageHandler;

    public EventListener(VkApiClient client, GroupActor actor) {
        super(client, actor);
        this.vk = client;
        this.actor = actor;
        messageHandler = new MessageHandler(vk, actor);
    }

    @Override
    public void groupJoin(Integer groupId, CallbackGroupJoin message) {
        LOG.info("Подписка: " + messageHandler.getSenderInfoById(message.getUserId(), UsersNameCase.NOMINATIVE));
    }

    @Override
    public void groupLeave(Integer groupId, CallbackGroupLeave message) {
        LOG.info("Отписка: " + messageHandler.getSenderInfoById(message.getUserId(), UsersNameCase.NOMINATIVE));
    }

    @Override
    public void messageNew(Integer groupId, Message message) {
        Integer senderId = messageHandler.getSenderId(message);

        if (senderId == null) {
            LOG.info("Обрабока сообщения отменена");
            LOG.info("Не удалось получить идентификатор пользователя\n" + message.toString());
            return;
        }

        LOG.info("Получено новое сообщение от " + messageHandler.getSenderInfoById(senderId, UsersNameCase.GENITIVE));
        if (messageHandler.isFollower(groupId.toString(), senderId)) {
            String messageText = messageHandler.getMessageText(message);
            LOG.info("Текст сообщения: " + messageText);

            if (messageText.equals("Начать")) {
                sendMessage(senderId, GREETING_MESSAGE);
                return;
            }

            if (message.getPayload() != null) {
                if (message.getPayload().contains("keyboardPossibleOptions")) {
                    JsonObject payload = new JsonParser().parse(message.getPayload()).getAsJsonObject();
                    String url = payload.get("keyboardPossibleOptions").getAsString();
                    sendMessage(senderId, GENIUS.createTrackData(url));
                    return;
                }
            }

            List<MessageAttachment> attachments = message.getAttachments();
            if (attachments != null && attachments.size() > 0) {
                LOG.info("Количество вложений: " + attachments.size());
                for (MessageAttachment attachment : attachments) {
                    if (attachment.getType() == MessageAttachmentType.AUDIO) {
                        String title = correct(attachment.getAudio().getTitle().trim());
                        String artist = correct(attachment.getAudio().getArtist().trim());
                        LOG.info("AUDIO: " + title + " " + artist);

                        Object search = null;
                        try {
                            search = GENIUS.search(title, artist);
                        } catch (Exception e) {
                            LOG.error(e);
                            e.printStackTrace();
                        }

                        if (search instanceof JsonArray) {
                            JsonArray hits = (JsonArray) search;
                            if (hits.size() > 0)
                                keyboardPossibleOptions(senderId, hits);
                            else
                                sendMessage(senderId, "Извините, но я ничего не нашел");
                        } else {
                            if (search != null) {
                                sendMessage(senderId, search.toString());
                            } else {
                                sendMessage(senderId, "Извините, но я ничего не нашел");
                            }
                        }
                    }
                }
            }
        } else {
            LOG.info("Игнорируем, т.к. отправитель не подписан на сообщество");
            sendMessage(senderId, "Извините, но только подписчики имеют доступ к функциям бота");
        }
    }

    private int randomId() {
        return (int) (Math.random() * 10000000);
    }

    private void sendMessage(Integer senderId, String text) {
        try {
            if (Objects.requireNonNull(text).length() > 4000) {
                List<String> messageParts = messageHandler.divideLongMessage(text);
                for (String messagePart : messageParts) {
                    vk.messages().send(actor).userId(senderId).randomId(randomId()).message(messagePart).execute();
                }
            } else {
                vk.messages().send(actor).userId(senderId).randomId(randomId()).message(text).execute();
            }
        } catch (ApiException | ClientException e) {
            LOG.error(e);
            e.printStackTrace();
        }
    }

    private String correct(String s) {
        if (!s.contains("("))
            return s;

        StringBuilder builder = new StringBuilder(s);
        while (builder.indexOf("(") > -1 && (builder.indexOf(")") > -1)) {
            int st = builder.indexOf("(");
            int end = builder.indexOf(")") + 1;
            builder.delete(st, end);
        }

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

    private void keyboardPossibleOptions(Integer senderId, JsonArray jsonArray) {
        List<List<KeyboardButton>> btnMatrix = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            JsonObject result = jsonElement.getAsJsonObject().getAsJsonObject("result");
            String url = result.get("url").getAsString();
            String label = result.get("full_title").getAsString()
                    .replace("by", "-");
            if (label.length() > 37) {
                label = label.substring(0, 37);
                label = label.concat("...");
            }

            ArrayList<KeyboardButton> row = new ArrayList<>();
            KeyboardButton button = new KeyboardButton();
            KeyboardButtonAction buttonAction = new KeyboardButtonAction();

            buttonAction.setType(KeyboardButtonActionType.TEXT);
            buttonAction.setPayload("{\"keyboardPossibleOptions\": \"" + url + "\"}");
            buttonAction.setLabel(label);
            button.setAction(buttonAction);
            button.setColor(KeyboardButtonColor.DEFAULT);
            row.add(button);

            btnMatrix.add(row);
        }

        Keyboard keyboard = new Keyboard();
        keyboard.setButtons(btnMatrix);
        keyboard.setOneTime(true);

        try {
            vk.messages().send(actor)
                    .userId(senderId)
                    .message("Я не нашел точных совпадений, но может что-то из этого подойдет")
                    .keyboard(keyboard)
                    .randomId(randomId())
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    private void successLyricsFindKeyboard(Integer senderId) {
        List<List<KeyboardButton>> btnMatrix = new ArrayList<>();

        ArrayList<KeyboardButton> row1 = new ArrayList<>();

        KeyboardButton row1btn1 = new KeyboardButton();
        KeyboardButtonAction row1btn1act = new KeyboardButtonAction();

        row1btn1act.setType(KeyboardButtonActionType.TEXT);
        row1btn1act.setPayload("{\"successLyricsFindKeyboard\": \"" + SUCCESSFULLY_FOUND + "\"}");
        row1btn1act.setLabel(SUCCESSFULLY_FOUND);
        row1btn1.setAction(row1btn1act);
        row1btn1.setColor(KeyboardButtonColor.POSITIVE);
        row1.add(row1btn1);

        KeyboardButton row1btn2 = new KeyboardButton();
        KeyboardButtonAction row1btn2act = new KeyboardButtonAction();
        row1btn2act.setType(KeyboardButtonActionType.TEXT);
        row1btn2act.setPayload("{\"successLyricsFindKeyboard\": \"" + UNSUCCESSFULLY_FOUND + "\"}");
        row1btn2act.setLabel(UNSUCCESSFULLY_FOUND);
        row1btn2.setAction(row1btn2act);
        row1btn2.setColor(KeyboardButtonColor.NEGATIVE);
        row1.add(row1btn2);

//        ArrayList<KeyboardButton> row2 = new ArrayList<>();
//
//        KeyboardButton row2btn1 = new KeyboardButton();
//        KeyboardButtonAction row2btn1act = new KeyboardButtonAction();
//        row2btn1act.setType(KeyboardButtonActionType.TEXT);
//        row2btn1act.setPayload("{\"button\": \"1\"}");
//        row2btn1act.setLabel("btn3");
//        row2btn1.setAction(row2btn1act);
//        row2btn1.setColor(KeyboardButtonColor.DEFAULT);
//        row2.add(row2btn1);
//
//        KeyboardButton row2btn2 = new KeyboardButton();
//        KeyboardButtonAction row2btn2act = new KeyboardButtonAction();
//        row2btn2act.setType(KeyboardButtonActionType.TEXT);
//        row2btn2act.setLabel("btn4");
//        row2btn2act.setPayload("{\"button\": \"2\"}");
//        row2btn2.setAction(row2btn2act);
//        row2btn2.setColor(KeyboardButtonColor.DEFAULT);
//        row2.add(row2btn2);

        //        successLyricsFindKeyboard.add(row2);

        btnMatrix.add(row1);
        Keyboard keyboard = new Keyboard();
        keyboard.setButtons(btnMatrix);
        keyboard.setOneTime(true);

        try {
            vk.messages().send(actor)
                    .userId(senderId)
                    .message("Соответствует ли найденный текст вашей песне?")
                    .keyboard(keyboard)
                    .randomId(randomId())
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

}
