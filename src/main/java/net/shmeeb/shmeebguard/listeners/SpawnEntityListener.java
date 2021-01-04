package net.shmeeb.shmeebguard.listeners;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import org.spongepowered.api.entity.Entity;
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
        List<Entity> entities = event.getEntities();
        if (entities.isEmpty()) return;

        for (Entity entity : entities) {
//            if (entity instanceof Player) return;

            if (entity instanceof EntityPixelmon) {
                Optional<Region> region = ShmeebGuard.getRegionManager().getRegionAtPosition(entity.getLocation());
                if (!region.isPresent()) return;
                if (!region.get().getFlagTypes().contains(FlagTypes.SPAWN_POKEMON)) return;
                event.setCancelled(true);
                return;
            }
        }
    }
}