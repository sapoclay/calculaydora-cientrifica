# Calculadora Científica - CalculaYDora CientRífica

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-orange)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Una calculadora científica con interfaz gráfica desarrollada en Java Swing que soporta operaciones avanzadas y funciones matemáticas.

## Características Principales

- 🧮 **Operaciones básicas**:
  - Suma, resta, multiplicación, división
  - Potenciación (^) y porcentaje (%)
  - Paréntesis para agrupación
  - Números decimales

- 🔢 **Funciones científicas**:
  - Trigonometría: sin, cos, tan (en grados)
  - Funciones inversas: asin, acos, atan (resultado en grados)
  - Raíz cuadrada (sqrt)

- 🖥️ **Interfaz de usuario**:
  - Diseño responsive con GridBagLayout
  - Tooltips descriptivos para todos los botones
  - Manejo de errores con mensajes claros
  - Soporte para entrada mediante teclado
  - Historial de expresión visible

- ⚙️ **Funcionalidades avanzadas**:
  - Algoritmo Shunting-yard para evaluación de expresiones
  - Validación de paréntesis balanceados
  - Prevención de errores comunes (división por cero, puntos decimales múltiples)
  - Soporte para operadores de diferente precedencia

## Captura de Pantalla

![Calculadora Científica](screenshot.png) <!-- Puedes agregar una captura real aquí -->

## Cómo Usar

1. **Operaciones básicas**:
   - Usa los botones numéricos y operadores para construir tu expresión
   - Ejemplo: `(5 + 3) * 2`

2. **Funciones científicas**:
   - Selecciona una función seguida de un valor entre paréntesis
   - Ejemplo: `sin(45)` calcula el seno de 45 grados

3. **Características especiales**:
   - `←`: Borra el último carácter
   - `C`: Limpia toda la expresión
   - `%`: Calcula porcentajes (ej: `100 * 20%` = 20)
   - `^`: Potenciación (ej: `2^3` = 8)

## Detalles Técnicos

### Algoritmo de Evaluación
El programa utiliza el algoritmo **Shunting-yard** de Edsger Dijkstra para:
1. Convertir la expresión infix a postfix (notación polaca inversa)
2. Evaluar la expresión usando una pila de operaciones
3. Manejar precedencia de operadores y asociatividad

### Tokenización
El proceso de descomposición de la expresión incluye:
- Identificación de números (enteros y decimales)
- Reconocimiento de funciones matemáticas
- Detección de operadores y paréntesis

### Requisitos del Sistema

- Java Runtime Environment (JRE) 8 o superior
- Maven (para compilación desde código fuente)
- Resolución de pantalla mínima recomendada: 800x600

## Manejo de Errores

### El sistema detecta y notifica:

- Paréntesis desbalanceados
- Operaciones matemáticas inválidas
- Entradas mal formadas
- Divisiones por cero
- Resultados infinitos o NaN (Not a Number)

### Limitaciones conocidas

- No soporta números complejos
- Las funciones trigonométricas solo trabajan en grados
- Máximo de 15 dígitos para números decimales
- No hay historial de cálculos previos

## Licencia

Distribuido bajo licencia MIT. Ver LICENSE para más detalles.

Este README incluye:
1. Descripción general del proyecto
2. Características principales
3. Instrucciones de uso
4. Detalles técnicos de implementación
5. Requisitos del sistema
6. Instrucciones de compilación
7. Política de manejo de errores
8. Limitaciones conocidas
9. Información de licencia