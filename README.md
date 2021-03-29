# ShmeebGuard

## Commands
* Create a region by selecting a cuboid region with WorldEdit and running `/sg create [region name]`
* View a summary of a region's flags with `/sg edit [region name]`
* View all regions at your current location with `/sg here`

## Flags
### Flag Types
> All flag values are either true or false
* BLOCK_CHANGE
* SPAWN_POKEMON
* INTERACT_ENTITY_SECONDARY
* INTERACT_ENTITY_PRIMARY
* PLAYERS_TAKE_DAMAGE
* INTERACT_POKEMON
* DROP_ITEMS - prevents any ItemStack entities from being spawned
* DECAY - prevents leaves from decaying and ice from melting

## Special Flags
> Note that these flags will only be triggered when a user teleports, and not when they simply walk into a region
* TELEPORT_IN - users must have the permission node specified in order to warp into a region with this flag defined, otherwise they will be teleported to spawn
  * Set the permssion node: `/sg edit [region name] TELEPORT_IN allow [permission node]`
  * Clear the flag: `/sg edit [region name] TELEPORT_IN allow`
* ENTER_COMMANDS and EXIT_COMMANDS - upon warping into or warping out of a region with either of these flags the command(s) defined will be executed from console. The `%player%` variable will be replaced with the user who teleported
  * Add a command: `/sg edit [region name] [flag type] add [command]`
  * Remove a command: `/sg edit [region name] [flag type] remove [index]`
  * List all commands: `/sg edit [region name] [flag type] list`
