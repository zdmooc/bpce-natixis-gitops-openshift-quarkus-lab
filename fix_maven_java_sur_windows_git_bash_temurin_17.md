# Fix Maven/Java sur Windows + Git Bash (Temurin 17)

Objectif : avoir un environnement **cohérent** (Java 17 + Maven 3.9.x) dans **PowerShell** et **Git Bash** sur Windows, sans erreurs `JAVA_HOME` ni `ClassNotFoundException`.

---

## 1) Symptômes typiques

### 1.1 `JAVA_HOME` absent (Maven ne démarre pas)
- **Message** : `The JAVA_HOME environment variable is not defined correctly...`
- **Cause** : Maven ne trouve pas le JDK via `JAVA_HOME`.

### 1.2 `ClassNotFoundException: org.codehaus.plexus.classworlds.launcher.Launcher` (Git Bash)
- **Message** : `Erreur : impossible de trouver ou de charger la classe principale org.codehaus.plexus.classworlds.launcher.Launcher`
- **Cause principale** : Git Bash exécute `mvn` (script Unix) mais l’environnement (JAVA_HOME/paths) est aligné Windows → mélange qui casse le classpath Maven.
- **Solution robuste** : forcer Git Bash à appeler `mvn.cmd` (wrapper Windows) et garder `JAVA_HOME` au **format Windows**.

---

## 2) Vérifications de base

### 2.1 Vérifier Java (version + runtime)
**Commande (PowerShell ou Git Bash) :**
- `java -version`

**But :**
- Confirmer la version de Java réellement exécutée (ex. 17.0.17).

### 2.2 Vérifier Maven
**Commande :**
- `mvn -v`

**But :**
- Confirmer la version de Maven et surtout la ligne `Java version: ...`.

---

## 3) Fix côté PowerShell (session courante)

### 3.1 Définir `JAVA_HOME` pour la session
**Commande :**
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

**Description :**
- `JAVA_HOME` pointe vers le répertoire racine du JDK 17.
- On préfixe le `Path` avec `...\bin` pour que `java` et `javac` soient trouvés en priorité.

### 3.2 Contrôle
**Commande :**
```powershell
echo $env:JAVA_HOME
mvn -v
```

**Description :**
- `echo` affiche la valeur active de `JAVA_HOME`.
- `mvn -v` confirme que Maven utilise bien Java 17.

### 3.3 Rendre permanent côté Windows (optionnel)
**Commande :**
```powershell
[Environment]::SetEnvironmentVariable(
  "JAVA_HOME",
  "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot",
  "User"
)
```

**Description :**
- Écrit `JAVA_HOME` dans les variables utilisateur Windows.
- Nécessite d’ouvrir un **nouveau terminal** pour prise en compte.

---

## 4) Fix côté Git Bash (session courante)

### 4.1 Définir `JAVA_HOME` au format Windows
**Commande :**
```bash
export JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot'
```

**Description :**
- Git Bash peut exporter une variable avec une valeur Windows.
- Cette forme est particulièrement compatible avec `mvn.cmd`.

### 4.2 Mettre Java + Maven au début du PATH (format `/c/...`)
**Commande :**
```bash
export PATH="/c/Program Files/Eclipse Adoptium/jdk-17.0.17.10-hotspot/bin:/c/maven/apache-maven-3.9.8/bin:$PATH"
```

**Description :**
- On préfixe le PATH avec :
  - le `bin` du JDK 17 (pour `java`)
  - le `bin` de Maven (où se trouve `mvn.cmd`)
- On laisse le reste du PATH derrière.

### 4.3 Forcer `mvn` → `mvn.cmd`
**Commande :**
```bash
alias mvn='mvn.cmd'
```

**Description :**
- `mvn` dans Git Bash appelle par défaut le script Unix `mvn`.
- Ici on force l’usage de `mvn.cmd` (wrapper Windows), ce qui supprime l’erreur `ClassNotFoundException`.

### 4.4 Vérifier la résolution des commandes
**Commande :**
```bash
type -a mvn
java -version
mvn -v
```

**Description :**
- `type -a mvn` montre toutes les résolutions possibles et confirme l’alias.
- `java -version` confirme Java 17.
- `mvn -v` doit afficher `Java version: 17.0.17`.

---

## 5) Rendre le fix permanent dans Git Bash (`~/.bashrc`)

### 5.1 Ajouter la configuration
**Commande :**
```bash
cat >> ~/.bashrc <<'EOF'

# --- Java + Maven (Windows) ---
export JAVA_HOME='C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.17.10-hotspot'
export PATH="/c/Program Files/Eclipse Adoptium/jdk-17.0.17.10-hotspot/bin:/c/maven/apache-maven-3.9.8/bin:$PATH"
alias mvn='mvn.cmd'
EOF
```

**Description :**
- `cat >>` ajoute à la fin du fichier sans écraser.
- `<<'EOF'` (EOF **quoté**) empêche l’expansion des variables lors de l’écriture.
- Double antislash `\\` nécessaire dans `JAVA_HOME` pour conserver correctement les `\`.

### 5.2 Recharger sans redémarrer
**Commande :**
```bash
source ~/.bashrc
```

**Description :**
- Recharge le fichier de configuration dans le shell courant.

### 5.3 Corriger une erreur fréquente : `\` dans le PATH

#### Problème
Si dans `~/.bashrc` une ligne ressemblait à :
```bash
export PATH="/c/Program Files/Eclipse Adoptium\jdk-17..."
```
le `\` casse le chemin.

#### Correction (automatique) avec `sed`
**Commande :**
```bash
sed -i 's#Eclipse Adoptium\\jdk-17\.0\.17\.10-hotspot#Eclipse Adoptium/jdk-17.0.17.10-hotspot#g' ~/.bashrc
```

**Description :**
- Remplace `Eclipse Adoptium\jdk-...` par `Eclipse Adoptium/jdk-...`.
- `-i` modifie le fichier sur place.

### 5.4 Nettoyage final : `JAVA_HOME` 100% Windows
**Commande :**
```bash
sed -i "s#^export JAVA_HOME=.*#export JAVA_HOME='C:\\\\Program Files\\\\Eclipse Adoptium\\\\jdk-17.0.17.10-hotspot'#g" ~/.bashrc
```

**Description :**
- Remplace toute la ligne `export JAVA_HOME=...` par une version standardisée.
- Les `\\\\` assurent que le fichier contienne réellement `\\`.

### 5.5 Vérification finale
**Commande :**
```bash
source ~/.bashrc
echo "$JAVA_HOME"
mvn -v
```

**Description :**
- Confirme la valeur exacte de `JAVA_HOME`.
- Confirme Maven + Java 17 sans erreur.

---

## 6) Commande de build Quarkus (après correction)

### Build Maven
**Commande :**
```bash
mvn -DskipTests clean package
```

**Description :**
- `clean` : nettoie `target/`.
- `package` : compile + assemble l’artefact.
- `-DskipTests` : accélère en sautant l’exécution des tests.

---

## 7) Résultat attendu

- PowerShell : `mvn -v` affiche Java 17 et le runtime Temurin.
- Git Bash : `mvn -v` fonctionne également (grâce à `mvn.cmd`) et n’affiche plus d’erreur `ClassNotFoundException`.

