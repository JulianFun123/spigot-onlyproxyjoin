package de.interaapps.mc.onlyproxyjoin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class OnlyProxyJoin extends JavaPlugin {

    private String errorMessage = "§cPlease join through the Proxy";
    private ArrayList<String> proxies;

    public void onEnable() {

        PluginManager pluginManager = Bukkit.getPluginManager();

        if (!getConfig().contains("proxies")) {
            getConfig().set("proxies", new ArrayList<String>(){{
                    add("PROXY HERE (Like 431.563.32.132)");
            }});
            saveConfig();
        }

        if (!getConfig().contains("error_message")) {
            getConfig().set("error_message", "§cPlease join through the Proxy");
            saveConfig();
        }

        try {
            proxies = (ArrayList<String>) getConfig().get("proxies");
        } catch (ClassCastException e){
            proxies = new ArrayList<String>();
            Bukkit.getConsoleSender().sendMessage("§4Error: §cError while loading the proxies. Reload the server please.");
        }

        errorMessage = getConfig().getString("error_message");


        Bukkit.getConsoleSender().sendMessage("§a- = - = -  §9OnlyProxyJoin  §a- = - = -\n§aRegistered Proxies:");

        for (String proxy : proxies)
            Bukkit.getConsoleSender().sendMessage("§6- §b"+proxy);

        if (proxies.size() == 0)
            Bukkit.getConsoleSender().sendMessage("§4Error: §cThere are no Proxies registered!");

        Bukkit.getConsoleSender().sendMessage("§a- = - = -  §9OnlyProxyJoin  §a- = - = -");

        pluginManager.registerEvents(new Listener() {

            @EventHandler
            public void loginListener(PlayerLoginEvent event) {
                if (!proxies.contains(event.getAddress().getHostAddress())) {
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage(errorMessage);
                }
            }
        }, this);


        getCommand("onlyproxyjoin").setExecutor((commandSender, command, s, args) -> {

                if (commandSender.hasPermission("onlyproxyjoin.admin")) {
                    if (args.length == 2) {
                        if (args[0].equals("add")) {
                            proxies.add(args[1]);
                            getConfig().set("proxies", proxies);
                            saveConfig();
                            commandSender.sendMessage("§aAdded Proxy! IP: "+args[1]);
                        } else if (args[0].equals("remove")) {
                            proxies.remove(args[1]);
                            getConfig().set("proxies", proxies);
                            saveConfig();
                            commandSender.sendMessage("§aRemoved Proxy! IP: "+args[1]);
                        }
                    } else if (args.length == 1) {
                        if (args[0].equals("init")) {
                            if (commandSender instanceof Player) {
                                proxies.add( ((Player) commandSender).getAddress().getHostName() );
                                getConfig().set("proxies", proxies);
                                saveConfig();
                                commandSender.sendMessage("§aAdded Proxy! IP: "+((Player) commandSender).getAddress().getHostName());
                            } else
                                commandSender.sendMessage("§cYou have to be a Player!");
                        }
                    } else
                        commandSender.sendMessage("§cInvalid Argument!");
                } else
                    commandSender.sendMessage("§cPermissions denied");

                return true;
            }
        );

    }

}
