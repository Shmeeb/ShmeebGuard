package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import java.util.Map;

public class List implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        boolean found = false;

        for (Map.Entry<String, java.util.List<Region>> entry : ShmeebGuard.getRegionManager().getRegions().entrySet()) {
            for (Region region : entry.getValue()) {
                found = true;
                src.sendMessage(Utils.getText(region.toString()));
            }
        }

        if (!found) {
            src.sendMessage(Utils.getText("&cNo regions found"));
        }

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .executor(new List())
                .build();
    }
}