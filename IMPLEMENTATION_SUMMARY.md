# Block States Implementation - Change Summary

## Overview
Added support for custom block states in ProtectionStones plugin, allowing blocks like mushroom blocks to be configured with specific state properties (e.g., `RED_MUSHROOM_BLOCK[north=false,south=true,east=true,west=true,up=false,down=false]`).

## Modified Files

### 1. PSProtectBlock.java
**Changes:**
- Added `public transient String blockStates = null;` field to store extracted block state string
- Field is transient as it's derived from the `type` field during initialization

### 2. BlockUtil.java
**New Methods Added:**
- `getBlockStates(Block block)` - Extracts block state string from a block's BlockData
- `blockStatesMatch(String states1, String states2)` - Compares two block state strings
- `setBlockWithStates(Block block, String psType)` - Sets a block's material and applies block states

**Modified Methods:**
- `getProtectBlockType(Block block)` - Now checks for configured blocks with states and returns full type string including states if found
- `getProtectBlockItemFromType(String type)` - Extracts base material when block states are present

### 3. PSConfig.java
**Changes in block loading section:**
- Added parsing logic to extract block states from type field (format: `MATERIAL[states]`)
- Stores extracted states in `PSProtectBlock.blockStates` field
- Base material validation uses extracted base material instead of full type string
- Maintains backward compatibility with existing configurations

### 4. PSRegion.java
**Modified Methods:**
- `unhide()` - Now uses `BlockUtil.setBlockWithStates()` for non-head blocks to preserve block states
- `setType(PSProtectBlock type)` - Updated to use `BlockUtil.setBlockWithStates()` for proper state handling

## How It Works

### Configuration
Users can now specify block states in their TOML config files:
```toml
type = "RED_MUSHROOM_BLOCK[north=false,south=true,east=true,west=true,up=false,down=false]"
```

### Block Matching Process
1. When a block is placed, `BlockUtil.getProtectBlockType(Block)` is called
2. Method extracts the base material type (e.g., `RED_MUSHROOM_BLOCK`)
3. Method gets the block's current state string using `getBlockStates()`
4. Combines material and states: `RED_MUSHROOM_BLOCK[north=false,south=true,...]`
5. Checks if this exact combination exists in configuration
6. If found, returns the full string; otherwise returns just the base material
7. `ProtectionStones.getBlockOptions()` looks up the returned string in the HashMap

### Block Placement Process
1. When a region is unhidden or type is changed, `setBlockWithStates()` is called
2. Method parses the type string to extract base material and states
3. Constructs a Minecraft block data string: `minecraft:material[states]`
4. Creates BlockData from the string and applies it to the block
5. Falls back to simple material setting if parsing fails

### Region Storage
- Block type (including states) is stored in WorldGuard region flags
- No database schema changes needed
- Existing regions continue to work without modification

## Backward Compatibility

### Existing Configurations
- Configurations without block states continue to work normally
- `type = "EMERALD_ORE"` still matches any emerald ore block
- No migration needed for existing setups

### Existing Regions
- Stored regions use the type string from flags
- Blocks placed without states will only match configurations without states
- No data corruption or region invalidation

## Testing Recommendations

### Basic Functionality
1. Create a config with block states (e.g., mushroom block example)
2. Give player the protection stone: `/ps give [player] mushroom`
3. Place the block - verify it creates a region
4. Break and replace the block - verify state matching works
5. Use `/ps hide` and `/ps unhide` - verify states are preserved

### Edge Cases
1. Test with invalid state strings - should fall back gracefully
2. Test with partial state strings - should handle correctly
3. Test mixing state and non-state blocks of same material
4. Test region type changes with `/ps admin changetype`

### Compatibility
1. Test existing regions still work after update
2. Test existing config files load correctly
3. Test old protection blocks still function
4. Reload config with `/ps reload` - verify parsing works

## Known Limitations

1. **Items Don't Store States**: Block states are only checked when a block is placed in the world. Items don't have state information, so matching happens at placement time.

2. **State String Format**: Must exactly match Minecraft's block state format. Invalid states will cause fallback to base material only.

3. **Resource Pack Coordination**: If using custom block models via resource packs, ensure the block states in the config match the resource pack's variant definitions.

## Future Enhancements

Potential improvements that could be added:
- Visual indicator in `/ps get` menu showing block states
- Command to list all possible states for a material
- Wildcard matching for states (e.g., match any north/south combination)
- State validation during config loading with warnings for invalid states
- NBT support for additional item customization

## Example Use Cases

1. **Resource Pack Integration**: Different mushroom block states for different protection tiers (as in your Fabricate resource pack)
2. **Aesthetic Control**: Specific fence or stair orientations as protection blocks
3. **Functional Variants**: Different door states for different permission levels
4. **Visual Distinction**: Same material with different states for different regions
