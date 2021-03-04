package net.shmeeb.shmeebguard.objects;

import com.google.common.collect.Lists;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.stream.Collectors;

public class FlagValueArgument extends CommandElement {
    List<String> options = Lists.newArrayList("allow", "deny");

    public FlagValueArgument(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        if (args.hasNext()) {
            return args.next();
        } else {
            throw args.createError(Text.of("Invalid argument supplied!"));
        }
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        String name = "";

        try {
            name = args.peek();
        } catch (ArgumentParseException e) {}

        final String n = name;

        return options.stream()
                .filter(p -> n.isEmpty() || p.toLowerCase().startsWith(n.toLowerCase()))
                .collect(Collectors.toList());
    }
}