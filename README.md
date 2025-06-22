## How to install EasyNet for Fabric 1.21.0

1) Make sure you have a correct `build.gradle` file setup in your mod.
2) Under *Repositories* make sure to add `maven { url 'https://jitpack.io' }` as your maven. This is simply because EasyNet uses Jitpack.
3) Under *Dependencies* add `implementation 'com.github.letsmanuel:easynet:v1.0.15:dev'`. This adds the EasyNet Library to your mod.
4) Import it by adding: `import net.letsmanuel.easynet.network.EasyNet;`.

*Info:*
If you installed the EasyNet package but it doesnt appear under `net.letsmanuel.easynet` please carefully try again, it is a spelling or config mistake on your end. ↩️

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

Lets begin by setting up our server enviroment. To do so, lets register a packet handler via the EasyNet API.

```java
    net.letsmanuel.easynet.network.EasyNet.registerServerPacketHandler("my_first_package_handler", (player, data) -> {
        String message = data;
        // this is for debugging purposes, you can remove this in your mods final code without any issue!
        LOGGER.warn("Server has received package: "+message);
    });
````

What this does is register a new packet handler. A packet handler can receive a packet sent from any client, and work with the packet and its easy to use data.
Next, lets register the packet handler on our client devices. To do so, lets use the following snippet that shall be run in the Client Mod Initizalizer.

```java
    EasyNet.registerClientPacketHandler("response_packet", (data) -> {
        String message = data;
    });
```

Now our client and server are waiting for a packet. But noone is sending one. To do so, lets register a client command that sends something to our server for this example.

```java

    ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
        dispatcher.register(ClientCommandManager.literal("my_first_client_command")
                .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            String message = StringArgumentType.getString(context, "message");
                            MinecraftClient.getInstance().player.sendMessage(Text.literal("You typed: " + message), false);
                            return 1;
                        }))
                .executes(context -> {
                    MinecraftClient.getInstance().player.sendMessage(Text.literal("Hello from client command!"), false);
                    return 1;
                })
        );
    });

```

This code adds a command to minecraft. Next up lets send a packet to the server when the command is run. To do so, we will add the following line of code in our client code. Note that any spelling mistakes can crash the game, or prevent functionality!

```java
    EasyNet.sendPacket("my_first_package_handler", message);
```

Now whenever whe type the command, the server packet handler that we created will now trigger and run server sided code of our choice. Lets make our server respond with the player's message aswell as their username and health. This is also easy because of the EasyNet Player API. In our Server Packet Listener we will add the following code to extract the message aswell as player object and a few player infos out of the server packet the we got.

```java

    String playername = EasyNet.getPlayerName(player);
    Float playerhealth = EasyNet.getPlayerHealth(player);
    String message = playername+" has "+playerhealth.toString()+" Health and sent the following message via EasyNet: "+data;

```

This works because we can use the data argument to receive the extra data that we passed in the sendPacket argument. In our case the variable message from our command. We also receive the player who sent the packet as a bonus without extra code from EasyNet API.
Next up, lets send the message back to our original player, by sending a S2C packet. To do so, we just add this simple line under our variables on the server.

```java

    EasyNet.sendPacketToClient(player, "response_packet", message);

```

We defined our player, the id of the packet for the packet listeners on the client, and our extra context, wich is the custom message that we crafted.
Lets output this message in the client's chat by running this code in our client packet listener.

```java

    MinecraftClient.getInstance().player.sendMessage(Text.literal("Server responded with the following: " + message), false);       

```

Great job, you now have a working system for communicating with the server and client and using the advanced EasyNet API protocol to receive player data.

---





          

## 📘 EasyNet API Documentation

**Version: Fabric 1.21.0 | EasyNet v1.0.15\:dev**
A high-level networking abstraction layer that simplifies client-server communication using NBT or raw `String` data for mod developers.

---

## 🧠 Core Networking APIs

---

### Server-Side

---

* **`registerServerPacketHandler(String packetName, BiConsumer<ServerPlayerEntity, String> handler)`**
  Register a handler for packets sent from a client.

  **Parameters:**

  * `packetName`: Unique identifier for the packet.
  * `handler`: Callback executed when the server receives this packet from a client.

  **Returns:** `void`

  **Example:**

  ```java
  EasyNet.registerServerPacketHandler("chat_message", (player, data) -> {
      LOGGER.info("Received message: " + data + " from " + EasyNet.getPlayerName(player));
  });
  ```

---

* **`sendPacketToClient(ServerPlayerEntity player, String packetName, String content)`**
  Send a packet from the server to a specific client.

  **Parameters:**

  * `player`: Target player to send the packet to.
  * `packetName`: Packet identifier.
  * `content`: A `String` payload.

  **Returns:** `void`

  **Example:**

  ```java
  EasyNet.sendPacketToClient(player, "welcome_packet", "Hello, " + EasyNet.getPlayerName(player));
  ```

---

### Client-Side

---

* **`registerClientPacketHandler(String packetName, Consumer<String> handler)`**
  Register a client-side handler for packets sent from the server.

  **Parameters:**

  * `packetName`: Packet identifier to listen for.
  * `handler`: Callback to process the incoming message.

  **Returns:** `void`

  **Example:**

  ```java
  EasyNet.registerClientPacketHandler("welcome_packet", (data) -> {
      MinecraftClient.getInstance().player.sendMessage(Text.literal("Server says: " + data), false);
  });
  ```

---

* **`sendPacket(String packetName, String content)`**
  Send a packet from the client to the server.

  **Parameters:**

  * `packetName`: Unique identifier for the packet.
  * `content`: Payload to send, as a `String`.

  **Returns:** `void`

  **Example:**

  ```java
  EasyNet.sendPacket("chat_message", "Hi from the client!");
  ```

---

## 👤 Player Info APIs

---

* **`getPlayerName(ServerPlayerEntity player)`**
  Get the in-game name of the player.

  **Parameters:**

  * `player`: Player entity to query.

  **Returns:** `String`

  **Example:**

  ```java
  String name = EasyNet.getPlayerName(player);  // e.g., "Steve"
  ```

---

* **`getPlayerHealth(ServerPlayerEntity player)`**
  Retrieve the player's current health.

  **Parameters:**

  * `player`: Player entity to query.

  **Returns:** `float`

  **Example:**

  ```java
  float health = EasyNet.getPlayerHealth(player);  // e.g., 20.0
  ```

---

* **`getPlayerUuid(ServerPlayerEntity player)`**
  Get the player’s UUID as a string.

  **Parameters:**

  * `player`: Player entity to query.

  **Returns:** `String`

  **Example:**

  ```java
  String uuid = EasyNet.getPlayerUuid(player);  // e.g., "f84c6a79-1234-4d9b-b9c4-abcdef123456"
  ```

---

* **`isPlayerOnline(String uuid)`**
  Check if a player with the given UUID is currently online.

  **Parameters:**

  * `uuid`: UUID of the player, as a string.

  **Returns:** `boolean`

  **Example:**

  ```java
  boolean online = EasyNet.isPlayerOnline("f84c6a79-1234-4d9b-b9c4-abcdef123456");
  ```

---

*Note:* EasyNet does not wrap player position directly, but you can access it using standard Fabric/Minecraft API:

```java
double x = player.getX();
double y = player.getY();
double z = player.getZ();
```

---

## 🧪 Usage Examples

---

### Example 1: Client Sends Message to Server

#### Client

```java
EasyNet.sendPacket("chat_input", "Hello server!");
```

#### Server

```java
EasyNet.registerServerPacketHandler("chat_input", (player, msg) -> {
    LOGGER.info("[" + EasyNet.getPlayerName(player) + "]: " + msg);
});
```

---

### Example 2: Server Replies to Client with Status Info

#### Server

```java
EasyNet.registerServerPacketHandler("whoami", (player, msg) -> {
    String name = EasyNet.getPlayerName(player);
    float health = EasyNet.getPlayerHealth(player);
    String uuid = EasyNet.getPlayerUuid(player);
    boolean online = EasyNet.isPlayerOnline(uuid);

    double x = player.getX();
    double y = player.getY();
    double z = player.getZ();

    String response = String.format(
        "Name: %s\nUUID: %s\nHealth: %.1f\nOnline: %s\nPosition: (%.1f, %.1f, %.1f)",
        name, uuid, health, online ? "Yes" : "No", x, y, z
    );

    EasyNet.sendPacketToClient(player, "whoami_response", response);
});
```

#### Client

```java
EasyNet.sendPacket("whoami", "");

EasyNet.registerClientPacketHandler("whoami_response", (data) -> {
    MinecraftClient.getInstance().player.sendMessage(Text.literal(data), false);
});
```

---

### Example 3: Client Command That Sends Data

```java
ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
    dispatcher.register(ClientCommandManager.literal("shout")
        .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
            .executes(context -> {
                String msg = StringArgumentType.getString(context, "message");
                EasyNet.sendPacket("chat_broadcast", msg);
                return 1;
            }))
    );
});
```

---

### Example 4: Server Broadcast to All Players

```java
EasyNet.registerServerPacketHandler("chat_broadcast", (player, msg) -> {
    String finalMsg = EasyNet.getPlayerName(player) + ": " + msg;
    for (ServerPlayerEntity target : player.server.getPlayerManager().getPlayerList()) {
        EasyNet.sendPacketToClient(target, "broadcast_chat", finalMsg);
    }
});
```

```java
EasyNet.registerClientPacketHandler("broadcast_chat", (data) -> {
    MinecraftClient.getInstance().player.sendMessage(Text.literal(data), false);
});
```

---

## Best Practices

1. **Packet Naming:**

   * Use descriptive, unique packet names
   * Consider using names that include your mod to prevent issues.
   * Use `snake_case` for consistency

2. **Data Structure:**

   * Keep Data packets small and organized
   * Use only the data you must have, not could need.
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
*Version:* 1.0.15:dev
*Since:* Minecraft 1.21.0 / Fabric
