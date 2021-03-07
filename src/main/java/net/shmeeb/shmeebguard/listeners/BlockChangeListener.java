package net.shmeeb.shmeebguard.listeners;

import com.google.common.collect.Lists;
import net.shmeeb.shmeebguard.ShmeebGuard;
import net.shmeeb.shmeebguard.objects.FlagTypes;
import net.shmeeb.shmeebguard.objects.Region;
import net.shmeeb.shmeebguard.utils.Utils;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public class BlockChangeListener implements EventListener<ChangeBlockEvent> {
    List<BlockType> ice = Lists.newArrayList(BlockTypes.ICE, BlockTypes.PACKED_ICE);

    @Override
    public void handle(ChangeBlockEvent event) {
        if (event.isCancelled() || event instanceof ExplosionEvent || event.getTransactions().isEmpty()) return;

        for (Transaction<BlockSnapshot> tr : event.getTransactions()) {
            if (tr.getOriginal().getState().getType().equals(BlockTypes.DIRT)
                    && tr.getFinal().getState().getType().equals(BlockTypes.GRASS)
                    || tr.getOriginal().getState().getType().equals(BlockTypes.GRASS)
                    && tr.getFinal().getState().getType().equals(BlockTypes.DIRT)) return;
        }

        Optional<Location<World>> location = Utils.getLocation(event.getTransactions().get(0));
        if (!location.isPresent()) return;
        Optional<List<Region>> regions = ShmeebGuard.getRegionManager().getAllRegionsAtPosition(location.get());
        if (!regions.isPresent()) return;

        for (Region region : regions.get()) {

            if (region.getFlagTypes().contains(FlagTypes.DECAY)) {

                if (event instanceof ChangeBlockEvent.Decay) {
                    event.setCancelled(true);
                    return;
                } else if (event instanceof ChangeBlockEvent.Post) {
                    if (ice.contains(event.getTransactions().get(0).getOriginal().getState().getType())
                            && event.getTransactions().get(0).getFinal().getState().getType().equals(BlockTypes.WATER)) {
                        event.setCancelled(true);
                        return;
                    }

                    if (ice.contains(event.getTransactions().get(0).getFinal().getState().getType())
                            && event.getTransactions().get(0).getOriginal().getState().getType().equals(BlockTypes.WATER)) {
                        event.setCancelled(true);
                        return;
                    }
                }

            }

            if (!region.getFlagTypes().contains(FlagTypes.BLOCK_CHANGE)) continue;
            User user;

            if (event.getCause().containsType(Player.class)) {
                user = event.getCause().first(Player.class).get();
            } else if (event.getCause().containsType(User.class)) {
                user = event.getCause().first(User.class).get();
            } else {
                user = null;
            }

            if (user instanceof Player) {
                Player player = (Player) user;
                player.sendMessage(ChatTypes.ACTION_BAR, Utils.getText("&cYou don't have permission!"));
            }

            event.setCancelled(true);
            return;
        }

//        if (user != null && user.isOnline() && user.hasPermission("shmeebguard.bypass")) return;

    }
}