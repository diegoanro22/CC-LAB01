# Video mostrando el funcionamiento del analizador : https://canva.link/wpopgcpjddslcuu


# Analizador Léxico y Sintáctico para Compiscript (ANTLR4 + Java Swing)

**Curso:** Construcción de Compiladores  
**Proyecto:** Laboratorio 01 — Analizador Léxico y Sintáctico con ANTLR4  
**Lenguaje Objetivo:** Compiscript (`.cps`)  
**Plataforma:** Java 21 / Swing GUI / Maven  

---

## 📋 Descripción

Esta aplicación es una herramienta interactiva con interfaz gráfica de usuario (GUI) en Java Swing (estilo VS Code oscuro) que analiza archivos en lenguaje **Compiscript** (`.cps`), realiza su análisis léxico y sintáctico utilizando **ANTLR4**, y reporta **todos** los errores detectados con su respectiva línea, columna, tipo (LÉXICO o SINTÁCTICO), símbolo implicado y descripción en español claro.

### ✨ Características Principales
1. **Recuperación de Errores en Modo Pánico:** Utiliza `DefaultErrorStrategy` de ANTLR4 para continuar el análisis tras detectar un error y recolectar múltiples fallas en una sola corrida (sin detenerse en el primer error).
2. **Mensajes Inteligibles en Español:** Traducción y reformulación de errores crudos de ANTLR4 a explicaciones claras en español (`MensajesEspanol.java`).
3. **Interfaz Gráfica de Usuario (GUI):**
   - Tema oscuro inspirado en editores modernos.
   - Visor de código fuente `.cps` con numeración de líneas.
   - Selector interactivo de archivos (`JFileChooser` filtrado a `.cps`).
   - Panel de resultados con banner explícito de estado (verde si no hay errores, rojo si los hay).
   - Tabla interactiva (`JTable`): al hacer clic en un error, el visor resalta automáticamente la línea correspondiente en el código fuente.
4. **Procesamiento Asíncrono (`SwingWorker`):** Evita que la interfaz se congele durante el análisis de archivos grandes.
5. **Suite de 8 Casos de Prueba:** Archivos `.cps` de complejidad baja y media que cubren los requisitos de la rúbrica, probados mediante JUnit 5.

---

## 🚀 Requisitos del Sistema

- **Java JDK 21** o superior.
- **Apache Maven 3.8+**.

---

## 🛠️ Compilación y Ejecución

### 1. Compilar el proyecto
Para generar el código de ANTLR4 y compilar las clases Java:
```bash
mvn clean compile
```

### 2. Ejecutar la Suite de Pruebas Unitarias
Para ejecutar las pruebas automatizadas de JUnit 5 (incluyendo los 8 casos de prueba `.cps`):
```bash
mvn test
```

### 3. Ejecutar la Interfaz Gráfica (GUI)
Para iniciar la aplicación gráfica:
```bash
mvn exec:java
```

---

## 📁 Estructura del Proyecto

```
CC-LAB01/
├── pom.xml                                           # Configuración Maven (ANTLR4 plugin, compiler release 21)
├── README.md                                         # Documentación del proyecto
├── Plan de accion - Laboratorio 01.md                # Plan de organización del equipo
├── src/
│   ├── main/
│   │   ├── antlr4/com/lexsynanalyzer/parser/
│   │   │   └── LexSynAnalyzer.g4                     # Gramática oficial de Compiscript
│   │   └── java/com/lexsynanalyzer/
│   │       ├── analyzer/
│   │       │   ├── AnalysisError.java                # Record: tipo, línea, columna, símbolo, descripción
│   │       │   ├── AnalysisResult.java               # Contenedor de lista de errores y método exitoso()
│   │       │   ├── CapturingErrorListener.java       # Implementación de ANTLRErrorListener
│   │       │   ├── LexSynAnalyzer.java               # Fachada/API pública del analizador
│   │       │   ├── MensajesEspanol.java              # Traductor de mensajes ANTLR -> español
│   │       │   └── TipoError.java                    # Enum: LEXICO | SINTACTICO
│   │       └── gui/
│   │           ├── LexSynAnalyzerGui.java            # Ventana principal Swing (VS Code Theme)
│   │           └── ResultsTablePanel.java            # Panel de tabla de resultados y banner
│   └── test/
│       ├── java/com/lexsynanalyzer/
│       │   └── CasosPruebaTest.java                  # Pruebas JUnit 5 automatizadas para los 8 casos .cps
│       └── resources/casos/                          # Archivos de prueba exigidos por la rúbrica
│           ├── baja_sin_errores.cps
│           ├── baja_errores_lexicos.cps
│           ├── baja_errores_sintacticos.cps
│           ├── baja_errores_mixto.cps
│           ├── media_sin_errores.cps
│           ├── media_errores_lexicos.cps
│           ├── media_errores_sintacticos.cps
│           └── media_errores_mixto.cps
```

---

## 🧪 Casos de Prueba Exigidos por la Rúbrica

Los 8 archivos de prueba están ubicados en `src/test/resources/casos/`:

| Archivo | Complejidad | Errores Esperados | Criterio de Rúbrica Validado |
|---|---|---|---|
| `baja_sin_errores.cps` | Baja | 0 errores | Compila limpio (baja). Incluye variables (>=3 tipos), `const`, ops aritméticas, `if-else`, `while`, `for`, `switch` y `foreach`. |
| `baja_errores_lexicos.cps` | Baja | >= 3 léxicos, 0 sintácticos | Errores por caracteres inválidos (`@`, `#`, `$`). |
| `baja_errores_sintacticos.cps` | Baja | >= 3 sintácticos, 0 léxicos | Errores por falta de `;`, paréntesis no balanceados en `if`, `case` sin `:`. |
| `baja_errores_mixto.cps` | Baja | >= 2 léxicos + >= 2 sintácticos | Combinación de errores léxicos y sintácticos en complejidad baja. |
| `media_sin_errores.cps` | Media | 0 errores | Compila limpio (media). Incluye todo lo de baja + arreglos, 2 clases, 2 instanciaciones (`new`), 2 funciones y 2 llamadas. |
| `media_errores_lexicos.cps` | Media | >= 3 léxicos, 0 sintácticos | Errores léxicos en contexto de estructuras avanzadas. |
| `media_errores_sintacticos.cps` | Media | >= 3 sintácticos, 0 léxicos | Errores sintácticos en declaraciones de clases, arreglos o funciones. |
| `media_errores_mixto.cps` | Media | >= 2 léxicos + >= 2 sintácticos | Combinación de errores léxicos y sintácticos en complejidad media. |

---

## 🧠 Estrategia de Manejo de Errores

1. **Desactivación de Listeners por Defecto:** Se ejecuta `removeErrorListeners()` tanto en el `LexSynAnalyzerLexer` como en el `LexSynAnalyzerParser` para eliminar las salidas en inglés por `stderr`.
2. **Registro de Errores Personalizado:** Se agrega `CapturingErrorListener` para capturar cualquier fallo léxico o sintáctico, normalizando el número de columna (`charPositionInLine + 1`) para mostrar base 1 al usuario.
3. **Mapeo y Traducción en Español:** La clase `MensajesEspanol` interpreta los patrones de error de ANTLR4 (`mismatched input`, `extraneous input`, `missing token`, `token recognition error`) y los convierte en descripciones comprensibles para el usuario en español.
4. **Deduplicación:** Se evita mostrar múltiples mensajes derivados redundantes en la misma posición (misma línea y columna) utilizando un mapa indexado de claves únicas.
