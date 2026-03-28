# NekoJS Addon Example

Adds CSV (Comma-Separated Values) Parse/Read/Write support for KubeJS. This mod has no functionality for end users (players).

CSV files is popular in professional game development, and also can be more efficient when you want to do a lot of modifications than writing KubeJS script for each of them (e.g. when you need to add a lot of new blocks/items).

This mod is needed because KubeJS forbids access to java.io/nio, and only allows reading JSON formatted files. KubeJS scripters can't write a script to read CSV files without this mod unless using malicious dark magic to break KubeJS's IO limitation.

See the Usage Document for example usages.

---

#### Credits & Licensing

This project is a port of [KubeJS-CSV](https://www.curseforge.com/minecraft/mc-mods/kubejs-csv) by [ChloePrime](https://github.com/ChloePrime) to the NekoJS platform.

* **Original Project:** [ChloePrime/KubeJS-CSV](https://github.com/ChloePrime/KubeJS-CSV)
* **License:** The original code is licensed under the [MIT License](https://github.com/ChloePrime/KubeJS-CSV/blob/master/LICENSE).