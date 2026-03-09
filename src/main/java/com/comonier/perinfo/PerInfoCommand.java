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
import org.bukkit.event.Listener;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import java.util.*;

public class PerInfoCommand implements CommandExecutor, TabCompleter, Listener {
    private final PerInfo plugin;
    public PerInfoCommand(PerInfo plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        String n = c.getName().toLowerCase();

        // --- COMANDO UNIFICADO: /per ---
        if (n.equals("per")) {
            s.sendMessage("§b§lPerInfo v1.2 §7- Command List");
            s.sendMessage("§e/perinfo <cmd> §7- Plugin/permission");
            s.sendMessage("§e/iinfo §7- ID item must to be on hand");
            s.sendMessage("§e/perinfo reload §7- Reload plugin");
            s.sendMessage("");
            s.sendMessage("§b§lChat Link: (Clique/Hover):");
            s.sendMessage("§f[hand] §7- Link Item on Hand");
            s.sendMessage("§f[inv] §7- Link Inventory");
            s.sendMessage("§f[ec] §7- Link EnderChest");
            s.sendMessage("§f[money] §7- Link Money");
            s.sendMessage("§f[playtime] §7- Link PlayedTime");
            return true;
        }

        if (n.equals("iinfo") && s instanceof Player) {
            Player p = (Player) s;
            ItemStack i = p.getInventory().getItemInMainHand();
            if (i.getType() == Material.AIR) {
                p.sendMessage(plugin.getMsg("item-empty"));
                return true;
            }
            p.sendMessage(plugin.getMsg("item-info-header"));
            p.sendMessage(plugin.getMsg("item-id").replace("{id}", i.getType().name()));
            if (i.hasItemMeta() && i.getItemMeta().hasCustomModelData()) {
                p.sendMessage(plugin.getMsg("item-model").replace("{data}", String.valueOf(i.getItemMeta().getCustomModelData())));
            }
            return true;
        }

        if (n.equals("perinfo")) {
            if (args.length == 0) {
                return Bukkit.dispatchCommand(s, "per");
            }
            String sub = args[0].toLowerCase();

            if (sub.equals("viewec") && args.length > 1 && s instanceof Player) {
                Player t = Bukkit.getPlayer(args[1]);
                if (t != null) ((Player) s).openInventory(t.getEnderChest());
                return true;
            }
            if (sub.equals("viewinv") && args.length > 1 && s instanceof Player) {
                Player t = Bukkit.getPlayer(args[1]);
                if (t != null) ((Player) s).openInventory(t.getInventory());
                return true;
            }
            if (sub.equals("reload")) {
                plugin.loadMessages();
                s.sendMessage(plugin.getMsg("reload-success"));
                return true;
            }
            handleLookup(s, args[0]);
        }
        return true;
    }

    private void handleLookup(CommandSender s, String name) {
        String clean = name.replace("/", "");
        PluginCommand pc = Bukkit.getPluginCommand(clean);
        if (pc != null) {
            s.sendMessage(plugin.getMsg("plugin-info").replace("{plugin}", pc.getPlugin().getName()));
            s.sendMessage(plugin.getMsg("version-info").replace("{version}", pc.getPlugin().getDescription().getVersion()));
            String pr = pc.getPermission() != null ? pc.getPermission() : plugin.getMsg("not-defined");
            if (s instanceof Player) sendClick((Player) s, plugin.getMsg("perm-info"), pr);
            else s.sendMessage(plugin.getMsg("perm-info") + pr);
        } else {
            HelpTopic t = Bukkit.getHelpMap().getHelpTopic(name.startsWith("/") ? name : "/" + name);
            if (t != null) s.sendMessage("§eComando: §f" + t.getName() + " §7| §f" + t.getShortText());
            else s.sendMessage(plugin.getMsg("command-not-found").replace("{name}", name));
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

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return StringUtil.copyPartialMatches(args[0], Arrays.asList("reload", "iinfo"), new ArrayList<>());
        return new ArrayList<>();
    }
}
