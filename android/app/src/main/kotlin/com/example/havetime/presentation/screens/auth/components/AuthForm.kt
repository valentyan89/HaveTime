package com.example.havetime.presentation.screens.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.R
import com.example.havetime.presentation.common.utils.HaveText

@Composable
internal fun AuthForm(
    loginInput: String,
    passwordInput: String,
    isLoading: Boolean,
    onLoginChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HaveText(
            text = stringResource(R.string.authorization),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = loginInput,
            onValueChange = onLoginChanged,
            label = { HaveText(stringResource(R.string.login_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwordInput,
            onValueChange = onPasswordChanged,
            label = { HaveText(stringResource(R.string.password_label)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier.weight(1f),
                enabled =
                !isLoading &&
                    loginInput.isNotBlank() &&
                    passwordInput.isNotBlank()
            ) {
                HaveText(stringResource(R.string.registration))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onLogin,
                modifier = Modifier.weight(1f),
                enabled =
                !isLoading &&
                    loginInput.isNotBlank() &&
                    passwordInput.isNotBlank()
            ) {
                HaveText(stringResource(R.string.login_button))
            }
        }
    }
}
