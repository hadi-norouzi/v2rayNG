package com.v2ray.ang.ui

import android.app.Activity
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


val lightColorScheme = lightColorScheme()

val darkColorScheme = darkColorScheme(
    primary = Color(0xFF222222),
    onPrimary = Color(0xFFFFFFFF),
)

@Composable
fun V2rayNGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicTheming: Boolean = true,
    content: @Composable () -> Unit,
) {

    val colorScheme = when {
        dynamicTheming && supportsDynamicTheming() -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> if (darkTheme) darkColorScheme else lightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window?.statusBarColor =
                colorScheme.surface.toArgb() // surface becomes the the status bar color
            window?.navigationBarColor =
                colorScheme.primary.copy(alpha = 0.08f).compositeOver(colorScheme.surface.copy()).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !darkTheme

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !darkTheme // not darkTheme makes the status bar icons visible
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S