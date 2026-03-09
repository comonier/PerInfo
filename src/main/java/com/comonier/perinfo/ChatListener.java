package com.comonier.perinfo;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class ChatListener implements Listener {
    private final PerInfo plugin;
    public ChatListener(PerInfo plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String msg = event.getMessage();

        if (plugin.hasPerm(p, "chat-symbols", "perinfo.chat") == false) return;

        if (msg.contains("[hand]")) {
            ItemStack item = p.getInventory().getItemInMainHand();
            String name = item.getType() == Material.AIR ? "Mao Vazia" : item.getType().name().replace("_", " ").toLowerCase();
            msg = msg.replace("[hand]", "§b[" + name + "§b]§r");
        }

        if (msg.contains("[money]")) {
            if (plugin.getEcon() != null) {
                String valor = String.format("%.2f", plugin.getEcon().getBalance(p));
                msg = msg.replace("[money]", plugin.getMsg("money-format").replace("{amount}", valor) + "§r");
            }
        }

        if (msg.contains("[playtime]")) {
            int ticks = p.getStatistic(Statistic.PLAY_ONE_MINUTE);
            int mins = ticks / 1200;
            int hours = mins / 60;
            mins = mins % 60;
            msg = msg.replace("[playtime]", plugin.getMsg("time-format").replace("{h}", String.valueOf(hours)).replace("{m}", String.valueOf(mins)) + "§r");
        }

        event.setMessage(msg);
    }
}
