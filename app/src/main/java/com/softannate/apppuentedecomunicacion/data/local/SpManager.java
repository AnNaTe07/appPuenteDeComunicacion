package com.softannate.apppuentedecomunicacion.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softannate.apppuentedecomunicacion.modelos.dto.AlumnoDto;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para gestionar el almacenamiento local de datos del usuario.
 * <p>
 * Provee métodos para guardar y recuperar datos desde SharedPreferences y
 * EncryptedSharedPreferences. Ideal para mantener la sesión activa y proteger
 * datos sensibles como tokens de acceso.
 * <p>
 * Requiere inicialización con {@link #initialize(Context)} antes de utilizar.
 */
public class SpManager {

    private static final String PREF_NAME = "datosUsuario.xml";
    private static final String SECURE_PREF_NAME = "secure_datosUsuario.xml";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_ROL = "rol";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ID = "id";
    private static final String KEY_NOMBRE_COMPLETO = "nombre_completo";
    private static final String KEY_EXPIRACION_TOKEN = "expiracion_token";
    private static final String KEY_EXPIRACION_REFRESH_TOKEN = "refresh_expiracion";
    private static final String KEY_ESTABLECIMIENTOS_CON_CURSOS = "establecimientos_con_cursos";
    private static final String KEY_HIJOS_LIST = "hijos_list";

    private static SharedPreferences sp;
    private static SharedPreferences secureSp;

    public SpManager(Context context) {

        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            secureSp = EncryptedSharedPreferences.create(
                    SECURE_PREF_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inicializando EncryptedSharedPreferences", e);
        }
        sp=getDefaultPreferences(context);
    }

    /**
     * Inicializa las preferencias normales y cifradas para su uso.
     * @param context Contexto de aplicación
     */
    public static void initialize(Context context) {
        if (secureSp == null) {
            secureSp = getSecurePreferences(context);
        }
        if (sp == null) {
            sp = getDefaultPreferences(context);
        }
    }

    private static SharedPreferences getSecurePreferences(Context context) {

        try {
            // MasterKey para la encriptación
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            // Crea y devuelve la instancia de EncryptedSharedPreferences
            return EncryptedSharedPreferences.create(
                    context,
                    SECURE_PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // Esquema para encriptar las claves
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Esquema para encriptar los valores
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e("SpManager", "Error al inicializar EncryptedSharedPreferences", e);
            return null;
        }
    }

    private static SharedPreferences getDefaultPreferences(Context context) {
        return context.getSharedPreferences(SpManager.PREF_NAME, Context.MODE_PRIVATE);
    }


    // ------------------------------------ MÉTODOS INTERNOS ----------------------------------------

    /**
     * Guarda un string en las preferencias normales.
     */
    private static boolean putString(SharedPreferences sharedPreferences, String key, String value) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
            return true;
        } else {
            Log.e("SpManager", "Error: SharedPreferences no inicializada al guardar " + key);
            return false;
        }
    }

    /**
     * Obtiene un string desde las preferencias normales.
     */
    private static String getString(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences != null ? sharedPreferences.getString(key, null) : null;
    }
    // -------------------------------------- TOKENS ---------------------------------------------
    /**
     * Retorna el token de acceso guardado en EncryptedSharedPreferences.
     * @param context Contexto de la aplicación
     * @return Token de acceso (access_token) o null si no existe
     */
    public static String getAccessToken(Context context) {
        initialize(context);
        return getString(secureSp, KEY_ACCESS_TOKEN);
    }

    /**
     * Obtiene el refresh token guardado de forma segura.
     * @param context Contexto de aplicación
     * @return Refresh token o null si no existe
     */
    public static String getRefreshToken(Context context) {
        initialize(context);
        return getString(secureSp, KEY_REFRESH_TOKEN);
    }

    /**
     * Guarda el token de acceso en EncryptedSharedPreferences.
     * @param context Contexto de la aplicación
     * @param token Token JWT de acceso
     */
    public static void setAccessToken(Context context, String token) {
        initialize(context);
        putString(secureSp, KEY_ACCESS_TOKEN, token);
    }

    /**
     * Guarda el refresh token en almacenamiento cifrado.
     * @param context Contexto de aplicación
     * @param token Refresh token a guardar
     */
    public static void setRefreshToken(Context context, String token) {
        initialize(context);
        putString(secureSp, KEY_REFRESH_TOKEN, token);
    }

    // -------------------------------------- EXPIRACIÓN DE TOKENS --------------------------------------------

    /**
     * Obtiene la fecha de expiración del access token.
     * @param context Contexto de aplicación
     * @return Fecha de expiración en formato String, o null si no existe
     */
    public static String getExpiracionToken(Context context) {
        initialize(context);
        return getString(sp, KEY_EXPIRACION_TOKEN);
    }

    /**
     * Guarda la fecha de expiración del access token.
     * @param context Contexto de aplicación
     * @param token Fecha de expiración en formato String
     */
    public static  void setExpiracionToken(Context context, String token) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_EXPIRACION_TOKEN, token).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setExpiracionToken.");
        }
    }

    /**
     * Obtiene la fecha de expiración del refresh token.
     * @param context Contexto de aplicación
     * @return Fecha de expiración en formato String, o null si no existe
     */
    public static String getExpiracionRefreshoken(Context context) {
        initialize(context);
        return getString(sp, KEY_EXPIRACION_REFRESH_TOKEN);
    }

    /**
     * Guarda la fecha de expiración del refresh token.
     * @param context Contexto de aplicación
     * @param token Fecha de expiración en formato String
     */
    public static  void setExpiracionRefreshToken(Context context, String token) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_EXPIRACION_REFRESH_TOKEN, token).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setExpiracionRefreshToken.");
        }
    }

    // ----------------------------------- DATOS DE USUARIO ----------------------------------

    /**
     * Obtiene el nombre completo del usuario.
     * @param context Contexto de aplicación
     * @return Nombre completo o string vacío
     */
    public static String getNombreCompleto(Context context) {
        initialize(context);
        return getString(sp, KEY_NOMBRE_COMPLETO);
    }

    /**
     * Guarda el nombre completo del usuario.
     * @param context Contexto de aplicación
     * @param token Nombre completo
     */
    public static  void setNombreCompleto(Context context, String token) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_NOMBRE_COMPLETO, token).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setExpiracionRefreshToken.");
        }
    }

    /**
     * Guarda el rol asignado al usuario.
     * @param context Contexto de aplicación
     * @param rol Rol como string
     */
    public static void setRol(Context context, String rol) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_ROL, rol).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setRol.");
        }
    }

    /**
     * Recupera el rol del usuario.
     * @param context Contexto de aplicación
     * @return Rol como string
     */
    public static String getRol(Context context) {
        initialize(context);
        return getString(sp, KEY_ROL);
    }

    // -------------------------------------- ID DEL USUARIO --------------------------------------------------

    /**
     * Guarda el ID del usuario.
     * @param context Contexto de aplicación
     * @param id ID a guardar como String
     */
    public static void setId(Context context, String id) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_ID, id).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setId.");
        }
    }

    /**
     * Obtiene el ID del usuario.
     * @param context Contexto de aplicación
     * @return ID como String o null si no existe
     */
    public static String getId(Context context) {
        initialize(context);
        return getString(sp, KEY_ID);
    }

    /**
     * Obtiene el email almacenado del usuario.
     * @param context Contexto de aplicación
     * @return Email o string vacío si no existe
     */
    public static String getEmail(Context context) {
        initialize(context);
        return getString(sp, KEY_EMAIL);
    }

    /**
     * Guarda el email del usuario.
     * @param context Contexto de aplicación
     * @param email Email a guardar
     */
    public static void setEmail(Context context, String email) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_EMAIL, email).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setEmail.");
        }
    }

// -------------------------------------- LISTA DE ALUMNOS ----------------------------------------------

    /**
     * Guarda la lista de alumnos serializada en formato JSON.
     * @param context Contexto de aplicación
     * @param lista Lista de objetos AlumnoDto
     */
    public static void setAlumnosList(Context context, List<AlumnoDto> lista) {
        initialize(context);
        if (sp != null) {
            Gson gson = new Gson();
            String json = gson.toJson(lista);
            sp.edit().putString(KEY_HIJOS_LIST, json).apply();
        } else {
            Log.e("SpManager", "sp no inicializada al guardar lista de alumnos");
        }
    }

    /**
     * Obtiene la lista de alumnos deserializada desde las preferencias.
     * @param context Contexto de aplicación
     * @return Lista de AlumnoDto, vacía si no hay datos guardados
     */
    public static List<AlumnoDto> getAlumnosList(Context context) {
        initialize(context);
        if (sp != null) {
            String json = sp.getString(KEY_HIJOS_LIST, null);
            if (json != null) {
                Type type = new TypeToken<List<AlumnoDto>>() {}.getType();
                return new Gson().fromJson(json, type);
            }
        }
        return new ArrayList<>();
    }

    // ---------------------------------------- UTILIDADES --------------------------------------
    /**
     * Elimina tokens seguros y datos de usuario persistidos en sesión.
     * @param context Contexto de la aplicación
     */
    public static void clearTokens(Context context) {
        initialize(context);
        if (secureSp != null) {
            SharedPreferences.Editor secureEditor = secureSp.edit();
            secureEditor.remove(KEY_ACCESS_TOKEN);
            secureEditor.remove(KEY_REFRESH_TOKEN);
            secureEditor.apply();
            Log.d("SpManager", "Tokens seguros borrados.");
        }

        if (sp != null) {
            SharedPreferences.Editor spEditor = sp.edit();
            spEditor.remove(KEY_ROL);
            spEditor.remove(KEY_EMAIL);
            spEditor.remove(KEY_ID);
            spEditor.remove(KEY_ESTABLECIMIENTOS_CON_CURSOS);
            spEditor.remove(KEY_HIJOS_LIST);

            spEditor.apply();
            Log.d("SpManager", "Datos de usuario borrados.");
        }
        Log.d("SpManager", "Limpieza de sesión completada.");
    }
}