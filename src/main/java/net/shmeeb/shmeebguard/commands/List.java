package net.shmeeb.shmeebguard.commands;

import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class List implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        java.util.List<Region> regions = ShmeebGuard.getRegionManager().getAllRegions();

        if (regions.isEmpty()) {
            src.sendMessage(Utils.getText("&cNo regions found"));
            return CommandResult.success();
        }

        Collections.sort(regions, Comparator.comparing(Region::getName));

        PaginationList.builder().title(Text.builder().build())
                .contents(regions.stream().map(r -> r.toText()).collect(Collectors.toList()))
                .padding(Text.of(TextStyles.RESET, TextColors.DARK_GRAY, TextStyles.STRIKETHROUGH, "-"))
                .title(Text.of(TextColors.GREEN, " ShmeebGuard Regions ", TextStyles.ITALIC))
                .sendTo(src);

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .executor(new List())
                .build();
    }
}