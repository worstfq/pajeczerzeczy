# SpiderHunt

Plugin Paper **1.21.4+** (Java 21) dodajacy dwa tematyczne przedmioty:

| Przedmiot | Material | Dzialanie |
|-----------|----------|-----------|
| **Kiel Arachny** | Netherite Sword | PPM -> zastawia pulapke 3x3 (zasieg 5 blokow) |
| **Siec Usidlenia** | Black Dye (z glintem) | PPM -> zastawia pulapke 3x3, zuzywa 1 szt. |

## Mechanika
- Identyfikacja przedmiotow przez **PersistentDataContainer** (nie po nazwie/lore).
- Pulapka 3x3 z pajeczyn (COBWEB) powstaje na warstwie nad blokiem, na ktory patrzy gracz.
- Pajeczyna pojawia sie **wylacznie w miejscu powietrza** (nie zastepuje innych blokow).
- Efekty dzwiekowe i czasteczkowe przy tworzeniu pulapki.
- Glint bez widocznych enchantow.

## Cooldowny
Osobne, niezalezne, per-gracz. W pelni konfigurowalne w `config.yml`:

```yaml
spider_sword:
  cooldown_seconds: 10

spider_trap:
  cooldown_seconds: 15
```

Wartosci czytane sa dynamicznie - po restarcie serwera dzialaja od razu, bez rekompilacji.

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
   > To zachowanie GitHuba, nie pluginu. Goly jar pobierzesz z Releases (punkt 2).

2. **Tag `v*`** (np. `v1.0.0`) -> tworzony jest **Release** z **golym plikiem `.jar`**
   w zalacznikach. Stamtad pobierasz bezposrednio jar, bez zadnej otoczki ZIP.

### Pierwsze wrzucenie na GitHub
```bash
git init
git add .
git commit -m "SpiderHunt 1.0.0"
git branch -M main
git remote add origin https://github.com/<uzytkownik>/<repo>.git
git push -u origin main
```

### Wydanie wersji (goly jar w Releases)
```bash
git tag v1.0.0
git push origin v1.0.0
```
Po chwili w zakladce **Releases** pojawi sie `SpiderHunt-1.0.0.jar` gotowy do pobrania.
