package net.shmeeb.shmeebguard.listeners;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.monster.Creeper;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

import java.util.List;
import java.util.Optional;

public class SpawnEntityListener implements EventListener<SpawnEntityEvent> {

    @Override
    public void handle(SpawnEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntities().isEmpty()) return;
        Entity entity = event.getEntities().get(0);

        Optional<List<Region>> regions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(entity.getLocation());
        if (!regions.isPresent()) return;

        for (Region region : regions.get()) {

            if (entity instanceof EntityPixelmon) {
                if (!region.getFlagTypes().contains(FlagTypes.SPAWN_POKEMON)) continue;
                event.setCancelled(true);
                return;
            } else if (entity instanceof Item) {
                if (!region.getFlagTypes().contains(FlagTypes.DROP_ITEMS)) continue;
                event.setCancelled(true);
                return;
            }

        }
    }
}