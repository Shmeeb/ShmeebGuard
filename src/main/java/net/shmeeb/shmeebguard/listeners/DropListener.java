package net.shmeeb.shmeebguard.listeners;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.DropItemEvent;

import java.util.List;
import java.util.Optional;

public class DropListener implements EventListener<DropItemEvent.Dispense.Pre> {

    @Override
    public void handle(DropItemEvent.Dispense.Pre e) {
        if (e.isCancelled() || e.getOriginalDroppedItems().isEmpty() || !e.getCause().first(Player.class).isPresent()) return;
        Player player = e.getCause().first(Player.class).get();
        if (player.hasPermission("group.regular")) return;

        Optional<List<Region>> regions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(player.getLocation());
        if (!regions.isPresent()) return;

        for (Region region : regions.get()) {
            if (!region.getName().equalsIgnoreCase("spawn")) continue;

            player.sendMessage(Utils.getText("&c&l(!)&c You aren't allowed to drop items here!"));
            e.setCancelled(true);
            return;
        }
    }
}