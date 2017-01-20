package org.eientei.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageEmbedEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.sql.Date;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Created by Alexander Tumin on 2016-11-14
 */
public class MessagePersister extends ListenerAdapter {
    private final MessageAdminRepository repository;
    private final JDA jda;

    public MessagePersister(MessageAdminRepository repository, JDA jda) {
        this.repository = repository;
        this.jda = jda;
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        MessageChannel channel = event.getChannel();
        if (channel.getType() != ChannelType.TEXT) {
            return;
        }
        String mid = event.getMessageId();
        Message one = repository.findFirstByMidOrderByTimeDesc(mid);
        if (one == null) {
            return;
        }

        Message pojo = new Message(one);
        pojo.setTime(new Date(System.currentTimeMillis()));
        repository.save(pojo);

        String nick = event.getJDA().getUserById(pojo.getAuthorId()).getName();

        try {
            event.getChannel().sendMessage("< **" + nick + "** > *(deleted)*").block();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
        System.out.println("DEL " + pojo);
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        net.dv8tion.jda.core.entities.Message message = event.getMessage();
        MessageChannel channel = message.getChannel();
        if (channel.getType() != ChannelType.TEXT) {
            return;
        }

        String mid = message.getId();
        String messageContent = message.getContent();
        OffsetDateTime messageEdited = message.getEditedTime();
        Message one = repository.findFirstByMidOrderByTimeDesc(mid);
        if (one == null) {
            return;
        }

        Message pojo = new Message(one);
        pojo.setTime(Date.from(messageEdited.atZoneSimilarLocal(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()));
        pojo.setContent(messageContent);
        repository.save(pojo);

        System.out.println("EDITED " + pojo);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        net.dv8tion.jda.core.entities.Message message = event.getMessage();
        if (message.getChannel().getType() != ChannelType.TEXT) {
            return;
        }

        Message pojo = messageToPojo(message);
        repository.save(pojo);

        System.out.println("NEW " + pojo);
    }

    public static Message messageToPojo(net.dv8tion.jda.core.entities.Message message) {
        String mid = message.getId();
        User author = message.getAuthor();
        MessageChannel channel = message.getChannel();
        String channelId = channel.getId();

        String authorId = author.getId();
        String messageContent = message.getContent();
        OffsetDateTime messageCreated = message.getCreationTime();

        Message pojo = new Message(mid);
        pojo.setChannelId(channelId);
        pojo.setTime(Date.from(messageCreated.atZoneSimilarLocal(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()));
        pojo.setAuthorId(authorId);
        pojo.setContent(messageContent);
        return pojo;
    }

    @Override
    public void onMessageEmbed(MessageEmbedEvent event) {
        if (event.getChannel().getType() != ChannelType.TEXT) {
            return;
        }

        StringBuilder content = new StringBuilder();
        for (MessageEmbed embed : event.getMessageEmbeds()) {
            if (content.length() > 0) {
                content.append("\n");
            }
            content.append(embed.getUrl());
        }

        Message pojo = new Message(event.getMessageId());
        pojo.setChannelId(event.getChannel().getId());
        pojo.setTime(Date.from(event.getMessageEmbeds().get(0).getTimestamp().atZoneSimilarLocal(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant()));
        pojo.setAuthorId(jda.getUsersByName(event.getMessageEmbeds().get(0).getAuthor().getName(), true).get(0).getId());
        pojo.setContent(content.toString());

        repository.save(pojo);

        System.out.println("NEW " + pojo);
    }
}
