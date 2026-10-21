# Techmines Plus

Client-side Fabric mod for `techmines.minehut.gg`.

## Download and install

Latest public version: **0.10.2**. Choose the jar for your Minecraft version:

- [Minecraft 26.3: techmines-plus-0.10.2.jar](https://github.com/waboolio/techmines-plus/releases/download/v0.10.2/techmines-plus-0.10.2.jar)
- [Minecraft 26.2: techmines-plus-0.10.2+mc26.2.jar](https://github.com/waboolio/techmines-plus/releases/download/v0.10.2/techmines-plus-0.10.2%2Bmc26.2.jar)

Use these release attachments for counted downloads. Do not install the source-code ZIP.
Browse all versions on the [Releases page](https://github.com/waboolio/techmines-plus/releases).

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

The inventory shortcuts are arranged around the existing slots:

- Compact row: Wardrobe `/wardrobe`, Ender Chest `/ec`, Vaults `/pv 1` through `/pv 7`, Accessory Bag `/bag`, Auction House `/ah`.
- Left of the equipment slots: Elytra `/fly` aligned with the chestplate, and Heal `/heal` below it.
- Top bar: Overclock `/oc` and PvP toggle `/pvp`, beside Vendor Info.
- Right of the hotbar: Trash `/trash`.

Heal uses a glistering melon icon. Bag replaces Fly's previous compact-row position.
Overclock replaces the previous top-bar Trash button. The K menu has the same
commands in two rows, including all three new shortcuts.

Accessory Bag uses Minecraft's blue bundle icon immediately. Open **Overclocks**
once to copy its exact server item appearance. That display stack is cached
locally in `minecraft/config/techmines-plus/menu-icons.json`; blue lapis is shown
until captured. No menu items are clicked or moved.

Press **K** for the alternate menu. Rebind under Controls > Key Binds > Techmines Plus.
The inventory heading reads **Techmines Plus** using Minecraft's built-in font.
Back and Escape return to the previous menu. Server permissions and cooldowns apply.
Features activate through `techmines.minehut.gg` or `techmines.minehut.gg:25565`.
No server installation or access to private server files is needed.

## Vendor Info

Open **Vendor Info**, choose a world, then a mine. Browse resource costs for
swords, the relevant mining tool, armour per piece, talismans and elytras.
Previous/next mine buttons and paginated world/mine lists support browsing.

Version **0.10.2** includes the latest spreadsheet snapshot across **11 worlds and
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

Both 0.10.2 builds compile successfully and their bundled vendor data/version
metadata were checked. Totals were checked against all 99 spreadsheet pages,
including missing prices and four-piece armour sets. The refreshed pages still need an in-game appearance check.
Report issues with mod/Minecraft/Fabric versions, reproduction steps, screenshots
and relevant console errors at [GitHub Issues](https://github.com/waboolio/techmines-plus/issues).
Remove account tokens and personal information from shared logs.

## Build from source

Use a Java 25 JDK. Run `gradlew.bat build` on Windows or `./gradlew build` on Linux/macOS.
The main source targets Minecraft 26.3 and produces `build/libs/techmines-plus-0.10.2.jar`.
See the compatibility instructions for the separate 26.2 variant.

Unofficial community project; not affiliated with Mojang, Microsoft, Minehut or the Techmines server owners.

Recorded additional starter-tool costs, such as the Wood mine pickaxe, are also
included. The importer stores these under optional `additionalTotalCosts`.

## Download counts and publishing

GitHub counts downloads of each Release attachment separately. Counts represent
download requests, not unique players. Earlier repository-file downloads cannot
be reconstructed. Repository-file downloads and clones are not counted as
Release-attachment downloads.

When a new public version and its matching 26.3/26.2 jars are committed to main,
the Publish public release workflow creates vVERSION and attaches both jars.
Optional release notes go in release-notes/VERSION.md. Existing attachments are
never overwritten, preserving their counters. Always use a new version for
changed jars. The workflow can also be run manually in Actions.

## World button design (0.10.2)

Vendor Info world buttons now show warp-themed block icons, colored accents and
clear world names. All 11 worlds and pagination remain available. Vanilla items
are used for the icons; the custom green/gold server artwork is approximated
with a melon block. Vendor prices and totals are unchanged.

## Joining through Minehut

You can join Techmines directly or through the Minehut lobby. On Minehut proxy
addresses the mod recognizes the visible Techmines sidebar title or website.
It checks the current sidebar without remembering a prior server, so returning
to a lobby or switching to another server disables the features.
