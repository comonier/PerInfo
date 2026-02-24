package com.comonier.perinfo;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.ItemTag;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class PerInfo extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

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

        registerCommand("perinfo");
        registerCommand("iinfo");
        registerCommand("simbolos");

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, snapshotCache::clear, 12000L, 12000L);
        
        getLogger().info("PerInfo v1.1 - Sistema de Hover 1.21.1 Ativado!");
    }

    private void registerCommand(String name) {
        PluginCommand cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(this);
            cmd.setTabCompleter(this);
        }
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return;
        RegisteredServiceProvider rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) econ = (Economy) rsp.getProvider();
    }

    public void loadMessages() {
        reloadConfig();
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
        lang = getConfig().getString("language", "en");
    }

    private String getMsg(String path) {
        String fullPath = lang + "." + path;
        String message = messages.getString(fullPath, messages.getString("en." + path, path));
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private boolean hasPerm(Player player, String configPath, String permNode) {
        String mode = getConfig().getString("commands." + configPath + ".permission-mode", "default");
        if (mode.equalsIgnoreCase("default")) return true;
        return player.hasPermission(permNode);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("perinfo")) {
            if (args.length == 0) {
                sendPluginInfo(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                loadMessages();
                sender.sendMessage(getMsg("reload-success"));
                return true;
            }

            if (args[0].equalsIgnoreCase("view") && args.length >= 2) {
                if (sender instanceof Player player) {
                    if (snapshotCache.containsKey(args[1])) {
                        player.openInventory((Inventory) snapshotCache.get(args[1]));
                        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
                    }
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("list") && args.length >= 2) {
                Plugin target = Bukkit.getPluginManager().getPlugin(args[1]);
                if (target == null) {
                    sender.sendMessage(getMsg("plugin-not-found").replace("{name}", args[1]));
                    return true;
                }
                sender.sendMessage(getMsg("list-header").replace("{plugin}", target.getName()));
                Map cmds = target.getDescription().getCommands();
                if (cmds != null) {
                    cmds.forEach((k, v) -> {
                        Map details = (Map) v;
                        Object pObj = details.get("permission");
                        if (sender instanceof Player p) sendClickableMessage(p, ChatColor.YELLOW + "/" + k + ChatColor.WHITE + " - ", (pObj != null ? pObj.toString() : getMsg("not-defined")));
                    });
                }
                return true;
            }

            handleCommandSearch(sender, args[0]);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("iinfo")) {
            if (sender instanceof Player player) {
                if (hasPerm(player, "iinfo", "perinfo.iinfo") == false) {
                    player.sendMessage(getMsg("no-permission"));
                    return true;
                }
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType() == Material.AIR) {
                    player.sendMessage(getMsg("item-empty"));
                    return true;
                }
                handleIInfo(player, item);
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("simbolos")) {
            sender.sendMessage(getMsg("symbols-title"));
            String[] keys = {"@hand", "@inv", "@ec", "@money", "@playtime"};
            String[] descs = {"symbol-hand", "symbol-inv", "symbol-ec", "symbol-money", "symbol-playtime"};
            for (int i = 0; i < keys.length; i++) {
                sender.sendMessage(getMsg("symbols-line").replace("{symbol}", keys[i]).replace("{desc}", getMsg(descs[i])));
            }
            return true;
        }
        return true;
    }

    private void handleCommandSearch(CommandSender sender, String cmdName) {
        String search = cmdName.startsWith("/") ? cmdName : "/" + cmdName;
        HelpTopic topic = Bukkit.getHelpMap().getHelpTopic(search);
        if (topic == null) {
            sender.sendMessage(getMsg("command-not-found").replace("{name}", cmdName));
            return;
        }
        PluginCommand pCmd = Bukkit.getPluginCommand(cmdName.replace("/", ""));
        if (pCmd != null) {
            sender.sendMessage(getMsg("plugin-info").replace("{plugin}", pCmd.getPlugin().getName()));
            sender.sendMessage(getMsg("version-info").replace("{version}", pCmd.getPlugin().getDescription().getVersion()));
            String perm = (pCmd.getPermission() != null) ? pCmd.getPermission() : getMsg("not-defined");
            if (sender instanceof Player p) sendClickableMessage(p, getMsg("perm-info"), perm);
            else sender.sendMessage(getMsg("perm-info") + perm);
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Comando: " + ChatColor.WHITE + topic.getName());
            sender.sendMessage(ChatColor.YELLOW + "Resumo: " + ChatColor.WHITE + topic.getShortText());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        if (msg.contains("@") == false) return;
        Player player = event.getPlayer();
        if (hasPerm(player, "chat-symbols", "perinfo.chat") == false) return;

        event.setCancelled(true);
        String rawFormat = event.getFormat();
        if (rawFormat == null || rawFormat.contains("%1$s") == false) rawFormat = "<%1$s> %2$s";
        
        String header = String.format(rawFormat, player.getDisplayName(), "");
        TextComponent finalMsg = new TextComponent(TextComponent.fromLegacyText(header));
        String[] words = msg.split(" ");

        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (w.equalsIgnoreCase("@hand")) finalMsg.addExtra(createItemComponent(player.getInventory().getItemInMainHand()));
            else if (w.equalsIgnoreCase("@inv")) finalMsg.addExtra(createSnapshot(player, false));
            else if (w.equalsIgnoreCase("@ec")) finalMsg.addExtra(createSnapshot(player, true));
            else if (w.equalsIgnoreCase("@money")) {
                double b = (econ != null) ? econ.getBalance(player) : 0;
                finalMsg.addExtra(new TextComponent(getMsg("money-format").replace("{amount}", String.format("%.2f", b))));
            } else if (w.equalsIgnoreCase("@playtime")) {
                int t = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
                finalMsg.addExtra(new TextComponent(ChatColor.AQUA + getMsg("time-format").replace("{h}", String.valueOf(t/72000)).replace("{m}", String.valueOf((t%72000)/1200))));
            } else finalMsg.addExtra(new TextComponent(TextComponent.fromLegacyText(w)));
            
            if (i != words.length - 1) finalMsg.addExtra(" ");
        }
        for (Player online : Bukkit.getOnlinePlayers()) online.spigot().sendMessage(finalMsg);
        Bukkit.getConsoleSender().sendMessage(player.getName() + ": " + msg);
    }

    private TextComponent createItemComponent(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return new TextComponent(ChatColor.RED + "[Vazio]");
        
        ItemMeta meta = item.getItemMeta();
        String name = (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : item.getType().name().replace("_", " ").toLowerCase();
        TextComponent c = new TextComponent(ChatColor.AQUA + "[" + name + ChatColor.AQUA + "]");
        
        if (meta != null) {
            // Se houver encantos, listamos no hover de texto (SHOW_TEXT) para garantir visibilidade na 1.21.1
            if (meta.hasEnchants()) {
                StringBuilder sb = new StringBuilder(ChatColor.YELLOW + name + "\n");
                meta.getEnchants().forEach((en, lvl) -> {
                    sb.append(ChatColor.GRAY + " - " + en.getKey().getKey() + " " + lvl + "\n");
                });
                c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(sb.toString())));
                return c;
            }
        }
        
        // Se for um item comum sem encantos, enviamos o SHOW_ITEM padrão
        try {
            c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(item.getType().getKey().toString(), item.getAmount(), ItemTag.ofNbt("{}"))));
        } catch (Exception e) {
            c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + name)));
        }
        return c;
    }

    private TextComponent createSnapshot(Player p, boolean ec) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String title = (ec ? "EnderChest: " : "Inv: ") + p.getName();
        Inventory gui = Bukkit.createInventory(null, ec ? 27 : 45, title);
        gui.setContents(ec ? p.getEnderChest().getContents() : p.getInventory().getContents());
        snapshotCache.put(id, gui);
        TextComponent c = new TextComponent((ec ? ChatColor.LIGHT_PURPLE : ChatColor.GREEN) + (ec ? "[EnderChest]" : "[Inventory]"));
        c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Clique para ver de " + p.getName())));
        c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/perinfo view " + id));
        return c;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Inv: ") || event.getView().getTitle().contains("EnderChest: ")) event.setCancelled(true);
    }

    private void handleIInfo(Player player, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        player.sendMessage(getMsg("item-info-header"));
        sendClickableMessage(player, getMsg("item-id").replace("{id}", ""), item.getType().getKey().toString());
        if (meta != null) {
            if (meta.hasCustomModelData()) player.sendMessage(getMsg("item-model").replace("{data}", String.valueOf(meta.getCustomModelData())));
            if (meta.getEnchants().isEmpty() == false) {
                String enchants = meta.getEnchants().entrySet().stream().map(e -> e.getKey().getKey().getKey() + ":" + e.getValue()).collect(Collectors.joining(", "));
                player.sendMessage(getMsg("item-enchants").replace("{list}", enchants));
            }
            if (meta.hasLore()) player.sendMessage(getMsg("item-lore").replace("{count}", String.valueOf(meta.getLore().size())));
        }
    }

    private void sendPluginInfo(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== PerInfo v1.1 ===");
        sender.sendMessage(ChatColor.GRAY + "Desenvolvido por: " + ChatColor.WHITE + "Comonier");
        if (sender instanceof Player player) {
            player.spigot().sendMessage(createLink(" [Release] ", "https://github.com", ChatColor.GREEN));
            player.spigot().sendMessage(createLink(" [Source] ", "https://github.com", ChatColor.YELLOW));
            player.spigot().sendMessage(createLink(" [Discord] ", "https://discord.gg", ChatColor.BLUE));
        }
        this.getDescription().getCommands().forEach((n, m) -> {
            Map details = (Map) m;
            sender.sendMessage(ChatColor.GOLD + "/" + n + ChatColor.GRAY + " - " + ChatColor.WHITE + details.get("description"));
        });
        sender.sendMessage(getMsg("usage"));
    }

    private TextComponent createLink(String text, String url, ChatColor color) {
        TextComponent comp = new TextComponent(color + text + ChatColor.GRAY + "(" + url + ")");
        comp.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.YELLOW + "Clique para abrir!")));
        return comp;
    }

    private void sendClickableMessage(Player player, String prefix, String content) {
        TextComponent message = new TextComponent(prefix);
        TextComponent c = new TextComponent(ChatColor.WHITE + content);
        c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getMsg("click-to-copy"))));
        c.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, content));
        c.setUnderlined(true);
        message.addExtra(c);
        player.spigot().sendMessage(message);
    }

    @Override
    public List onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (c.getName().equalsIgnoreCase("perinfo")) {
            if (args.length == 1) {
                List list = new ArrayList(Arrays.asList("list", "reload", "view"));
                for (HelpTopic topic : Bukkit.getHelpMap().getHelpTopics()) {
                    String name = topic.getName();
                    if (name.startsWith("/")) list.add(name.substring(1));
                }
                return StringUtil.copyPartialMatches(args[0], list, new ArrayList());
            }
        }
        return new ArrayList();
    }
}
