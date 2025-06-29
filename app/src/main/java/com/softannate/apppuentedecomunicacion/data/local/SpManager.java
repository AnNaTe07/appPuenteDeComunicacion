package com.softannate.apppuentedecomunicacion.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;

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

    private static String getString(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences != null ? sharedPreferences.getString(key, null) : null;
    }

    public static String getAccessToken(Context context) {
        initialize(context);
        return getString(secureSp, KEY_ACCESS_TOKEN);
    }

    public static String getRefreshToken(Context context) {
        initialize(context);
        return getString(secureSp, KEY_REFRESH_TOKEN);
    }

    public static void setAccessToken(Context context, String token) {
        initialize(context);
        putString(secureSp, KEY_ACCESS_TOKEN, token);
    }

    public static void setRefreshToken(Context context, String token) {
        initialize(context);
        putString(secureSp, KEY_REFRESH_TOKEN, token);
    }

    public static String getExpiracionToken(Context context) {
        initialize(context);
        return getString(sp, KEY_EXPIRACION_TOKEN);
    }

    public static  void setExpiracionToken(Context context, String token) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_EXPIRACION_TOKEN, token).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setExpiracionToken.");
        }
    }
    public static String getExpiracionRefreshoken(Context context) {
        initialize(context);
        return getString(sp, KEY_EXPIRACION_REFRESH_TOKEN);
    }

    public static  void setExpiracionRefreshToken(Context context, String token) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_EXPIRACION_REFRESH_TOKEN, token).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setExpiracionRefreshToken.");
        }
    }
    public static String getNombreCompleto(Context context) {
        initialize(context);
        return getString(sp, KEY_NOMBRE_COMPLETO);
    }

    public static  void setNombreCompleto(Context context, String token) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_NOMBRE_COMPLETO, token).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setExpiracionRefreshToken.");
        }
    }

    public static void setRol(Context context, String rol) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_ROL, rol).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setRol.");
        }
    }

    public static String getRol(Context context) {
        initialize(context);
        return getString(sp, KEY_ROL);
    }

    public static void setId(Context context, String id) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_ID, id).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setId.");
        }
    }

    public static String getId(Context context) {
        initialize(context);
        return getString(sp, KEY_ID);
    }

    public static String getEmail(Context context) {
        initialize(context);
        return getString(sp, KEY_EMAIL);
    }
    public static void setEmail(Context context, String email) {
        initialize(context);
        SharedPreferences.Editor editor = sp.edit();
        if (sp != null) {
            editor.putString(KEY_EMAIL, email).apply();
        } else {
            Log.e("SpManager", "Error: sp no inicializada al setEmail.");
        }
    }
/*
    public static void setEstablecimientosConCursos(Context context, List<EstablecimientoConCursos> lista) {
        initialize(context);
        if (lista != null) {
            Gson gson = new Gson();
            String json = gson.toJson(lista);
            sp.edit().putString(KEY_ESTABLECIMIENTOS_CON_CURSOS, json).apply();

            //cantidad de establecimientos y cursos
            sp.edit().putInt("cantidad_establecimientos", lista.size()).apply();
            int totalCursos = 0;
            for (EstablecimientoConCursos e : lista) {
                if (e.getCursos() != null) totalCursos += e.getCursos().size();
            }
            sp.edit().putInt("cantidad_cursos", totalCursos).apply();

            // Logs
            Log.d("SpManager", "Guardado JSON establecimientosConCursos: " + json);
            Log.d("SpManager", "Cantidad establecimientos: " + lista.size());
            Log.d("SpManager", "Cantidad total cursos: " + totalCursos);
        }
    }

 */
    /*
    public static List<EstablecimientoConCursos> getEstablecimientosConCursos(Context context) {
        initialize(context);
        String json = sp.getString(KEY_ESTABLECIMIENTOS_CON_CURSOS, null);
        if (json == null) return new ArrayList<>();
        try {
            Type type = new TypeToken<List<EstablecimientoConCursos>>() {}.getType();
            return new Gson().fromJson(json, type);
        } catch (JsonSyntaxException e) {
            Log.e("SpManager", "Error al deserializar establecimientos con cursos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

     */

    public static int getCantidadEstablecimientos(Context context) {
        initialize(context);
        return sp.getInt("cantidad_establecimientos", 0);
    }

    public static int getCantidadCursos(Context context) {
        initialize(context);
        return sp.getInt("cantidad_cursos", 0);
    }

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
            spEditor.apply();
            Log.d("SpManager", "Datos de usuario borrados.");
        }
        Log.d("SpManager", "Limpieza de sesión completada.");
    }
}