package net.shmeeb.shmeebguard.objects;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.Region;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NameArgument extends CommandElement {
    public NameArgument(Text key) {
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

        List<Region> allRegions = new ArrayList<>();
        final String n = name;

        for (Map.Entry<String, List<Region>> entry : ShmeebGuard.getRegionManager().getRegions().entrySet()) {
            allRegions.addAll(entry.getValue());
        }

        return allRegions.stream().map(Region::getName).filter(p -> n.isEmpty() || p.toLowerCase().startsWith(n.toLowerCase())).collect(Collectors.toList());
    }
}