package org.eientei.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.springframework.format.datetime.DateFormatter;

import java.util.Locale;

/**
 * Created by Alexander Tumin on 2016-11-18
 */
public class MessageRevisionDTO {
    private long id;
    private String time;
    private String authorName;
    private String authorAvatar;
    private String content;

    public MessageRevisionDTO(JDA jda, DateFormatter formatter, Message message) {
        this.id = message.getId();
        this.time = formatter.print(message.getTime(), Locale.getDefault());
        User user = jda.getUserById(message.getAuthorId());
        if (user != null) {
            this.authorName = user.getName();
            this.authorAvatar = user.getAvatarUrl();
            if (this.authorAvatar == null) {
                this.authorAvatar = user.getDefaultAvatarUrl();
            }
        } else {
            this.authorName = "Anonymous";
        }
        this.content = message.getContent();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
