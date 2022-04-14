package net.shmeeb.shmeebguard.listeners;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public class InteractEntityListener implements EventListener<InteractEntityEvent> {

    @Override
    public void handle(InteractEntityEvent event) {
        if (event.isCancelled()) return;

        if (!Utils.isLocationInWorldBorder(event.getTargetEntity().getLocation().getPosition(), event.getTargetEntity().getWorld())) {
            event.setCancelled(true);
            return;
        }

        for (Region region : ShmeebGuard.getRegionManager().getAllRegionsAtPosition(event.getTargetEntity().getLocation())) {

            if (event instanceof InteractEntityEvent.Primary) {

                if (event.getTargetEntity() instanceof EntityPixelmon && region.getFlagTypes().contains(FlagTypes.INTERACT_POKEMON)) {
                    event.setCancelled(true);
                    return;
                }

                if (region.getFlagTypes().contains(FlagTypes.INTERACT_ENTITY_PRIMARY)) {
                    event.setCancelled(true);
                    return;
                }

            } else if (event instanceof InteractEntityEvent.Secondary) {

                if (event.getTargetEntity() instanceof EntityPixelmon && region.getFlagTypes().contains(FlagTypes.INTERACT_POKEMON)) {
                    event.setCancelled(true);
                    return;
                }

                if (region.getFlagTypes().contains(FlagTypes.INTERACT_ENTITY_SECONDARY)) {
                    event.setCancelled(true);
                    return;
                }
            }

        }
    }
}