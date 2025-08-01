package com.example.dismov.ui.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dismov.viewmodel.ToolViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailableToolsScreen(viewModel: ToolViewModel, token: String) {
    val tools by remember { derivedStateOf { viewModel.tools } }
    val errorMessage by remember { derivedStateOf { viewModel.errorMessage } }

    val coroutineScope = rememberCoroutineScope()
    var loadingToolId by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var textoBusqueda by remember { mutableStateOf(TextFieldValue("")) }
    val herramientasFiltradas = tools.filter {
        it.name.contains(textoBusqueda.text, ignoreCase = true)
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedToolId by remember { mutableStateOf<String?>(null) }
    var selectedToolName by remember { mutableStateOf<String>("") }

    LaunchedEffect(Unit) {
        viewModel.fetchTools(token)
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
                placeholder = { Text("Buscar herramienta") },
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
                text = "Herramientas disponibles",
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

            if (successMessage != null) {
                Text(
                    text = successMessage ?: "",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(herramientasFiltradas) { tool ->
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
                            AsyncImage(
                                model = tool.imageUrl,
                                contentDescription = "Imagen herramienta",
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(Color.White, shape = RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = tool.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                )
                                Text(
                                    text = tool.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray,
                                    maxLines = 3
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Stock disponible: ${tool.availableQuantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }

                            Button(
                                onClick = {
                                    selectedToolId = tool._id
                                    selectedToolName = tool.name
                                    showConfirmDialog = true
                                },
                                enabled = tool.availableQuantity > 0 && loadingToolId != tool._id,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E3A5F),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (loadingToolId == tool._id) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pedir", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showConfirmDialog && selectedToolId != null) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                loadingToolId = selectedToolId
                                successMessage = null
                                try {
                                    viewModel.requestLoan(
                                        selectedToolId!!,
                                        token,
                                        onSuccess = {
                                            successMessage = "Préstamo solicitado para $selectedToolName"
                                            viewModel.fetchTools(token)
                                        },
                                        onError = {
                                            successMessage = "Error al pedir prestada la herramienta: $it"
                                        }
                                    )
                                } finally {
                                    loadingToolId = null
                                    showConfirmDialog = false
                                }
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Confirmar préstamo") },
                text = { Text("¿Deseas pedir la herramienta \"$selectedToolName\"?") },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}
