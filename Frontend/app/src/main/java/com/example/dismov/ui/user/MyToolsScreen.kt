package com.example.dismov.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dismov.viewmodel.LoanViewModel
import com.example.dismov.viewmodel.ToolViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyToolsScreen(
    token: String,
    userId: String
) {
    val loanViewModel: LoanViewModel = viewModel()
    val toolViewModel: ToolViewModel = viewModel()

    val loans = loanViewModel.loans
    val tools = toolViewModel.tools
    val errorMessage = loanViewModel.errorMessage

    val coroutineScope = rememberCoroutineScope()
    var loadingLoanId by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loanViewModel.fetchUserLoans(token)
        toolViewModel.fetchTools(token)
    }

    Scaffold(
        containerColor = Color(0xFFE8F0FE),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Herramientas",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A5F))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFE8F0FE))
                .padding(16.dp)
        ) {
            errorMessage?.let {
                Text(text = it, color = Color.Red)
            }

            successMessage?.let {
                Text(text = it, color = Color(0xFF388E3C))
            }

            val myLoans = loans.filter {
                it.user?._id == userId && it.status == "activo"
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(myLoans) { loan ->
                    val matchedTool = tools.find { it._id == loan.tool?._id }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = matchedTool?.name ?: "Herramienta desconocida",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E3A5F)
                                )
                            )
                            Text(
                                text = matchedTool?.description ?: "Sin descripción",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Fecha de entrega: ${loan.endDate?.substring(0, 10) ?: "No definida"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )

                            matchedTool?.image?.let { imagePath ->
                                AsyncImage(
                                    model = "http://10.0.2.2:3000/$imagePath",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .padding(top = 8.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        loadingLoanId = loan._id
                                        successMessage = null
                                        try {
                                            loanViewModel.returnLoan(token, loan._id)
                                            successMessage = "Herramienta devuelta exitosamente"
                                            loanViewModel.fetchUserLoans(token)
                                            toolViewModel.fetchTools(token)
                                        } catch (e: Exception) {
                                            successMessage = "Error al devolver herramienta"
                                        } finally {
                                            loadingLoanId = null
                                        }
                                    }
                                },
                                enabled = loadingLoanId != loan._id,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E3A5F),
                                    contentColor = Color.White
                                )
                            ) {
                                if (loadingLoanId == loan._id) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Devolver")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
