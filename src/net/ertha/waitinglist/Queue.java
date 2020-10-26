package net.ertha.waitinglist;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.ArrayList;
import java.util.LinkedList;

public class Queue {

    public String serverName;
    public java.util.Queue<ProxiedPlayer> normalQueue = new LinkedList<>();
    public java.util.Queue<ProxiedPlayer> vipQueue = new LinkedList<>();
    public Integer maxPlayers = 0;
    public Integer onlinePlayers = 0;
    public Boolean online = Boolean.FALSE;
    public Integer reservedSpots = 10;
    public ArrayList<String> connectingPlayer = new ArrayList<>();
    private final WaitingList wl;


    public Queue(WaitingList waitingList, String _serverName){
         serverName = _serverName;
         wl = waitingList;
    }

    public void add(ProxiedPlayer player){
        wl.verbose("Adding "+player.getName()+" to Queue");
        if(player.hasPermission("waitinglist.admin"))
            connect(player);
        else if(player.hasPermission("waitinglist."+serverName+".vip"))
            vipQueue.add(player);
        else
            normalQueue.add(player);
        warnNextInQueue(player);
    }

    public void remove(ProxiedPlayer player){
        wl.verbose("Removing "+player.getName()+" from Queue");
        vipQueue.remove(player);
        normalQueue.remove(player);
    }

    private void connect(ProxiedPlayer player){
        wl.verbose("Connecting: "+player.getName());
        connectingPlayer.add(player.getName());
        ServerInfo info = ProxyServer.getInstance().getServerInfo(serverName);
        player.connect(info, ServerConnectEvent.Reason.PLUGIN);
    }

    public void ping(){
        wl.verbose("Ping for "+serverName);
        ProxyServer.getInstance().getServerInfo(serverName).ping((v, throwable) -> {
            if (throwable != null) {
                online = false;
                return;
            }
            if (v == null) {
                online = false;
                return;
            }
            online = true;
            ServerPing.Players players = v.getPlayers();
            if (players != null) {
                maxPlayers = players.getMax();
                onlinePlayers = players.getOnline();
            } else {
                maxPlayers = 0;
                onlinePlayers = 0;
            }
            wl.verbose("Currently ("+onlinePlayers+"\\"+maxPlayers+") online");

            if(maxPlayers > onlinePlayers + reservedSpots){
                wl.verbose("There is room on "+serverName);
                if(!vipQueue.isEmpty())
                    connect(vipQueue.poll());
                else if(!normalQueue.isEmpty())
                    connect(normalQueue.poll());

                wl.verbose("Checked the Queues!");
                warnNextInQueue();
            }
        });
    }

    private void warnNextInQueue(){
        if(!vipQueue.isEmpty())
            vipQueue.peek().sendMessage(new TextComponent("You are next in line for "+serverName+" you could be connected after 10 seconds."));
        if(!normalQueue.isEmpty())
            normalQueue.peek().sendMessage(new TextComponent("You are next in line for "+serverName+" you could be connected after 10 seconds."));
    }

    private void warnNextInQueue(ProxiedPlayer player){
        if(!vipQueue.isEmpty()&&vipQueue.peek().getUniqueId().equals(player.getUniqueId()))
            vipQueue.peek().sendMessage(new TextComponent("You are next in line for "+serverName+" you could be connected within 10 seconds."));

        if(!normalQueue.isEmpty()&&normalQueue.peek().getUniqueId().equals(player.getUniqueId()))
            normalQueue.peek().sendMessage(new TextComponent("You are next in line for "+serverName+" you could be connected within of 10 seconds."));

    }



}
