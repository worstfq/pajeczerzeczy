# SpiderHunt

Plugin Paper **1.21.4+** (Java 21) dodajacy dwa tematyczne przedmioty:

| Przedmiot | Material | Dzialanie |
|-----------|----------|-----------|
| **Kiel Arachny** | Netherite Sword | PPM -> pulapka 3x3 na bloku, na ktory patrzy gracz (zasieg 5 blokow) |
| **Siec Usidlenia** | Splash Potion (z glintem) | PPM -> rzut; pulapka 3x3 powstaje tam, gdzie mikstura wyladuje. **Nieskonczona** - nie zuzywa sie |

## Mechanika
- Identyfikacja przedmiotow przez **PersistentDataContainer** (nie po nazwie/lore).
- Pulapka 3x3 z pajeczyn (COBWEB). Pajeczyna pojawia sie **wylacznie w miejscu powietrza**
  (nie zastepuje innych blokow).
- Miecz celuje raytrace'em w blok (max 5 blokow); mikstura tworzy pulapke w punkcie ladowania.
- Efekty dzwiekowe i czasteczkowe przy tworzeniu pulapki + dzwiek rzutu mikstury.
- Glint bez widocznych enchantow.

## Cooldowny
- Osobne, niezalezne, per-gracz dla miecza i mikstury.
- **Wizualny cooldown jak przy tarczy** - biale przesuniecie na itemie w hotbarze
  (`Player#setCooldown`), zsynchronizowane z dlugoscia cooldownu z configu.
- Podczas cooldownu gracz dostaje wiadomosc z pozostalym czasem.
- Wartosci czytane dynamicznie - po restarcie serwera dzialaja od razu, bez rekompilacji.

## Konfiguracja (`config.yml`)
```yaml
messages:
  # MiniMessage: <red> <gray> <#6A0DAD> <bold>...</bold> itd.
  prefix: "<dark_gray>[</dark_gray><#6A0DAD><bold>Łowy</bold></#6A0DAD><dark_gray>]</dark_gray> "

spider_sword:
  cooldown_seconds: 10

spider_trap:
  cooldown_seconds: 15
```

## Komendy
- `/spidersword give <gracz>` — uprawnienie `spidersword.give`
- `/spidertrap give <gracz> [ilosc]` — uprawnienie `spidertrap.give`

## Budowanie lokalne
```bash
mvn clean package
```
Gotowy plik: `target/SpiderHunt-1.0.0.jar` -> wrzuc do folderu `plugins/`.

## Auto-build na GitHub (CI/CD)
Repozytorium zawiera workflow `.github/workflows/build.yml`:

1. **Push / Pull Request** na `main` lub `master` -> automatyczny build (`mvn package`).
   Jar laduje jako artefakt w zakladce **Actions**.
   > Uwaga: GitHub ZAWSZE pakuje artefakt do `.zip` przy pobieraniu z przegladarki.
   > Goly jar pobierzesz z Releases (punkt 2).

2. **Tag `v*`** (np. `v1.0.0`) -> tworzony jest **Release** z **golym plikiem `.jar`**.

### Pierwsze wrzucenie na GitHub
```bash
git init
git add .
git commit -m "SpiderHunt"
git branch -M main
git remote add origin https://github.com/<uzytkownik>/<repo>.git
git push -u origin main
```

### Wydanie wersji (goly jar w Releases)
```bash
git tag v1.0.0
git push origin v1.0.0
```
