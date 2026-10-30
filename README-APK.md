# OmniHub: cómo obtener el APK

## Opción A: en la nube con GitHub (sin instalar nada)
1. Crea un repositorio en github.com y sube todo el contenido de esta carpeta (incluida `.github/`).
2. Entra a la pestaña **Actions** -> **Build APK** -> **Run workflow**.
3. Espera unos minutos y descarga el artefacto **omnihub-debug-apk** (es un .zip con `app-debug.apk`).
4. Pásalo al celular, ábrelo y acepta "instalar de orígenes desconocidos" si te lo pide.

## Opción B: Android Studio
1. Abre la carpeta en Android Studio (versión reciente, con soporte para AGP 9).
2. Crea el keystore de debug que pide el build (una sola vez, en la raíz del proyecto):
   `keytool -genkey -v -keystore debug.keystore -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"`
3. Menú **Build -> Build APK(s)**, o conecta el celular por USB y pulsa **Run**.

## Datos reales
- Clima: Open-Meteo. Comercios, rutas y mapa: OpenStreetMap (Overpass, Nominatim, OSRM). Ninguno requiere clave.
- Al abrir la app pide permiso de ubicación. Sin permiso usa Buenos Aires como referencia.
- Las alertas son avisos automáticos por pronóstico, NO alertas oficiales del SMN.
- Los comercios con "[DEMO]" en la descripción son de ejemplo. Los de OpenStreetMap no tienen precios ni horarios verificados.
- Las ofertas siguen siendo de ejemplo: no existe una API pública; hace falta un backend donde los comercios las carguen.
