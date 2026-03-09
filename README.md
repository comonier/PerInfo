# 🛡️ PerInfo v1.2

**Advanced Permission Lookup, Item Inspection, and Interactive Chat Symbols.**

PerInfo is a lightweight but powerful utility plugin for Spigot/Paper servers (optimized for 1.21+) that allows players to share items, inventories, and statistics in chat with full interactive support (Click & Hover), even on servers with aggressive chat management like VentureChat.

---

## 🚀 **Key Features**

**Independent Chat Engine**: Overrides modern chat plugins to ensure [hand], [inv], and [ec] always work with Click/Hover events.<br>
**Interactive Symbols**:<br>
**[hand]**: Shows item in hand with a detailed hover (Lore & Enchants) and technical info on click.<br>
**[inv]**: Interactive button to view player's inventory.<br>
**[ec]**: Interactive button to view player's Ender Chest.<br>
**[money]** & **[playtime]**: Real-time stats injection.<br>
**Permission & Plugin Lookup**: Instantly find which plugin owns a command and copy its permission node with one click.<br>
**Technical Inspection**: /iinfo command to see IDs, NBT data, and Custom Model Data.<br>

---

## 🛠️ **Technical Implementation**

Unlike older versions, v1.2 uses a Low-Priority Interceptor logic:<br>
**Capture**: It listens at EventPriority.LOWEST to grab the message before other plugins.<br>
**Cancel**: It cancels the original AsyncPlayerChatEvent to prevent duplicated or "plain-text" messages.<br>
**Reconstruct**: It rebuilds the entire message using the BungeeCord Chat API (JSON Components) to inject ClickEvent and HoverEvent that actually work on Minecraft 1.21.<br>

---

## 📋 **Commands & Permissions**


| Command | Description | Permission |
| :--- | :--- | :--- |
| `/perinfo <cmd>` | Lookup plugin/permission for a command | `perinfo.use` |
| `/iinfo` | Show technical details of held item | `perinfo.iinfo` |
| `/per` | List available chat symbols | `perinfo.chat` |
| `/perinfo reload` | Reload config and messages | `perinfo.admin` |

---

## ⚙️ **Requirements**

**Java 21**<br>
**Spigot/Paper 1.21+**<br>
**Vault** (Optional, for [money] support)<br>

---

## 📦 **Installation**

Download the **PerInfo.jar** from the Releases page.<br>
Drop it into your **/plugins/** folder.<br>
Restart your server (recommended over /reload).<br>
Configure your language in **config.yml**.<br>

---
Developed with ☕ and persistence by **Comonier**.
