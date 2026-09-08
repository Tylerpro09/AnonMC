# AnonMC

Mod cliente para **Minecraft Java 26.2 + Fabric** que oculta los nombres visibles de los jugadores y les asigna alias temporales:

- `Anonimo`
- `Anonimo1`
- `Anonimo2`
- `Anonimo3`
- ...

El objetivo es que, durante una partida, los jugadores no vean directamente los usernames reales desde las superficies normales de la interfaz.

## Qué censura

- Nombre sobre la cabeza del jugador.
- Lista de jugadores `TAB`.
- Mensajes de chat de jugadores.
- Mensajes de sistema/servidor que contengan usernames conocidos (por ejemplo, ciertos mensajes de entrada/salida o muerte).
- Prefijos/rangos en TAB no se muestran allí, para evitar pistas obvias de identidad.

## Cómo funciona

AnonMC **no modifica el GameProfile, UUID ni el nombre usado por la conexión**. Solo sustituye la representación visible en el cliente.

Los alias se asignan cuando el cliente encuentra a cada jugador. Permanecen estables durante la sesión y se borran al entrar/salir de un servidor. Por eso un jugador puede recibir otro número tras reconectar.

## Requisitos

- Minecraft Java Edition `26.2`
- Fabric Loader `0.19.5` o posterior compatible
- Fabric API `0.159.0+26.2`
- Java `25`

## Instalación

1. Instala Fabric Loader para Minecraft 26.2.
2. Instala Fabric API.
3. Coloca el JAR de AnonMC en la carpeta `mods`.
4. Instala el mod en **cada cliente que deba ver nombres anonimizados**.

> Importante: es un mod cliente. Un jugador que no tenga AnonMC puede seguir viendo los nombres reales que el servidor le envía. Para un modo de juego donde sea obligatorio, conviene añadir después un componente servidor que exija el mod durante el login.

## Compilar

Con JDK 25 y Gradle 9.5.1:

```bash
gradle build
```

El JAR quedará en:

```text
build/libs/anonmc-1.0.0.jar
```

También hay un workflow de GitHub Actions que compila automáticamente el proyecto.

## Privacidad / alcance

AnonMC está diseñado para censurar la identidad en la UI normal de Minecraft. No pretende convertir el protocolo de Minecraft en anónimo. Herramientas externas, logs, mods de terceros, pantallas de depuración o análisis de paquetes pueden seguir accediendo al GameProfile real.

Las skins tampoco se sustituyen en v1.0.0. Si los jugadores usan skins reconocibles, estas todavía pueden dar pistas de identidad.

## Próximas mejoras propuestas

- Ocultar/reemplazar skins.
- Alias compartidos y controlados por servidor.
- Handshake que expulse clientes sin AnonMC.
- Censura de autocompletado de comandos y otras GUIs.
- Archivo de configuración para prefijo, numeración y reinicio de alias.

## Licencia

MIT.
