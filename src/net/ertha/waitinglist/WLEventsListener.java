package net.ertha.waitinglist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;


public class WLEventsListener implements Listener{
    private final String server;
    private final WaitingList waitingList;

    public WLEventsListener(String _server, WaitingList _waitingList){
        server = _server;
        waitingList = _waitingList;

    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event){
            if(event.getCursor().length() <= 6) {
                event.getSuggestions().add(server);
            }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerConnect(ServerConnectEvent event){
        if(event.getTarget().getName().equals(server)){
            for (WLQueue queue:waitingList.Queues) {
                if(queue.connectingPlayer.contains(event.getPlayer().getName())){
                    queue.connectingPlayer.remove(event.getPlayer().getName());
                    return;
                }
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage(new TextComponent(
                    ChatColor.GRAY + "[" + ChatColor.RED + "WaitingList" + ChatColor.GRAY + "] " +
                            ChatColor.YELLOW + server + " has a waiting list and you are now on it. " + ChatColor.GREEN +
                            "Please enjoy another server while you wait."));
            waitingList.addToQueue(event.getPlayer(),event.getTarget().getName());
        }
    }

    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent event){
        if(!event.getPlayer().isConnected()){
            waitingList.playerDisconnect(event.getPlayer());
        }

    }


}
