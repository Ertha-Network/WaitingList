package net.ertha.waitinglist;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WaitingList extends Plugin {

    public ArrayList<Queue> Queues = new ArrayList<Queue>();
    public Boolean enableVerbose = false;
    private final Configuration configuration = new Config(this).get();

    @Override
    public void onEnable() {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();

        List<String> servers = configuration.getStringList("Servers");
        for (String server : servers) {

            Queue que = new Queue(this, server);
            getLogger().info("Created queue for "+que.serverName);
            Queues.add(que);

            pm.registerCommand(this, new Commands(this));
            pm.registerListener(this, new EventsListener(server, this));

            ProxyServer.getInstance().getScheduler().schedule(this, this::checkQueue, 10, 10, TimeUnit.SECONDS);

        }
        getLogger().info("has loaded");
    }

    public void checkQueue() {
        for (Queue queue : Queues) {
            queue.ping();
        }
    }

    public void verbose(String log) {
        if (enableVerbose)
            getLogger().info(log);
    }

    @Override
    public void onDisable() {
        getLogger().info("has unloaded");
    }

    public void playerDisconnect(ProxiedPlayer player) {
        for (Queue queue : Queues) {
            queue.remove(player);
        }
    }

    public void addToQueue(ProxiedPlayer player, String server) {
        for (Queue queue : Queues) {
            if (queue.serverName.equals(server)) {
                queue.add(player);
            }
        }
    }

}
