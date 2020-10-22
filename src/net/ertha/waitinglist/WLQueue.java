package net.ertha.waitinglist;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.LinkedList;
import java.util.Queue;

public class WLQueue {

    public final String serverName;
    public Queue<ProxiedPlayer> normalQueue = new LinkedList<>();
    public Queue<ProxiedPlayer> vipQueue = new LinkedList<>();
    public Integer maxPlayers = 0;
    public Integer onlinePlayers = 0;
    public Boolean online = Boolean.FALSE;
    public Integer reservedSpots = 10;


    public WLQueue(String _serverName){
         serverName = _serverName;
    }

    public void add(ProxiedPlayer player){
        if(player.hasPermission("waitinglist.admin"))
            connect(player);
        else if(player.hasPermission("waitinglist.vip"))
            vipQueue.add(player);
        else
            normalQueue.add(player);
    }

    public void remove(ProxiedPlayer player){
        vipQueue.remove(player);
        normalQueue.remove(player);
    }

    private void connect(ProxiedPlayer player){
        ServerInfo info = ProxyServer.getInstance().getServerInfo("anarchy");
        player.connect(info, ServerConnectEvent.Reason.PLUGIN);
    }

    public void ping(){
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

            if(maxPlayers < onlinePlayers + reservedSpots){
                if(!vipQueue.isEmpty())
                    connect(vipQueue.poll());
                else if(!normalQueue.isEmpty())
                    connect(normalQueue.poll());


            }
        });
    }




}
