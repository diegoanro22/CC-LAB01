# Plan de acción — Laboratorio 01 (Construcción de Compiladores 2026-II)
## Analizador léxico y sintáctico de Compiscript con ANTLR4 — Equipo de 3 personas

---

## 1. Contexto

**Qué se pide:** el curso "Construcción de Compiladores" exige entregar, en grupos de hasta 3 personas, una herramienta con GUI que reciba un archivo `.cps` (código Compiscript), lo analice léxica y sintácticamente, y reporte **todos** los errores encontrados (no solo el primero) con línea, columna, tipo, símbolo/lexema y descripción inteligible en español. Es obligatorio usar una herramienta generadora de analizadores.

**Entrega:** miércoles 12 de agosto de 2026, 23:59 — es decir, **mañana**. El equipo ya decidió la ruta técnica:

- **Herramienta generadora:** ANTLR4 (ya se cuenta con `Gramática de Compiscript.g4`, provista por el curso, lista para usarse sin traducir a otro formato — a diferencia de la ruta alternativa de reusar `yalex`/`yapar` de CP-PR01, que habría exigido traducir la gramática EBNF a BNF plana SLR(1) y construir recuperación de errores y tracking de línea/columna desde cero).
- **Lenguaje/plataforma:** Java (mismo lenguaje que CP-PR01), porque además la GUI se va a reusar/adaptar del proyecto `CP-PR01` (Swing, estilo VS Code oscuro: `YalexGui.java`, `YaparPanel.java`) para no partir de cero visualmente y para que se vea "amigable y estética" como exige el enunciado.
- Este documento es el plan de referencia para repartir el trabajo entre los 3 integrantes y sirve como contexto compartido del equipo (se puede pasar tal cual a los compañeros).

**Verificado en este entorno:** la carpeta `CC-LAB01` solo contiene los documentos (PDF del enunciado, PDF de rúbrica, `Definición de Compiscript.md`, `Gramática de Compiscript.g4`) — **no hay código todavía**, se parte de cero. Tampoco hay JDK/Maven instalados en esta máquina; cada integrante debe verificar su propio entorno (ver §3).

---

## 2. Riesgos técnicos y decisiones de diseño a tener en cuenta

Estos puntos no son "trabajo extra", son la diferencia entre sacar 10/10 o perder puntos por incumplimientos que la rúbrica penaliza fuerte (algunos con **0 puntos**, otros con **-2 puntos**):

1. **No usar `BailErrorStrategy` de ANTLR.** Por defecto ANTLR usa `DefaultErrorStrategy`, que ya hace recuperación en modo pánico (inserta/elimina tokens, sincroniza en los FOLLOW sets) y sigue parseando tras un error — esto es exactamente lo que pide el lab. `BailErrorStrategy` aborta en el primer error: **no debe usarse**.
2. **El lexer de ANTLR ya recupera solo.** Ante un carácter no reconocido, ANTLR lanza un `LexerNoViableAltException`, reporta "token recognition error" vía el listener, y por defecto (`Lexer.recover`) consume un carácter y continúa. No hay que reimplementar esto, solo capturarlo correctamente.
3. **Hay que quitar los listeners por defecto.** Tanto el Lexer como el Parser traen `ConsoleErrorListener` que imprime a stderr en inglés/formato técnico. Hay que `removeErrorListeners()` en ambos y añadir un listener propio que capture `(línea, columna, tipo, símbolo/lexema, mensaje)` a una lista compartida — nunca dejar pasar el mensaje crudo de ANTLR al usuario (la rúbrica exige mensajes en español e inteligibles, y el enunciado prohíbe mostrar trazas internas tal cual).
4. **Traducción de mensajes.** Los mensajes por defecto de ANTLR ("mismatched input 'x' expecting y", "extraneous input", "missing ';'", "token recognition error at: '@'") deben traducirse/reformularse a español claro. Esto es una capa de mapeo, no trivial pero acotada.
5. **Evitar ruido / bucles infinitos en la recuperación.** Con la gramática dada (que combina `assignment` como alternativa explícita de `statement` Y también como parte de `expressionStatement` vía `assignmentExpr` dentro de `expression`) puede haber ambigüedad de gramática que ANTLR resuelve por orden de alternativa, pero conviene revisar los warnings que emite `antlr4-maven-plugin` al generar (`mvn generate-sources`) por si reporta ambigüedades o "rule XXX contains an ambiguity" — no es motivo de pánico automáticamente pero hay que leerlos. Además, como salvaguarda barata: si el mismo (línea, columna) repite error más de N veces seguidas, cortar/ignorar duplicados antes de listarlos (satisface el punto del enunciado de "evitar mensajes derivados que no aporten información").
6. **No pasarse de alcance.** El lab prohíbe explícitamente análisis semántico, ejecución o generación de código (con penalización de **0 puntos** si se hace). El analizador solo debe validar léxico + sintaxis.
7. **GUI es obligatoria y se evalúa aparte del análisis en sí**: selección de archivo desde la GUI (no hardcodear ruta), resultados mostrados en la GUI (una tabla es ideal, no un bloque de texto plano), mensaje explícito cuando no hay errores.

---

## 3. Estructura del proyecto (dentro de este mismo repo, CC-LAB01)

El proyecto Maven vive directamente en esta carpeta (`CC-LAB01/`, ya es su propio repo git) — no en un repo/carpeta separada:

```
CC-LAB01/                                              (este mismo repo)
  pom.xml                                               JDK 21, antlr4-maven-plugin, antlr4-runtime — artifactId "lexsynanalyzer"
  src/main/antlr4/com/lexsynanalyzer/parser/LexSynAnalyzer.g4   copia de la gramática dada por el curso (grammar LexSynAnalyzer;)
  src/main/java/com/lexsynanalyzer/analyzer/
    AnalysisError.java                                  record: tipo, linea, columna, simbolo, descripcion
    AnalysisResult.java                                 lista de AnalysisError + boolean exitoso
    LexSynAnalyzer.java                                 API pública: analyze(File) -> AnalysisResult
    TipoError.java                                       enum LEXICO | SINTACTICO
    CapturingErrorListener.java                          implementa ANTLRErrorListener (uno para lexer, otro para parser)
    MensajesEspanol.java                                 traduce mensajes ANTLR -> español inteligible
  src/main/java/com/lexsynanalyzer/gui/
    LexSynAnalyzerGui.java                                adaptado de YalexGui.java (mismo tema oscuro)
    ResultsTablePanel.java                                tabla de errores (Tipo|Línea|Columna|Token|Descripción)
  src/test/resources/casos/
    baja_sin_errores.cps
    baja_errores_lexicos.cps
    baja_errores_sintacticos.cps
    baja_errores_mixto.cps
    media_sin_errores.cps
    media_errores_lexicos.cps
    media_errores_sintacticos.cps
    media_errores_mixto.cps
  README.md                                  cómo compilar/correr, para el video y para Canvas
```

`pom.xml` declara el plugin `org.antlr:antlr4-maven-plugin` apuntando a `src/main/antlr4`, y la dependencia `org.antlr:antlr4-runtime` en scope `compile`. El build (`mvn generate-sources` o `mvn compile`) genera automáticamente `Lab01Lexer`, `Lab01Parser` y las clases base de visitor/listener en `target/generated-sources/antlr4` (ya verificado: compila sin errores ni warnings de ambigüedad).

Nota: la gramática del lenguaje sigue llamándose "Compiscript" (así la dio el curso, y los archivos de prueba usan extensión `.cps`) — lo único renombrado es el nombre del proyecto/paquetes (`lab01`), no el lenguaje que se está analizando.

---

## 4. Mapeo explícito rúbrica → responsable → entregable

| # | Criterio de rúbrica | Cubierto por | Cómo se verifica |
|---|---|---|---|
| 1 | Compila sin errores (baja) | C (archivo) + A+B (analyzer) | `baja_sin_errores.cps` → `AnalysisResult.exitoso == true` |
| 2 | ≥3 errores léxicos (baja) | C + A | `baja_errores_lexicos.cps` produce ≥3 `AnalysisError` tipo LÉXICO |
| 3 | ≥3 errores sintácticos (baja) | C + B | ídem, tipo SINTÁCTICO |
| 4 | ≥2+2 combinados (baja) | C + A+B | `baja_errores_mixto.cps` |
| 5 | Compila sin errores (media) | C + A+B | `media_sin_errores.cps` |
| 6 | ≥3 léxicos (media) | C + A | `media_errores_lexicos.cps` |
| 7 | ≥3 sintácticos (media) | C + B | `media_errores_sintacticos.cps` |
| 8 | ≥2+2 combinados (media) | C + A+B | `media_errores_mixto.cps` |
| 9 | Línea/columna correctas | A (pipeline léxico) + B (pipeline sintáctico) | line/col de ANTLR son 1-indexed en línea y 0-indexed en columna — decidir y documentar la convención mostrada en GUI (recomendado: mostrar columna+1 para que ambas empiecen en 1, más intuitivo al usuario). Se decide una sola vez en el kickoff para que ambos pipelines sean consistentes |
| 10 | Reportes inteligibles en español | A (`MensajesEspanol` léxico) + B (`MensajesEspanol` sintáctico) | revisión manual por C de cada mensaje mostrado |
| — | GUI obligatoria/estética | C | Swing dark theme heredado de CP-PR01 |
| — | Selección de archivo desde GUI | C | `JFileChooser` filtrado a `*.cps` |
| — | Resultados en GUI, no solo consola | C | `ResultsTablePanel` (JTable) |
| — | No detenerse en el primer error | A+B | `DefaultErrorStrategy`, nunca `BailErrorStrategy`, en ambos pipelines |
| — | Sin ciclos infinitos / ruido | B | deduplicación por (línea,columna), principalmente ruido en cascada sintáctica + pruebas de C |
| — | Video ≤10 min, en Canvas | C (lidera) + todos (demo) | grabación final |

---

## 5. División de trabajo — 3 personas

### Kickoff (solo Persona A, ~30 min, antes de separarse en pipelines)

1. Crear el proyecto Maven (`pom.xml` con `antlr4-maven-plugin` + `antlr4-runtime`), copiar `Gramática de Compiscript.g4` a `src/main/antlr4/`, correr `mvn generate-sources` y confirmar que compila sin errores fatales. Revisar y anotar cualquier warning de ambigüedad que emita ANTLR.
2. Definir y compartir con B el **contrato**: `AnalysisError` (record con `tipo` [LÉXICO|SINTÁCTICO], `linea`, `columna`, `simbolo`, `descripcion`), `AnalysisResult`, y la firma pública `CompiscriptAnalyzer.analyze(File) -> AnalysisResult`. Con esto B puede empezar su pipeline en paralelo sin esperar a que A avance más.
3. Decidir y documentar la convención de línea/columna (recomendado: columna+1 para que ambas empiecen en 1) — se decide una sola vez para que los dos pipelines sean consistentes.

### Persona A — Pipeline léxico (dueño de punta a punta)
**Responsable de que el análisis léxico cumpla los criterios 2, 6 y parte de 1, 9, 10 de la rúbrica.**

1. Implementar `CapturingErrorListener` aplicado al **Lexer** (`removeErrorListeners()` + `addErrorListener(...)`), capturando `LexerNoViableAltException` / token recognition error y construyendo `AnalysisError` tipo LÉXICO.
2. Implementar `MensajesEspanol` para errores léxicos: carácter no reconocido, string sin cerrar, literal mal formado, identificador inválido, etc.
3. Implementar la parte de `CompiscriptAnalyzer` que corre el lexer sobre el `CharStream`, conecta el listener léxico y recolecta esos `AnalysisError`.
4. Probar con 2-3 snippets propios (caracteres inválidos, string sin cerrar) antes de integrar con B y pasarle casos a C.

### Persona B — Pipeline sintáctico (dueño de punta a punta)
**Responsable de que el análisis sintáctico cumpla los criterios 3, 7 y parte de 1, 9, 10 de la rúbrica.**

1. Implementar `CapturingErrorListener` aplicado al **Parser** (mismatched input, extraneous input, missing token, no viable alternative), construyendo `AnalysisError` tipo SINTÁCTICO.
2. Implementar `MensajesEspanol` para errores sintácticos: falta `;`, paréntesis/llaves desbalanceadas, palabra reservada mal usada, estructura inesperada, `case` sin `:`, etc.
3. Implementar la parte de `CompiscriptAnalyzer` que invoca `parser.program()` (regla inicial), conecta el listener sintáctico y recolecta esos `AnalysisError`.
4. Agregar la salvaguarda anti-ruido (deduplicar errores repetidos en la misma posición — el ruido en cascada ocurre casi siempre en el pipeline sintáctico) y probarla con 2-3 snippets propios.
5. **Integración A+B:** una vez ambos pipelines funcionan por separado, juntar las dos mitades en un único `CompiscriptAnalyzer.analyze(...)` que corre lexer+parser y mezcla ambas listas de `AnalysisError` (si queda vacía ⇒ éxito). Entregarlo como API standalone probable desde línea de comandos/tests unitarios **antes** de que C la conecte a la GUI.

### Persona C — GUI (Swing, estilo CP-PR01) + casos de prueba `.cps` + video
**Responsable de que se cumplan los requisitos de interfaz, de que existan y funcionen los 8 archivos de prueba exigidos por la rúbrica, y de la entrega final (video + Canvas).**

1. Adaptar `YalexGui.java`/`YaparPanel.java` de CP-PR01 como base: mismo tema oscuro (constantes de color, tipografía, layout con barra lateral/tabs/terminal), pero simplificado a lo que este lab necesita (no hace falta el editor de `.yal`/DFA graph — sí hace falta: botón "Abrir archivo .cps", área de vista del código fuente cargado, botón "Analizar", panel de resultados). Puede avanzar con datos mockeados mientras A+B terminan.
2. Implementar `ResultsTablePanel`: `JTable` con columnas **Tipo | Línea | Columna | Token/Lexema | Descripción**, coloreando filas por tipo (léxico vs sintáctico) igual que YaparPanel colorea shift/reduce/accept.
3. Implementar el estado "sin errores": banner o mensaje visible y explícito (ej. verde, "✔ Archivo analizado correctamente. No se encontraron errores léxicos ni sintácticos.") — el enunciado lo pide explícitamente.
4. Conectar el botón "Analizar" a `CompiscriptAnalyzer.analyze(...)` en un `SwingWorker` (mismo patrón que `YaparPanel.runPipeline()`) para no congelar la UI.
5. Pulido visual final: iconos, espaciados, mensajes de estado — este ítem cuenta puntos aparte en la rúbrica del enunciado ("interfaz gráfica amigable y estética").
6. Escribir los 8 archivos `.cps` (ver §6 abajo), basándose en la sintaxis de `Definición de Compiscript.md` y validando manualmente contra `Gramática de Compiscript.g4` que el código "sin errores" realmente es válido según la gramática (ojo con detalles como que `variableDeclaration` no lleva `=` sino que usa `initializer`, que `print` es una palabra reservada de statement y no una función común, etc.). Puede arrancar esto desde el día 1 sin esperar a A/B.
7. Para los archivos "con errores", diseñar errores **deliberados y controlados**: ej. léxicos = caracteres no soportados por el lexer (`@`, `#`, `$`, string sin cerrar), identificadores mal formados; sintácticos = falta de `;`, paréntesis/llaves desbalanceadas, palabra reservada mal usada, `case` sin `:`, etc. Verificar que cada archivo dispare **como mínimo** la cantidad de errores que pide la rúbrica (3 léxicos / 3 sintácticos / 2+2 combinados) sin que un solo error "reviente" el archivo entero en errores en cascada sin sentido.
8. Escribir el guion corto del video (demo: abrir GUI → cargar cada uno de los 8 casos → mostrar resultado → explicar en <10 min) y coordinar la grabación con A y B (quién explica qué parte). Subir a YouTube y entregar el link en Canvas.
9. Redactar el `README.md` del proyecto (cómo compilar y correr) — útil también como apoyo del video.

### Validación final entre los tres

Una vez A+B integraron el analyzer y C tiene la GUI conectada, corren los 8 `.cps` juntos y cada quien reporta bugs de su área: A revisa parsing léxico raro, B revisa parsing sintáctico raro, C revisa bugs de GUI — llevando una checklist cruzada contra la tabla de §4.

---

## 6. Especificación exacta de los 8 archivos de prueba

**Complejidad baja** debe incluir: declaración de variables de ≥3 tipos, ≥1 constante, ≥2 operaciones aritméticas con operadores distintos, ≥1 `if`/`if-else`, ≥1 `for`/`while`/`do-while`, ≥1 `foreach`/`switch-case`.

**Complejidad media** = todo lo de baja + declaración y uso de ≥1 arreglo, ≥2 clases declaradas, ≥2 objetos instanciados, ≥2 funciones declaradas, ≥2 llamadas a función.

| Archivo | Complejidad | Errores a inyectar |
|---|---|---|
| `baja_sin_errores.cps` | baja | ninguno — debe compilar limpio |
| `baja_errores_lexicos.cps` | baja | ≥3 errores léxicos (caracteres inválidos / literales mal formados), 0 sintácticos |
| `baja_errores_sintacticos.cps` | baja | ≥3 errores sintácticos (estructura inválida), 0 léxicos |
| `baja_errores_mixto.cps` | baja | ≥2 léxicos + ≥2 sintácticos |
| `media_sin_errores.cps` | media | ninguno — debe compilar limpio |
| `media_errores_lexicos.cps` | media | ≥3 errores léxicos, 0 sintácticos |
| `media_errores_sintacticos.cps` | media | ≥3 errores sintácticos, 0 léxicos |
| `media_errores_mixto.cps` | media | ≥2 léxicos + ≥2 sintácticos |

---

## 7. Cronograma (dado que la entrega es mañana 23:59)

**Hoy (11 ago):**
1. *Cuanto antes, todos en paralelo (≈1h):* cada quien verifica su entorno (JDK 21, Maven); A crea el repo/`pom.xml` y confirma que `Gramática de Compiscript.g4` genera código sin errores fatales con `antlr4-maven-plugin`; comparte el repo con B y C.
2. *Resto del día/noche, en paralelo:*
   - A: implementa `AnalysisError`/`CapturingErrorListener`/`MensajesEspanol`/`CompiscriptAnalyzer`.
   - B: adapta la GUI de CP-PR01 (puede avanzar con datos de ejemplo simulados mientras A termina).
   - C: escribe los 8 `.cps`, validándolos a mano contra la gramática (no necesita esperar a A/B para empezar).
3. *Checkpoint nocturno:* A entrega `CompiscriptAnalyzer` funcional (aunque sea sin pulir mensajes); B integra la llamada real.

**Mañana (12 ago):**
4. *Mañana:* integración GUI + analyzer; primeras corridas con los 8 `.cps` de C; lista de bugs.
5. *Mediodía:* corrección de bugs (línea/columna, mensajes, deduplicación de ruido, ajustes de gramática/casos si algo no calza).
6. *Tarde:* pulido visual de GUI; repaso criterio por criterio contra la tabla de §4 — **ningún punto de la rúbrica sin marcar**.
7. *Tarde-noche:* grabación y edición del video (≤10 min), subida a YouTube, entrega en Canvas con margen antes de las 23:59.

---

## 8. Verificación final antes de grabar el video

- [ ] Los 8 `.cps` de §6 existen y cada uno dispara exactamente el tipo/cantidad de errores esperado (ni de más por cascada de ruido, ni de menos).
- [ ] Un archivo sin errores muestra el mensaje explícito de éxito.
- [ ] Cada error mostrado tiene tipo, línea, columna, símbolo/lexema y descripción en español, sin texto de excepción cruda de Java/ANTLR visible.
- [ ] El análisis de un archivo con múltiples errores los reporta **todos** en una sola corrida (no se detiene en el primero).
- [ ] La selección del archivo se hace desde la GUI (no por argumento de línea de comandos ni ruta fija).
- [ ] Los resultados se ven en la GUI, no solo en consola/terminal.
- [ ] No aparecen errores duplicados/derivados sin valor informativo, ni cuelgues (bucles infinitos) al parsear ninguno de los 8 casos.
- [ ] El video dura ≤10 minutos y cubre cada uno de los 8 casos + explicación breve del enfoque técnico (ANTLR4 + Java + Swing).
