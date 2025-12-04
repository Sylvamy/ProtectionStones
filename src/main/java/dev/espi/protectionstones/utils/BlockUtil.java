/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.espi.protectionstones.utils;

import dev.espi.protectionstones.PSProtectBlock;
import dev.espi.protectionstones.ProtectionStones;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;
import java.util.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bukkit.block.data.BlockData;

public class BlockUtil {
    static final int MAX_USERNAME_LENGTH = 16;
    public static HashMap<String, String> uuidToBase64Head = new HashMap<>();
    
    /**
     * Get block state string from block data
     * @param block the block to get states from
     * @return block state string in format "key1=value1,key2=value2" or null if no relevant states
     */
    public static String getBlockStates(Block block) {
        BlockData blockData = block.getBlockData();
        String blockDataStr = blockData.getAsString();
        
        // Extract states from format "minecraft:material[state1=value1,state2=value2]"
        int startBracket = blockDataStr.indexOf('[');
        int endBracket = blockDataStr.indexOf(']');
        
        if (startBracket != -1 && endBracket != -1 && endBracket > startBracket) {
            return blockDataStr.substring(startBracket + 1, endBracket);
        }
        return null;
    }
    
    /**
     * Check if two block state strings match
     * @param states1 first state string
     * @param states2 second state string
     * @return true if they match or both are null
     */
    public static boolean blockStatesMatch(String states1, String states2) {
        if (states1 == null && states2 == null) return true;
        if (states1 == null || states2 == null) return false;
        return states1.equals(states2);
    }
    
    /**
     * Set block type with optional block states
     * @param block the block to set
     * @param psType the protection stone type string (e.g., "RED_MUSHROOM_BLOCK[north=false,south=true]")
     */
    public static void setBlockWithStates(Block block, String psType) {
        // Extract base material and states
        String baseMaterial = psType;
        String blockStates = null;
        
        int bracketIndex = psType.indexOf('[');
        if (bracketIndex != -1) {
            baseMaterial = psType.substring(0, bracketIndex);
            int endBracket = psType.indexOf(']');
            if (endBracket != -1 && endBracket > bracketIndex) {
                blockStates = psType.substring(bracketIndex + 1, endBracket);
            }
        }
        
        // Set the material
        Material material = Material.getMaterial(baseMaterial);
        if (material != null) {
            if (blockStates != null) {
                // Create block data string in Minecraft format
                String blockDataString = "minecraft:" + baseMaterial.toLowerCase() + "[" + blockStates + "]";
                try {
                    BlockData blockData = Bukkit.createBlockData(blockDataString);
                    block.setBlockData(blockData);
                } catch (IllegalArgumentException e) {
                    // If parsing fails, just set the material
                    block.setType(material);
                }
            } else {
                block.setType(material);
            }
        }
    }

    public static ItemStack getProtectBlockItemFromType(String type) {
        // Extract base material if block states are present
        String baseMaterial = type;
        int bracketIndex = type.indexOf('[');
        if (bracketIndex != -1) {
            baseMaterial = type.substring(0, bracketIndex);
        }
        
        if (baseMaterial.startsWith(Material.PLAYER_HEAD.toString())) {
            return new ItemStack(Material.PLAYER_HEAD);
        } else {
            return new ItemStack(Material.getMaterial(baseMaterial));
        }
    }

    // used for preventing unnecessary calls to .getOwningPlayer() which could cause server freezes
    private static boolean isOwnedSkullTypeConfigured() {
        for (PSProtectBlock b : ProtectionStones.getInstance().getConfiguredBlocks()) {
            if (b.type.startsWith("PLAYER_HEAD:")) {
                return true;
            }
        }
        return false;
    }

    public static String getProtectBlockType(ItemStack i) {
        if (i.getType() == Material.PLAYER_HEAD || i.getType() == Material.LEGACY_SKULL_ITEM) {
            SkullMeta sm = (SkullMeta) i.getItemMeta();

            // PLAYER_HEAD
            if (!sm.hasOwner() || !isOwnedSkullTypeConfigured()) {
                return Material.PLAYER_HEAD.toString();
            }

            // PLAYER_HEAD:base64
            if (ProtectionStones.getBlockOptions("PLAYER_HEAD:" + sm.getOwningPlayer().getUniqueId()) != null) {
                return Material.PLAYER_HEAD + ":" + sm.getOwningPlayer().getUniqueId();
            }

            // PLAYER_HEAD:name
            return Material.PLAYER_HEAD + ":" + sm.getOwningPlayer().getName(); // return name if it doesn't exist
        }
        return i.getType().toString();
    }

    public static String getProtectBlockType(Block block) {
        String baseType;
        
        if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {

            Skull s = (Skull) block.getState();
            if (s.hasOwner() && isOwnedSkullTypeConfigured()) {
                OfflinePlayer op = s.getOwningPlayer();
                if (ProtectionStones.getBlockOptions("PLAYER_HEAD:" + op.getUniqueId()) != null) {
                    // PLAYER_HEAD:base64
                    baseType = Material.PLAYER_HEAD + ":" + op.getUniqueId();
                } else {
                    // PLAYER_HEAD:name
                    baseType = Material.PLAYER_HEAD + ":" + op.getName(); // return name if doesn't exist
                }
            } else { // PLAYER_HEAD
                baseType = Material.PLAYER_HEAD.toString();
            }
        } else if (block.getType() == Material.CREEPER_WALL_HEAD) {
            baseType = Material.CREEPER_HEAD.toString();
        } else if (block.getType() == Material.DRAGON_WALL_HEAD) {
            baseType = Material.DRAGON_HEAD.toString();
        } else if (block.getType() == Material.ZOMBIE_WALL_HEAD) {
            baseType = Material.ZOMBIE_HEAD.toString();
        } else if (block.getType() == Material.SKELETON_WALL_SKULL) {
            baseType = Material.SKELETON_SKULL.toString();
        } else if (block.getType() == Material.WITHER_SKELETON_WALL_SKULL) {
            baseType = Material.WITHER_SKELETON_SKULL.toString();
        } else {
            baseType = block.getType().toString();
        }
        
        // Check if there's a configured block with states for this material
        String blockStates = getBlockStates(block);
        if (blockStates != null) {
            String typeWithStates = baseType + "[" + blockStates + "]";
            if (ProtectionStones.getBlockOptions(typeWithStates) != null) {
                return typeWithStates;
            }
        }
        
        return baseType;
    }

    public static void setHeadType(String psType, Block b) {
        if (psType.split(":").length < 2) return;
        String name = psType.split(":")[1];
        if (name.length() > MAX_USERNAME_LENGTH) {
            blockWithBase64(b, name);
        } else {
            OfflinePlayer op = Bukkit.getOfflinePlayer(psType.split(":")[1]);
            Skull s = (Skull) b.getState();
            s.setOwningPlayer(op);
            s.update();
        }
    }

    public static PlayerProfile getProfile(String uuid, String base64) {
        String name = uuid.substring(0, 16);
        PlayerProfile profile = Bukkit.getServer().createPlayerProfile(UUID.fromString(uuid), name);
        PlayerTextures textures = profile.getTextures();

        // decode base64 to URL
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        String decodedString = new String(decodedBytes);

        // read decoded string as JSON object
        // sample: {"textures":{"SKIN":{"url":"http://textures.minecraft.net/texture/..."}}}
        String url = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(decodedString);
            JSONObject jsonTextures = (JSONObject) object.get("textures");
            JSONObject jsonSkin = (JSONObject) jsonTextures.get("SKIN");
            url = (String) jsonSkin.get("url");
        } catch (ParseException exception) {
            throw new RuntimeException("Invalid JSON retrieved from base64 " + decodedString, exception);
        }

        URL urlObject;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid decoded URL from head data: " + url, exception);
        }

        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }

    public static ItemStack setHeadType(String psType, ItemStack item) {
        String name = psType.split(":")[1];
        if (name.length() > MAX_USERNAME_LENGTH) { // base 64 head
            String uuid = name;

            // decode base64 to URL
            String base64 = uuidToBase64Head.get(name);
            PlayerProfile profile = getProfile(uuid, base64);

            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setOwnerProfile(profile);
            item.setItemMeta(meta);

            return item;
        } else { // normal name head
            SkullMeta sm = (SkullMeta) item.getItemMeta();
            sm.setOwningPlayer(Bukkit.getOfflinePlayer(name));
            item.setItemMeta(sm);
            return item;
        }
    }

    private static void blockWithBase64(Block block, String uuid) {
        String base64 = uuidToBase64Head.get(uuid);
        PlayerProfile profile = getProfile(uuid, base64);

        Skull skull = (Skull) block.getState();
        skull.setOwnerProfile(profile);
        skull.update(false);
    }

    public static boolean isBase64PSHead(String type) {
        return type.startsWith("PLAYER_HEAD") && type.split(":").length > 1 && type.split(":")[1].length() > MAX_USERNAME_LENGTH;
    }

    public static String getUUIDFromBase64PS(PSProtectBlock b) {
        String base64 = b.type.split(":")[1];
        // return UUID.nameUUIDFromBytes(base64.getBytes()).toString(); <- I should be using this

        // the below is bad, because hashcode should really not be used... unfortunately, this is used in production so it will have to stay like this
        // until I can find a way to convert items to the new uuid
        // see github issue #126
        return new UUID(base64.hashCode(), base64.hashCode()).toString();
    }
}
