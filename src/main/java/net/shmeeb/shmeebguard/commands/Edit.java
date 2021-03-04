package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.FlagValueArgument;
import net.shmeeb.shmeebguard.objects.NameArgument;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class Edit implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        String name = args.<String>getOne(Text.of("name")).get();
        Optional<Region> region = ShmeebGuard.getRegionManager().getRegion(name);
        Optional<FlagTypes> flagType = args.getOne(Text.of("flagType"));
        Optional<String> flagValue = args.getOne(Text.of("flagValue"));

        if (!region.isPresent()) {
            src.sendMessage(Utils.getText("&cNo region found"));
            return CommandResult.success();
        }

        if (!flagType.isPresent()) {
            src.sendMessage(Text.EMPTY);
            src.sendMessage(region.get().flagsToText());
            src.sendMessage(Text.EMPTY);

            return CommandResult.success();
        }

        if (!flagValue.isPresent()) {
            src.sendMessage(Utils.getText("&aThe &6" + flagType.get().name() + "&a flag is currently set to &6"
                    + (region.get().getFlagTypes().contains(flagType.get()) ? "DENY" : "ALLOW")));
            return CommandResult.success();
        }

        if (flagValue.get().equalsIgnoreCase("allow")) {
            region.get().getFlagTypes().remove(flagType.get());
        } else if (flagValue.get().equalsIgnoreCase("deny")) {
            region.get().getFlagTypes().add(flagType.get());
        } else if (flagType.get().equals(FlagTypes.TELEPORT_IN)) {
            if (!flagValue.get().contains(".")) {
                src.sendMessage(Utils.getText("&cFlag values for TELEPORT_IN must be a permission node (contain a period)"));
                return CommandResult.success();
            }

            region.get().getCustomFlagValues().put(FlagTypes.TELEPORT_IN, flagValue.get());

            if (!region.get().getFlagTypes().contains(FlagTypes.TELEPORT_IN)) {
                region.get().getFlagTypes().add(FlagTypes.TELEPORT_IN);
            }

        } else {
            src.sendMessage(Utils.getText("&cInvalid flag value"));
            return CommandResult.success();
        }

        region.get().saveFlags();
        src.sendMessage(Utils.getText("&aSuccessfully set the &6" + flagType.get().name() + "&a flag to &6" + flagValue.get()));

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .arguments(
                        new NameArgument(Text.of("name")),
                        GenericArguments.optional(GenericArguments.enumValue(Text.of("flagType"), FlagTypes.class)),
                        GenericArguments.optional(new FlagValueArgument(Text.of("flagValue")))
                ).executor(new Edit())
                .build();
    }
}