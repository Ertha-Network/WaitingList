package net.ertha.waitinglist;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WaitingList extends Plugin {

    private File file;
    private Configuration configuration;
    public ArrayList<WLQueue> Queues = new ArrayList<WLQueue>();
    public Boolean enableVerbose = false;

    @Override
    public void onEnable(){
        PluginManager pm = ProxyServer.getInstance().getPluginManager();


        file = new File(ProxyServer.getInstance().getPluginsFolder()+ "/WaitingList.yml");

        try {
            if (!file.exists()) {
                file.createNewFile();

                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                configuration.set("Servers.1", "anarchy");
            }else {
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,file);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String server = configuration.getString("Servers.1");

        WLQueue que = new WLQueue(this, server);
        getLogger().info(que.serverName);
        Queues.add(que);// This creates a NUll exception needs to be fixed...

        pm.registerCommand(this, new WLCommands(this));
        pm.registerListener(this, new WLEventsListener(server, this));

        ProxyServer.getInstance().getScheduler().schedule(this,this::checkQueue,10, 10, TimeUnit.SECONDS);

        getLogger().info("has loaded");
    }

    public void checkQueue(){
        for (WLQueue queue:Queues) {
            queue.ping();
        }
    }

    public void verbose(String log){
        if(enableVerbose)
            getLogger().info(log);
    }

    @Override
    public void onDisable(){
        getLogger().info("has unloaded");
    }

    public void playerDisconnect(ProxiedPlayer player){
        for (WLQueue queue:Queues) {
            queue.remove(player);
        }
    }

    public void addToQueue(ProxiedPlayer player, String server){
        for (WLQueue queue:Queues) {
            if(queue.serverName.equals(server)){
                queue.add(player);
            }
        }
    }

}
