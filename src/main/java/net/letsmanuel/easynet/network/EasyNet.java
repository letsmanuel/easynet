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
 * • Player instance management
 * • Error handling and debugging support
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                              QUICK START                                    │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * 1. SERVER SIDE SETUP:
 *
 *    // Register a packet handler
 *    EasyNet.registerServerPacketHandler("my_packet", (player, data) -> {
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
 * sendPacketToClient(ServerPlayerEntity player, String packetName, String content)
 * ┌─ Description: Send a packet from server to a specific client
 * ├─ Parameters:
 * │  • player: Target player to send the packet to
 * │  • packetName: Unique identifier for the packet type
 * │  • content: String data to send (JSON recommended)
 * └─ Returns: void
 *
 * registerServerPacketHandler(String packetName, BiConsumer<ServerPlayerEntity, String> handler)
 * ┌─ Description: Register a handler for incoming client packets
 * ├─ Parameters:
 * │  • packetName: Unique identifier for the packet type
 * │  • handler: Callback function that processes the packet data
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
 * registerPlayerInstance(String modId, ServerPlayerEntity player)
 * ┌─ Description: Register a player instance with a mod identifier
 * ├─ Parameters:
 * │  • modId: Your mod's identifier
 * │  • player: Player instance to register
 * └─ Returns: void
 *
 * getRegisteredPlayer(String modId, String playerUuid)
 * ┌─ Description: Retrieve a registered player by mod ID and UUID
 * ├─ Parameters:
 * │  • modId: Your mod's identifier
 * │  • playerUuid: Player's UUID as string
 * └─ Returns: ServerPlayerEntity or null if not found
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
 * EasyNet.registerServerPacketHandler("chat_message", (player, data) -> {
 *     // Parse JSON string data
 *     // String format: {"message":"Hello everyone!","timestamp":"12345"}
 *     System.out.println("Chat data: " + data);
 * });
 *
 * // Client side - send message
 * String chatData = "{\"message\":\"Hello everyone!\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
 * EasyNet.sendPacket("chat_message", chatData);
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * EXAMPLE 2: PLAYER POSITION SYNC
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * // Server side - send position updates
 * String posData = "{\"x\":" + player.getX() + ",\"y\":" + player.getY() + ",\"z\":" + player.getZ() + "}";
 * EasyNet.sendPacketToClient(targetPlayer, "position_update", posData);
 *
 * // Client side - handle position updates
 * EasyNet.registerClientPacketHandler("position_update", (data) -> {
 *     System.out.println("Position data: " + data);
 *     // Parse JSON: {"x":123.45,"y":67.89,"z":101.23}
 * });
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * EXAMPLE 3: CUSTOM GUI DATA
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * // Server side - send GUI data
 * String guiData = "{\"windowId\":123,\"title\":\"Custom Inventory\",\"items\":[\"diamond\",\"gold\",\"iron\"]}";
 * EasyNet.sendPacketToClient(player, "open_gui", guiData);
 *
 * // Client side - handle GUI opening
 * EasyNet.registerClientPacketHandler("open_gui", (data) -> {
 *     System.out.println("GUI data: " + data);
 *     // Parse and use the JSON data to open custom GUI
 * });
 *
 * @author letsmanuel
 * @version 2.0 - String-Only API
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
    private static final Map<String, BiConsumer<ServerPlayerEntity, String>> SERVER_HANDLERS = new HashMap<>();
    private static final Map<String, Consumer<String>> CLIENT_HANDLERS = new HashMap<>();
    private static final Map<String, ServerPlayerEntity> REGISTERED_PLAYERS = new HashMap<>();

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
     * @param player Target player to send the packet to
     * @param packetName Unique identifier for the packet type
     * @param content String data to send (JSON recommended)
     */
    public static void sendPacketToClient(ServerPlayerEntity player, String packetName, String content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);

            // Register payload type safely for S2C
            registerPayloadType(packetId, true);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            ServerPlayNetworking.send(player, payload);
        } catch (Exception e) {
            System.err.println("Failed to send packet to client '" + packetName + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Register a handler for incoming client packets
     * @param packetName Unique identifier for the packet type
     * @param handler Callback function that processes the packet data (player, stringData)
     */
    public static void registerServerPacketHandler(String packetName, BiConsumer<ServerPlayerEntity, String> handler) {
        SERVER_HANDLERS.put(packetName, handler);
        CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);

        // Register payload type safely for C2S
        registerPayloadType(packetId, false);

        ServerPlayNetworking.registerGlobalReceiver(packetId, (payload, context) -> {
            try {
                EasyNetPayload easyPayload = (EasyNetPayload) payload;
                String data = easyPayload.getData();
                String packetNameFromPayload = easyPayload.getPacketName();

                // Call the registered handler with string data
                BiConsumer<ServerPlayerEntity, String> registeredHandler = SERVER_HANDLERS.get(packetNameFromPayload);
                if (registeredHandler != null) {
                    registeredHandler.accept(context.player(), data);
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
                    registeredHandler.accept(data);
                }
            } catch (Exception e) {
                System.err.println("Error handling client packet '" + packetName + "': " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Register a player instance with a mod identifier
     * @param modId Your mod's identifier
     * @param player Player instance to register
     */
    public static void registerPlayerInstance(String modId, ServerPlayerEntity player) {
        String key = modId + ":" + player.getUuidAsString();
        REGISTERED_PLAYERS.put(key, player);
    }

    /**
     * Retrieve a registered player by mod ID and UUID
     * @param modId Your mod's identifier
     * @param playerUuid Player's UUID as string
     * @return ServerPlayerEntity or null if not found
     */
    public static ServerPlayerEntity getRegisteredPlayer(String modId, String playerUuid) {
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
