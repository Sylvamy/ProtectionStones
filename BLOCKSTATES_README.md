# Block States Support for Protection Stones

## Overview

This modification adds support for custom block states in Protection Stones plugin. You can now use specific block state configurations for protection blocks, allowing for more precise control over which block variants are recognized as protection stones.

## What are Block States?

Block states are properties that define the appearance and behavior of blocks in Minecraft. For example, mushroom blocks have directional properties (north, south, east, west, up, down) that control which sides have the mushroom texture.

## Usage

### Configuration Format

In your block configuration TOML files (in the `blocks/` folder), you can now specify block states using bracket notation:

```toml
type = "MATERIAL[state1=value1,state2=value2,...]"
```

### Example: Mushroom Block

```toml
type = "RED_MUSHROOM_BLOCK[north=false,south=true,east=true,west=true,up=false,down=false]"
alias = "mushroom"
description = "Custom mushroom block protection zone."
```

### Example: Brown Mushroom Block (Fabric of Reality)

Based on your Minecraft resource pack example:

```toml
type = "BROWN_MUSHROOM_BLOCK[north=false,south=true,east=true,west=true,up=false,down=false]"
alias = "fabric"
description = "Fabric of Reality protection block."
```

### Common Block States

#### Mushroom Blocks
- `north`, `south`, `east`, `west`, `up`, `down` - Boolean values controlling texture on each face
- Example: `RED_MUSHROOM_BLOCK[north=false,south=true,east=true,west=true,up=false,down=false]`

#### Fences and Walls
- `north`, `south`, `east`, `west` - Boolean values for connections
- Example: `OAK_FENCE[north=true,south=true,east=false,west=false]`

#### Stairs
- `facing` - Direction the stairs face (north, south, east, west)
- `half` - top or bottom
- `shape` - straight, inner_left, inner_right, outer_left, outer_right
- Example: `OAK_STAIRS[facing=north,half=bottom,shape=straight]`

#### Doors
- `facing` - Direction the door faces
- `half` - upper or lower
- `hinge` - left or right
- `open` - true or false
- Example: `OAK_DOOR[facing=north,half=lower,hinge=left,open=false]`

## How It Works

### Block Matching

When a player places a block, the plugin now:
1. Checks the block's material type
2. Extracts the block's state properties
3. Matches both the material AND states against configured protection blocks
4. Only blocks with matching states (if configured) will be recognized as protection stones

### Backward Compatibility

Existing configurations without block states will continue to work exactly as before:
- `type = "EMERALD_ORE"` - Matches any emerald ore block (no state checking)
- `type = "RED_MUSHROOM_BLOCK"` - Matches any red mushroom block regardless of state

### Region Restoration

When regions are unhidden or restored, the plugin will:
- Set the correct material type
- Apply the configured block states
- Ensure the block appears exactly as configured

## Technical Details

### Modified Files

1. **PSProtectBlock.java** - Added `blockStates` field to store state information
2. **BlockUtil.java** - Added methods for:
   - Extracting block states from blocks
   - Setting blocks with specific states
   - Matching block states
3. **PSConfig.java** - Parse block state notation from config files
4. **PSRegion.java** - Updated block placement to use states
5. **ProtectionStones.java** - Block matching uses full type string including states

### Finding Block States

To find the exact block state string for any block:

1. Place the block in-game
2. Use the F3 debug screen and look at the block you're looking at
3. Copy the state properties shown (e.g., `[north=false,south=true,east=true,west=true,up=false,down=false]`)
4. Use these properties in your config file

Alternatively, you can use commands like:
```
/minecraft:setblock ~ ~ ~ minecraft:red_mushroom_block[north=false,south=true,east=true,west=true,up=false,down=false]
```

Then check the block and copy its state string.

## Example Configuration File

See `example_mushroom_block.toml` for a complete example configuration using block states.

## Notes

- Block states are case-sensitive
- The order of states in the bracket notation doesn't matter
- If invalid states are provided, the plugin will fall back to placing just the base material
- Each unique combination of material + states is treated as a separate protection block type
- You can have multiple protection block types using the same material with different states

## Troubleshooting

### Block not being recognized
- Ensure the block state string exactly matches the in-game block states
- Check server logs for any warnings about unrecognized materials
- Verify the syntax: `MATERIAL[state1=value1,state2=value2]` (no spaces)

### Block placed with wrong appearance
- Verify the state values are valid for that material type
- Check that all required states are specified
- Some blocks may have default states that override your configuration

### Items not matching blocks
- Items don't store block states - matching is done when the block is placed
- The plugin extracts states from the placed block, not the item
