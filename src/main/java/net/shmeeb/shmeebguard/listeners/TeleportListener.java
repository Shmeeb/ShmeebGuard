package net.shmeeb.shmeebguard.listeners;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

public class TeleportListener implements EventListener<MoveEntityEvent.Teleport> {

    @Override
    public void handle(MoveEntityEvent.Teleport event) {
        if (event.isCancelled()) return;
        if (!event.getCause().first(Player.class).isPresent()) return;
        Player player = event.getCause().first(Player.class).get();

        Optional<List<Region>> regions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(event.getToTransform().getLocation());
        if (!regions.isPresent()) return;

        for (Region region : regions.get()) {
            if (!region.getFlagTypes().contains(FlagTypes.TELEPORT_IN)) continue;
            String perm = region.getCustomFlagValues().getOrDefault(FlagTypes.TELEPORT_IN, "none");
            if (perm.equals("none")) continue;

            if (!player.hasPermission(perm)) {
                event.setCancelled(true);

                player.sendMessage(Text.EMPTY);

                if (perm.toLowerCase().startsWith("group.")) {
                    String rank = perm.replace("group.", "");
                    rank = rank.substring(0, 1).toUpperCase() + rank.substring(1);

                    player.sendMessage(Utils.getText("&c&l(!)&c The &6&l" + region.getName()
                            + "&c region is only accessible by users with the &6&l" + rank + "&c rank, teleporting you to spawn..."));
                } else {
                    player.sendMessage(Utils.getText("&c&l(!)&c You don't have permission to enter this region!"));
                }

                player.sendMessage(Text.EMPTY);

                Task.builder().delayTicks(2).execute(task -> {
                    player.setLocation(player.getWorld().getSpawnLocation().add(0, 1, 0));
                    player.setLocation(player.getWorld().getSpawnLocation());
                }).submit(ShmeebGuard.getInstance());

                return;
            }
        }
    }
}