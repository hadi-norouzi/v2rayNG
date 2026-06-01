package com.v2ray.ang.ui

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import com.v2ray.ang.R

@Composable
fun V2RayNGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled by default to match existing XML colors exactly
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = colorResource(R.color.md_theme_primary),
            onPrimary = colorResource(R.color.md_theme_onPrimary),
            primaryContainer = colorResource(R.color.md_theme_primaryContainer),
            onPrimaryContainer = colorResource(R.color.md_theme_onPrimaryContainer),
            secondary = colorResource(R.color.md_theme_secondary),
            onSecondary = colorResource(R.color.md_theme_onSecondary),
            secondaryContainer = colorResource(R.color.md_theme_secondaryContainer),
            onSecondaryContainer = colorResource(R.color.md_theme_onSecondaryContainer),
            tertiary = colorResource(R.color.md_theme_tertiary),
            onTertiary = colorResource(R.color.md_theme_onTertiary),
            tertiaryContainer = colorResource(R.color.md_theme_tertiaryContainer),
            onTertiaryContainer = colorResource(R.color.md_theme_onTertiaryContainer),
            error = colorResource(R.color.md_theme_error),
            errorContainer = colorResource(R.color.md_theme_errorContainer),
            onError = colorResource(R.color.md_theme_onError),
            onErrorContainer = colorResource(R.color.md_theme_onErrorContainer),
            background = colorResource(R.color.md_theme_background),
            onBackground = colorResource(R.color.md_theme_onBackground),
            surface = colorResource(R.color.md_theme_surface),
            onSurface = colorResource(R.color.md_theme_onSurface),
            surfaceVariant = colorResource(R.color.md_theme_surfaceVariant),
            onSurfaceVariant = colorResource(R.color.md_theme_onSurfaceVariant),
            outline = colorResource(R.color.md_theme_outline),
            inverseSurface = colorResource(R.color.md_theme_inverseSurface),
            inverseOnSurface = colorResource(R.color.md_theme_inverseOnSurface),
            inversePrimary = colorResource(R.color.md_theme_inversePrimary),
        )
        else -> lightColorScheme(
            primary = colorResource(R.color.md_theme_primary),
            onPrimary = colorResource(R.color.md_theme_onPrimary),
            primaryContainer = colorResource(R.color.md_theme_primaryContainer),
            onPrimaryContainer = colorResource(R.color.md_theme_onPrimaryContainer),
            secondary = colorResource(R.color.md_theme_secondary),
            onSecondary = colorResource(R.color.md_theme_onSecondary),
            secondaryContainer = colorResource(R.color.md_theme_secondaryContainer),
            onSecondaryContainer = colorResource(R.color.md_theme_onSecondaryContainer),
            tertiary = colorResource(R.color.md_theme_tertiary),
            onTertiary = colorResource(R.color.md_theme_onTertiary),
            tertiaryContainer = colorResource(R.color.md_theme_tertiaryContainer),
            onTertiaryContainer = colorResource(R.color.md_theme_onTertiaryContainer),
            error = colorResource(R.color.md_theme_error),
            errorContainer = colorResource(R.color.md_theme_errorContainer),
            onError = colorResource(R.color.md_theme_onError),
            onErrorContainer = colorResource(R.color.md_theme_onErrorContainer),
            background = colorResource(R.color.md_theme_background),
            onBackground = colorResource(R.color.md_theme_onBackground),
            surface = colorResource(R.color.md_theme_surface),
            onSurface = colorResource(R.color.md_theme_onSurface),
            surfaceVariant = colorResource(R.color.md_theme_surfaceVariant),
            onSurfaceVariant = colorResource(R.color.md_theme_onSurfaceVariant),
            outline = colorResource(R.color.md_theme_outline),
            inverseSurface = colorResource(R.color.md_theme_inverseSurface),
            inverseOnSurface = colorResource(R.color.md_theme_inverseOnSurface),
            inversePrimary = colorResource(R.color.md_theme_inversePrimary),
        )
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
