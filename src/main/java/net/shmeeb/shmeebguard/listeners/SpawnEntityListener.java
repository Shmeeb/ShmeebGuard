package net.shmeeb.shmeebguard.listeners;

import com.pixelmonmod.pixelmon.api.events.spawning.SpawnEvent;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.entities.pokemon.SpawnActionPokemon;
import com.pixelmonmod.pixelmon.api.world.MutableLocation;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public class SpawnEntityListener implements EventListener<SpawnEntityEvent> {

    @SubscribeEvent
    public void onSpawn(SpawnEvent event) {
        if (!(event.action instanceof SpawnActionPokemon)) return;

        MutableLocation spawnLocation = event.action.spawnLocation.location;
        Location<World> spongeLocation = new Location<>((World) spawnLocation.world, spawnLocation.pos.getX(), spawnLocation.pos.getY(), spawnLocation.pos.getZ());
        Optional<List<Region>> regions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(spongeLocation);
        if (!regions.isPresent()) return;

        for (Region region : regions.get()) {
            if (!region.getFlagTypes().contains(FlagTypes.ALLOW_NATURAL_SPAWNS)) continue;
            event.setCanceled(true);
            return;
        }
    }

    @Override
    public void handle(SpawnEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntities().isEmpty()) return;
        Entity entity = event.getEntities().get(0);

        Optional<List<Region>> regions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(entity.getLocation());
        if (!regions.isPresent()) return;

        for (Region region : regions.get()) {

            if (entity instanceof EntityPixelmon) {

                if (region.getFlagTypes().contains(FlagTypes.SPAWN_POKEMON)) {
                    event.setCancelled(true);
                    return;
                }

            } else if (entity instanceof Item) {
                if (!region.getFlagTypes().contains(FlagTypes.DROP_ITEMS)) continue;
                event.setCancelled(true);
                return;
            }

        }
    }
}