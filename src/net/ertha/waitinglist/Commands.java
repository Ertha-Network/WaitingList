package net.ertha.waitinglist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class Commands extends Command {

    public final WaitingList waitingList;

    public Commands(WaitingList _waitingList){
        super("enter");
        waitingList = _waitingList;
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings){
        String string = strings.toString();
        if(waitingList.getProxy().getServerInfo(string).getName().equals(string))
        if(commandSender instanceof ProxiedPlayer){
            waitingList.addToQueue((ProxiedPlayer) commandSender, string);
        }
    }
}
