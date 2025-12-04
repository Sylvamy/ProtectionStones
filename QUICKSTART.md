# Quick Start: Mushroom Block Protection Stones

This guide shows you how to set up mushroom blocks with specific states as protection stones, perfect for integration with custom resource packs like Fabricate.

## Your Use Case: Fabric of Reality

Based on your resource pack configuration where:
```json
"north=false,south=true,east=true,west=true,up=false,down=false": { 
    "model": "fabricate:blocks/fabric_of_reality" 
}
```

## Step-by-Step Setup

### 1. Create the Configuration File

In your `plugins/ProtectionStones/blocks/` folder, create a new file: `fabric_of_reality.toml`

```toml
# Fabric of Reality Protection Block
type = "BROWN_MUSHROOM_BLOCK[north=false,south=true,east=true,west=true,up=false,down=false]"
alias = "fabric"
description = "Fabric of Reality protection block"
restrict_obtaining = true

[region]
    distance_between_claims = -1
    x_radius = 32
    z_radius = 32
    y_radius = -1
    chunk_radius = -1
    home_x_offset = 0.0
    home_y_offset = 1.0
    home_z_offset = 0.0
    flags = []
    allowed_flags = []
    hidden_flags_from_info = []
    priority = 0
    allow_overlap_unowned_regions = false
    allow_other_regions_to_overlap = "DENY"
    allow_merging = false
    allowed_merging_into_types = []

[block_data]
    display_name = "§l§dFabric of Reality"
    lore = [
        "§5A mysterious protection stone",
        "§7Place to create a protected area"
    ]
    enchanted_effect = true
    price = -1.0
    allow_craft_with_custom_recipe = false
    custom_recipe = []
    recipe_amount = 1
    custom_model_data = -1

[economy]
    tax_amount = -1.0
    tax_period = -1
    tax_payment_time = 86400
    start_with_tax_autopay = false
    tenant_rent_role = ""
    landlord_still_owner = true

[behaviour]
    auto_hide = false
    auto_merge = false
    no_drop = false
    prevent_piston_push = true
    prevent_explode = true
    destroy_region_when_explode = false
    prevent_silk_touch = true
    cost_to_place = -1.0
    allow_smelt_item = false
    allow_use_in_crafting = false

[player]
    allow_shift_right_break = false
    prevent_teleport_in = false
    no_moving_when_tp_waiting = true
    tp_waiting_seconds = 0
    prevent_ps_get = false
    prevent_ps_home = false
    permission = ""

[event]
    enable = false
    on_region_create = []
    on_region_destroy = []
```

### 2. Reload the Plugin

```
/ps reload
```

### 3. Give Yourself the Protection Stone

```
/ps give [username] fabric
```

Or if you're op/admin:
```
/ps give fabric
```

### 4. Place the Block

- Place the brown mushroom block you received
- It should create a protected region
- The block will have the exact state configuration matching your resource pack

## Multiple Variants

You can create multiple protection stones using the same mushroom block material but different states. Based on your resource pack, you could create:

### Item Grid Accessor
```toml
type = "BROWN_MUSHROOM_BLOCK[north=false,south=false,east=false,west=true,up=false,down=false]"
alias = "grid_accessor"
```

### Item Grid Controller
```toml
type = "BROWN_MUSHROOM_BLOCK[north=false,south=true,east=false,west=false,up=false,down=false]"
alias = "grid_controller"
```

### Item Grid Cell
```toml
type = "BROWN_MUSHROOM_BLOCK[north=false,south=false,east=false,west=false,up=false,down=true]"
alias = "grid_cell"
```

### Item Grid Cell Dense
```toml
type = "BROWN_MUSHROOM_BLOCK[north=true,south=true,east=false,west=true,up=true,down=false]"
alias = "grid_cell_dense"
```

## Red Mushroom Example

For red mushroom blocks with the same states:
```toml
type = "RED_MUSHROOM_BLOCK[north=false,south=true,east=true,west=true,up=false,down=false]"
alias = "red_fabric"
description = "Red Fabric of Reality"
```

## Testing

1. **Get the item**: `/ps give fabric`
2. **Place it**: Right-click to place the brown mushroom block
3. **Verify region**: Use `/ps info` while standing in the region
4. **Check appearance**: With your resource pack loaded, it should show the "Fabric of Reality" model
5. **Test matching**: Break and replace - only the exact state combination should work

## Troubleshooting

### Block doesn't create region
- Verify the state string exactly matches: `north=false,south=true,east=true,west=true,up=false,down=false`
- Check server console for errors
- Ensure you reloaded the plugin after creating the config

### Block creates region but wrong appearance
- This is a resource pack issue, not a plugin issue
- Verify your resource pack's blockstate file matches the configured states
- Check the resource pack is loaded and active

### Can't get the item
- Check the alias is correct: `/ps give fabric`
- Verify you have permission: `protectionstones.get`
- Check the config file loaded correctly: `/ps reload` and watch console

## Integration with Your Setup

This implementation allows you to:
1. Use specific mushroom block states as protection blocks
2. Have different models for different protection types via resource pack
3. Maintain multiple distinct protection stone types using the same base material
4. Keep visual consistency with your Fabricate resource pack

The plugin now recognizes and preserves the exact block state configuration, ensuring your custom models display correctly!
