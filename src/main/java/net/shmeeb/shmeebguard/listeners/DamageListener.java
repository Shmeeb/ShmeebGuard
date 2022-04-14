package net.shmeeb.shmeebguard.listeners;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.DamageEntityEvent;

public class DamageListener implements EventListener<DamageEntityEvent> {

    @Override
    public void handle(DamageEntityEvent event) {
        if (event.isCancelled()) return;
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Player)) return;

        for (Region region : ShmeebGuard.getRegionManager().getAllRegionsAtPosition(entity.getLocation())) {
            if (!region.getFlagTypes().contains(FlagTypes.PLAYERS_TAKE_DAMAGE)) continue;
            event.setCancelled(true);
            return;
        }
    }
}