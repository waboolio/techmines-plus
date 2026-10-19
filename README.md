# Techmines Plus

Client-side Fabric mod for `techmines.minehut.gg`.

## Download and install

Latest public version: **0.8.0**. Choose the jar for your Minecraft version:

- [Minecraft 26.3: techmines-plus-0.8.0.jar](downloads/techmines-plus-0.8.0.jar)
- [Minecraft 26.2: techmines-plus-0.8.0+mc26.2.jar](downloads/techmines-plus-0.8.0+mc26.2.jar)

Open the file on GitHub and use the download button. Do not install a sources jar.

1. In Prism Launcher, select your instance and click **Edit**.
2. Under **Version**, install **Fabric Loader 0.19.5 or newer**.
3. Under **Mods**, add the matching Techmines Plus jar and Fabric API:
   - Minecraft 26.3: **Fabric API 0.163.0+26.3**.
   - Minecraft 26.2: **Fabric API 0.161.0+26.2**.
4. Disable older Techmines jars; keep only one edition and version active.
5. Use **Java 25**, launch, and join `techmines.minehut.gg`.

Get Fabric API from [Modrinth](https://modrinth.com/mod/fabric-api/versions).
Having Fabric API under Mods alone does not install Fabric Loader.
See [26.2 compatibility instructions](MINECRAFT-26.2.md).

## Features

Five compact inventory buttons replace the unused recipe-book button; Trash and PvP sit beside Vendor Info above the inventory:

- Armor stand: `/wardrobe`.
- Ender chest: `/ec`.
- Chest: numbered, coloured vault picker for `/pv 1` through `/pv 7`.
- Elytra: `/fly`.
- Emerald: auction house `/ah`.
- Hopper: trash UI `/trash`.
- Iron sword: toggle PvP mode `/pvp`.

Press **K** for the alternate menu. Rebind under Controls > Key Binds > Techmines Plus.
The inventory heading reads **Techmines Plus** using Minecraft's built-in font.
Back and Escape return to the previous menu. Server permissions and cooldowns apply.
Features activate through `techmines.minehut.gg` or `techmines.minehut.gg:25565`.
No server installation or access to private server files is needed.

## Vendor Info

Open **Vendor Info**, choose a world, then a mine. Browse resource costs for
swords, the relevant mining tool, armour per piece, talismans and elytras.
Previous/next mine buttons and paginated world/mine lists support browsing.

Version **0.8.0** includes the latest spreadsheet snapshot across **11 worlds and
99 mine/boss pages**, including Acidic Lakes, Steampunk, Sakura Forest, Wild West
and Planet X. Confirmed spreadsheet entries are shown without a prefix. Blue and
orange spreadsheet estimates carry a cyan **~** prefix. Excel formulas alone do
not mark a confirmed price as estimated.

Missing prices remain unrecorded, including unknown boss upgrades and unavailable
armour IV/V tiers. Prices are a snapshot; verify estimates against server vendors.
Astralium Sword I is retained as entered (1.16B); its page flags that the pickaxe
and talisman imply a base near 5.154B. The Bulbasaur talisman unit is still unconfirmed.
See [vendor data instructions](VENDOR-DATA.md) to update the catalogue.

Mine 80 (Pixel Grove, Charmander / Firestone) now includes the missing sword,
pickaxe and armour estimates. Confirmed starting prices remain unchanged.

## Resource totals

Each mine shows its total directly below Talisman and Elytra. It includes all five
sword tiers, all five mining-tool tiers, four armour pieces at each tier I-III,
the talisman and the elytra. Armour IV/V is not counted. These are cumulative
purchase costs, not just the final upgrade cost.

The bundled catalogue preserves full numeric spreadsheet prices for totals. A `~`
marks estimates. Missing or unconfirmed prices produce a **Recorded subtotal**
instead of a full total; pages with no known prices show **Total: unavailable**.
The total inherits any estimate marker in its included prices.

## Testing and bug reports

Both 0.8.0 builds compile successfully and their bundled vendor data/version
metadata were checked. Totals were checked against all 99 spreadsheet pages,
including missing prices and four-piece armour sets. The refreshed pages still need an in-game appearance check.
Report issues with mod/Minecraft/Fabric versions, reproduction steps, screenshots
and relevant console errors at [GitHub Issues](https://github.com/waboolio/techmines-plus/issues).
Remove account tokens and personal information from shared logs.

## Build from source

Use a Java 25 JDK. Run `gradlew.bat build` on Windows or `./gradlew build` on Linux/macOS.
The main source targets Minecraft 26.3 and produces `build/libs/techmines-plus-0.8.0.jar`.
See the compatibility instructions for the separate 26.2 variant.

Unofficial community project; not affiliated with Mojang, Microsoft, Minehut or the Techmines server owners.

Recorded additional starter-tool costs, such as the Wood mine pickaxe, are also
included. The importer stores these under optional `additionalTotalCosts`.
