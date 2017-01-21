package org.eientei.discord;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Alexander Tumin on 2016-11-14
 */
@Entity
@Table(name = "Message")
public class MessageLog {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private String mid;
    private String channelId;

    @OrderBy
    private Date time;

    private String authorId;

    @Column(columnDefinition = "TEXT")
    private String content;

    public MessageLog(MessageLog one) {
        this.mid = one.getMid();
        this.channelId = one.getChannelId();
        this.authorId = one.getAuthorId();
    }

    public MessageLog() {
    }

    public MessageLog(String mid) {
        this.mid = mid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString() {
        return getMid() + " #" + getChannelId() + " [" + String.valueOf(getTime()) + "] <" + getAuthorId() + "> " + String.valueOf(getContent());
    }
}
