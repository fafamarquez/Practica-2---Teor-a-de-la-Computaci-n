# Práctica 2: Simulador de Autómatas Finitos y Análisis JFLAP

## 📌 Introducción y Objetivo de la Práctica
El objetivo principal de esta práctica es profundizar en el análisis, diseño y validación de Autómatas Finitos Deterministas (AFD) y No Deterministas (AFND). Para ello, el proyecto se divide en el modelado gráfico de autómatas haciendo uso de la herramienta JFLAP, y el desarrollo de un simulador propio en Java capaz de procesar archivos nativos `.xml` o `.jff`, permitiendo la inserción manual de la quíntupla y validando el recorrido de cadenas de manera visual y paso a paso.

## ⚙️ Instrucciones de Instalación y Ejecución
1. Clonar el repositorio localmente usando el comando:
   `git clone https://github.com/tu-usuario/tu-repositorio.git`
2. Abrir el proyecto en **Apache NetBeans** (el proyecto está configurado con Maven, por lo que las dependencias se gestionarán automáticamente).
3. Localizar la clase principal de la interfaz gráfica (`guiMain.java` dentro de `src/main/java/mx/ipn/escom/...`).
4. Compilar y ejecutar el archivo (Shift + F6) para lanzar el simulador.
5. Para las pruebas, cargar los archivos JFLAP/XML/JSON exportados, o ingresar la tabla de transiciones manualmente.

## 📂 Descripción de los Autómatas Implementados
Se anexan en este repositorio los diseños correspondientes a las listas de ejercicios solicitadas, probados y exportados desde JFLAP:

* **Lista 2 — AFD (Autómatas Finitos Deterministas):**
  * Implementación de los lenguajes regulares asegurando que para cada estado y cada símbolo del alfabeto exista exactamente una transición definida.
* **Lista 3 — AFND Estándar y AFND-λ:**
  * **AFND Estándar:** Diseño de autómatas aprovechando el no determinismo y la multiplicidad de caminos.
  * **AFND-λ:** Resolución de autómatas mediante transiciones vacías (épsilon/lambda). Se incluye también el proceso metodológico de conversión de estos AFND-λ a AFND estándar, y finalmente a su AFD equivalente.

## 💻 Ejemplos de Uso (Capturas de Pantalla)
A continuación se demuestra el funcionamiento del simulador validando los autómatas:
* **Ejemplo 1 — Importando autómata de archivo .jff:**
<img width="831" height="550" alt="image" src="https://github.com/user-attachments/assets/20012743-0473-4051-9a6c-0acba715c92f" />
<img width="831" height="550" alt="image" src="https://github.com/user-attachments/assets/7d5d13f3-d0ea-4093-885a-c686f17dbb6c" />
<img width="831" height="550" alt="image" src="https://github.com/user-attachments/assets/f9cdf0d8-571a-4e55-b88d-2471eee36c92" />
<img width="831" height="550" alt="image" src="https://github.com/user-attachments/assets/5cd5ef15-8f60-492c-b36f-beba89c7b547" />

* **Ejemplo 2 — Definiendo parámetros del autómata de forma manual .jff:**
<img width="831" height="550" alt="image" src="https://github.com/user-attachments/assets/ca47d045-a1bd-4936-9479-7993dcabdc85" />
<img width="831" height="550" alt="image" src="https://github.com/user-attachments/assets/baac9957-c6ef-4c9d-8de3-a1f1561fa3f8" />

## 🗂️ Estructura del Repositorio
```text
📦 Practica2_TeoriaComputacion
 ┣ 📂 src/main/java            # Código fuente en Java (Simulador GUI y Lógica AutomataAFD)
 ┣ 📂 pom.xml                  # Configuración de dependencias de Maven
 ┣ 📂 JFLAP_Automatas          # Archivos de prueba y ejercicios
 ┃ ┣ 📂 Lista2_AFD             # Ejercicios resueltos en formato .jff y .xml
 ┃ ┗ 📂 Lista3_AFND            # Diseños de AFND y sus conversiones desde AFND-λ
 ┗ 📜 README.md                # Documentación del proyecto
