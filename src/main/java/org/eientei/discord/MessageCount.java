package org.eientei.discord;

import java.util.Date;

/**
 * Created by Alexander Tumin on 2016-11-18
 */
public class MessageCount {
    private final Date date;
    private final long count;

    public MessageCount(Date date, long count) {
        this.date = date;
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public long getCount() {
        return count;
    }
}
