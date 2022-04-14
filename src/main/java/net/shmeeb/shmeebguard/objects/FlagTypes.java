package net.shmeeb.shmeebguard.objects;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum FlagTypes {
    BLOCK_CHANGE, SPAWN_POKEMON, ALLOW_NATURAL_SPAWNS, INTERACT_ENTITY_SECONDARY, INTERACT_ENTITY_PRIMARY,
    PLAYERS_TAKE_DAMAGE, DROP_ITEMS, DECAY, INTERACT_POKEMON, TELEPORT_IN, ENTER_COMMANDS, EXIT_COMMANDS, AUTO_BUTCHER;

    public static List<String> enumValues = Arrays.stream(FlagTypes.values()).map(Enum::name).collect(Collectors.toList());

}