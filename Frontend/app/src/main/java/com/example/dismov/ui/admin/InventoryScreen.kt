package com.example.dismov.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.dismov.models.Tool
import com.example.dismov.viewmodel.ToolViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(viewModel: ToolViewModel, token: String) {
    val tools = viewModel.tools
    val errorMessage = viewModel.errorMessage

    // Estados del formulario
    var showDialog by remember { mutableStateOf(false) }
    var editingToolId by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    // Búsqueda
    var textoBusqueda by remember { mutableStateOf(TextFieldValue("")) }
    val herramientasFiltradas = tools.filter {
        it.name.contains(textoBusqueda.text, ignoreCase = true)
    }

    // Cargar herramientas al inicio
    LaunchedEffect(Unit) {
        viewModel.fetchTools(token)
    }

    // Limpiar formulario
    fun clearForm() {
        editingToolId = null
        name = ""
        description = ""
        quantity = ""
        showDialog = false
    }

    Scaffold(
        containerColor = Color(0xFFE8F0FE),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    clearForm()
                    showDialog = true
                },
                containerColor = Color(0xFF1E3A5F),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar herramienta")
            }
        }
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
                    .padding(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF1E3A5F),
                    unfocusedIndicatorColor = Color(0xFFBBBBBB)
                )
            )

            Text(
                text = "Inventario",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A5F),
                ),
                modifier = Modifier.padding(start = 16.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn {
                items(herramientasFiltradas) { tool ->
                    TarjetaInventario(
                        name = tool.name,
                        description = tool.description,
                        availableQuantity = tool.availableQuantity,
                        imageUrl = tool.imageUrl,
                        onEdit = {
                            editingToolId = tool._id
                            name = tool.name
                            description = tool.description
                            quantity = tool.availableQuantity.toString()
                            showDialog = true
                        },
                        onDelete = {
                            viewModel.deleteTool(tool._id, token)
                        }
                    )
                }
            }
        }

        // Diálogo para añadir / editar herramienta
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        val q = quantity.toIntOrNull() ?: 0
                        if (editingToolId == null) {
                            viewModel.addTool(
                                name.trim(),
                                description.trim(),
                                q,
                                null,
                                token
                            )
                        } else {
                            viewModel.updateTool(
                                toolId = editingToolId!!,
                                name = name.trim(),
                                description = description.trim(),
                                quantity = q,
                                imageFile = null,
                                token = token
                            )
                        }
                        clearForm()
                    }) {
                        Text(if (editingToolId == null) "Guardar" else "Actualizar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = {
                    Text(
                        if (editingToolId == null) "Añadir herramienta" else "Editar herramienta",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                            label = { Text("Cantidad disponible") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun TarjetaInventario(
    name: String,
    description: String,
    availableQuantity: Int,
    imageUrl: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
            AsyncImage(
                model = imageUrl,
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
                    text = name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cantidad disponible: $availableQuantity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End,
                modifier = Modifier.width(88.dp)
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E3A5F),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", fontSize = 12.sp)
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar", fontSize = 12.sp)
                }
            }
        }
    }
}
