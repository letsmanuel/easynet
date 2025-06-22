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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EasyNet {
    private static final Map<String, CustomPayload.Id<EasyNetPayload>> PACKET_IDS = new ConcurrentHashMap<>();
    private static final Map<String, BiConsumer<ServerPlayerEntity, String>> SERVER_HANDLERS = new ConcurrentHashMap<>();
    private static final Map<String, Consumer<String>> CLIENT_HANDLERS = new ConcurrentHashMap<>();
    private static final Map<String, ServerPlayerEntity> REGISTERED_PLAYERS = new ConcurrentHashMap<>();
    private static final Set<CustomPayload.Id<EasyNetPayload>> REGISTERED_S2C = ConcurrentHashMap.newKeySet();
    private static final Set<CustomPayload.Id<EasyNetPayload>> REGISTERED_C2S = ConcurrentHashMap.newKeySet();

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
    // OBFUSCATION-SAFE UTILITIES
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Safely cast any object to ServerPlayerEntity
     * This works even in obfuscated environments because the type hierarchy is preserved
     */
    private static ServerPlayerEntity safePlayerCast(Object playerObj) {
        if (playerObj == null) {
            return null;
        }

        try {
            // Direct cast - this works even with obfuscated class names
            // because the JVM type system is preserved
            return (ServerPlayerEntity) playerObj;
        } catch (ClassCastException e) {
            logError("Invalid player object type: " + playerObj.getClass().getSimpleName() +
                    " - Expected ServerPlayerEntity or its obfuscated equivalent");
            return null;
        }
    }

    /**
     * Safe error logging that won't cause issues in obfuscated environments
     */
    private static void logError(String message) {
        System.err.println("[EasyNet] " + message);
    }

    /**
     * Safe info logging
     */
    private static void logInfo(String message) {
        System.out.println("[EasyNet] " + message);
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // SERVER SIDE METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Send a packet from server to a specific client
     * @param player Target player (ServerPlayerEntity or any object that can be cast to it)
     * @param packetName Unique identifier for the packet type
     * @param content String data to send (JSON recommended)
     */
    public static void sendPacketToClient(Object player, String packetName, String content) {
        ServerPlayerEntity serverPlayer = safePlayerCast(player);
        if (serverPlayer == null) {
            logError("Cannot send packet '" + packetName + "' - invalid player object");
            return;
        }

        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            registerPayloadTypeSafe(packetId, true);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            ServerPlayNetworking.send(serverPlayer, payload);
        } catch (Exception e) {
            logError("Failed to send packet '" + packetName + "' to client: " + e.getMessage());
        }
    }

    /**
     * Send a packet to all connected players
     * @param packetName Unique identifier for the packet type
     * @param content String data to send
     */
    public static void broadcastPacketToAllClients(String packetName, String content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            registerPayloadTypeSafe(packetId, true);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            ServerPlayNetworking.sendToAll(payload);
        } catch (Exception e) {
            logError("Failed to broadcast packet '" + packetName + "': " + e.getMessage());
        }
    }

    /**
     * Register a handler for incoming client packets
     * @param packetName Unique identifier for the packet type
     * @param handler Callback function (ServerPlayerEntity player, String data)
     */
    public static void registerServerPacketHandler(String packetName, BiConsumer<ServerPlayerEntity, String> handler) {
        SERVER_HANDLERS.put(packetName, handler);
        CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
        registerPayloadTypeSafe(packetId, false);

        ServerPlayNetworking.registerGlobalReceiver(packetId, (payload, context) -> {
            try {
                EasyNetPayload easyPayload = (EasyNetPayload) payload;
                String data = easyPayload.getData();
                String receivedPacketName = easyPayload.getPacketName();

                // Get player from context
                ServerPlayerEntity player = safePlayerCast(context.player());
                if (player == null) {
                    logError("No valid player found for packet '" + receivedPacketName + "'");
                    return;
                }

                // Get the handler
                BiConsumer<ServerPlayerEntity, String> registeredHandler = SERVER_HANDLERS.get(receivedPacketName);
                if (registeredHandler == null) {
                    logError("No handler registered for packet '" + receivedPacketName + "'");
                    return;
                }

                // Execute on server thread for thread safety
                executeOnServerThread(player, () -> {
                    try {
                        registeredHandler.accept(player, data);
                    } catch (Exception e) {
                        logError("Error in server packet handler '" + receivedPacketName + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                logError("Error processing server packet '" + packetName + "': " + e.getMessage());
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
    public static void sendPacketToServer(String packetName, String content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            registerPayloadTypeSafe(packetId, false);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            ClientPlayNetworking.send(payload);
        } catch (Exception e) {
            logError("Failed to send packet '" + packetName + "' to server: " + e.getMessage());
        }
    }

    /**
     * Register a handler for incoming server packets
     * @param packetName Unique identifier for the packet type
     * @param handler Callback function that processes packet data (String data)
     */
    public static void registerClientPacketHandler(String packetName, Consumer<String> handler) {
        CLIENT_HANDLERS.put(packetName, handler);
        CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
        registerPayloadTypeSafe(packetId, true);

        ClientPlayNetworking.registerGlobalReceiver(packetId, (payload, context) -> {
            try {
                EasyNetPayload easyPayload = (EasyNetPayload) payload;
                String data = easyPayload.getData();
                String receivedPacketName = easyPayload.getPacketName();

                Consumer<String> registeredHandler = CLIENT_HANDLERS.get(receivedPacketName);
                if (registeredHandler == null) {
                    logError("No handler registered for client packet '" + receivedPacketName + "'");
                    return;
                }

                // Execute on client thread for thread safety
                executeOnClientThread(context, () -> {
                    try {
                        registeredHandler.accept(data);
                    } catch (Exception e) {
                        logError("Error in client packet handler '" + receivedPacketName + "': " + e.getMessage());
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                logError("Error processing client packet '" + packetName + "': " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // PLAYER UTILITY METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Get player name safely
     */
    public static String getPlayerName(Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        return player != null ? player.getName().getString() : "Unknown Player";
    }

    /**
     * Get player UUID safely
     */
    public static String getPlayerUuid(Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        return player != null ? player.getUuidAsString() : "";
    }

    /**
     * Check if player is valid and online
     */
    public static boolean isPlayerValid(Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        return player != null && !player.isDisconnected();
    }

    /**
     * Get player's current world name
     */
    public static String getPlayerWorld(Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        if (player == null || player.getServerWorld() == null) {
            return "unknown";
        }
        return player.getServerWorld().getRegistryKey().getValue().toString();
    }

    /**
     * Get player's position as JSON string
     */
    public static String getPlayerPosition(Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        if (player == null) {
            return "{\"x\":0,\"y\":0,\"z\":0}";
        }
        return String.format("{\"x\":%.2f,\"y\":%.2f,\"z\":%.2f}",
                player.getX(), player.getY(), player.getZ());
    }

    /**
     * Get player's health
     */
    public static float getPlayerHealth(Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        return player != null ? player.getHealth() : 0.0f;
    }

    /**
     * Get player's food level
     */
    public static int getPlayerFoodLevel(Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        return player != null ? player.getHungerManager().getFoodLevel() : 0;
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // PLAYER REGISTRY METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Register a player instance with a unique key
     */
    public static void registerPlayer(String key, Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        if (player != null) {
            REGISTERED_PLAYERS.put(key, player);
        }
    }

    /**
     * Get a registered player by key
     */
    public static ServerPlayerEntity getRegisteredPlayer(String key) {
        return REGISTERED_PLAYERS.get(key);
    }

    /**
     * Remove a registered player
     */
    public static void unregisterPlayer(String key) {
        REGISTERED_PLAYERS.remove(key);
    }

    /**
     * Register player by mod ID and UUID
     */
    public static void registerPlayerByMod(String modId, Object playerObj) {
        ServerPlayerEntity player = safePlayerCast(playerObj);
        if (player != null) {
            String key = modId + ":" + player.getUuidAsString();
            REGISTERED_PLAYERS.put(key, player);
        }
    }

    /**
     * Get player by mod ID and UUID
     */
    public static ServerPlayerEntity getPlayerByMod(String modId, String playerUuid) {
        String key = modId + ":" + playerUuid;
        return REGISTERED_PLAYERS.get(key);
    }

    /**
     * Remove player by mod ID and UUID
     */
    public static void unregisterPlayerByMod(String modId, String playerUuid) {
        String key = modId + ":" + playerUuid;
        REGISTERED_PLAYERS.remove(key);
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // DEBUG AND UTILITY METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Get all registered packet names
     */
    public static String[] getRegisteredPacketNames() {
        return PACKET_IDS.keySet().toArray(new String[0]);
    }

    /**
     * Get count of registered players
     */
    public static int getRegisteredPlayerCount() {
        return REGISTERED_PLAYERS.size();
    }

    /**
     * Clear all registered players (useful for cleanup)
     */
    public static void clearRegisteredPlayers() {
        REGISTERED_PLAYERS.clear();
    }

    /**
     * Check if a packet type is registered
     */
    public static boolean isPacketRegistered(String packetName) {
        return PACKET_IDS.containsKey(packetName);
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // INTERNAL HELPER METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Get or create packet ID - thread safe
     */
    private static CustomPayload.Id<EasyNetPayload> getOrCreatePacketId(String packetName) {
        return PACKET_IDS.computeIfAbsent(packetName, name -> {
            String cleanName = name.toLowerCase().replaceAll("[^a-z0-9_]", "_");
            return new CustomPayload.Id<>(Identifier.of("easynet", cleanName));
        });
    }

    /**
     * Safely register payload types to avoid duplicate registration
     */
    private static void registerPayloadTypeSafe(CustomPayload.Id<EasyNetPayload> packetId, boolean isServerToClient) {
        try {
            if (isServerToClient) {
                if (REGISTERED_S2C.add(packetId)) {
                    PayloadTypeRegistry.playS2C().register(packetId, EasyNetPayload.CODEC);
                }
            } else {
                if (REGISTERED_C2S.add(packetId)) {
                    PayloadTypeRegistry.playC2S().register(packetId, EasyNetPayload.CODEC);
                }
            }
        } catch (Exception e) {
            // Payload already registered - this is normal, ignore silently
        }
    }

    /**
     * Execute task on server thread safely
     */
    private static void executeOnServerThread(ServerPlayerEntity player, Runnable task) {
        try {
            if (player.getServer() != null) {
                player.getServer().execute(task);
            } else {
                // Fallback: execute directly if no server reference
                task.run();
            }
        } catch (Exception e) {
            logError("Failed to execute task on server thread: " + e.getMessage());
            // Fallback: execute directly
            try {
                task.run();
            } catch (Exception fallbackException) {
                logError("Fallback execution also failed: " + fallbackException.getMessage());
            }
        }
    }

    /**
     * Execute task on client thread safely
     */
    private static void executeOnClientThread(ClientPlayNetworking.Context context, Runnable task) {
        try {
            context.client().execute(task);
        } catch (Exception e) {
            logError("Failed to execute task on client thread: " + e.getMessage());
            // Fallback: execute directly
            try {
                task.run();
            } catch (Exception fallbackException) {
                logError("Fallback execution also failed: " + fallbackException.getMessage());
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // BACKWARDS COMPATIBILITY METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * @deprecated Use sendPacketToServer instead
     */
    @Deprecated
    public static void sendPacket(String packetName, String content) {
        sendPacketToServer(packetName, content);
    }

    /**
     * @deprecated Use registerServerPacketHandler with proper typing instead
     */
    @Deprecated
    public static void registerServerPacketHandler(String packetName, BiConsumer<Object, String> handler) {
        registerServerPacketHandler(packetName, (player, data) -> handler.accept(player, data));
    }

    /**
     * @deprecated Use safePlayerCast internally - not needed for external use
     */
    @Deprecated
    public static ServerPlayerEntity castToServerPlayer(Object playerObj) {
        return safePlayerCast(playerObj);
    }
}
