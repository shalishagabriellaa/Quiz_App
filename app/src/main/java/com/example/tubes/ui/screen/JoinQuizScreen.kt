package com.example.tubes.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size // <-- penting: ini yang benar
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy           = Color(0xFF121142) // top bar
private val Purple         = Color(0xFF4C4FA4) // primary button
private val BorderLavender = Color(0xFF9BA3E5)
private val TextGray       = Color(0xFF6F7294)

enum class JoinTab { PIN, QR }

@Composable
fun JoinQuizScreen(
    onBack: () -> Unit = {},
    onJoin: (String) -> Unit = {}
) {
    var tab by remember { mutableStateOf(JoinTab.PIN) }
    var pin by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF6F7FF),
                        Color(0xFFF1F2FF),
                        Color(0xFFF8F0FF)
                    )
                )
            )
    ) {
        Column(Modifier.fillMaxSize()) {

            /* ===== Top Bar ===== */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .background(Navy)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Mascot placeholder (kanan atas)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = Color.White
                        )
                    }
                }

                Text(
                    text = "Join Quiz",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(18.dp))

            /* ===== Segmented Tab ===== */
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                // jarak antar pill
                // pakai Spacer(weight) di dalam RowScope.TabPill
            ) {
                TabPill(
                    text = "Enter PIN",
                    selected = tab == JoinTab.PIN
                ) { tab = JoinTab.PIN }

                Spacer(modifier = Modifier.padding(7.dp)) // jarak horizontal kecil

                TabPill(
                    text = "Scan QR Code",
                    selected = tab == JoinTab.QR,
                    outlined = true
                ) { tab = JoinTab.QR }
            }

            Spacer(Modifier.height(28.dp))

            /* ===== Content ===== */
            if (tab == JoinTab.PIN) {
                // Input PIN besar di tengah
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .weight(1f), // child Column di dalam Column parent -> valid
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = pin,
                        onValueChange = { new -> pin = new.filter { it.isDigit() }.take(12) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        textStyle = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        ),
                        placeholder = {
                            Text(
                                "ENTER PIN",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextGray.copy(alpha = 0.55f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent
                        )
                    )
                }
            } else {
                // Tab QR (placeholder instruksi)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // child Box di dalam Column parent -> valid
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Scan a QR Code",
                        color = TextGray,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            /* ===== Join Now Button ===== */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .shadow(10.dp, RoundedCornerShape(28.dp), clip = false)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Purple)
                    .height(56.dp)
                    .clickable { onJoin(pin) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Join Now",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

/**
 * Dibuat sebagai RowScope extension agar Modifier.weight(1f) di dalamnya legal.
 */
@Composable
private fun RowScope.TabPill(
    text: String,
    selected: Boolean,
    outlined: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    val fillColor = if (selected) Purple else Color.White.copy(alpha = 0.4f)
    val textColor = if (selected) Color.White else Color(0xFF333A7A)

    // ✅ Jika selected DAN outlined = true (Scan QR) → border tetap 1.5dp
    // ✅ Jika selected DAN outlined = false (Enter PIN) → border hilang
    val borderWidth = if (selected && !outlined) 0.dp else 1.5.dp
    val borderColor = if (selected && !outlined) Color.Transparent else BorderLavender

    Box(
        modifier = Modifier
            .height(44.dp)
            .weight(1f) // valid karena berada dalam RowScope
            .clip(shape)
            .background(fillColor)
            .border(borderWidth, borderColor, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JoinQuizScreenPreview() {
    MaterialTheme {
        JoinQuizScreen()
    }
}
