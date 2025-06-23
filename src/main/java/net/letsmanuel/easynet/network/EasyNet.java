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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * EasyNet - A simple, obfuscation-safe networking library for Fabric mods
 *
 * This version avoids complex reflection and uses generic Object types
 * to handle obfuscation issues automatically.
 */
public class EasyNet {
    private static final Map<String, CustomPayload.Id<EasyNetPayload>> PACKET_IDS = new ConcurrentHashMap<>();
    private static final Map<String, BiConsumer<Object, String>> SERVER_HANDLERS = new ConcurrentHashMap<>();
    private static final Map<String, Consumer<String>> CLIENT_HANDLERS = new ConcurrentHashMap<>();
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
    // SERVER SIDE METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Send a packet from server to a specific client
     * @param player Target player (will be cast to ServerPlayerEntity)
     * @param packetName Unique identifier for the packet type
     * @param content String data to send (JSON recommended)
     */
    public static void sendPacketToClient(Object player, String packetName, String content) {
        if (!(player instanceof ServerPlayerEntity)) {
            logError("Cannot send packet '" + packetName + "' - player is not a ServerPlayerEntity");
            return;
        }

        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            registerPayloadTypeSafe(packetId, true);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            ServerPlayNetworking.send((ServerPlayerEntity) player, payload);
        } catch (Exception e) {
            logError("Failed to send packet '" + packetName + "' to client: " + e.getMessage());
        }
    }

    /**
     * Send a packet to multiple players
     * @param players Iterable of ServerPlayerEntity objects
     * @param packetName Unique identifier for the packet type
     * @param content String data to send
     */
    public static void sendPacketToPlayers(Iterable<?> players, String packetName, String content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            registerPayloadTypeSafe(packetId, true);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);

            for (Object playerObj : players) {
                if (playerObj instanceof ServerPlayerEntity) {
                    ServerPlayNetworking.send((ServerPlayerEntity) playerObj, payload);
                }
            }
        } catch (Exception e) {
            logError("Failed to send packet '" + packetName + "' to players: " + e.getMessage());
        }
    }

    /**
     * Register a handler for incoming client packets
     * @param packetName Unique identifier for the packet type
     * @param handler Callback function (Object player, String data)
     */
    public static void registerServerPacketHandler(String packetName, BiConsumer<Object, String> handler) {
        SERVER_HANDLERS.put(packetName, handler);
        CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
        registerPayloadTypeSafe(packetId, false);

        ServerPlayNetworking.registerGlobalReceiver(packetId, (payload, context) -> {
            try {
                EasyNetPayload easyPayload = (EasyNetPayload) payload;
                String data = easyPayload.getData();
                String receivedPacketName = easyPayload.getPacketName();

                Object player = context.player();
                if (player == null) {
                    logError("No valid player found for packet '" + receivedPacketName + "'");
                    return;
                }

                BiConsumer<Object, String> registeredHandler = SERVER_HANDLERS.get(receivedPacketName);
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
    // UTILITY METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Get player name safely
     */
    public static String getPlayerName(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return player.getName().getString();
        }
        return "Unknown Player";
    }

    /**
     * Get player UUID safely
     */
    public static String getPlayerUuid(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return player.getUuidAsString();
        }
        return "";
    }

    /**
     * Check if player is valid and online
     */
    public static boolean isPlayerValid(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return !player.isDisconnected();
        }
        return false;
    }

    public static boolean isPlayerAdmin(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return !player.hasPermissionLevel(2);
        }
        return false;
    }

    public static boolean hasPlayerPermissionLevel(Object playerObj, Integer level) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return !player.hasPermissionLevel(level);
        }
        return false;
    }

    public static boolean isPlayerCreative(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return !player.isCreative();
        }
        return false;
    }

    public static boolean isPlayerSpectator(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return player.isSpectator();
        }
        return false;
    }


    public static String getPlayerIP(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return player.getIp();
        }
        return "";
    }


    /**
     * Get player's health
     */
    public static float getPlayerHealth(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return player.getHealth();
        }
        return 0.0f;
    }

    public static float attackPlayer(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            player.attack(player);
            return player.getHealth();
        }
        return 0.0f;
    }

    /**
     * Get player's position as JSON string
     */
    public static String getPlayerPosition(Object playerObj) {
        if (playerObj instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerObj;
            return String.format("{\"x\":%.2f,\"y\":%.2f,\"z\":%.2f}",
                    player.getX(), player.getY(), player.getZ());
        }
        return "{\"x\":0,\"y\":0,\"z\":0}";
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // DEBUGGING METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Get all registered packet names
     */
    public static String[] getRegisteredPacketNames() {
        return PACKET_IDS.keySet().toArray(new String[0]);
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

    private static CustomPayload.Id<EasyNetPayload> getOrCreatePacketId(String packetName) {
        return PACKET_IDS.computeIfAbsent(packetName, name -> {
            String cleanName = name.toLowerCase().replaceAll("[^a-z0-9_]", "_");
            return new CustomPayload.Id<>(Identifier.of("easynet", cleanName));
        });
    }

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

    private static void executeOnServerThread(Object player, Runnable task) {
        try {
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                if (serverPlayer.getServer() != null) {
                    serverPlayer.getServer().execute(task);
                    return;
                }
            }
            // Fallback: execute directly
            task.run();
        } catch (Exception e) {
            logError("Failed to execute task on server thread: " + e.getMessage());
            task.run();
        }
    }

    private static void executeOnClientThread(ClientPlayNetworking.Context context, Runnable task) {
        try {
            context.client().execute(task);
        } catch (Exception e) {
            logError("Failed to execute task on client thread: " + e.getMessage());
            task.run();
        }
    }

    private static void logError(String message) {
        System.err.println("[EasyNet] " + message);
    }

    private static void logInfo(String message) {
        System.out.println("[EasyNet] " + message);
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
     * Broadcast to all players - requires you to provide the player list
     * @deprecated Use sendPacketToPlayers instead for clarity
     */
    @Deprecated
    public static void broadcastPacketToAllClients(String packetName, String content) {
        logError("broadcastPacketToAllClients requires a player list - use sendPacketToPlayers(players, packetName, content) instead");
    }
}
