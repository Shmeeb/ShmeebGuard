package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class List implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        java.util.List<Region> regions = ShmeebGuard.getRegionManager().getAllRegions();

        if (regions.isEmpty()) {
            src.sendMessage(Utils.getText("&cNo regions found"));
            return CommandResult.success();
        }

        regions.forEach(region -> src.sendMessage(Utils.getText(region.toString())));

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .executor(new List())
                .build();
    }
}