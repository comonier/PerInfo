package com.comonier.perinfo;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.Map;

public class ChatListener implements Listener {
    private final PerInfo plugin;
    public ChatListener(PerInfo plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String m = e.getMessage();

        if (!plugin.hasPerm(p, "chat-symbols", "perinfo.chat")) return;

        if (m.contains("[hand]") || m.contains("[inv]") || m.contains("[ec]") || m.contains("[money]") || m.contains("[playtime]")) {
            e.setCancelled(true);
            
            TextComponent message = new TextComponent("");
            message.addExtra(new TextComponent("§7[§bPerInfo§7] §f" + p.getDisplayName() + " §8➜ §f"));

            String[] words = m.split(" ");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                TextComponent part;

                if (word.equalsIgnoreCase("[hand]")) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    part = new TextComponent(getHandNameOnly(item));
                    
                    // Adiciona o Hover Detalhado (Lore + Enchants)
                    part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(getItemHover(item))));
                    part.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/iinfo"));
                } 
                else if (word.equalsIgnoreCase("[inv]")) {
                    part = new TextComponent("§b[INV]");
                    part.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/perinfo viewinv " + p.getName()));
                    part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§bClique para abrir o inventário")));
                } 
                else if (word.equalsIgnoreCase("[ec]")) {
                    part = new TextComponent("§5[EC]");
                    part.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/perinfo viewec " + p.getName()));
                    part.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§dClique para abrir o EnderChest")));
                } 
                else if (word.equalsIgnoreCase("[money]")) {
                    String val = (plugin.getEcon() != null) ? String.format("%.2f", plugin.getEcon().getBalance(p)) : "0.00";
                    part = new TextComponent("§a$" + val);
                } 
                else if (word.equalsIgnoreCase("[playtime]")) {
                    int mi = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
                    part = new TextComponent("§e" + (mi/60) + "h " + (mi%60) + "m");
                } 
                else {
                    part = new TextComponent(TextComponent.fromLegacyText(word));
                }
                message.addExtra(part);
                if (i < words.length - 1) message.addExtra(" ");
            }

            Bukkit.getOnlinePlayers().forEach(o -> o.spigot().sendMessage(message));
            Bukkit.getConsoleSender().sendMessage("§7[PerInfo-Log] §f" + p.getName() + ": " + m);
        }
    }

    // Pega apenas o nome colorido para o chat
    private String getHandNameOnly(ItemStack i) {
        if (i == null || i.getType() == Material.AIR) return "§b[Mão Vazia]";
        ItemMeta meta = i.getItemMeta();
        String name = (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : i.getType().name().replace("_", " ").toLowerCase();
        return "§b[" + name + "§b]§r";
    }

    // Monta o texto do Hover (Lore + Encantos)
    private String getItemHover(ItemStack i) {
        if (i == null || i.getType() == Material.AIR) return "§cNada na mão";
        
        StringBuilder sb = new StringBuilder();
        ItemMeta meta = i.getItemMeta();
        
        // Titulo no Hover
        String name = (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : "§f" + i.getType().name();
        sb.append(name).append("\n");

        // Encantos
        if (i.hasItemMeta() && !i.getItemMeta().getEnchants().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : i.getItemMeta().getEnchants().entrySet()) {
                String enchantName = entry.getKey().getKey().getKey().replace("_", " ");
                sb.append("§7").append(enchantName).append(" ").append(entry.getValue()).append("\n");
            }
        }

        // Lore
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            for (String line : lore) {
                sb.append("§d").append(line).append("\n");
            }
        }
        
        sb.append("\n§b§l» §fClique para detalhes técnicos");
        return sb.toString();
    }
}
