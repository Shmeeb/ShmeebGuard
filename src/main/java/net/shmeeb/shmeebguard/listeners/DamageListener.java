package net.shmeeb.shmeebguard.listeners;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.List;
import java.util.Optional;

public class DamageListener implements EventListener<DamageEntityEvent> {

    @Override
    public void handle(DamageEntityEvent event) {
        if (event.isCancelled()) return;
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Player)) return;
        Optional<List<Region>> regions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(entity.getLocation());
        if (!regions.isPresent()) return;

        for (Region region : regions.get()) {
            if (!region.getFlagTypes().contains(FlagTypes.PLAYERS_TAKE_DAMAGE)) continue;
            event.setCancelled(true);
            return;
        }
    }
}