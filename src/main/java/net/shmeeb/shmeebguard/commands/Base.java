package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public class Base implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {

        src.sendMessage(Utils.getText("todo"));

        return CommandResult.success();
    }

    public static CommandSpec build() {
        return CommandSpec.builder()
                .permission(ShmeebGuard.PERM)
                .child(Create.build(), "create")
                .child(Debug.build(), "debug")
                .child(Delete.build(), "delete")
                .child(Edit.build(), "edit")
                .child(Here.build(), "here")
                .child(List.build(), "list")
                .child(Reload.build(), "reload")
                .executor(new Base())
                .build();
    }
}

/*


    /sg create/delete/list [name]
    /sg edit <region> <key> <value>
    /sg reload/help

     */
