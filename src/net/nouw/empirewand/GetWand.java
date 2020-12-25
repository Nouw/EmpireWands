package net.nouw.empirewand;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetWand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            ItemStack wand = new ItemStack(Material.BLAZE_ROD);
            wand.addUnsafeEnchantment(Enchantment.DURABILITY, 3);

            ItemMeta metaWand = wand.getItemMeta();
            if (metaWand != null) {
                metaWand.setDisplayName(ChatColor.RED + "Empire wand" + ChatColor.GRAY + "(Thunder)");
            }

            wand.setItemMeta(metaWand);

            player.getInventory().addItem(wand);

            System.out.println("Gave EmpireWand to: " + player.getDisplayName());

        }

        return true;
    }
}
