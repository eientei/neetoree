package org.eientei.discord.commands;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.UserStatus;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.server.ServerMemberAddListener;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import de.btobastian.sdcf4j.CommandHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alexander <iamtakingiteasy> Tumin on 2017-01-21.
 */
@Component
public class VoteMute implements CommandExecutor, ServerMemberAddListener {
    private final CommandHandler handler;
    private final DiscordAPI api;
    private final Map<String, Set<String>> voteMap = new HashMap<>();
    private final Map<String, Long> delays = new HashMap<>();
    private final Map<String, Long> inits = new HashMap<>();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public VoteMute(CommandHandler handler, DiscordAPI api) {
        this.handler = handler;
        this.api = api;
    }

    @PostConstruct
    public void postConstruct() {
        handler.registerCommand(this);
        api.registerListener(this);
        for (Server server : api.getServers()) {
            for (Role role : server.getRoles()) {
                if (role.getName().equalsIgnoreCase("PNDORNUXA")) {
                    for (User user : role.getUsers()) {
                        role.removeUser(user);
                    }
                }
            }
        }
    }

    @Command(aliases = {"!votemute"})
    public synchronized String onCommand(Message message, Server server) {
        User user = message.getAuthor();
        Role role = null;
        for (Role r : server.getRoles()) {
            if (r.getName().equalsIgnoreCase("PNDORNUXA")) {
                role = r;
                break;
            }
        }

        if (role == null) {
            return "Role 'PNDORNUXA' not found";
        }

        if (role.getUsers().contains(user)) {
            return "Это не для вас написано, молодой человек";
        }

        long totalUsers = server.getMembers().stream().filter(u -> u.getStatus() == UserStatus.ONLINE).count();
        int required = (int) (totalUsers / 100.0 * 30.0);

        User cnd = message.getMentions().isEmpty() ? null : message.getMentions().get(0);
        if (cnd != null) {
            Set<String> votes = voteMap.computeIfAbsent(cnd.getId(), k -> new HashSet<>());
            votes.add(user.getId());
            if (votes.size() >= required) {
                votes.clear();
                role.addUser(cnd);
                long delay = delays.computeIfAbsent(cnd.getId(), k -> 16L);
                delay <<= 1;
                delays.put(cnd.getId(), delay);
                inits.put(user.getId(), System.currentTimeMillis());
                Role finalRole = role;
                executor.schedule(() -> finalRole.removeUser(user), delay, TimeUnit.MINUTES);
                return cnd.getMentionTag() + " был замьючен на " + delay + " минут(ы)";
            } else {
                return votes.size() + " из " + required + " голосов для мьюта " + cnd.getMentionTag();
            }
        }

        return "Некого педалить...";
    }

    @Override
    public void onServerMemberAdd(DiscordAPI api, User user, Server server) {
        for (String userId : inits.keySet()) {
            if (userId.equals(user.getId())) {
                for (Role role : server.getRoles()) {
                    if (role.getName().equalsIgnoreCase("PNDORNUXA")) {
                        role.addUser(user);
                        return;
                    }
                }
            }
        }
    }
}
