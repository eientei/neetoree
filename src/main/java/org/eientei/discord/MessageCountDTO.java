package org.eientei.discord;

import org.springframework.format.datetime.DateFormatter;

import java.util.Locale;

/**
 * Created by Alexander Tumin on 2016-11-18
 */
public class MessageCountDTO {
    private String date;
    private long count;

    public MessageCountDTO(DateFormatter formatter, MessageCount messageCount) {
        this.date = formatter.print(messageCount.getDate(), Locale.getDefault());
        this.count = messageCount.getCount();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
