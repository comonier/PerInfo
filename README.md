🛡️ PerInfo v1.2

Advanced Permission Lookup, Item Inspection, and Interactive Chat Symbols.

PerInfo is a lightweight but powerful utility plugin for Spigot/Paper servers (optimized for 1.21+) that allows players to share items, inventories, and statistics in chat with full interactive support (Click & Hover), even on servers with aggressive chat management like VentureChat.

🚀 Key Features

Independent Chat Engine: Overrides modern chat plugins to ensure [hand], [inv], and [ec] always work with Click/Hover events.

Interactive Symbols:

[hand]: Shows item in hand with a detailed hover (Lore & Enchants) and technical info on click.
[inv]: Interactive button to view player's inventory.
[ec]: Interactive button to view player's Ender Chest.
[money] & [playtime]: Real-time stats injection.
Permission & Plugin Lookup: Instantly find which plugin owns a command and copy its permission node with one click.
Technical Inspection: /iinfo command to see IDs, NBT data, and Custom Model Data.

🛠️ Technical Implementation
Unlike older versions, v1.2 uses a Low-Priority Interceptor logic:
Capture: It listens at EventPriority.LOWEST to grab the message before other plugins.
Cancel: It cancels the original AsyncPlayerChatEvent to prevent duplicated or "plain-text" messages.
Reconstruct: It rebuilds the entire message using the BungeeCord Chat API (JSON Components) to inject ClickEvent and HoverEvent that actually work on Minecraft 1.21.
📋 Commands & Permissions
Command	Description	Permission
/perinfo <cmd>	Lookup plugin/permission for a command	perinfo.use
/iinfo	 Show technical details of held item	perinfo.iinfo
/per     List available chat symbols	perinfo.chat
/perinfo reload	Reload config and messages	perinfo.admin

⚙️ Requirements
Java 21
Spigot/Paper 1.21+
Vault (Optional, for [money] support)

📦 Installation
Download the PerInfo.jar from the Releases page.
Drop it into your /plugins/ folder.
Restart your server (recommended over /reload).
Configure your language in config.yml.
Developed with ☕ and persistence by Comonier.