package org.eientei.discord;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alexander <iamtakingiteasy> Tumin on 2017-01-15.
 */
public class CommandDispatcher extends ListenerAdapter {
    private final JDA jda;
    private final Map<User, VoteMute> voteMute = new HashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public CommandDispatcher(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String trim = event.getMessage().getContent().trim();
        if (trim.startsWith("!")) {
            trim = trim.substring(1);
            if (trim.startsWith("votemute ")) {
                trim = trim.substring("votemute ".length()).trim();
                List<User> users = jda.getUsersByName(trim, true);
                if (users.size() == 0) {
                    try {
                        event.getChannel().sendMessage("No such user: " + trim).block();
                    } catch (RateLimitedException ignore) {
                    }
                    return;
                }
                if (users.size() > 1) {
                    StringBuilder usersString = new StringBuilder();
                    for (User user : users) {
                        if (usersString.length() > 0) {
                            usersString.append(", ");
                        }
                        usersString.append(user.getName());
                    }
                    try {
                        event.getChannel().sendMessage("Too many users; possible matches are: " + usersString.toString()).block();
                    } catch (RateLimitedException ignore) {
                    }
                    return;
                }

                double onliners = 0;
                for (Member member : event.getTextChannel().getMembers()) {
                    if (member.getOnlineStatus() == OnlineStatus.ONLINE) {
                        onliners++;
                    }
                }

                int min = (int) Math.ceil(onliners * 15.0 / 100.0);
                if (min < 2) {
                    min = 2;
                }
                if (min > 6) {
                    min = 6;
                }

                User user = users.get(0);
                if (!voteMute.containsKey(user)) {
                    voteMute.put(user, new VoteMute(min));
                }

                if (voteMute.get(user).add()) {
                    voteMute.remove(user);
                    for (Member member : event.getTextChannel().getMembers()) {
                        if (member.getUser() == user) {
                            List<Role> roles = event.getGuild().getRolesByName("PNDORNUXA", true);
                            member.getRoles().add(roles.get(0));
                            executorService.schedule((Runnable) () -> member.getRoles().remove(roles.get(0)), 1, TimeUnit.DAYS);
                            try {
                                event.getChannel().sendMessage("User " + user.getName() + " was PNDORNUT for 24 hours").block();
                            } catch (RateLimitedException e) {
                            }
                            return;
                        }
                    }
                }

                

                try {
                    event.getChannel().sendMessage("User " + user.getName() + " will be PNDORNUT for 24 hours if " + (min - voteMute.get(user).getCurrent()) + " more users will vote the same").block();
                } catch (RateLimitedException ignore) {
                }
            }
        }
    }
}
