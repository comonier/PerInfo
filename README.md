# PerInfo 🛠️

A robust utility plugin for Minecraft **1.21.1** designed for players and server owners to inspect command permissions, item metadata, and share live data in chat using dynamic symbols.

## 🚀 Features

- **Command Inspection (`/perinfo`):** Identify the source plugin, version, and required permission for any command.
- **Item Deep Search (`/iinfo`):** View technical details like NamespacedKeys, Lore, Enchants, and Custom Model Data.
- **Chat Symbols (@):** Share live data directly in chat messages:
  - `@hand`: Shows the item in your main hand (with hover tooltip).
  - `@inv`: Displays a summary of your inventory.
  - `@ec`: Displays a summary of your Ender Chest.
  - `@money`: Shows your current balance (requires Vault).
  - `@playtime`: Shows your total time played on the server.
- **Click-to-Copy:** Permissions and Item IDs in chat are underlined; click them to copy instantly to your clipboard.
- **Multi-language Support:** Built-in support for English (**en**) and Portuguese (**pt**).
- **Flexible Permissions:** Toggle between `default` (public access) or `true` (restricted) for every feature via `config.yml`.

## 🛠️ Requirements

- **Java:** 25
- **Platform:** Spigot / Paper 1.21.1
- **Dependency:** [Vault](https://www.spigotmc.org/resources/vault.34315/) (Optional, for @money support).

## 💻 Commands & Permissions


| Command | Description | Permission Node |
| :--- | :--- | :--- |
| `/perinfo <cmd>` | Check info about a command | `perinfo.use` |
| `/perinfo list <pl>`| List all commands from a plugin | `perinfo.use` |
| `/perinfo reload` | Reload config and messages | `perinfo.use` (OP) |
| `/iinfo` | Inspect item in hand | `perinfo.iinfo` |
| `/simbolos` | Show tutorial for @ symbols | (Public) |
| `@symbol` | Use symbols in chat | `perinfo.chat` |

## ⚙️ Configuration

The `config.yml` allows you to define the access level for each feature:
- `permission-mode: default` -> Everyone can use it.
- `permission-mode: true` -> Requires the specific permission node (e.g., via LuckPerms).

---
**Developed by [Comonier](https://github.com/comonier/)**  
*Project #5 - Utility Suite*
