package net.shmeeb.shmeebguard.commands;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.AABB;

public class Create implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        Player player = (Player) src;
        String world = args.<String>getOne(Text.of("world")).orElse(player.getWorld().getName());
        String name = args.<String>getOne(Text.of("name")).get();
        Region selection;
        AABB box = null;

        if (ShmeebGuard.getRegionManager().getRegion(name).isPresent()) {
            src.sendMessage(Utils.getText("&cA region with that name already exists"));
            return CommandResult.success();
        }

        try {
            selection = WorldEdit.getInstance().getSession(player.getName()).getWorldSelection();
        } catch (Exception e) {
            src.sendMessage(Utils.getText("&cPlease select a cuboid region with the WorldEdit wand"));
            return CommandResult.success();
        }

        try {
            box = new AABB(
                    selection.getMinimumPoint().getX(),
                    selection.getMinimumPoint().getY(),
                    selection.getMinimumPoint().getZ(),

                    selection.getMaximumPoint().getX(),
                    selection.getMaximumPoint().getY(),
                    selection.getMaximumPoint().getZ()
            );
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                switch (e.getMessage()) {
                    case "The box is degenerate on x":
                        src.sendMessage(Utils.getText("&cThe area is not correctly setup, you have selected the same 'X' location for both corners."));
                        break;
                    case "The box is degenerate on y":
                        src.sendMessage(Utils.getText("&cThe area is not correctly setup, you have selected the same 'Y' location for both corners. Try going 1 block lower."));
                        break;
                    case "The box is degenerate on z":
                        src.sendMessage(Utils.getText("&cThe area is not correctly setup, you have selected the same 'Z' location for both corners."));
                        break;
                    default:
                        src.sendMessage(Utils.getText("&cUnknown problem with making the area region has occurred."));
                        break;
                }
            }
        }

        net.shmeeb.shmeebguard.objects.Region region = new net.shmeeb.shmeebguard.objects.Region(name, box, world, null);
        ShmeebGuard.getRegionManager().createRegion(region);

        player.sendMessage(Utils.getText("&aSuccessfully created region. " + region.toString()));

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.optional(GenericArguments.string(Text.of("world")))
                ).executor(new Create())
                .build();
    }
}