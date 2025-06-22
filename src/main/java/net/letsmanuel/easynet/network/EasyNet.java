/**
 * ╔═══════════════════════════════════════════════════════════════════════════════╗
 * ║                                   EASYNET                                     ║
 * ║                        Simplified Networking for Minecraft Fabric            ║
 * ╚═══════════════════════════════════════════════════════════════════════════════╝
 *
 * EasyNet is a lightweight networking library that simplifies packet communication
 * between Minecraft clients and servers using Fabric's networking API. It provides
 * an intuitive interface for sending and receiving custom packets with string data.
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                                 FEATURES                                    │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * • Simple packet registration and handling
 * • Automatic payload serialization/deserialization
 * • Support for both client-to-server (C2S) and server-to-client (S2C) packets
 * • String-based data transfer (JSON recommended)
 * • Player instance management with obfuscation-safe casting
 * • Error handling and debugging support
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                              QUICK START                                    │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * 1. SERVER SIDE SETUP:
 *
 *    // Register a packet handler
 *    EasyNet.registerServerPacketHandler("my_packet", (playerObj, data) -> {
 *        ServerPlayerEntity player = EasyNet.castToServerPlayer(playerObj);
 *        System.out.println("Received packet from " + player.getName().getString());
 *        System.out.println("Data: " + data);
 *    });
 *
 *    // Send a packet to a specific client
 *    String jsonData = "{\"message\":\"Hello Client!\",\"type\":\"greeting\"}";
 *    EasyNet.sendPacketToClient(player, "response_packet", jsonData);
 *
 * 2. CLIENT SIDE SETUP:
 *
 *    // Register a packet handler
 *    EasyNet.registerClientPacketHandler("response_packet", (data) -> {
 *        System.out.println("Server says: " + data);
 *    });
 *
 *    // Send a packet to the server
 *    String jsonData = "{\"action\":\"player_action\",\"value\":\"jump\"}";
 *    EasyNet.sendPacket("my_packet", jsonData);
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                               API REFERENCE                                 │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * SERVER SIDE METHODS
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * sendPacketToClient(Object player, String packetName, String content)
 * ┌─ Description: Send a packet from server to a specific client
 * ├─ Parameters:
 * │  • player: Target player to send the packet to (ServerPlayerEntity)
 * │  • packetName: Unique identifier for the packet type
 * │  • content: String data to send (JSON recommended)
 * └─ Returns: void
 *
 * registerServerPacketHandler(String packetName, BiConsumer<Object, String> handler)
 * ┌─ Description: Register a handler for incoming client packets
 * ├─ Parameters:
 * │  • packetName: Unique identifier for the packet type
 * │  • handler: Callback function that processes the packet data (use castToServerPlayer)
 * └─ Returns: void
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * CLIENT SIDE METHODS
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * sendPacket(String packetName, String content)
 * ┌─ Description: Send a packet from client to server
 * ├─ Parameters:
 * │  • packetName: Unique identifier for the packet type
 * │  • content: String data to send (JSON recommended)
 * └─ Returns: void
 *
 * registerClientPacketHandler(String packetName, Consumer<String> handler)
 * ┌─ Description: Register a handler for incoming server packets
 * ├─ Parameters:
 * │  • packetName: Unique identifier for the packet type
 * │  • handler: Callback function that processes the packet data
 * └─ Returns: void
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * UTILITY METHODS
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * castToServerPlayer(Object playerObj)
 * ┌─ Description: Safely cast Object to ServerPlayerEntity (handles obfuscation)
 * ├─ Parameters:
 * │  • playerObj: Player object from packet handler
 * └─ Returns: ServerPlayerEntity or null if cast fails
 *
 * registerPlayerInstance(String modId, Object player)
 * ┌─ Description: Register a player instance with a mod identifier
 * ├─ Parameters:
 * │  • modId: Your mod's identifier
 * │  • player: Player instance to register (ServerPlayerEntity)
 * └─ Returns: void
 *
 * getRegisteredPlayer(String modId, String playerUuid)
 * ┌─ Description: Retrieve a registered player by mod ID and UUID
 * ├─ Parameters:
 * │  • modId: Your mod's identifier
 * │  • playerUuid: Player's UUID as string
 * └─ Returns: Object (cast with castToServerPlayer) or null if not found
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                            USAGE EXAMPLES                                   │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * EXAMPLE 1: SIMPLE CHAT MESSAGE
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * // Server side - register handler
 * EasyNet.registerServerPacketHandler("chat_message", (playerObj, data) -> {
 *     ServerPlayerEntity player = EasyNet.castToServerPlayer(playerObj);
 *     if (player != null) {
 *         // Parse JSON string data
 *         // String format: {"message":"Hello everyone!","timestamp":"12345"}
 *         System.out.println("Chat data: " + data);
 *     }
 * });
 *
 * // Client side - send message
 * String chatData = "{\"message\":\"Hello everyone!\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
 * EasyNet.sendPacket("chat_message", chatData);
 *
 * @author letsmanuel
 * @version 2.1 - Obfuscation-Safe API
 * @since Minecraft 1.21+ / Fabric
 */

package net.letsmanuel.easynet.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EasyNet {
    private static final Map<String, CustomPayload.Id<EasyNetPayload>> PACKET_IDS = new HashMap<>();
    private static final Map<String, BiConsumer<Object, String>> SERVER_HANDLERS = new HashMap<>();
    private static final Map<String, Consumer<String>> CLIENT_HANDLERS = new HashMap<>();
    private static final Map<String, Object> REGISTERED_PLAYERS = new HashMap<>();

    public static class EasyNetPayload implements CustomPayload {
        private final String packetName;
        private final String data;
        private final CustomPayload.Id<EasyNetPayload> id;

        public static final PacketCodec<RegistryByteBuf, EasyNetPayload> CODEC = PacketCodec.of(
                EasyNetPayload::write,
                EasyNetPayload::read
        );

        public EasyNetPayload(String packetName, String data) {
            this.packetName = packetName;
            this.data = data != null ? data : "";
            this.id = getOrCreatePacketId(packetName);
        }

        public static EasyNetPayload read(RegistryByteBuf buf) {
            String packetName = buf.readString();
            String data = buf.readString();
            return new EasyNetPayload(packetName, data);
        }

        public void write(RegistryByteBuf buf) {
            buf.writeString(packetName);
            buf.writeString(data);
        }

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return id;
        }

        public String getData() {
            return data;
        }

        public String getPacketName() {
            return packetName;
        }
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // OBFUSCATION-SAFE CASTING UTILITIES
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Safely cast Object to ServerPlayerEntity (handles obfuscation)
     * This is necessary because when EasyNet is used as a dependency,
     * ServerPlayerEntity might be obfuscated to class names like "class3222"
     *
     * @param playerObj Player object from packet handler
     * @return ServerPlayerEntity or null if cast fails
     */
    public static ServerPlayerEntity castToServerPlayer(Object playerObj) {
        if (playerObj == null) return null;

        try {
            // Direct cast attempt (works in dev environment)
            if (playerObj instanceof ServerPlayerEntity) {
                return (ServerPlayerEntity) playerObj;
            }

            // Reflection-based cast for obfuscated environments
            Class<?> playerClass = playerObj.getClass();

            // Check if this looks like ServerPlayerEntity by checking common methods
            if (hasServerPlayerMethods(playerClass)) {
                return (ServerPlayerEntity) playerObj;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Failed to cast player object: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check if a class has the characteristic methods of ServerPlayerEntity
     */
    private static boolean hasServerPlayerMethods(Class<?> clazz) {
        try {
            // Check for some characteristic ServerPlayerEntity methods
            boolean hasNetworkHandler = false;
            boolean hasServer = false;

            for (java.lang.reflect.Method method : clazz.getMethods()) {
                String methodName = method.getName();
                if (methodName.equals("networkHandler") || methodName.contains("NetworkHandler")) {
                    hasNetworkHandler = true;
                }
                if (methodName.equals("getServer") || methodName.contains("Server")) {
                    hasServer = true;
                }
            }

            return hasNetworkHandler || hasServer;
        } catch (Exception e) {
            return false;
        }
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // SERVER SIDE METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Send a packet from server to a specific client
     * @param player Target player to send the packet to (ServerPlayerEntity)
     * @param packetName Unique identifier for the packet type
     * @param content String data to send (JSON recommended)
     */
    public static void sendPacketToClient(Object player, String packetName, String content) {
        try {
            ServerPlayerEntity serverPlayer = castToServerPlayer(player);
            if (serverPlayer == null) {
                System.err.println("Failed to cast player object for packet '" + packetName + "'");
                return;
            }

            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);

            // Register payload type safely for S2C
            registerPayloadType(packetId, true);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            ServerPlayNetworking.send(serverPlayer, payload);
        } catch (Exception e) {
            System.err.println("Failed to send packet to client '" + packetName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Register a handler for incoming client packets
     * @param packetName Unique identifier for the packet type
     * @param handler Callback function that processes the packet data (Object player, String data)
     *                Use castToServerPlayer(player) to get ServerPlayerEntity
     */
    public static void registerServerPacketHandler(String packetName, BiConsumer<Object, String> handler) {
        SERVER_HANDLERS.put(packetName, handler);
        CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);

        // Register payload type safely for C2S
        registerPayloadType(packetId, false);

        ServerPlayNetworking.registerGlobalReceiver(packetId, (payload, context) -> {
            try {
                EasyNetPayload easyPayload = (EasyNetPayload) payload;
                String data = easyPayload.getData();
                String packetNameFromPayload = easyPayload.getPacketName();

                // Get the player entity from the context as Object to avoid obfuscation issues
                Object player = context.player();

                // Ensure we have a valid player entity
                if (player == null) {
                    System.err.println("Warning: No player entity found for packet '" + packetNameFromPayload + "'");
                    return;
                }

                // Call the registered handler with the player object and string data
                BiConsumer<Object, String> registeredHandler = SERVER_HANDLERS.get(packetNameFromPayload);
                if (registeredHandler != null) {
                    // Execute on the server thread to ensure thread safety
                    context.server().execute(() -> {
                        try {
                            registeredHandler.accept(player, data);
                        } catch (Exception handlerException) {
                            System.err.println("Error in packet handler for '" + packetNameFromPayload + "': " + handlerException.getMessage());
                            handlerException.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Error handling server packet '" + packetName + "': " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // CLIENT SIDE METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Send a packet from client to server
     * @param packetName Unique identifier for the packet type
     * @param content String data to send (JSON recommended)
     */
    public static void sendPacket(String packetName, String content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);

            // Register payload type safely for C2S
            registerPayloadType(packetId, false);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            ClientPlayNetworking.send(payload);
        } catch (Exception e) {
            System.err.println("Failed to send packet to server '" + packetName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Register a handler for incoming server packets
     * @param packetName Unique identifier for the packet type
     * @param handler Callback function that processes the packet data (stringData)
     */
    public static void registerClientPacketHandler(String packetName, Consumer<String> handler) {
        CLIENT_HANDLERS.put(packetName, handler);
        CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);

        // Register payload type safely for S2C
        registerPayloadType(packetId, true);

        ClientPlayNetworking.registerGlobalReceiver(packetId, (payload, context) -> {
            try {
                EasyNetPayload easyPayload = (EasyNetPayload) payload;
                String data = easyPayload.getData();
                String packetNameFromPayload = easyPayload.getPacketName();

                // Call the registered handler with string data
                Consumer<String> registeredHandler = CLIENT_HANDLERS.get(packetNameFromPayload);
                if (registeredHandler != null) {
                    // Execute on the client thread to ensure thread safety
                    context.client().execute(() -> {
                        try {
                            registeredHandler.accept(data);
                        } catch (Exception handlerException) {
                            System.err.println("Error in client packet handler for '" + packetNameFromPayload + "': " + handlerException.getMessage());
                            handlerException.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Error handling client packet '" + packetName + "': " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // UTILITY METHODS FOR PLAYER ACCESS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Get player name from player object
     * @param playerObj The player object (use output from packet handler)
     * @return Player's display name as string
     */
    public static String getPlayerName(Object playerObj) {
        ServerPlayerEntity player = castToServerPlayer(playerObj);
        if (player == null) return "Unknown Player";
        return player.getName().getString();
    }

    /**
     * Get player UUID from player object
     * @param playerObj The player object (use output from packet handler)
     * @return Player's UUID as string
     */
    public static String getPlayerUuid(Object playerObj) {
        ServerPlayerEntity player = castToServerPlayer(playerObj);
        if (player == null) return "";
        return player.getUuidAsString();
    }

    /**
     * Check if player is valid and online
     * @param playerObj The player object (use output from packet handler)
     * @return true if player is valid and connected
     */
    public static boolean isPlayerValid(Object playerObj) {
        ServerPlayerEntity player = castToServerPlayer(playerObj);
        return player != null && !player.isDisconnected();
    }

    /**
     * Get player's current world name
     * @param playerObj The player object (use output from packet handler)
     * @return World name as string
     */
    public static String getPlayerWorld(Object playerObj) {
        ServerPlayerEntity player = castToServerPlayer(playerObj);
        if (player == null || player.getServerWorld() == null) return "unknown";
        return player.getServerWorld().getRegistryKey().getValue().toString();
    }

    /**
     * Get player's position as JSON string
     * @param playerObj The player object (use output from packet handler)
     * @return JSON string with x, y, z coordinates
     */
    public static String getPlayerPosition(Object playerObj) {
        ServerPlayerEntity player = castToServerPlayer(playerObj);
        if (player == null) return "{\"x\":0,\"y\":0,\"z\":0}";
        return String.format("{\"x\":%.2f,\"y\":%.2f,\"z\":%.2f}",
                player.getX(), player.getY(), player.getZ());
    }

    /**
     * Register a player instance with a mod identifier
     * @param modId Your mod's identifier
     * @param playerObj Player instance to register (ServerPlayerEntity)
     */
    public static void registerPlayerInstance(String modId, Object playerObj) {
        ServerPlayerEntity player = castToServerPlayer(playerObj);
        if (player != null) {
            String key = modId + ":" + player.getUuidAsString();
            REGISTERED_PLAYERS.put(key, playerObj);
        }
    }

    /**
     * Retrieve a registered player by mod ID and UUID
     * @param modId Your mod's identifier
     * @param playerUuid Player's UUID as string
     * @return Object (cast with castToServerPlayer) or null if not found
     */
    public static Object getRegisteredPlayer(String modId, String playerUuid) {
        String key = modId + ":" + playerUuid;
        return REGISTERED_PLAYERS.get(key);
    }

    /**
     * Remove a registered player instance
     * @param modId Your mod's identifier
     * @param playerUuid Player's UUID as string
     */
    public static void removeRegisteredPlayer(String modId, String playerUuid) {
        String key = modId + ":" + playerUuid;
        REGISTERED_PLAYERS.remove(key);
    }

    /**
     * Get all registered packet names for debugging
     * @return Array of all registered packet names
     */
    public static String[] getRegisteredPacketNames() {
        return PACKET_IDS.keySet().toArray(new String[0]);
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // INTERNAL HELPER METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Get or create a packet ID for the given packet name
     */
    private static CustomPayload.Id<EasyNetPayload> getOrCreatePacketId(String packetName) {
        return PACKET_IDS.computeIfAbsent(packetName,
                name -> new CustomPayload.Id<>(Identifier.of("easynet", name.toLowerCase().replaceAll("[^a-z0-9_]", "_"))));
    }

    /**
     * Safely register payload types to avoid duplicate registration errors
     */
    private static void registerPayloadType(CustomPayload.Id<EasyNetPayload> packetId, boolean isServerToClient) {
        try {
            if (isServerToClient) {
                PayloadTypeRegistry.playS2C().register(packetId, EasyNetPayload.CODEC);
            } else {
                PayloadTypeRegistry.playC2S().register(packetId, EasyNetPayload.CODEC);
            }
        } catch (Exception e) {
            // Payload type already registered - this is expected behavior
            // Silent fail to avoid spam in logs
        }
    }
}
