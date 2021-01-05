package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.NameArgument;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class Delete implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        String name = args.<String>getOne(Text.of("name")).get();
        Optional<Region> region = ShmeebGuard.getRegionManager().getRegion(name);

        if (!region.isPresent()) {
            src.sendMessage(Utils.getText("&cNo region found"));
            return CommandResult.success();
        }

        ShmeebGuard.getRegionManager().deleteRegion(name);
        src.sendMessage(Utils.getText("&aSuccessfully deleted the &6" + name + "&a region"));

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .arguments(new NameArgument(Text.of("name")))
                .executor(new Delete())
                .build();
    }
}