package com.comonier.perinfo;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import java.util.*;

public class PerInfoCommand implements CommandExecutor, TabCompleter, Listener {
    private final PerInfo plugin;
    public PerInfoCommand(PerInfo plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        String cmdName = c.getName().toLowerCase();

        if (cmdName.equals("iinfo")) {
            if (s instanceof Player p) {
                if (plugin.hasPerm(p, "iinfo", "perinfo.iinfo") == false) {
                    p.sendMessage(plugin.getMsg("no-permission"));
                    return true;
                }
                ItemStack item = p.getInventory().getItemInMainHand();
                if (item.getType() == Material.AIR) {
                    p.sendMessage(plugin.getMsg("item-empty"));
                    return true;
                }
                p.sendMessage(plugin.getMsg("item-info-header"));
                p.sendMessage(plugin.getMsg("item-id").replace("{id}", item.getType().name()));
                if (item.getItemMeta() != null) {
                    if (item.getItemMeta().hasCustomModelData()) {
                        p.sendMessage(plugin.getMsg("item-model").replace("{data}", String.valueOf(item.getItemMeta().getCustomModelData())));
                    }
                }
            }
            return true;
        }

        if (cmdName.equals("simbolos")) {
            s.sendMessage(plugin.getMsg("symbols-title"));
            String[] syms = {"[hand]", "[inv]", "[ec]", "[money]", "[playtime]"};
            String[] keys = {"symbol-hand", "symbol-inv", "symbol-ec", "symbol-money", "symbol-playtime"};
            int i = 0;
            while (i != syms.length) {
                s.sendMessage(plugin.getMsg("symbols-line").replace("{symbol}", syms[i]).replace("{desc}", plugin.getMsg(keys[i])));
                i++;
            }
            return true;
        }

        if (cmdName.equals("perinfo")) {
            if (args.length == 0) {
                s.sendMessage("§bPerInfo v1.1 §7- Use /perinfo [comando]");
                return true;
            }
            String sub = args[0].toLowerCase();
            if (sub.equals("reload")) {
                plugin.loadMessages();
                s.sendMessage(plugin.getMsg("reload-success"));
                return true;
            }
            if (sub.equals("list")) {
                if (args.length >= 2) {
                    Plugin t = Bukkit.getPluginManager().getPlugin(args[1]);
                    if (t != null) {
                        s.sendMessage(plugin.getMsg("list-header").replace("{plugin}", t.getName()));
                        if (t.getDescription().getCommands() != null) {
                            t.getDescription().getCommands().forEach((k, v) -> {
                                Object pObj = ((Map) v).get("permission");
                                if (s instanceof Player p) sendClick(p, "§e/" + k + " §f- ", (pObj != null ? pObj.toString() : "N/A"));
                            });
                        }
                    }
                }
                return true;
            }
            handleLookup(s, args[0]);
        }
        return true;
    }

    private void handleLookup(CommandSender s, String name) {
        PluginCommand pc = Bukkit.getPluginCommand(name.replace("/", ""));
        if (pc != null) {
            s.sendMessage(plugin.getMsg("plugin-info").replace("{plugin}", pc.getPlugin().getName()));
            s.sendMessage(plugin.getMsg("version-info").replace("{version}", pc.getPlugin().getDescription().getVersion()));
            String perm = pc.getPermission();
            if (perm == null) perm = plugin.getMsg("not-defined");
            if (s instanceof Player p) sendClick(p, plugin.getMsg("perm-info"), perm);
        } else {
            HelpTopic t = Bukkit.getHelpMap().getHelpTopic(name.startsWith("/") ? name : "/" + name);
            if (t != null) s.sendMessage("§eComando: §f" + t.getName() + " §7| §f" + t.getShortText());
        }
    }

    private void sendClick(Player p, String pre, String con) {
        TextComponent msg = new TextComponent("");
        for (BaseComponent bc : TextComponent.fromLegacyText(pre)) msg.addExtra(bc);
        TextComponent tc = new TextComponent("§f" + con);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, con));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(plugin.getMsg("click-to-copy"))));
        msg.addExtra(tc);
        p.spigot().sendMessage(msg);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("Inv: ")) e.setCancelled(true);
        if (e.getView().getTitle().contains("Ender: ")) e.setCancelled(true);
    }

    @Override
    public List onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return StringUtil.copyPartialMatches(args[0], Arrays.asList("list", "reload"), new ArrayList());
        return new ArrayList();
    }
}
