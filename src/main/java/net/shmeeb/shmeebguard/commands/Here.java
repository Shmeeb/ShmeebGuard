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
import java.util.Optional;

public class Here implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        Player player = (Player) src;
        Optional<List<Region>> regions = ShmeebGuard.getRegionManager().getRegionsAtPosition(player.getLocation());

        if (!regions.isPresent()) {
            player.sendMessage(Utils.getText("&cNo regions found"));
            return CommandResult.success();
        }

        for (Region region : regions.get()) {
            player.sendMessage(Utils.getText("&a" + region.toString()));
        }

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .executor(new Here())
                .build();
    }
}