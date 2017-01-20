package org.eientei.discord;

import net.dv8tion.jda.core.JDA;
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

    public MessageDTO(JDA jda, DateFormatter formatter, Message message) {
        this.id = message.getMid();
        this.channel = jda.getTextChannelById(message.getChannelId()).getName();
        revisions.add(new MessageRevisionDTO(jda, formatter, message));
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
