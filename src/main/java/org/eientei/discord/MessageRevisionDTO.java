package org.eientei.discord;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.User;
import org.springframework.format.datetime.DateFormatter;

import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alexander Tumin on 2016-11-18
 */
public class MessageRevisionDTO {
    private long id;
    private String time;
    private String authorName;
    private String authorAvatar;
    private String content;

    public MessageRevisionDTO(DiscordAPI api, DateFormatter formatter, MessageLog messageLog) throws ExecutionException, InterruptedException {
        this.id = messageLog.getId();
        this.time = formatter.print(messageLog.getTime(), Locale.getDefault());
        User user = api.getUserById(messageLog.getAuthorId()).get();
        if (user != null) {
            this.authorName = user.getName();
            this.authorAvatar = user.getAvatarUrl().toString();
        } else {
            this.authorName = "Anonymous";
        }
        this.content = messageLog.getContent();
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
