package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

public class Here implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        Player player = (Player) src;
        List<Region> regions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(player.getLocation());

        if (regions.isEmpty()) {
            player.sendMessage(Utils.getText("&cNo regions found"));
            return CommandResult.success();
        }

        for (Region region : regions) {
            player.sendMessage(region.toText());
        }

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .executor(new Here())
                .build();
    }
}