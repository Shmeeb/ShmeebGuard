package net.shmeeb.shmeebguard.listeners;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.plugin.PluginContainer;

public class DropListener implements EventListener<DropItemEvent.Dispense.Pre> {

    @Override
    public void handle(DropItemEvent.Dispense.Pre e) {
        if (e.isCancelled() || e.getOriginalDroppedItems().isEmpty() || !e.getCause().first(Player.class).isPresent()) return;
        Player player = e.getCause().first(Player.class).get();
        if (player.hasPermission("group.regular")) return;

        if (e.getCause().first(PluginContainer.class).isPresent() && e.getCause().first(PluginContainer.class).get().getName().equalsIgnoreCase("Minecraft")) return;

        for (Region region : ShmeebGuard.getRegionManager().getAllRegionsAtPosition(player.getLocation())) {
            if (!region.getName().equalsIgnoreCase("spawn")) continue;

            player.sendMessage(Utils.getText("&c&l(!)&c You aren't allowed to drop items here!"));
            e.setCancelled(true);
            return;
        }
    }
}