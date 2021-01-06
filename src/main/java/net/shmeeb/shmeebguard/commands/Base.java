package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Base {

    public static CommandSpec build() {
        return CommandSpec.builder()
                .permission(ShmeebGuard.PERM)
                .child(Create.build(), "create")
                .child(Debug.build(), "debug")
                .child(Delete.build(), "delete")
                .child(Edit.build(), "edit")
                .child(Redefine.build(), "redefine")
                .child(Here.build(), "here")
                .child(List.build(), "list")
                .child(Reload.build(), "reload")
                .build();
    }
}