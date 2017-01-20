package org.eientei.discord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Alexander Tumin on 2016-11-14
 */
@Repository
public interface MessageRepository extends org.springframework.data.repository.Repository<Message, Long> {
    @Query("select m from Message m where m.id in (select max(m.id) from Message m where m.authorId is not null and m.channelId = :cid and m.time between :first and :last group by m.mid) order by m.time")
    Page<Message> findByChannelIdAndTimeBetweenOrderByTimeAsc(@Param("cid") String channelId, @Param("first") Date start, @Param("last") Date end, Pageable pageable);

    @Query("select distinct m.channelId from Message m where m.channelId is not null")
    List<String> findDistinctChannelId();

    @Query("select new org.eientei.discord.MessageCount(max(m.time), count(m)) from Message m where m.time is not null and m.channelId = :cid group by function( 'to_char', m.time, 'YYYY-MM-DD') order by max(m.time)")
    List<MessageCount> findMessageCount(@Param("cid") String channelId);
}
