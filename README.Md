# Movs App

## Tabla de contenido

1. [Descripción](#1-descripción)
2. [Rutas](#2-rutas)
3. [Autenticación](#3-autenticación)
4. [Dependencias](#4-dependencias)
5. [Ejecución](#5-ejecución)

## 1. Descripción

1. Movs App es una app de películas hecha con Vue.
2. El home muestra un cover redondeado con imagen, título, descripción y frase inspirada en un kdrama.
3. La sección app muestra un grid de películas con tarjetas animadas.

## 2. Rutas

1. `/` muestra el home.
2. `/home` redirige al home.
3. `/signin` permite iniciar sesión.
4. `/signup` permite crear una cuenta.
5. `/about` muestra información de la app.
6. `/app` muestra el grid de películas.

## 3. Autenticación

1. El registro pide nombres, apellidos, correo y clave.
2. El inicio de sesión valida correo y clave.
3. Los datos se guardan en `localStorage`.

## 4. Dependencias

1. Vue.
2. Vue Router.
3. Vite.
4. Bootstrap instalado en `node_modules`.
5. Tailwind instalado en `node_modules`.
6. Lucide instalado en `node_modules`.

## 5. Ejecución

1. Instalar dependencias con `npm install`.
2. Ejecutar el proyecto con `npm run dev`.
