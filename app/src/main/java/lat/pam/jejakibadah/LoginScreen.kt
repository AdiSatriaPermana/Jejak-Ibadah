package lat.pam.jejakibadah

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*

@Composable
fun LoginScreen(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onToggleTheme: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onContinueOffline: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    // Mode Tab: 0 = Masuk, 1 = Daftar
    var selectedAuthTab by remember { mutableIntStateOf(0) }

    // State Input Form
    var fullName by remember { mutableStateOf("Aldi Pramana") }
    var email by remember { mutableStateOf("hamba.allah@domain.id") }
    var password by remember { mutableStateOf("123456789012") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // Palet Adaptif Light (#F8FAFC) & Night Mode (#0B1120)
    val screenBg = if (isDarkTheme) DarkCanvas else LightCanvas
    val cardBg = if (isDarkTheme) DarkCard else Color.White
    val subCardBg = if (isDarkTheme) DarkSubCard else Color(0xFFF1F5F9)
    val borderColor = if (isDarkTheme) DarkBorder else LightBorder
    val primaryText = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val secondaryText = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val accentBlue = if (isDarkTheme) BrandCyan else BrandBlue

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top App Bar (Tombol Kembali + Badge Aman & Terenkripsi + Toggle Night Mode)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onNavigateBack,
                    shape = CircleShape,
                    color = cardBg,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = primaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Badge Aman & Terenkripsi
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(cardBg)
                            .border(1.dp, borderColor, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = accentBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "AMAN & TERENKRIPSI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = secondaryText
                            )
                        }
                    }

                    // Tombol Cepat Switch Light / Night Mode
                    Surface(
                        onClick = onToggleTheme,
                        shape = CircleShape,
                        color = cardBg,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isDarkTheme) "☀️" else "🌙",
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Hero Brand Identity
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BrandBlue, BrandCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Logo Jejak Ibadah",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = 4.dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(cardBg)
                        .border(1.dp, borderColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = accentBlue,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Jejak Ibadah",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = primaryText
            )
            Text(
                text = "TEMAN SETIA IBADAH HARIAN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = accentBlue,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = if (selectedAuthTab == 0)
                    "Selamat Datang Kembali. Lanjutkan pencatatan ritme spiritual harian Anda."
                else
                    "Buat akun baru untuk menyinkronkan streak dan riwayat ibadah Anda.",
                fontSize = 13.sp,
                color = secondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .widthIn(max = 290.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Segmented Tab Switcher (Masuk vs Daftar)
            Surface(
                shape = CircleShape,
                color = subCardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val tabs = listOf("Masuk", "Daftar")
                    tabs.forEachIndexed { index, title ->
                        val active = selectedAuthTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(if (active) BrandBlue else Color.Transparent)
                                .clickable { selectedAuthTab = index }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                color = if (active) Color.White else secondaryText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Tombol Masuk dengan Google + Logo 4-Warna Custom Canvas
            Surface(
                onClick = onLoginSuccess,
                shape = RoundedCornerShape(14.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GoogleFourColorIcon(modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (selectedAuthTab == 0) "Masuk dengan Google" else "Daftar dengan Google",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryText
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = accentBlue,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Pencadangan streak & ibadah otomatis",
                    fontSize = 11.sp,
                    color = secondaryText
                )
            }

            // 5. Divider "ATAU DENGAN EMAIL"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = borderColor
                )
                Text(
                    text = "ATAU DENGAN EMAIL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.7.sp,
                    color = secondaryText,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = borderColor
                )
            }

            // 6. Form Input Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Input Nama Lengkap (khusus tab Daftar)
                if (selectedAuthTab == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Nama Lengkap",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryText
                        )
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            placeholder = { Text("Masukkan nama lengkap", fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = secondaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = cardBg,
                                unfocusedContainerColor = cardBg,
                                focusedBorderColor = accentBlue,
                                unfocusedBorderColor = borderColor,
                                focusedTextColor = primaryText,
                                unfocusedTextColor = primaryText
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Input Alamat Email
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Alamat Email",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryText
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        placeholder = { Text("nama@email.com", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = secondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedBorderColor = accentBlue,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = primaryText,
                            unfocusedTextColor = primaryText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Input Kata Sandi
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Kata Sandi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryText
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        placeholder = { Text("Masukkan kata sandi", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = secondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = if (passwordVisible) "Sembunyi" else "Lihat",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accentBlue,
                                modifier = Modifier
                                    .clickable { passwordVisible = !passwordVisible }
                                    .padding(end = 12.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = cardBg,
                            unfocusedContainerColor = cardBg,
                            focusedBorderColor = accentBlue,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = primaryText,
                            unfocusedTextColor = primaryText
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Ingat Saya & Lupa Kata Sandi
                if (selectedAuthTab == 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { rememberMe = !rememberMe }
                        ) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = BrandBlue,
                                    checkmarkColor = Color.White
                                ),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ingat Saya",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = secondaryText
                            )
                        }

                        Text(
                            text = "Lupa Kata Sandi?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = accentBlue,
                            modifier = Modifier.clickable { }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Tombol Utama: Masuk ke Akun / Daftar Sekarang
                Button(
                    onClick = onLoginSuccess,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandBlue,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (selectedAuthTab == 0) "Masuk ke Akun" else "Daftar Sekarang",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7. Tombol Lanjutkan Tanpa Akun (Mode Offline 100%)
            Surface(
                onClick = onContinueOffline,
                shape = RoundedCornerShape(14.dp),
                color = subCardBg,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = accentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lanjutkan Tanpa Akun (Mode Offline 100%)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryText
                    )
                }
            }

            Text(
                text = "Data hanya tersimpan di perangkat lokal tanpa internet.",
                fontSize = 11.sp,
                color = secondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 8. Footer Syarat & Kebijakan Privasi
            val policyText = buildAnnotatedString {
                append("Dengan masuk, Anda menyetujui ")
                withStyle(SpanStyle(color = accentBlue, fontWeight = FontWeight.SemiBold)) {
                    append("Syarat Ketentuan")
                }
                append(" & ")
                withStyle(SpanStyle(color = accentBlue, fontWeight = FontWeight.SemiBold)) {
                    append("Kebijakan Privasi")
                }
                append(" Jejak Ibadah.")
            }
            Text(
                text = policyText,
                fontSize = 11.sp,
                color = secondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun GoogleFourColorIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 3.2.dp.toPx()
        val diameter = size.minDimension - stroke
        val topLeft = Offset(stroke / 2f, stroke / 2f)
        val arcSize = Size(diameter, diameter)

        // Merah (atas)
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = -145f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        // Kuning (kiri)
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 145f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        // Hijau (bawah)
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 40f,
            sweepAngle = 105f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        // Biru (kanan)
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -10f,
            sweepAngle = 55f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Butt)
        )
        // Garis horizontal huruf G
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(size.width * 0.52f, size.height * 0.5f),
            end = Offset(size.width * 0.92f, size.height * 0.5f),
            strokeWidth = stroke
        )
    }
}