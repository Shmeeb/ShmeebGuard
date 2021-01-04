package net.shmeeb.shmeebguard.commands;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Debug implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {





        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .executor(new Debug())
                .build();
    }
}