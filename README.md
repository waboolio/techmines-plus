# Techmines Plus

A client-side Fabric mod adding command shortcuts for the Techmines Minecraft server.
Server address: `techmines.minehut.gg`.

## Download and install

Download **techmines-plus-0.3.0.jar** from [the tester downloads folder](downloads/).
Open the file on GitHub, then use the download button. Do not download the sources jar.

1. In Prism Launcher, select your Minecraft **26.3** instance and click **Edit**.
2. In **Version**, install **Fabric Loader 0.19.5 or newer**. Having Fabric API in Mods alone is insufficient.
3. In **Mods**, add Techmines Plus and **Fabric API 0.163.0+26.3**.
4. Disable any older Techmines Plus jar; keep only one active version.
5. Use **Java 25**, launch, and connect to `techmines.minehut.gg`.

Fabric API is available from https://modrinth.com/mod/fabric-api/versions.

## Features

Three item buttons beside the inventory recipe book:
- Armor stand: `/wardrobe`
- Ender chest: `/ec`
- Chest: opens a coloured, numbered picker for `/pv 1` through `/pv 7`

Press **K** for an alternate shortcut menu. Rebind under Controls > Key Binds > Techmines Plus.
The inventory heading reads **Techmines Plus**. Back and Escape return to the previous menu.

Each click sends one command. Server permissions and cooldowns still apply.
Shortcuts activate only when connecting through `techmines.minehut.gg` or `techmines.minehut.gg:25565`.
Joining through the general Minehut lobby or another address does not activate them.
No server installation or server files are needed.

## Tester builds

The public build uses Minecraft's built-in font rather than the personal-use Pixel Game artwork.
The previous 0.2.4 interface was tested in-game. Version 0.2.5 compiles successfully;
its replacement heading still needs an in-game check.

Please report bugs at https://github.com/waboolio/techmines-plus/issues.
Include the mod version, Minecraft/Fabric versions, steps to reproduce, a screenshot,
and the relevant console error. Remove account tokens and personal information from logs.
Check inventory placement with the recipe book open and closed and test Back/Escape navigation.

## Build from source

Requires a Java 25 JDK. On Windows run `gradlew.bat build`; on Linux/macOS run `./gradlew build`.
The installable jar is written to `build/libs/techmines-plus-0.3.0.jar`.
Minecraft and Fabric dependencies are downloaded by Gradle and are not included here.

Unofficial community project; not affiliated with Mojang, Microsoft, Minehut, or the Techmines server owners.

## Vendor Info (0.3.0)

Click **Vendor Info** above the inventory panel or in the K menu.
Choose a world, then a mine. Each mine page shows its resource, tiers I-V for
sword/tool/armour (per piece), and separate talisman and elytra costs.
Use the previous/next mine buttons to browse and Back/Escape to return.
The world and mine lists paginate when needed.

All 11 named worlds are listed. The supplied workbook contains eight Pixel Grove
mines and partial data for eight Planet X mines. Other worlds display a no-data
message. Blank cells are not zero costs. The Bulbasaur talisman price has no unit
in the workbook and is flagged; the Astralium elytra cell is a label rather than
a price and is marked unconfirmed. Empty Boss templates are not included.
Prices are a workbook snapshot; verify them against current server vendors.

See [vendor data instructions](VENDOR-DATA.md) to extend the catalogue.
0.3.0 builds successfully; the new menus still need an in-game check.

