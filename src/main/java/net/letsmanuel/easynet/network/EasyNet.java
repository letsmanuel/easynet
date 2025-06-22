package net.letsmanuel.easynet.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EasyNet {
    private static final Map<String, CustomPayload.Id<EasyNetPayload>> PACKET_IDS = new ConcurrentHashMap<>();
    private static final Map<String, BiConsumer<Object, String>> SERVER_HANDLERS = new ConcurrentHashMap<>();
    private static final Map<String, Consumer<String>> CLIENT_HANDLERS = new ConcurrentHashMap<>();
    private static final Map<String, Object> REGISTERED_PLAYERS = new ConcurrentHashMap<>();
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
    // OBFUSCATION-SAFE UTILITIES - NO CASTING APPROACH
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Safe error logging
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

    /**
     * Safely get player name using reflection to avoid casting issues
     */
    private static String getPlayerNameSafe(Object playerObj) {
        if (playerObj == null) return "Unknown Player";
        
        try {
            // Try multiple methods to get player name
            Class<?> clazz = playerObj.getClass();
            
            // Try getName() method
            try {
                Object nameObj = clazz.getMethod("getName").invoke(playerObj);
                if (nameObj != null) {
                    Object stringObj = nameObj.getClass().getMethod("getString").invoke(nameObj);
                    if (stringObj instanceof String) {
                        return (String) stringObj;
                    }
                }
            } catch (Exception e) {
                // Try alternative methods
            }
            
            // Try getDisplayName() method
            try {
                Object displayNameObj = clazz.getMethod("getDisplayName").invoke(playerObj);
                if (displayNameObj != null) {
                    Object stringObj = displayNameObj.getClass().getMethod("getString").invoke(displayNameObj);
                    if (stringObj instanceof String) {
                        return (String) stringObj;
                    }
                }
            } catch (Exception e) {
                // Continue to fallback
            }
            
            return "Player_" + System.identityHashCode(playerObj);
        } catch (Exception e) {
            return "Unknown Player";
        }
    }

    /**
     * Safely get player UUID using reflection
     */
    private static String getPlayerUuidSafe(Object playerObj) {
        if (playerObj == null) return "";
        
        try {
            Class<?> clazz = playerObj.getClass();
            
            // Try getUuidAsString() method
            try {
                Object uuidObj = clazz.getMethod("getUuidAsString").invoke(playerObj);
                if (uuidObj instanceof String) {
                    return (String) uuidObj;
                }
            } catch (Exception e) {
                // Try getUuid() method
                try {
                    Object uuidObj = clazz.getMethod("getUuid").invoke(playerObj);
                    if (uuidObj != null) {
                        return uuidObj.toString();
                    }
                } catch (Exception e2) {
                    // Continue to fallback
                }
            }
            
            return "uuid_" + System.identityHashCode(playerObj);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Safely check if player is disconnected using reflection
     */
    private static boolean isPlayerDisconnectedSafe(Object playerObj) {
        if (playerObj == null) return true;
        
        try {
            Class<?> clazz = playerObj.getClass();
            
            // Try isDisconnected() method
            try {
                Object result = clazz.getMethod("isDisconnected").invoke(playerObj);
                if (result instanceof Boolean) {
                    return (Boolean) result;
                }
            } catch (Exception e) {
                // If we can't check, assume connected
                return false;
            }
            
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Safely get server from player using reflection
     */
    private static Object getServerFromPlayerSafe(Object playerObj) {
        if (playerObj == null) return null;
        
        try {
            Class<?> clazz = playerObj.getClass();
            
            // Try getServer() method
            try {
                return clazz.getMethod("getServer").invoke(playerObj);
            } catch (Exception e) {
                // Try server field
                try {
                    return clazz.getField("server").get(playerObj);
                } catch (Exception e2) {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely execute on server thread using reflection
     */
    private static void executeOnServerThreadSafe(Object playerObj, Runnable task) {
        try {
            Object server = getServerFromPlayerSafe(playerObj);
            if (server != null) {
                Class<?> serverClass = server.getClass();
                try {
                    serverClass.getMethod("execute", Runnable.class).invoke(server, task);
                    return;
                } catch (Exception e) {
                    // Fallback to direct execution
                }
            }
        } catch (Exception e) {
            // Continue to fallback
        }
        
        // Fallback: execute directly
        try {
            task.run();
        } catch (Exception e) {
            logError("Failed to execute task: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // SERVER SIDE METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Send a packet from server to a specific client
     * @param player Target player (any player object)
     * @param packetName Unique identifier for the packet type
     * @param content String data to send (JSON recommended)
     */
    public static void sendPacketToClient(Object player, String packetName, String content) {
        if (player == null) {
            logError("Cannot send packet '" + packetName + "' - player is null");
            return;
        }

        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            registerPayloadTypeSafe(packetId, true);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            
            // Use reflection to call ServerPlayNetworking.send
            Class<?> networkingClass = ServerPlayNetworking.class;
            networkingClass.getMethod("send", Object.class, CustomPayload.class).invoke(null, player, payload);
            
        } catch (Exception e) {
            logError("Failed to send packet '" + packetName + "' to client: " + e.getMessage());
        }
    }

    /**
     * Send a packet to all connected players using server reference
     * @param server The minecraft server instance
     * @param packetName Unique identifier for the packet type
     * @param content String data to send
     */
    public static void broadcastPacketToAllClients(Object server, String packetName, String content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            registerPayloadTypeSafe(packetId, true);

            EasyNetPayload payload = new EasyNetPayload(packetName, content);
            
            // Use PlayerLookup.all() with reflection
            Class<?> playerLookupClass = PlayerLookup.class;
            Object playersCollection = playerLookupClass.getMethod("all", Object.class).invoke(null, server);
            
            if (playersCollection instanceof Iterable) {
                for (Object player : (Iterable<?>) playersCollection) {
                    sendPacketToClient(player, packetName, content);
                }
            }
        } catch (Exception e) {
            logError("Failed to broadcast packet '" + packetName + "': " + e.getMessage());
        }
    }

    /**
     * Send a packet to all registered players
     * @param packetName Unique identifier for the packet type
     * @param content String data to send
     */
    public static void broadcastPacketToRegisteredClients(String packetName, String content) {
        try {
            CustomPayload.Id<EasyNetPayload> packetId = getOrCreatePacketId(packetName);
            registerPayloadTypeSafe(packetId, true);

            for (Object player : REGISTERED_PLAYERS.values()) {
                if (player != null && !isPlayerDisconnectedSafe(player)) {
                    sendPacketToClient(player, packetName, content);
                }
            }
        } catch (Exception e) {
            logError("Failed to broadcast packet to registered clients '" + packetName + "': " + e.getMessage());
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

                // Get player from context - this is safe because it's provided by Fabric
                Object player = context.player();
                if (player == null) {
                    logError("No player found for packet '" + receivedPacketName + "'");
                    return;
                }

                BiConsumer<Object, String> registeredHandler = SERVER_HANDLERS.get(receivedPacketName);
                if (registeredHandler == null) {
                    logError("No handler registered for packet '" + receivedPacketName + "'");
                    return;
                }

                // Execute on server thread safely
                executeOnServerThreadSafe(player, () -> {
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

                // Execute on client thread safely
                try {
                    context.client().execute(() -> {
                        try {
                            registeredHandler.accept(data);
                        } catch (Exception e) {
                            logError("Error in client packet handler '" + receivedPacketName + "': " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    // Fallback: execute directly
                    try {
                        registeredHandler.accept(data);
                    } catch (Exception e2) {
                        logError("Error in client packet handler '" + receivedPacketName + "': " + e2.getMessage());
                    }
                }

            } catch (Exception e) {
                logError("Error processing client packet '" + packetName + "': " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // PLAYER UTILITY METHODS - ALL REFLECTION BASED
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Get player name safely
     */
    public static String getPlayerName(Object playerObj) {
        return getPlayerNameSafe(playerObj);
    }

    /**
     * Get player UUID safely
     */
    public static String getPlayerUuid(Object playerObj) {
        return getPlayerUuidSafe(playerObj);
    }

    /**
     * Check if player is valid and online
     */
    public static boolean isPlayerValid(Object playerObj) {
        return playerObj != null && !isPlayerDisconnectedSafe(playerObj);
    }

    /**
     * Get player's current world name using reflection
     */
    public static String getPlayerWorld(Object playerObj) {
        if (playerObj == null) return "unknown";
        
        try {
            Class<?> clazz = playerObj.getClass();
            
            // Try getServerWorld() method
            try {
                Object worldObj = clazz.getMethod("getServerWorld").invoke(playerObj);
                if (worldObj != null) {
                    Object registryKeyObj = worldObj.getClass().getMethod("getRegistryKey").invoke(worldObj);
                    if (registryKeyObj != null) {
                        Object valueObj = registryKeyObj.getClass().getMethod("getValue").invoke(registryKeyObj);
                        if (valueObj != null) {
                            return valueObj.toString();
                        }
                    }
                }
            } catch (Exception e) {
                // Try getWorld() method
                try {
                    Object worldObj = clazz.getMethod("getWorld").invoke(playerObj);
                    if (worldObj != null) {
                        return worldObj.getClass().getSimpleName();
                    }
                } catch (Exception e2) {
                    // Continue to fallback
                }
            }
            
            return "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Get player's position as JSON string using reflection
     */
    public static String getPlayerPosition(Object playerObj) {
        if (playerObj == null) return "{\"x\":0,\"y\":0,\"z\":0}";
        
        try {
            Class<?> clazz = playerObj.getClass();
            
            // Try getX(), getY(), getZ() methods
            try {
                Object xObj = clazz.getMethod("getX").invoke(playerObj);
                Object yObj = clazz.getMethod("getY").invoke(playerObj);
                Object zObj = clazz.getMethod("getZ").invoke(playerObj);
                
                double x = xObj instanceof Number ? ((Number) xObj).doubleValue() : 0.0;
                double y = yObj instanceof Number ? ((Number) yObj).doubleValue() : 0.0;
                double z = zObj instanceof Number ? ((Number) zObj).doubleValue() : 0.0;
                
                return String.format("{\"x\":%.2f,\"y\":%.2f,\"z\":%.2f}", x, y, z);
            } catch (Exception e) {
                return "{\"x\":0,\"y\":0,\"z\":0}";
            }
        } catch (Exception e) {
            return "{\"x\":0,\"y\":0,\"z\":0}";
        }
    }

    /**
     * Get player's health using reflection
     */
    public static float getPlayerHealth(Object playerObj) {
        if (playerObj == null) return 0.0f;
        
        try {
            Class<?> clazz = playerObj.getClass();
            
            // Try getHealth() method
            try {
                Object healthObj = clazz.getMethod("getHealth").invoke(playerObj);
                if (healthObj instanceof Number) {
                    return ((Number) healthObj).floatValue();
                }
            } catch (Exception e) {
                // Continue to fallback
            }
            
            return 0.0f;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    // ════════════════════════════════════════════════════════════════════════════════
    // PLAYER REGISTRY METHODS
    // ════════════════════════════════════════════════════════════════════════════════

    /**
     * Register a player instance with a unique key
     */
    public static void registerPlayer(String key, Object playerObj) {
        if (playerObj != null) {
            REGISTERED_PLAYERS.put(key, playerObj);
        }
    }

    /**
     * Get a registered player by key
     */
    public static Object getRegisteredPlayer(String key) {
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
        if (playerObj != null) {
            String uuid = getPlayerUuidSafe(playerObj);
            String key = modId + ":" + uuid;
            REGISTERED_PLAYERS.put(key, playerObj);
        }
    }

    /**
     * Get player by mod ID and UUID
     */
    public static Object getPlayerByMod(String modId, String playerUuid) {
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
     * Broadcast to all registered clients (legacy method name)
     */
    public static void broadcastPacketToAllClients(String packetName, String content) {
        broadcastPacketToRegisteredClients(packetName, content);
    }
}
