package org.eientei.discord;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
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
import java.util.*;
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
    private final DiscordAPI api;

    @Autowired
    public LogsRest(MessageRepository repository, DiscordAPI api) {
        this.repository = repository;
        this.api = api
        ;
    }

    @RequestMapping(value = "channels")
    public List<String> channels() {
        return repository.findDistinctChannelId()
                .stream()
                .map(id -> api.getChannelById(id) != null ? api.getChannelById(id).getName() : null)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "messages/{channel}/{date}")
    public Page<MessageDTO> messages(@PathVariable String channel, @PathVariable String date, @PageableDefault(size = 10000) Pageable pageable) throws ParseException {
        Date begin = dateformatter.parse(date, Locale.getDefault());
        Date end = Date.from(begin.toInstant().plus(1, ChronoUnit.DAYS));
        if (findChannel(channel).isEmpty()) {
            return new PageImpl<>(Collections.emptyList());
        }
        String channelId = findChannel(channel).get(0).getId();
        return repository.findByChannelIdAndTimeBetweenOrderByTimeAsc(channelId, begin, end, pageable).map(
                message -> new MessageDTO(api, timeformatter, message)
        );
    }

    private List<Channel> findChannel(String name) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : api.getChannels()) {
            if (channel.getName().equalsIgnoreCase(name)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    @RequestMapping(value = "dates/{channel}")
    public List<MessageCountDTO> dates(@PathVariable String channel) {
        if (findChannel(channel).isEmpty()) {
            return Collections.singletonList(new MessageCountDTO(dateformatter, new MessageCount(new Date(0), 0)));
        }
        String channelId = findChannel(channel).get(0).getId();
        return repository.findMessageCount(channelId)
                .stream()
                .map(c -> new MessageCountDTO(dateformatter, c))
                .collect(Collectors.toList());
    }
}
