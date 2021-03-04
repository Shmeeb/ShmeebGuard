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

import java.util.Optional;

public class Create implements CommandExecutor {

    //sg create name world x1 z1 x2 z2

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)  {
        String name = args.<String>getOne(Text.of("name")).get();
        Optional<String> world = args.getOne(Text.of("world"));
        Optional<Integer> x1 = args.getOne(Text.of("x1"));
        Optional<Integer> z1 = args.getOne(Text.of("z1"));
        Optional<Integer> x2 = args.getOne(Text.of("x2"));
        Optional<Integer> z2 = args.getOne(Text.of("z2"));
        Region selection;
        AABB box = null;

        if (ShmeebGuard.getRegionManager().getRegion(name).isPresent()) {
            src.sendMessage(Utils.getText("&cA region with that name already exists"));
            return CommandResult.success();
        }

        try {

            if (world.isPresent() && x1.isPresent() && z1.isPresent() && x2.isPresent() && z2.isPresent()) {
                box = new AABB(
                        x1.get(),
                        0,
                        z1.get(),
                        x2.get(),
                        255,
                        z2.get()
                );
            } else if (src instanceof Player) {
                Player player = (Player) src;
                world = Optional.of(player.getWorld().getName());

                try {
                    selection = WorldEdit.getInstance().getSession(player.getName()).getWorldSelection();
                } catch (Exception e) {
                    src.sendMessage(Utils.getText("&cPlease select a cuboid region with the WorldEdit wand"));
                    return CommandResult.success();
                }

                box = new AABB(
                        selection.getMinimumPoint().getBlockX(),
                        selection.getMinimumPoint().getBlockY(),
                        selection.getMinimumPoint().getBlockZ(),

                        selection.getMaximumPoint().getBlockX(),
                        selection.getMaximumPoint().getBlockY(),
                        selection.getMaximumPoint().getBlockZ()
                );
            } else {
                src.sendMessage(Utils.getText("&cEither select a region with WorldEdit or specify the location like so: /sg create <name> <world> <x1> <z1> <x2> <z2>"));
                return CommandResult.success();
            }

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

        net.shmeeb.shmeebguard.objects.Region region = new net.shmeeb.shmeebguard.objects.Region(name, box, world.get(), null, null);
        ShmeebGuard.getRegionManager().createRegion(region);
        src.sendMessage(Utils.getText("&aSuccessfully created region: ").concat(region.toText()));

        return CommandResult.success();
    }

    static CommandSpec build() {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.optional(GenericArguments.string(Text.of("world"))),
                        GenericArguments.optional(GenericArguments.integer(Text.of("x1"))),
                        GenericArguments.optional(GenericArguments.integer(Text.of("z1"))),
                        GenericArguments.optional(GenericArguments.integer(Text.of("x2"))),
                        GenericArguments.optional(GenericArguments.integer(Text.of("z2")))
                ).executor(new Create())
                .build();
    }
}