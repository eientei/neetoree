package org.eientei.discord;

import de.btobastian.javacord.DiscordAPI;
import org.springframework.format.datetime.DateFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Tumin on 2016-11-15
 */
public class MessageDTO {
    private String id;
    private String channel;
    private List<MessageRevisionDTO> revisions = new ArrayList<>();

    public MessageDTO(DiscordAPI api, DateFormatter formatter, MessageLog messageLog) {
        this.id = messageLog.getMid();
        this.channel = api.getChannelById(messageLog.getChannelId()).getName();
        try {
            revisions.add(new MessageRevisionDTO(api, formatter, messageLog));
        } catch (Exception ignore) {
        }
    }

    public String getId() {
        return id;
    }

    public String getChannel() {
        return channel;
    }

    public List<MessageRevisionDTO> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<MessageRevisionDTO> revisions) {
        this.revisions = revisions;
    }
}
