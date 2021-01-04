package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Reload implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {

        ShmeebGuard.getInstance().loadConfig();
        ShmeebGuard.getRegionManager().getRegions().clear();
        ShmeebGuard.getRegionManager().loadRegions();

        src.sendMessage(Utils.getText("&aConfig & data successfully reloaded"));

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .executor(new Reload())
                .build();
    }
}