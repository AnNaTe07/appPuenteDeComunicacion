# appPuenteDeComunicacion

## Descripción

Aplicación móvil Android desarrollada en Java con arquitectura MVVM (ViewModel + LiveData) que permite la comunicación interna entre roles del ámbito educativo: tutores, docentes, preceptores, directivos y administradores.  
La app se conecta a una API REST interna (.NET) para autenticación y gestión de datos, usa Firebase para mensajería push y almacenamiento de imágenes, y WebSockets para manejar los estados de mensajes (enviado, recibido, leído).

## Tecnologías

- Java (Android)  
- MVVM (ViewModel + LiveData)  
- Firebase Cloud Messaging (notificaciones push)  
- Firebase Storage (imágenes)  
- API REST backend en .NET con Entity Framework y base de datos MySQL  
- JWT para autenticación  
- SharedPreferences para almacenar tokens  
- Interceptor HTTP para controlar expiración e invalidación del token y usar refresh token  
- WebSockets para actualizar el estado de los mensajes en tiempo real  

## Roles de usuario

- Tutor  
- Docente  
- Preceptor  
- Directivo  
- Administrador  

Los roles se almacenan en la base de datos. En cada request, el backend valida el token JWT y el rol para permitir o restringir funcionalidades en la app.

## Funcionalidades actuales

- Login / Logout  
- Mensajería interna entre usuarios  
- Edición de perfil del usuario  
- Notificaciones push en tiempo real  
- WebSocket usado para estados de mensajes:  
  - “Enviado” con 1 check  
  - “Recibido” con 2 checks  
  - “Leído” con cambio de color del check  
- Control del token expirado / inválido mediante interceptor y refresh token  
- Envío de notificaciones por email cuando aplica  

## Funcionalidades planificadas

- Acceso a calificaciones y asistencia (aún en desarrollo)  

## Seguridad / Autenticación

Cada request al backend que requiere autenticación está protegido con JWT.  
Tokens se guardan en SharedPreferences; se verifica la expiración o invalidación del token mediante un interceptor. Si el token expiró o es inválido, se compara el refresh token guardado con el de la base, y se obtiene un nuevo token si corresponde; en caso contrario, se obliga al usuario a iniciar sesión otra vez.

## Cómo funciona el flujo de mensajes

- Enviar mensaje → se guarda como enviado, marca 1 check  
- Cuando otro usuario lo recibe → marca 2 checks  
- Cuando el mensaje es leído → cambio visual (color) del check  
- Todo esto en tiempo real mediante WebSockets  

## Backend

- Plataforma .NET con Entity Framework para acceso a datos  
- Base de datos MySQL  
- API REST interna 
- Firebase para notificaciones push y almacenamiento de imágenes  




 
