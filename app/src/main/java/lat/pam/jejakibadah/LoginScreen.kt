package lat.pam.jejakibadah

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    isDarkTheme: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f,
    onToggleTheme: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onContinueOffline: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    // 0 = Masuk, 1 = Daftar
    var authTab by remember { mutableIntStateOf(0) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("hamba.allah@domain.id") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotSheet by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var resetSentMessage by remember { mutableStateOf<String?>(null) }

    val screenBg = MaterialTheme.colorScheme.background
    val cardBg = MaterialTheme.colorScheme.surface
    val subCardBg = if (isDarkTheme) DarkSubCard else Color(0xFFF1F5F9)
    val borderColor = MaterialTheme.colorScheme.outline
    val primaryText = MaterialTheme.colorScheme.onSurface
    val secondaryText = MaterialTheme.colorScheme.onSurfaceVariant
    val accentBlue = if (isDarkTheme) BrandCyan else BrandBlue

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = screenBg,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(max = 640.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tombol Kembali / Lewati ke Offline
                    Surface(
                        onClick = onNavigateBack,
                        shape = CircleShape,
                        color = cardBg,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Kembali",
                                tint = primaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Badge Tengah: AMAN & TERENKRIPSI (Sesuai Prototipe Hal. 1)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(cardBg)
                            .border(1.dp, borderColor, CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = accentBlue,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "AMAN & TERENKRIPSI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = secondaryText,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    // Toggle Light / Dark Mode
                    Surface(
                        onClick = onToggleTheme,
                        shape = CircleShape,
                        color = cardBg,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                contentDescription = "Ganti Tema",
                                tint = accentBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Hero Brand Identity (Sesuai Prototipe Hal. 1)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Logo Jejak Ibadah",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
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
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = accentBlue,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Jejak Ibadah",
                        fontSize = 24.sp,
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
                        text = if (authTab == 0) {
                            "Selamat Datang Kembali. Lanjutkan pencatatan ritme spiritual harian Anda."
                        } else {
                            "Buat akun untuk mencadangkan streak dan riwayat ibadah Anda secara otomatis."
                        },
                        fontSize = 12.sp,
                        color = secondaryText,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .widthIn(max = 320.dp)
                    )
                }

                // 2. Segmented Tab Masuk / Daftar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(subCardBg)
                        .border(1.dp, borderColor, CircleShape)
                        .padding(4.dp)
                ) {
                    listOf("Masuk", "Daftar").forEachIndexed { index, label ->
                        val active = authTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(CircleShape)
                                .background(if (active) BrandBlue else Color.Transparent)
                                .clickable {
                                    authTab = index
                                    errorMessage = null
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                color = if (active) Color.White else secondaryText
                            )
                        }
                    }
                }

                // 3. Tombol Masuk dengan Google (Pencadangan streak & ibadah otomatis)
                Surface(
                    onClick = onLoginSuccess,
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Color(0xFF0C2340) else Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = accentBlue
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (authTab == 0) "Masuk dengan Google" else "Daftar dengan Google",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryText
                            )
                            Text(
                                text = "Pencadangan streak & ibadah otomatis",
                                fontSize = 11.sp,
                                color = secondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Divider "ATAU DENGAN EMAIL"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = borderColor
                    )
                    Text(
                        text = "ATAU DENGAN EMAIL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        color = secondaryText,
                        maxLines = 1,
                        softWrap = false
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = borderColor
                    )
                }

                // 4. Form Email & Kata Sandi
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (authTab == 1) {
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
                                    placeholder = { Text("Masukkan nama lengkap", fontSize = 13.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.Person,
                                            contentDescription = null,
                                            tint = accentBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Alamat Email
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Alamat Email",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryText
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    errorMessage = null
                                },
                                placeholder = { Text("hamba.allah@domain.id", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Email,
                                        contentDescription = null,
                                        tint = accentBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Kata Sandi
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Kata Sandi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryText
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    errorMessage = null
                                },
                                placeholder = { Text("Minimal 6 karakter", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Lock,
                                        contentDescription = null,
                                        tint = accentBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                            contentDescription = "Tampilkan Sandi",
                                            tint = secondaryText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Ingat Saya & Lupa Kata Sandi
                        if (authTab == 0) {
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
                                        colors = CheckboxDefaults.colors(checkedColor = BrandBlue)
                                    )
                                    Text(
                                        text = "Ingat Saya",
                                        fontSize = 12.sp,
                                        color = secondaryText
                                    )
                                }

                                Text(
                                    text = "Lupa Kata Sandi?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = accentBlue,
                                    modifier = Modifier.clickable {
                                        resetEmail = email
                                        resetSentMessage = null
                                        showForgotSheet = true
                                    }
                                )
                            }
                        }

                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                fontSize = 12.sp,
                                color = if (isDarkTheme) Color(0xFFF87171) else Color(0xFFDC2626)
                            )
                        }

                        // Tombol Utama: Masuk ke Akun / Daftar Akun
                        Button(
                            onClick = {
                                if (email.isBlank() || !email.contains("@")) {
                                    errorMessage = "Masukkan alamat email yang valid."
                                } else if (password.length < 6) {
                                    errorMessage = "Kata sandi minimal 6 karakter."
                                } else {
                                    onLoginSuccess()
                                }
                            },
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
                                text = if (authTab == 0) "Masuk ke Akun" else "Daftar Akun Baru",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // 5. Opsi Akses Cepat Mode Offline (Tanpa Login)
                TextButton(
                    onClick = onContinueOffline,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Lanjutkan Tanpa Akun (Mode Offline Lokal)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = secondaryText
                    )
                }
            }
        }

        // BottomSheet Lupa Kata Sandi
        if (showForgotSheet) {
            ModalBottomSheet(
                onDismissRequest = { showForgotSheet = false },
                containerColor = cardBg,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Reset Kata Sandi",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryText
                    )
                    Text(
                        text = "Masukkan email terdaftar untuk menerima tautan pengaturan ulang kata sandi.",
                        fontSize = 13.sp,
                        color = secondaryText
                    )
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Alamat Email") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (resetSentMessage != null) {
                        Text(
                            text = resetSentMessage ?: "",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) EmeraldDark else EmeraldLight
                        )
                    }
                    Button(
                        onClick = {
                            resetSentMessage = "Tautan reset kata sandi telah dikirim ke $resetEmail."
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text("Kirim Tautan Reset", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}