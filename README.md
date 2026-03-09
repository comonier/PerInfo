# 🛡️ PerInfo v1.2

**Advanced Permission Lookup, Item Inspection, and Interactive Chat Symbols.**

O **PerInfo** é um utilitário leve e poderoso para servidores Spigot/Paper (otimizado para 1.21+) que permite aos jogadores compartilhar itens, inventários e estatísticas no chat com suporte interativo total (Click & Hover), mesmo em servidores com gerenciamento de chat agressivo, como o VentureChat.

---

## 🚀 Principais Funcionalidades

*   **Motor de Chat Independente:** Sobrescreve plugins de chat modernos para garantir que tags como `[hand]`, `[inv]` e `[ec]` sempre funcionem com eventos de Clique/Hover.
*   **Símbolos Interativos:**
    *   `[hand]`: Mostra o item na mão com hover detalhado (Lore & Enchants) e informações técnicas ao clicar.
    *   `[inv]`: Botão interativo para visualizar o inventário do jogador.
    *   `[ec]`: Botão interativo para visualizar o Ender Chest do jogador.
    *   `[money]` & `[playtime]`: Injeção de estatísticas em tempo real.
*   **Busca de Permissões e Plugins:** Descubra instantaneamente qual plugin possui um comando e copie a permissão necessária com um clique.
*   **Inspeção Técnica:** Comando `/iinfo` para visualizar IDs, dados NBT e Custom Model Data.

---

## 🛠️ Implementação Técnica

Diferente de versões anteriores, a v1.2 utiliza uma lógica de **Interceptação de Baixa Prioridade**:

1.  **Captura:** Ouve em `EventPriority.LOWEST` para capturar a mensagem antes de outros plugins.
2.  **Cancelamento:** Cancela o `AsyncPlayerChatEvent` original para evitar mensagens duplicadas ou em texto simples.
3.  **Reconstrução:** Reconstrói a mensagem completa usando a **BungeeCord Chat API (JSON Components)** para injetar `ClickEvent` e `HoverEvent` funcionais no Minecraft 1.21.

---

## 📋 Comandos e Permissões


| Comando | Descrição | Permissão |
| :--- | :--- | :--- |
| `/perinfo <cmd>` | Busca plugin/permissão de um comando | `perinfo.use` |
| `/iinfo` | Mostra detalhes técnicos do item na mão | `perinfo.iinfo` |
| `/per` | Lista os símbolos de chat disponíveis | `perinfo.chat` |
| `/perinfo reload` | Recarrega a config e as mensagens | `perinfo.admin` |

---

## ⚙️ Requisitos

*   **Java 21**
*   **Spigot/Paper 1.21+**
*   **Vault** (Opcional, necessário para suporte ao `[money]`)

---

## 📦 Instalação

1.  Baixe o `PerInfo.jar` na página de Releases.
2.  Coloque-o na sua pasta `/plugins/`.
3.  Reinicie seu servidor (recomendado em vez de `/reload`).
4.  Configure seu idioma no arquivo `config.yml`.

---
Desenvolvido com ☕ e persistência por **Comonier**.
