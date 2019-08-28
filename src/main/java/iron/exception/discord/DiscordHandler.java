package iron.exception.discord;

import iron.exception.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DiscordHandler extends ListenerAdapter {

    public DiscordHandler() {
        try {
            this.jda = new JDABuilder(Config.discordToken).build();
        } catch (LoginException e) { e.printStackTrace(); }
        jda.addEventListener(this);
    }

    private JDA jda;


    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getAuthor().isBot()) return;


        if(e.getMessage().getContentRaw().startsWith("todo")) {
            try {
                sendTodo(e.getMessage().getContentRaw().substring(5), e.getAuthor().getAsMention() + ": #" + e.getChannel().getName() + " at (" + System.currentTimeMillis() + ")");
            } catch (Exception ex){
                e.getChannel().sendMessage("<@" + Config.user + "> couldn't parse the todo somehow").queue(x -> {
                    x.delete().submitAfter(Config.deleteCommandSecs, TimeUnit.MILLISECONDS);
                        });

            }
            e.getMessage().delete().submitAfter(Config.deleteCommandSecs, TimeUnit.MILLISECONDS);
        } else if(e.getTextChannel().equals(this.jda.getGuildById(Config.todoServer).getTextChannelById(Config.todoChannel))){
            e.getMessage().delete().submitAfter(Config.deleteCommandSecs, TimeUnit.MILLISECONDS);
        }

        /* extra (can be removed) */
        if (e.getMessage().getContentRaw().equals("ping"))
        {
            MessageChannel channel = e.getChannel();
            long time = System.currentTimeMillis();
            channel.sendMessage("Pong!") /* => RestAction<Message> */
                    .queue(response /* => Message */ -> {
                        response.editMessageFormat("pong: %d ms", System.currentTimeMillis() - time).queue();
                    });
        }

    }

    public void sendTodo(String msg, String desc){
        TextChannel c = this.jda.getGuildById(Config.todoServer).getTextChannelById(Config.todoChannel);

        c.sendTyping().queue();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(msg);
        eb.setDescription(desc);
        eb.setColor(Color.GREEN);

        c.sendMessage(eb.build()).queue(x -> {
            x.addReaction("U+1F550").queue(); // one o'clock
            x.addReaction("U+1F53C").queue(); // upwards button
            x.addReaction("U+1F53D").queue(); // downwards button
            x.addReaction("U+1F564").queue(); // nine-thirty
            x.addReaction("U+270F").queue(); // pencil
            x.addReaction("U+274C").queue(); // cross mark
            x.addReaction("U+2705").queue(); // check mark button
        });



    }


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e){
        System.out.println("got reacting");
        if(e.getMember().getUser().getIdLong() != Config.user){
            e.getReaction().removeReaction(e.getUser()).queue();
            return;
        }

        TextChannel c = this.jda.getGuildById(Config.todoServer).getTextChannelById(Config.dumpChannel);
        //c.sendMessage(e.getChannel().get)


//        e.getReactionEmote().getEmote().



    }



}
