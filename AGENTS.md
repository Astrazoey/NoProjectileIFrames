# Multiloader Template

A template for creating Fabric + NeoForge multiloader Minecraft mods for MC 26.2.

## Project Structure

```
├── common/          # All game logic, mixins, config (platform-agnostic)
├── fabric/          # Fabric entrypoint + Fabric API usage
├── neoforge/        # NeoForge entrypoint + NeoForge API usage
└── build-logic/     # Gradle convention plugins (don't edit unless changing build)
```

## How It Works

- **common** compiles against NeoForm (vanilla Minecraft, Mojang mappings, no loader). All game logic, mixins targeting vanilla classes, and config live here.
- **fabric** applies Fabric Loom, includes common sources in its compilation. Entry point: `ExampleModFabric` implements `ModInitializer` → calls `CommonClass.init()`.
- **neoforge** applies ModDevGradle, includes common sources the same way. Entry point: `ExampleModNeoForge` with `@Mod` → calls `CommonClass.init()` inside `RegisterEvent`.
- Common code is compiled into each loader JAR via source inclusion (not shadowJar).

## Getting Started

1. **Rename the project**: Change `rootProject.name` in `settings.gradle`, and update `mod_id`, `mod_name`, `mod_author`, `group` in `gradle.properties`.
2. **Rename the package**: Change `com.example.examplemod` to your package in ALL Java files and the mixin config `package` field.
3. **Add your config fields**: Edit `ExampleConfig.java` — add fields with `@SerializedName`, getters, and bump `CURRENT_VERSION` when changing defaults.
4. **Add your mixins**: Create mixins in `common/.../mixin/` and register them in `examplemod.mixins.json`.
5. **Add your logic**: Put game logic in `CommonClass.init()` or helper classes in common.

## Patterns

### Config

`ExampleConfig` uses Gson JSON with `config_version` tracking. If the loaded version is lower than `CURRENT_VERSION`, the config regenerates with defaults (unless `enable_config_updates` is false).

### Platform Abstraction

`IPlatformHelper` + `Services` (ServiceLoader) provides the config directory. Each loader module has its own implementation registered in `META-INF/services/`.

### Loader-Specific Mixins

When a mixin targets NeoForge-patched methods or client-only classes:
- **Shared logic** → helper class in common (e.g., `TankHelper`, `QuickStepHelper`)
- **Fabric mixin** → `fabric/.../mixin/` or `fabric/src/client/java/.../mixin/`
- **NeoForge mixin** → `neoforge/.../mixin/` or NeoForge event listener
- Each mixin is a thin delegate calling the common helper

### NeoForge Events vs Fabric Callbacks

| Fabric API | NeoForge Equivalent |
|---|---|
| `CreativeModeTabEvents` | `BuildCreativeModeTabContentsEvent` (mod bus) |
| `LootTableEvents.MODIFY` | `LootTableLoadEvent` (game bus) |
| `UseBlockCallback` | `PlayerInteractEvent.RightClickBlock` (game bus) |
| `PlayerPickItemEvents` | Mixin to `BlockStateBase.getCloneItemStack` |
| `EnchantmentEvents.MODIFY_WITH_LOOKUP` | Mixin to `Enchantment.getMaxLevel`/`getWeight`/`areCompatible` |
| `DefaultItemComponentEvents.MODIFY` | Mixin to `ItemStack.isEnchantable` |
| `PayloadTypeRegistry` + `ServerPlayNetworking` | `RegisterPayloadHandlersEvent` + `PlayerLoggedInEvent` |

### Common Pitfalls

- **NeoForge mod IDs**: No hyphens — use `snake_case` (e.g., `my_mod` not `my-mod`).
- **NeoForge patched methods**: `@Redirect` on INVOKE often breaks. Use `@Inject` at HEAD/RETURN/TAIL or NeoForge events.
- **`hurtServer` cancellation**: Corrupts NeoForge's `DamageContainer` stack. Use `LivingIncomingDamageEvent` instead.
- **Client classes in common**: NeoForm is server-only — `LocalPlayer`, render layers, etc. don't exist. Put client mixins in Fabric client source set or NeoForge main source set.
- **ItemStack overrides**: NeoForge may override methods (e.g., `getMaxStackSize`) bypassing interface mixins. Use dual mixins (interface + class with `require = 0`).

## Version Management

All versions are in `gradle.properties`. The `processResources` task expands `${...}` placeholders into `fabric.mod.json` and `neoforge.mods.toml`.

To add a new placeholder: add the property to `gradle.properties`, then add it to the `expandProps` map in `build-logic/src/main/groovy/multiloader-common.gradle`.
