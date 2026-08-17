package com.example.paxrioverde.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import com.example.paxrioverde.ui.theme.PaxDesignSystem

/**
 * Campo de texto padronizado com a identidade visual do app.
 */
@Composable
fun PaxTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = PaxDesignSystem.Colors.BrandGreen) } },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = singleLine,
        enabled = enabled,
        readOnly = readOnly,
        shape = PaxDesignSystem.Shapes.Button, // Usando o mesmo arredondamento dos botões
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PaxDesignSystem.Colors.BrandGreen,
            focusedLabelColor = PaxDesignSystem.Colors.BrandGreen,
            unfocusedBorderColor = PaxDesignSystem.Colors.TextSecondary.copy(alpha = 0.3f),
            cursorColor = PaxDesignSystem.Colors.BrandGreen
        )
    )
}
