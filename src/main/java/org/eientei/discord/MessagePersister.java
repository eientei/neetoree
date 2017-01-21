package org.eientei.discord;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.Embed;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.listener.message.MessageDeleteListener;
import de.btobastian.javacord.listener.message.MessageEditListener;

import java.net.URL;
import java.util.Date;

/**
 * Created by Alexander Tumin on 2016-11-14
 */
public class MessagePersister implements MessageCreateListener, MessageEditListener, MessageDeleteListener {
    private final MessageAdminRepository repository;
    private final DiscordAPI api;

    public MessagePersister(MessageAdminRepository adminRepository, DiscordAPI api) {
        repository = adminRepository;
        this.api = api;
    }

    public static MessageLog messageToPojo(Message message) {
        String mid = message.getId();
        User author = message.getAuthor();
        Channel channel = message.getChannelReceiver();
        String channelId = channel.getId();

        String authorId = author.getId();
        String messageContent = message.getContent();

        for (Embed embed: message.getEmbeds()) {
            URL url = embed.getUrl();
            messageContent = messageContent + " " + url.toString();
        }

        Date messageCreated = message.getCreationDate().getTime();

        MessageLog pojo = new MessageLog(mid);
        pojo.setChannelId(channelId);
        pojo.setTime(messageCreated);
        pojo.setAuthorId(authorId);
        pojo.setContent(messageContent);
        return pojo;
    }

    @Override
    public void onMessageCreate(DiscordAPI discordAPI, Message message) {
        MessageLog pojo = messageToPojo(message);
        repository.save(pojo);

        System.out.println("NEW " + pojo);
    }

    @Override
    public void onMessageDelete(DiscordAPI discordAPI, Message message) {
        Channel channel = message.getChannelReceiver();

        String mid = message.getId();
        MessageLog one = repository.findFirstByMidOrderByTimeDesc(mid);
        if (one == null) {
            return;
        }

        MessageLog pojo = new MessageLog(one);
        pojo.setTime(new Date(System.currentTimeMillis()));
        repository.save(pojo);

        try {
            User user = message.getAuthor();
            String nick = user.getNickname(channel.getServer());
            channel.sendMessage("< **" + nick + "** > *(deleted)*");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("DEL " + pojo);
    }

    @Override
    public void onMessageEdit(DiscordAPI discordAPI, Message message, String old) {;
        Channel channel = message.getChannelReceiver();

        String mid = message.getId();
        String messageContent = message.getContent();

        for (Embed embed: message.getEmbeds()) {
            URL url = embed.getUrl();
            messageContent = messageContent + " " + url.toString();
        }

        Date messageEdited = message.getCreationDate().getTime();
        MessageLog one = repository.findFirstByMidOrderByTimeDesc(mid);
        if (one == null) {
            return;
        }

        MessageLog pojo = new MessageLog(one);
        pojo.setTime(messageEdited);
        pojo.setContent(messageContent);
        repository.save(pojo);

        System.out.println("EDITED " + pojo);
    }
}
