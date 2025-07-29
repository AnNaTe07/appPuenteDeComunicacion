package com.softannate.apppuentedecomunicacion.data.local;

import android.content.Context;
import com.softannate.apppuentedecomunicacion.utils.DateUtils;

/**
 * Clase utilitaria para verificar si los tokens de sesión han expirado.
 * <p>
 * Utiliza fechas almacenadas en {@link SpManager} y las compara con el momento actual
 * mediante {@link DateUtils}. Permite validar tanto el access token como el refresh token.
 */
public class TokenExpirationChecker {

    /**
     * Verifica si el token de acceso ha expirado.
     * @param context Contexto de aplicación
     * @return true si el token expiró, false si aún es válido
     */
    public static boolean isTokenExpired(Context context) {
        String exp = SpManager.getExpiracionToken(context);
        return DateUtils.isBeforeNow(exp);
    }

    /**
     * Verifica si el refresh token ha expirado.
     * @param context Contexto de aplicación
     * @return true si el refresh token expiró, false si aún es válido
     */
    public static boolean isRefreshTokenExpired(Context context) {
        String exp = SpManager.getExpiracionRefreshoken(context);
        return DateUtils.isBeforeNow(exp);
    }
}

