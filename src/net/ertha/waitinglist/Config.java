package net.ertha.waitinglist;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Config {

    private final File file = new File(ProxyServer.getInstance().getPluginsFolder()+ "/WaitingList/WaitingList.yml");
    private Configuration configuration;

    private final WaitingList wl;


    public Config(WaitingList waitingList) {
        wl = waitingList;
        createConfig();
        getConfiguration();
    }

    public Configuration get(){
        return this.configuration;
    }

    private void getConfiguration() {
        if(configuration == null) {
            try {
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createConfig(){
        String directoryName = ProxyServer.getInstance().getPluginsFolder()+ "/WaitingList";

        File directory = new File(directoryName);
        if (! directory.exists()){
            directory.mkdir();
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
        try {
            if (!file.exists()) {
                file.createNewFile();

                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                List<String> servers = new ArrayList();
                servers.add("anarchy");
                configuration.set("Servers", servers);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
