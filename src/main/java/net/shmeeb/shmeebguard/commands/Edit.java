package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.NameArgument;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Edit implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        String name = args.<String>getOne(Text.of("name")).get();
        Optional<Region> region = ShmeebGuard.getRegionManager().getRegion(name);
        Optional<FlagTypes> flagType = args.getOne(Text.of("flagType"));
        Optional<String> flagValue = args.getOne(Text.of("flagValue"));
        Optional<String> target = args.getOne(Text.of("target"));

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

            if (flagType.get().equals(FlagTypes.ENTER_COMMANDS) || flagType.get().equals(FlagTypes.EXIT_COMMANDS)) {
                Sponge.getCommandManager().process(src, "sg edit " + name + " " + flagType.get().name() + " list");
            } else if (flagType.get().equals(FlagTypes.TELEPORT_IN)) {

                src.sendMessage(Utils.getText("&aThe &6" + flagType.get().name() + "&a flag is currently set to &6ALLOW"
                        + (region.get().getCustomFlagValues().containsKey(flagType.get()) ? "&a for &b"
                        + region.get().getCustomFlagValues().get(flagType.get()) : "")));

            } else {
                src.sendMessage(Utils.getText("&aThe &6" + flagType.get().name() + "&a flag is currently set to &6"
                        + (region.get().getFlagTypes().contains(flagType.get()) ? "DENY" : "ALLOW")));
            }

            return CommandResult.success();
        }

        //sg edit <region> [flag type] [allow/deny/add/remove] [command/index]

        ///sg edit testZZZ teleport_in allow [target]
        ///sg edit testZZZ teleport_in allow

        if (flagType.get().equals(FlagTypes.TELEPORT_IN)) {

            if (!flagValue.get().equalsIgnoreCase("allow")) {
                src.sendMessage(Utils.getText("&cCorrect usage:"));
                src.sendMessage(Utils.getText("&c/sg edit " + name + " " + flagType.get().name() + " allow"));
                src.sendMessage(Utils.getText("&c/sg edit " + name + " " + flagType.get().name() + " allow group.rankName"));
                return CommandResult.success();
            }

            //remove flag
            if (!target.isPresent()) {

                if (region.get().getFlagTypes().contains(FlagTypes.TELEPORT_IN)) {
                    region.get().getCustomFlagValues().put(FlagTypes.TELEPORT_IN, null);
                    region.get().getFlagTypes().remove(FlagTypes.TELEPORT_IN);
                    region.get().saveFlags();

                    src.sendMessage(Utils.getText("&aSuccessfully removed the &6" + flagType.get().name() + "&a permission node requirement"));
                } else {
                    src.sendMessage(Utils.getText("&cThis region already allows all users to teleport in"));
                }

                return CommandResult.success();
            }

            if (!target.get().contains(".")) {
                src.sendMessage(Utils.getText("&cValues for the &6TELEPORT_IN&a flag must be a permission node (contain a period)"));
                return CommandResult.success();
            }

            region.get().getCustomFlagValues().put(FlagTypes.TELEPORT_IN, target.get());

            if (!region.get().getFlagTypes().contains(FlagTypes.TELEPORT_IN)) {
                region.get().getFlagTypes().add(FlagTypes.TELEPORT_IN);
            }

            region.get().saveFlags();
            src.sendMessage(Utils.getText("&aSuccessfully set the &6" + flagType.get().name() + "&a permission node requirement to &6" + target.get()));

        } else if (flagType.get().equals(FlagTypes.ENTER_COMMANDS) || flagType.get().equals(FlagTypes.EXIT_COMMANDS)) {
            boolean enter = flagType.get().equals(FlagTypes.ENTER_COMMANDS);

            //sg edit <region> [flag type] [allow/deny/add/remove/list] [command/index]

//            if (!flagValue.isPresent()) {
//                src.sendMessage(Utils.getText("&cCorrect usage:"));
//                src.sendMessage(Utils.getText("&cCorrect usage: /sg edit " + region.get().getName() + " " + flagType.get().name() + " add <command>"));
//                src.sendMessage(Utils.getText("&cCorrect usage: /sg edit " + region.get().getName() + " " + flagType.get().name() + " remove <index>"));
//                src.sendMessage(Utils.getText("&cCorrect usage: /sg edit " + region.get().getName() + " " + flagType.get().name() + " list"));
//                return CommandResult.success();
//            }

            List<String> commands = new ArrayList<>();

            if (region.get().getCustomFlagValues().containsKey(flagType.get())) {
                commands = new ArrayList<>(Utils.stringToList(region.get().getCustomFlagValues().get(flagType.get())));
            }

            if (flagValue.get().equals("list")) {

                if (commands.isEmpty()) {
                    src.sendMessage(Utils.getText("&cThere are no &6" + (enter ? "entry" : "exit") + "&c commands defined for &6" + name));
                    return CommandResult.success();
                }

                src.sendMessage(Utils.getText("&a" + flagType.get().name() + " settings for " + region.get().getName() + ":"));

                for (int i = 0; i < commands.size(); i++) {
                    src.sendMessage(Utils.getText("&7" + (i + 1) + ") " + commands.get(i)));
                }

                return CommandResult.success();
            }

            if (flagValue.get().equals("add")) {
                String command = target.get();

                commands.add(command);

                region.get().getCustomFlagValues().put(flagType.get(), Utils.listToString(commands));

                if (!region.get().getFlagTypes().contains(flagType.get())) {
                    region.get().getFlagTypes().add(flagType.get());
                }

                src.sendMessage(Utils.getText("&aSuccessfully updated the " + flagType.get().name() + " flag!"));
                region.get().saveFlags();

                return CommandResult.success();
            }

            if (flagValue.get().equals("remove")) {

                if (commands.isEmpty()) {
                    src.sendMessage(Utils.getText("&cThere are no " + (enter ? "entry" : "exit") + " commands currently defined for this region!"));
                    return CommandResult.success();
                }

                try {
                    int index = Integer.parseInt(target.get());

                    if (index <= 0 || commands.size() < index) {
                        src.sendMessage(Utils.getText("&cInvalid index provided (must range between 1 and " + (commands.size()) + ")"));
                        return CommandResult.success();
                    }

                    commands.remove(index - 1);

                    if (commands.isEmpty()) {
                        region.get().getFlagTypes().remove(flagType.get());
                        region.get().getCustomFlagValues().put(flagType.get(), null);
                    } else {
                        region.get().getCustomFlagValues().put(flagType.get(), Utils.listToString(commands));
                    }

                    src.sendMessage(Utils.getText("&aSuccessfully updated the " + flagType.get().name() + " flag!"));
                    region.get().saveFlags();

                } catch (NumberFormatException e) {
                    src.sendMessage(Utils.getText("&cInvalid index provided (must be an integer)"));
                    return CommandResult.success();
                }

                return CommandResult.success();
            }

            src.sendMessage(Utils.getText("&cInvalid flagValue argument provided (must be add/remove/list)"));

        } else if (flagValue.get().equalsIgnoreCase("allow")) {

            if (flagType.get().equals(FlagTypes.AUTO_BUTCHER)) {
                if (region.get().getFlagTypes().contains(flagType.get())) {
                    src.sendMessage(Utils.getText("&cThat flag is not defined"));
                    return CommandResult.success();
                }

                denyFlag(region.get(), flagType.get());
            } else {

                if (!region.get().getFlagTypes().contains(flagType.get())) {
                    src.sendMessage(Utils.getText("&cThat flag is not defined"));
                    return CommandResult.success();
                }

                allowFlag(region.get(), flagType.get());
            }

            src.sendMessage(Utils.getText("&aSuccessfully set the &6" + flagType.get().name() + "&a flag to &6allow"));

        } else if (flagValue.get().equalsIgnoreCase("deny")) {

            if (flagType.get().equals(FlagTypes.AUTO_BUTCHER)) {
                if (!region.get().getFlagTypes().contains(flagType.get())) {
                    src.sendMessage(Utils.getText("&cThat flag is already defined"));
                    return CommandResult.success();
                }

                allowFlag(region.get(), flagType.get());
            } else {
                if (region.get().getFlagTypes().contains(flagType.get())) {
                    src.sendMessage(Utils.getText("&cThat flag is already defined"));
                    return CommandResult.success();
                }

                denyFlag(region.get(), flagType.get());
            }

            src.sendMessage(Utils.getText("&aSuccessfully set the &6" + flagType.get().name() + "&a flag to &6deny"));

        } else {
            src.sendMessage(Utils.getText("&cInvalid flag value. This should not happen. please report this bug"));
            return CommandResult.success();
        }

        return CommandResult.success();
    }

    static void denyFlag(Region region, FlagTypes flagType) {
        region.getFlagTypes().add(flagType);
        region.saveFlags();
    }

    static void allowFlag(Region region, FlagTypes flagType) {
        region.getFlagTypes().remove(flagType);
        region.saveFlags();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .arguments(
                        new NameArgument(Text.of("name")),
                        GenericArguments.optional(GenericArguments.enumValue(Text.of("flagType"), FlagTypes.class)),
                        GenericArguments.optional(GenericArguments.choices(Text.of("flagValue"), new HashMap<String, String>() {{
                            put("allow", "allow");
                            put("deny", "deny");
                            put("add", "add");
                            put("remove", "remove");
                            put("list", "list");
                        }})),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("target")))
                ).executor(new Edit())
                .build();
    }
}