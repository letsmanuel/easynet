## How to install EasyNet for Fabric 1.21.0

1) Make sure you have a correct `build.gradle` file setup in your mod.
2) Under *Repositories* make sure to add `maven { url 'https://jitpack.io' }` as your maven. This is simply because EasyNet uses Jitpack.
3) Under *Dependencies* add `modImplementation "com.github.letsmanuel:easynet:v1.0.5"`. This adds the EasyNet Library to your mod.

## EasyNet Usage Documentation:
**Simplified Networking for Minecraft Fabric**

EasyNet is a lightweight networking library that simplifies packet communication between Minecraft clients and servers using Fabric's networking API. It provides an intuitive interface for sending and receiving custom packets with NBT data.

---

## Features

- Simple packet registration and handling  
- Automatic payload serialization/deserialization  
- Support for both client-to-server (C2S) and server-to-client (S2C) packets  
- NBT-based data transfer  
- Player instance management  
- Error handling and debugging support  

---

## Quick Start

### 1. Server Side Setup

```java
// Register a packet handler
EasyNet.registerServerPacketHandler("my_packet", (player, data) -> {
    System.out.println("Received packet from " + player.getName().getString());
    // Process the NBT data here
});

// Send a packet to a specific client
NbtCompound data = new NbtCompound();
data.putString("message", "Hello Client!");
EasyNet.sendPacketToClient(player, "response_packet", data);
````

### 2. Client Side Setup

```java
// Register a packet handler
EasyNet.registerClientPacketHandler("response_packet", (data) -> {
    String message = data.getString("message");
    System.out.println("Server says: " + message);
});

// Send a packet to the server
NbtCompound data = new NbtCompound();
data.putString("action", "player_action");
EasyNet.sendPacket("my_packet", data);
```

---

## API Reference

### Server Side Methods

* **`sendPacketToClient(ServerPlayerEntity player, String packetName, NbtCompound content)`**
  Send a packet from server to a specific client.

  **Parameters:**

  * `player`: Target player to send the packet to
  * `packetName`: Unique identifier for the packet type
  * `content`: NBT data to send

  **Returns:** `void`

* **`registerServerPacketHandler(String packetName, BiConsumer<ServerPlayerEntity, NbtCompound> handler)`**
  Register a handler for incoming client packets.

  **Parameters:**

  * `packetName`: Unique identifier for the packet type
  * `handler`: Callback function that processes the packet

  **Returns:** `void`

---

### Client Side Methods

* **`sendPacket(String packetName, NbtCompound content)`**
  Send a packet from client to server.

  **Parameters:**

  * `packetName`: Unique identifier for the packet type
  * `content`: NBT data to send

  **Returns:** `void`

* **`registerClientPacketHandler(String packetName, Consumer<NbtCompound> handler)`**
  Register a handler for incoming server packets.

  **Parameters:**

  * `packetName`: Unique identifier for the packet type
  * `handler`: Callback function that processes the packet

  **Returns:** `void`

---

### Utility Methods

* **`registerPlayerInstance(String modId, ServerPlayerEntity player)`**
  Register a player instance with a mod identifier.

  **Parameters:**

  * `modId`: Your mod's identifier
  * `player`: Player instance to register

  **Returns:** `void`

* **`getRegisteredPlayer(String modId, String playerUuid)`**
  Retrieve a registered player by mod ID and UUID.

  **Parameters:**

  * `modId`: Your mod's identifier
  * `playerUuid`: Player's UUID as string

  **Returns:** `ServerPlayerEntity` or `null` if not found

---

## Usage Examples

### Example 1: Simple Chat Message

```java
// Server side - register handler
EasyNet.registerServerPacketHandler("chat_message", (player, data) -> {
    String message = data.getString("message");
    // Broadcast to all players
    server.getPlayerManager().broadcast(
        Text.literal(player.getName().getString() + ": " + message), false
    );
});

// Client side - send message
NbtCompound chatData = new NbtCompound();
chatData.putString("message", "Hello everyone!");
EasyNet.sendPacket("chat_message", chatData);
```

---

### Example 2: Player Position Sync

```java
// Server side - send position updates
NbtCompound posData = new NbtCompound();
posData.putDouble("x", player.getX());
posData.putDouble("y", player.getY());
posData.putDouble("z", player.getZ());
EasyNet.sendPacketToClient(targetPlayer, "position_update", posData);

// Client side - handle position updates
EasyNet.registerClientPacketHandler("position_update", (data) -> {
    double x = data.getDouble("x");
    double y = data.getDouble("y");
    double z = data.getDouble("z");
    // Update player position on client
});
```

---

### Example 3: Custom GUI Data

```java
// Server side - send GUI data
NbtCompound guiData = new NbtCompound();
guiData.putInt("windowId", 123);
guiData.putString("title", "Custom Inventory");
NbtList items = new NbtList();
// Add items to list...
guiData.put("items", items);
EasyNet.sendPacketToClient(player, "open_gui", guiData);

// Client side - handle GUI opening
EasyNet.registerClientPacketHandler("open_gui", (data) -> {
    int windowId = data.getInt("windowId");
    String title = data.getString("title");
    NbtList items = data.getList("items", NbtElement.COMPOUND_TYPE);
    // Open custom GUI with the provided data
});
```

---

## Best Practices

1. **Packet Naming:**

   * Use descriptive, unique packet names
   * Consider prefixing with your mod ID, e.g., `mymod:player_action`
   * Use `snake_case` for consistency

2. **Data Structure:**

   * Keep NBT data lightweight and organized
   * Use appropriate NBT types for your data
   * Document your packet structure for maintainability

3. **Error Handling:**

   * Always validate incoming data in your handlers
   * Check for null values and handle gracefully
   * Log errors for debugging purposes

4. **Performance:**

   * Avoid sending large amounts of data frequently
   * Consider batching multiple updates into single packets
   * Use client-side caching when appropriate

5. **Compatibility:**

   * Register handlers during mod initialization
   * Test with both single-player and multiplayer environments
   * Handle cases where players might not have your mod installed

---

## Limitations

* Currently uses string-based NBT serialization
* Packet registration must be done during mod initialization
* No built-in packet size limits (consider data size for performance)
* Error handling is basic (consider implementing custom error callbacks)

---

## Troubleshooting

### Common Issues

* **"Packet not received"**: Check if handlers are registered on the correct side
* **"ClassCastException"**: Ensure payload types match between client/server
* **"NullPointerException"**: Validate NBT data exists before accessing
* **"Already registered"**: Packet IDs are registered automatically, ignore warnings

### Debug Tips

* Enable console logging to see packet send/receive events
* Use try-catch blocks in your handlers for better error reporting
* Verify both client and server are using the same packet names
* Check that mod is loaded on both sides for multiplayer testing

---

*Author:* letsmanuel
*Version:* 1.0
*Since:* Minecraft 1.21+ / Fabric
