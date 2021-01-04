package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.objects.NameArgument;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Edit implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        String name = args.<String>getOne(Text.of("name")).get();
        Optional<Region> region = ShmeebGuard.getRegionManager().getRegion(name);
        Optional<FlagTypes> flagType = args.getOne(Text.of("flagType"));
        Optional<String> flagValue = args.getOne(Text.of("flagValue"));

        if (!region.isPresent()) {
            src.sendMessage(Utils.getText("&cNo region found"));
            return CommandResult.success();
        }

        List<FlagTypes> flagTypes = region.get().getFlagTypes();

        if (!flagType.isPresent()) {
            src.sendMessage(Text.EMPTY);
            src.sendMessage(Utils.getText("&eCurrent flags for " + name));

            for (FlagTypes value : FlagTypes.values()) {
                src.sendMessage(Utils.getText("&7" + value.name() + ": " + (flagTypes.contains(value) ? "&cDENY" : "&aALLOW")));
            }

            src.sendMessage(Text.EMPTY);
            return CommandResult.success();
        }

        if (!flagValue.isPresent()) {
            src.sendMessage(Utils.getText("&aThe &6" + flagType.get().name() + "&a flag is currently set to &6" + (flagTypes.contains(flagType.get()) ? "DENY" : "ALLOW")));
            return CommandResult.success();
        }

        if (flagValue.get().equalsIgnoreCase("allow")) {
            flagTypes.remove(flagType.get());
        } else {
            flagTypes.add(flagType.get());
        }

        region.get().updateFlagTypes(flagTypes);
        src.sendMessage(Utils.getText("&aSuccessfully set the &6" + flagType.get().name() + "&a flag to &6" + flagValue.get()));

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .arguments(
                        new NameArgument(Text.of("name")),
                        GenericArguments.optional(GenericArguments.enumValue(Text.of("flagType"), FlagTypes.class)),
                        GenericArguments.optional(GenericArguments.choices(Text.of("flagValue"), new HashMap<String, String>() {{
                            put("allow", "allow");
                            put("deny", "deny");
                        }}))
                ).executor(new Edit())
                .build();
    }
}