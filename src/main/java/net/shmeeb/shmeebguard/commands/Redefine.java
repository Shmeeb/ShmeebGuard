package net.shmeeb.shmeebguard.commands;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.NameArgument;
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

import java.util.List;
import java.util.Optional;

public class Redefine implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        Player player = (Player) src;
        String name = args.<String>getOne(Text.of("name")).get();
        Optional<net.shmeeb.shmeebguard.objects.Region> region = ShmeebGuard.getRegionManager().getRegion(name);
        Region selection;
        AABB box = null;

        if (!region.isPresent()) {
            src.sendMessage(Utils.getText("&cA region with that doesn't exist"));
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
                    selection.getMinimumPoint().getBlockX(),
                    selection.getMinimumPoint().getBlockY(),
                    selection.getMinimumPoint().getBlockZ(),

                    selection.getMaximumPoint().getBlockX(),
                    selection.getMaximumPoint().getBlockY(),
                    selection.getMaximumPoint().getBlockZ()
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

        List<FlagTypes> flagTypes = region.get().getFlagTypes();

        ShmeebGuard.getRegionManager().deleteRegion(name);
        net.shmeeb.shmeebguard.objects.Region newRegion = new net.shmeeb.shmeebguard.objects.Region(name, box, player.getWorld().getName(), flagTypes);
        ShmeebGuard.getRegionManager().createRegion(newRegion);

        player.sendMessage(Utils.getText("&aSuccessfully redefined region. ").concat(region.get().toText()));

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .arguments(
                        new NameArgument(Text.of("name"))
                ).executor(new Redefine())
                .build();
    }
}