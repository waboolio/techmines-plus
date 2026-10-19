# Minecraft 26.2 compatibility

Use downloads/techmines-plus-0.7.1+mc26.2.jar for 26.2.
Keep using downloads/techmines-plus-0.7.1.jar for 26.3.
Install one Techmines Plus jar at a time.

26.2 requires Java 25, Fabric Loader 0.19.5+, and Fabric API 0.161.0+26.2.
The API jar must match the Minecraft version. Prism must have Fabric Loader
installed under Edit > Version as well as Fabric API under Mods.

The separate 26.2 build is necessary because the key-input enum changed from
KEYSYM in 26.2 to KEYBOARD in 26.3. No unsupported version range is declared.
Both builds compile. The inventory mixin target fields and label hook were
checked against 26.2. Live testing is still required on a 26.2 instance.

## Building the 26.2 variant

In a separate copy of this project:
1. In gradle.properties, set minecraft_version=26.2,
   fabric_api_version=0.161.0+26.2, version=0.7.1+mc26.2.
2. In src/main/resources/fabric.mod.json, set the minecraft dependency to 26.2.
3. Replace src/client/java/gg/techmines/plus/TechminesPlus.java with the variant
   at compatibility/26.2/TechminesPlus.java.
4. Run gradlew.bat build using a Java 25 JDK.

The main branch's original files continue to target Minecraft 26.3.

