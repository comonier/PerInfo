# 🛠️ PerInfo

**PerInfo** is a robust utility plugin developed for Minecraft **1.21.1**. It was designed to provide total transparency regarding commands and permissions, while transforming the chat experience through dynamic, real-time sharing of items and player status.

---

### 📚 Core Purpose

This plugin addresses three main pain points for modern server administrators:
1. **Command Tracking:** Instantly identifies which plugin owns a command and what permission is required to use it.
2. **Item Showcasing:** Allows players to display their items with enchantments and metadata in chat using interactive components.
3. **Chat Synchronization:** Built to coexist with complex systems like *VentureChat*, ensuring links and hovers work even in highly modified environments.

---

### ✨ Key Features

*   🔍 **Command Tracker:** Find the source of any command (Vanilla or Plugins) with `/perinfo`.
*   📦 **Item Metadata:** Deep inspection of held items, including internal ID, Custom Model Data, and Lore.
*   💬 **Dynamic Symbols (@):** Automatic replacement of terms in chat with interactive components:
    *   `@hand`: Link to the held item (includes text hover for enchantments on 1.21.1).
    *   `@inv` & `@ec`: Generates clickable links to open a live "snapshot" of the player's inventory or Ender Chest.
    *   `@money` & `@playtime`: Displays economy balance and total playtime.
*   📋 **Click-to-Copy:** Underlined permissions and item IDs in chat can be copied with a single click.

---

### 💻 Command List


| Command | Description | Usage |
| :--- | :--- | :--- |
| `/perinfo` | Shows plugin info and commands | `/perinfo` |
| `/perinfo {cmd}` | Searches for specific command details | `/perinfo fly` |
| `/perinfo list {pl}` | Lists all commands from a specific plugin | `/perinfo list Essentials` |
| `/perinfo reload` | Reloads configurations and messages | `/perinfo reload` |
| `/iinfo` | Technically inspects the held item | `/iinfo` |
| `/simbolos` | Quick tutorial on how to use `@` symbols | `/simbolos` |

---

### 🔐 Permission List


| Permission | Description | Default |
| :--- | :--- | :--- |
| `perinfo.use` | Allows using the main command and lookups | `OP` |
| `perinfo.iinfo` | Allows inspecting item metadata | `OP` |
| `perinfo.chat` | Allows using `@` symbols in chat | `Everyone (true)` |

---

### ⚠️ Final Considerations & Setup

To ensure the best performance on your hosting (OVH or similar):

1. **Economy Dependency:** The `@money` symbol requires the **Vault** plugin and an active economy provider (e.g., EssentialsX).
2. **Chat Priority:** PerInfo is configured with `LOWEST` chat priority to ensure links work alongside VentureChat. Avoid chat plugins that aggressively strip JSON formatting.
3. **Java Version:** This plugin uses modern features and requires **Java 21 or higher**.
4. **Cache Management:** Inventories opened via `@inv` are automatically cleared every 10 minutes to prevent excessive RAM consumption.

---
**Developed by [Comonier](https://github.com/comonier/PerInfo)**  
*Project #5 - Utility Suite*
