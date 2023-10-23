package com.github.tezvn.lunix;

import com.github.tezvn.lunix.chat.v2.DefaultMessenger;
import com.github.tezvn.lunix.menu.Menu;
import com.github.tezvn.lunix.transaction.PlayerTransaction;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


@Getter
public class Lunix extends JavaPlugin implements Listener {

    private boolean registered;

    @Override
    public void onEnable() {
        Menu.register(this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Menu.unregister();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if(!(entity instanceof Player)) return;
        if(event.getHand() != EquipmentSlot.HAND) return;
        new PlayerTransaction(this).setPlayer(event.getPlayer()).setOpposite((Player) entity).start();
    }

}
