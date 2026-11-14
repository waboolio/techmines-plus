# Reforge catalogue

The Reforges tab in the user-maintained Techmines.xlsx records 21 reforges across eight rarity tiers. Each row contains the reforge, supported item types, tier letter/colour, one or two signed percentage bonuses and screenshot source. Blank second bonuses are absent, not zero. Swift and Itchy include penalties.

Import confirmed workbook rows with:

    python tools/import_reforges.py path/to/Techmines.xlsx src/main/resources/assets/techmines_plus/data/reforges.json

The sheet stores percentages as Excel fractions. The importer converts them into percentage points for the tooltip matcher. The workbook and screenshots are not published.

The mod reads modern PublicBukkitValues reforge keys or the legacy reforge tag. It uses complete bonus matches only when explicit rarity metadata is absent. It does not infer reforge rarity from the item colour, base stats or name alone. No item components are changed.
