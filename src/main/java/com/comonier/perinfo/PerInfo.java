package com.comonier.perinfo;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PerInfo extends JavaPlugin {

    private FileConfiguration messages;
    private String lang;
    private static Economy econ = null;
    private final Map snapshotCache = new HashMap();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        loadMessages();
        setupEconomy();

        PerInfoCommand executor = new PerInfoCommand(this);
        setupCmd("perinfo", executor);
        setupCmd("iinfo", executor);
        setupCmd("simbolos", executor);

        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(executor, this);
        
        Bukkit.getScheduler().runTaskTimer(this, snapshotCache::clear, 12000L, 12000L);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        econ = null;
    }

    private void setupCmd(String n, PerInfoCommand ex) {
        PluginCommand pc = getCommand(n);
        if (pc != null) {
            pc.setExecutor(ex);
            pc.setTabCompleter(ex);
        }
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) econ = (Economy) rsp.getProvider();
    }

    public void loadMessages() {
        reloadConfig();
        File f = new File(getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(f);
        lang = getConfig().getString("language", "en");
    }

    public String getMsg(String path) {
        String fullPath = lang + "." + path;
        String m = messages.getString(fullPath, messages.getString("en." + path, path));
        return ChatColor.translateAlternateColorCodes('&', m);
    }

    public boolean hasPerm(Player p, String path, String node) {
        String mode = getConfig().getString("commands." + path + ".permission-mode", "default");
        if (mode.equalsIgnoreCase("default") == false) return p.hasPermission(node);
        return true;
    }

    public Economy getEcon() { return econ; }
    public Map getSnapshotCache() { return snapshotCache; }
}
