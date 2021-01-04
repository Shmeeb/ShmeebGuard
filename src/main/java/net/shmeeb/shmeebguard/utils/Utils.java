package net.shmeeb.shmeebguard.utils;

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

    public static void verbose(String string) {
        Text text = Utils.getText(string);
        MessageChannel.TO_CONSOLE.send(text);

        Sponge.getServer().getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("shmeebguard.use")) {
                p.sendMessage(text);
            }
        });
    }


    public static Text getText(String message) {
        return TextSerializers.FORMATTING_CODE.deserialize(color(message));
    }

    public static String color(String string) {
        return TextSerializers.FORMATTING_CODE.serialize(Text.of(string));
    }
}