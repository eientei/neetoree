package org.eientei.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.spi.LocaleServiceProvider;
import java.util.stream.Collectors;

/**
 * Created by Alexander Tumin on 2016-11-15
 */
@Controller
@RequestMapping(value = "/api/logs", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@ResponseBody
public class LogsRest {
    public final static DateFormatter dateformatter = new DateFormatter("yyyy-MM-dd");
    public final static DateFormatter timeformatter = new DateFormatter("HH:mm:ss");
    private final MessageRepository repository;
    private final JDA jda;

    @Autowired
    public LogsRest(MessageRepository repository, JDA jda) {
        this.repository = repository;
        this.jda = jda;
    }

    @RequestMapping(value = "channels")
    public List<String> channels() {
        return repository.findDistinctChannelId()
                .stream()
                .map(id -> jda.getTextChannelById(id) != null ? jda.getTextChannelById(id).getName() : null)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "messages/{channel}/{date}")
    public Page<MessageDTO> messages(@PathVariable String channel, @PathVariable String date, @PageableDefault(size = 10000) Pageable pageable) throws ParseException {
        Date begin = dateformatter.parse(date, Locale.getDefault());
        Date end = Date.from(begin.toInstant().plus(1, ChronoUnit.DAYS));
        if (jda.getTextChannelsByName(channel, true).isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }
        String channelId = jda.getTextChannelsByName(channel, true).get(0).getId();
        return repository.findByChannelIdAndTimeBetweenOrderByTimeAsc(channelId, begin, end, pageable).map(
                message -> new MessageDTO(jda, timeformatter, message)
        );
    }

    @RequestMapping(value = "dates/{channel}")
    public List<MessageCountDTO> dates(@PathVariable String channel) {
        if (jda.getTextChannelsByName(channel, true).isEmpty()) {
            return Collections.singletonList(new MessageCountDTO(dateformatter, new MessageCount(new Date(0), 0)));
        }
        String channelId = jda.getTextChannelsByName(channel, true).get(0).getId();
        return repository.findMessageCount(channelId)
                .stream()
                .map(c -> new MessageCountDTO(dateformatter, c))
                .collect(Collectors.toList());
    }
}
