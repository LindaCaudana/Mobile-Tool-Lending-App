package com.example.dismov.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.dismov.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: LoanViewModel = viewModel(),
    token: String
) {
    val loans = viewModel.adminLoans
    val errorMessage = viewModel.errorMessage

    var textoBusqueda by remember { mutableStateOf(TextFieldValue("")) }
    var comentarioSeleccionado by remember { mutableStateOf<String?>(null) }

    val prestamosFiltrados = loans.filter {
        it.tool?.name?.contains(textoBusqueda.text, ignoreCase = true) == true ||
                it.user?.name?.contains(textoBusqueda.text, ignoreCase = true) == true
    }

    LaunchedEffect(Unit) {
        viewModel.fetchAllLoans(token)
    }

    Scaffold(
        containerColor = Color(0xFFE8F0FE)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F0FE))
                .padding(padding)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = { Text("Buscar préstamo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF1E3A5F),
                    unfocusedIndicatorColor = Color(0xFFBBBBBB)
                )
            )

            Text(
                text = "Préstamos",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A5F),
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(prestamosFiltrados) { loan ->
                    TarjetaPrestamoVisual(
                        herramienta = loan.tool?.name ?: "Herramienta desconocida",
                        usuario = loan.user?.name ?: "Desconocido",
                        fechaInicio = loan.startDate.take(10),
                        fechaFin = loan.endDate?.take(10),
                        notas = loan.notes,
                        entregado = loan.endDate != null,
                        onMostrarComentario = { comentarioSeleccionado = it }
                    )
                }
            }
        }

        if (comentarioSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { comentarioSeleccionado = null },
                confirmButton = {
                    Button(onClick = { comentarioSeleccionado = null }) {
                        Text("Cerrar")
                    }
                },
                title = { Text("Comentario") },
                text = {
                    Text(comentarioSeleccionado ?: "")
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun TarjetaPrestamoVisual(
    herramienta: String,
    usuario: String,
    fechaInicio: String,
    fechaFin: String?,
    notas: String?,
    entregado: Boolean,
    onMostrarComentario: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        border = BorderStroke(1.dp, Color(0xFF1E3A5F)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            val icon = if (entregado) Icons.Default.CheckCircle else Icons.Default.Warning
            val iconColor = if (entregado) Color(0xFF4CAF50) else Color(0xFFE53935)
            Icon(
                imageVector = icon,
                contentDescription = if (entregado) "Entregado" else "No entregado",
                tint = iconColor,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = herramienta,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                )
                Text("Usuario: $usuario", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Inicio: $fechaInicio", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)

                    if (entregado && !notas.isNullOrBlank()) {
                        Button(
                            onClick = { onMostrarComentario(notas) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A5F),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Comment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Comentario", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (entregado) {
                    Text("Devolución: ${fechaFin ?: "No disponible"}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                }
            }
        }
    }
}
