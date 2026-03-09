package com.comonier.perinfo;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player; // IMPORT ESSENCIAL
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class PerInfo extends JavaPlugin {
    private FileConfiguration messages;
    private String lang;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("messages.yml", false);
        loadMessages();
        setupEconomy();
        
        PerInfoCommand executor = new PerInfoCommand(this);
        setupCmd("perinfo", executor);
        setupCmd("iinfo", executor);
        setupCmd("per", executor);
        
        // Registro dos Listeners
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(executor, this);
    }

    private void setupCmd(String n, PerInfoCommand ex) {
        PluginCommand pc = getCommand(n);
        if (pc != null) { pc.setExecutor(ex); pc.setTabCompleter(ex); }
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) econ = rsp.getProvider();
    }

    public void loadMessages() {
        reloadConfig();
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
        lang = getConfig().getString("language", "en");
    }

    public String getMsg(String path) {
        String fullPath = lang + "." + path;
        String m = messages.getString(fullPath, messages.getString("en." + path, path));
        return ChatColor.translateAlternateColorCodes('&', m);
    }

    public boolean hasPerm(Player p, String path, String node) {
        return p.hasPermission(node);
    }

    public Economy getEcon() { return econ; }
}
