package net.shmeeb.shmeebguard.listeners;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class TeleportListener implements EventListener<MoveEntityEvent.Teleport> {

    @Override
    public void handle(MoveEntityEvent.Teleport event) {
        if (event.isCancelled()) return;
        if (!event.getCause().first(Player.class).isPresent()) return;

        Player player = event.getCause().first(Player.class).get();
        Location<World> toLoc = event.getToTransform().getLocation();
        Location<World> fromLoc = event.getFromTransform().getLocation();
        List<Region> toRegions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(toLoc);
        List<Region> fromRegions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(fromLoc);
        List<String> commands = new ArrayList<>();

        try {
            if (toRegions.isEmpty()) return;

            for (Region region : toRegions) {

                if (region.getFlagTypes().contains(FlagTypes.TELEPORT_IN)) {
                    String perm = region.getCustomFlagValues().getOrDefault(FlagTypes.TELEPORT_IN, "none");
                    if (perm.equals("none")) continue;
                    if (player.hasPermission(perm)) continue;

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

                if (region.getFlagTypes().contains(FlagTypes.ENTER_COMMANDS)) {

                    if (region.getCustomFlagValues().containsKey(FlagTypes.ENTER_COMMANDS)) {
                        commands.addAll(Utils.stringToList(region.getCustomFlagValues().get(FlagTypes.ENTER_COMMANDS)));
                    }

                }
            }

        } finally {

            for (Region region : fromRegions) {
                if (!region.getFlagTypes().contains(FlagTypes.EXIT_COMMANDS)) continue;

                if (region.getCustomFlagValues().containsKey(FlagTypes.EXIT_COMMANDS)) {
                    commands.addAll(Utils.stringToList(region.getCustomFlagValues().get(FlagTypes.EXIT_COMMANDS)));
                }

            }

            commands.forEach(cmd -> {
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd.replace("%player%", player.getName()));
            });
        }
    }
}