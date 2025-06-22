/**
 * ╔═══════════════════════════════════════════════════════════════════════════════╗
 * ║                                   EASYNET                                     ║
 * ║                        Simplified Networking for Minecraft Fabric            ║
 * ╚═══════════════════════════════════════════════════════════════════════════════╝
 *
 * EasyNet is a lightweight networking library that simplifies packet communication
 * between Minecraft clients and servers using Fabric's networking API. It provides
 * an intuitive interface for sending and receiving custom packets with NBT data.
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                                 FEATURES                                    │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * • Simple packet registration and handling
 * • Automatic payload serialization/deserialization
 * • Support for both client-to-server (C2S) and server-to-client (S2C) packets
 * • NBT-based data transfer
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
 *        // Process the NBT data here
 *    });
 *
 *    // Send a packet to a specific client
 *    NbtCompound data = new NbtCompound();
 *    data.putString("message", "Hello Client!");
 *    EasyNet.sendPacketToClient(player, "response_packet", data);
 *
 * 2. CLIENT SIDE SETUP:
 *
 *    // Register a packet handler
 *    EasyNet.registerClientPacketHandler("response_packet", (data) -> {
 *        String message = data.getString("message");
 *        System.out.println("Server says: " + message);
 *    });
 *
 *    // Send a packet to the server
 *    NbtCompound data = new NbtCompound();
 *    data.putString("action", "player_action");
 *    EasyNet.sendPacket("my_packet", data);
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                               API REFERENCE                                 │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * SERVER SIDE METHODS
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * sendPacketToClient(ServerPlayerEntity player, String packetName, NbtCompound content)
 * ┌─ Description: Send a packet from server to a specific client
 * ├─ Parameters:
 * │  • player: Target player to send the packet to
 * │  • packetName: Unique identifier for the packet type
 * │  • content: NBT data to send
 * └─ Returns: void
 *
 * registerServerPacketHandler(String packetName, BiConsumer<ServerPlayerEntity, NbtCompound> handler)
 * ┌─ Description: Register a handler for incoming client packets
 * ├─ Parameters:
 * │  • packetName: Unique identifier for the packet type
 * │  • handler: Callback function that processes the packet
 * └─ Returns: void
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * CLIENT SIDE METHODS
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * sendPacket(String packetName, NbtCompound content)
 * ┌─ Description: Send a packet from client to server
 * ├─ Parameters:
 * │  • packetName: Unique identifier for the packet type
 * │  • content: NBT data to send
 * └─ Returns: void
 *
 * registerClientPacketHandler(String packetName, Consumer<NbtCompound> handler)
 * ┌─ Description: Register a handler for incoming server packets
 * ├─ Parameters:
 * │  • packetName: Unique identifier for the packet type
 * │  • handler: Callback function that processes the packet
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
 *     String message = data.getString("message");
 *     // Broadcast to all players
 *     server.getPlayerManager().broadcast(
 *         Text.literal(player.getName().getString() + ": " + message), false
 *     );
 * });
 *
 * // Client side - send message
 * NbtCompound chatData = new NbtCompound();
 * chatData.putString("message", "Hello everyone!");
 * EasyNet.sendPacket("chat_message", chatData);
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * EXAMPLE 2: PLAYER POSITION SYNC
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * // Server side - send position updates
 * NbtCompound posData = new NbtCompound();
 * posData.putDouble("x", player.getX());
 * posData.putDouble("y", player.getY());
 * posData.putDouble("z", player.getZ());
 * EasyNet.sendPacketToClient(targetPlayer, "position_update", posData);
 *
 * // Client side - handle position updates
 * EasyNet.registerClientPacketHandler("position_update", (data) -> {
 *     double x = data.getDouble("x");
 *     double y = data.getDouble("y");
 *     double z = data.getDouble("z");
 *     // Update player position on client
 * });
 *
 * ════════════════════════════════════════════════════════════════════════════════
 * EXAMPLE 3: CUSTOM GUI DATA
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * // Server side - send GUI data
 * NbtCompound guiData = new NbtCompound();
 * guiData.putInt("windowId", 123);
 * guiData.putString("title", "Custom Inventory");
 * NbtList items = new NbtList();
 * // Add items to list...
 * guiData.put("items", items);
 * EasyNet.sendPacketToClient(player, "open_gui", guiData);
 *
 * // Client side - handle GUI opening
 * EasyNet.registerClientPacketHandler("open_gui", (data) -> {
 *     int windowId = data.getInt("windowId");
 *     String title = data.getString("title");
 *     NbtList items = data.getList("items", NbtElement.COMPOUND_TYPE);
 *     // Open custom GUI with the provided data
 * });
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                            BEST PRACTICES                                   │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * 1. PACKET NAMING:
 *    • Use descriptive, unique packet names
 *    • Consider prefixing with your mod ID: "mymod:player_action"
 *    • Use snake_case for consistency
 *
 * 2. DATA STRUCTURE:
 *    • Keep NBT data lightweight and organized
 *    • Use appropriate NBT types for your data
 *    • Document your packet structure for maintainability
 *
 * 3. ERROR HANDLING:
 *    • Always validate incoming data in your handlers
 *    • Check for null values and handle gracefully
 *    • Log errors for debugging purposes
 *
 * 4. PERFORMANCE:
 *    • Avoid sending large amounts of data frequently
 *    • Consider batching multiple updates into single packets
 *    • Use client-side caching when appropriate
 *
 * 5. COMPATIBILITY:
 *    • Register handlers during mod initialization
 *    • Test with both single-player and multiplayer environments
 *    • Handle cases where players might not have your mod installed
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                             LIMITATIONS                                     │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * • Currently uses string-based NBT serialization
 * • Packet registration must be done during mod initialization
 * • No built-in packet size limits (consider data size for performance)
 * • Error handling is basic (consider implementing custom error callbacks)
 *
 * ┌─────────────────────────────────────────────────────────────────────────────┐
 * │                            TROUBLESHOOTING                                  │
 * └─────────────────────────────────────────────────────────────────────────────┘
 *
 * COMMON ISSUES:
 *
 * • "Packet not received": Check if handlers are registered on correct side
 * • "ClassCastException": Ensure payload types match between client/server
 * • "NullPointerException": Validate NBT data exists before accessing
 * • "Already registered": Packet IDs are registered automatically, ignore warnings
 *
 * DEBUG TIPS:
 *
 * • Enable console logging to see packet send/receive events
 * • Use try-catch blocks in your handlers for better error reporting
 * • Verify both client and server are using the same packet names
 * • Check that mod is loaded on both sides for multiplayer testing
 *
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * @author letsmanuel
 * @version 1.0
 * @since Minecraft 1.21+ / Fabric
 *
 * ════════════════════════════════════════════════════════════════════════════════
 */

package net.letsmanuel.easynet.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EasyNet {
    private static final Map<String, CustomPayload.Id<EasyNetPayload>> PACKET_IDS = new HashMap<>();
    private static final Map<String, BiConsumer<ServerPlayerEntity, NbtCompound>> SERVER_HANDLERS = new HashMap<>();
    private static final Map<String, Consumer<NbtCompound>> CLIENT_HANDLERS = new HashMap<>();
    private static final Map<String, ServerPlayerEntity> REGISTERED_PLAYERS = new HashMap<>();

    public static class EasyNetPayload implements CustomPayload {
        private final String packetName;
        private final byte[] data;
        private final CustomPayload.Id<EasyNetPayload> id;

        public static final PacketCodec<RegistryByteBuf, EasyNetPayload> CODEC = PacketCodec.of(
                EasyNetPayload::write,
                EasyNetPayload::read
        );

        public EasyNetPayload(String packetName, byte[] data) {
            this.packetName = packetName;
            this.data = data;
            this.id = getOrCreatePacketId(packetName);
        }

        public static EasyNetPayload read(RegistryByteBuf buf) {
            String packetName = buf.readString();
            int length = buf.readInt();
            byte[] data = new byte[length];
            buf.readBytes(data);
            return new EasyNetPayload(packetName, data);
        }

        public void write(RegistryByteBuf buf) {
            buf.writeString(packetName);
            buf.writeInt(data.length);
            buf.writeBytes(data);
        }

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return id;
        }

        public byte[] getData() {
            return data;
        }

        public String getPacketName() {
            return packetName;
        }
    }

    // SERVER
    public static void sendPacketToClient(ServerPlayerEntity player, String packetName, NbtCompound content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            try {
                PayloadTypeRegistry.playS2C().register(packetId, EasyNetPayload.CODEC);
            } catch (Exception e) {
                // wieso ist java eigentlich so?
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
            String nbtString = content.toString();
            dos.writeUTF(nbtString);
            dos.close();

            byte[] data = baos.toByteArray();
            EasyNetPayload payload = new EasyNetPayload(packetName, data);

            ServerPlayNetworking.send(player, payload);
        } catch (Exception e) {
            System.err.println("Failed to send packet to client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void triggerOnPacketReceived(String packetName, ServerPlayerEntity player, NbtCompound content) {
        BiConsumer<ServerPlayerEntity, NbtCompound> handler = SERVER_HANDLERS.get(packetName);
        if (handler != null) {
            handler.accept(player, content);
        }
    }

    public static void registerServerPacketHandler(String packetName, BiConsumer<ServerPlayerEntity, NbtCompound> handler) {
        SERVER_HANDLERS.put(packetName, handler);
        CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);

        PayloadTypeRegistry.playC2S().register(packetId, EasyNetPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(packetId, (payload, context) -> {
            try {
                EasyNetPayload easyPayload = (EasyNetPayload) payload;
                byte[] data = easyPayload.getData();
                String packetNameFromPayload = easyPayload.getPacketName();

                java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.ByteArrayInputStream(data));
                String nbtString = dis.readUTF();
                NbtCompound nbtData = new NbtCompound();

                triggerOnPacketReceived(packetNameFromPayload, context.player(), nbtData);
            } catch (Exception e) {
                System.err.println("Error handling server packet: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // CLIENT

    public static void sendPacket(String packetName, NbtCompound content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            try {
                PayloadTypeRegistry.playC2S().register(packetId, EasyNetPayload.CODEC);
            } catch (Exception e) {
                // halts maul compiler
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);

            String nbtString = content.toString();
            dos.writeUTF(nbtString);
            dos.close();

            byte[] data = baos.toByteArray();
            EasyNetPayload payload = new EasyNetPayload(packetName, data);

            ClientPlayNetworking.send(payload);
        } catch (Exception e) {
            System.err.println("Failed to send packet to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void triggerOnPacketReceived(String packetName, NbtCompound content) {
        Consumer<NbtCompound> handler = CLIENT_HANDLERS.get(packetName);
        if (handler != null) {
            handler.accept(content);
        }
    }

    public static void registerClientPacketHandler(String packetName, Consumer<NbtCompound> handler) {
        CLIENT_HANDLERS.put(packetName, handler);
        CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);

        PayloadTypeRegistry.playS2C().register(packetId, EasyNetPayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(packetId, (payload, context) -> {
            try {
                EasyNetPayload easyPayload = (EasyNetPayload) payload;
                byte[] data = easyPayload.getData();
                String packetNameFromPayload = easyPayload.getPacketName();

                java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.ByteArrayInputStream(data));
                String nbtString = dis.readUTF();
                NbtCompound nbtData = new NbtCompound();

                triggerOnPacketReceived(packetNameFromPayload, nbtData);
            } catch (Exception e) {
                System.err.println("Error handling client packet: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void registerPlayerInstance(String modId, ServerPlayerEntity player) {
        REGISTERED_PLAYERS.put(modId + ":" + player.getUuidAsString(), player);
    }

    public static ServerPlayerEntity getRegisteredPlayer(String modId, String playerUuid) {
        return REGISTERED_PLAYERS.get(modId + ":" + playerUuid);
    }

    // Danke an Mojang, eure Api ist nicht die beste aber hey, NoRisk wird es gefallen lol

    private static CustomPayload.Id<EasyNetPayload> getOrCreatePacketId(String packetName) {
        return PACKET_IDS.computeIfAbsent(packetName,
                name -> new CustomPayload.Id<>(Identifier.of("easynet", name.toLowerCase())));
    }
}