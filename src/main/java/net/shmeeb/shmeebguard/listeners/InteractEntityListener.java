package net.shmeeb.shmeebguard.listeners;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import java.util.Optional;

public class InteractEntityListener implements EventListener<InteractEntityEvent> {

    @Override
    public void handle(InteractEntityEvent event) {
        if (event.isCancelled()) return;

        Optional<Region> region = ShmeebGuard.getRegionManager().getRegionAtPosition(event.getTargetEntity().getLocation());
        if (!region.isPresent()) return;

        if (event instanceof InteractEntityEvent.Primary) {
            if (!region.get().getFlagTypes().contains(FlagTypes.INTERACT_ENTITY_PRIMARY)) return;
        } else if (event instanceof InteractEntityEvent.Secondary) {
            if (!region.get().getFlagTypes().contains(FlagTypes.INTERACT_ENTITY_SECONDARY)) return;
        }

        event.setCancelled(true);
    }
}