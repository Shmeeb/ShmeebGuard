package net.shmeeb.shmeebguard.utils;

import com.flowpowered.math.vector.Vector3d;
import net.shmeeb.shmeebguard.ShmeebGuard;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Utils {
    public static Optional<Location<World>> getLocation(Transaction<BlockSnapshot> transaction) {
        if (transaction == null) return Optional.empty();
        Optional<Location<World>> ret = transaction.getOriginal().getLocation();
        if (ret.isPresent()) return ret;
        ret = transaction.getFinal().getLocation();

        if (!ret.isPresent()) {
            Logger logger = ShmeebGuard.getInstance().getLogger();
            logger.warn("Encountered a block transaction with no location:");
            logger.warn(transaction.toString());
        }

        return ret;
    }

    public static boolean isLocationInWorldBorder(Vector3d location, World world) {

        // Diameter, not radius - we'll want the radius later. We use long, we want the floor!
        long radius = (long)Math.floor(world.getWorldBorder().getDiameter() / 2.0);

        // We get the current position and subtract the border centre. This gives us an effective distance from the
        // centre in all three dimensions. We just care about the magnitude in the x and z directions, so we get the
        // positive amount.
        Vector3d displacement = location.sub(world.getWorldBorder().getCenter()).abs();

        // Check that we're not too far out.
        return !(displacement.getX() > radius || displacement.getZ() > radius);
    }

    public static void verbose(String string) {
        Text text = Utils.getText(string);
        MessageChannel.TO_CONSOLE.send(text);

        Sponge.getServer().getOnlinePlayers().forEach(p -> {
            if (p.hasPermission(ShmeebGuard.PERM)) {
                p.sendMessage(text);
            }
        });
    }

    public static List<String> stringToList(String string) {
        return Arrays.asList(string.split("!!").clone());
    }

    public static String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            String end = i == list.size() - 1 ? "" : "!!";
            sb.append(list.get(i) + end);
        }

        return sb.toString();
    }


    public static Text getText(String message) {
        return TextSerializers.FORMATTING_CODE.deserialize(color(message));
    }

    public static String color(String string) {
        return TextSerializers.FORMATTING_CODE.serialize(Text.of(string));
    }
}