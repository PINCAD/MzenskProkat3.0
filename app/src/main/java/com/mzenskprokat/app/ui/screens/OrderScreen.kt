package com.mzenskprokat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mzenskprokat.app.models.*
import com.mzenskprokat.app.viewmodels.OrderViewModel

@Composable
@Preview
fun OrderScreen(
    viewModel: OrderViewModel = viewModel()
) {
    var customerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val orderState by viewModel.orderState.collectAsState()

    LaunchedEffect(orderState) {
        when (orderState) {
            is Result.Success -> {
                showSuccessDialog = true
            }
            is Result.Error -> {
                errorMessage = (orderState as Result.Error).message
                showErrorDialog = true
            }
            else -> {}
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Оформление заказа",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Заполните форму, и мы свяжемся с вами",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Информация о клиенте
        item {
            Text(
                text = "Контактные данные",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("Ваше имя *") },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон *") },
                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                placeholder = { Text("+7 (XXX) XXX-XX-XX") }
            )
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email *") },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                placeholder = { Text("example@mail.com") }
            )
        }

        // Информация о заказе
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Информация о заказе",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("Наименование продукции *") },
                leadingIcon = { Icon(Icons.Outlined.List, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Например: Х20Н80") }
            )
        }

        item {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Количество *") },
                leadingIcon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Например: 100 кг") }
            )
        }

        item {
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Комментарий (необязательно)") },
                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                placeholder = { Text("Дополнительная информация к заказу") }
            )
        }

        // Информационная карточка
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "После отправки заявки наш менеджер свяжется с вами для уточнения деталей и расчета стоимости заказа.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Кнопка отправки
        item {
            val isFormValid = customerName.isNotBlank() &&
                    phone.isNotBlank() &&
                    email.isNotBlank() &&
                    productName.isNotBlank() &&
                    quantity.isNotBlank()

            Button(
                onClick = {
                    val order = OrderRequest(
                        customerName = customerName,
                        phone = phone,
                        email = email,
                        productName = productName,
                        quantity = quantity,
                        comment = comment
                    )
                    viewModel.submitOrder(order)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid && orderState !is Result.Loading
            ) {
                if (orderState is Result.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Отправка...")
                } else {
                    Icon(Icons.Outlined.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Отправить заявку")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Диалог успешной отправки
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetOrderState()
                // Очистить форму
                customerName = ""
                phone = ""
                email = ""
                productName = ""
                quantity = ""
                comment = ""
            },
            icon = {
                Icon(
                    Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Заявка отправлена!") },
            text = {
                Text("Спасибо за вашу заявку! Наш менеджер свяжется с вами в ближайшее время для уточнения деталей заказа.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.resetOrderState()
                        customerName = ""
                        phone = ""
                        email = ""
                        productName = ""
                        quantity = ""
                        comment = ""
                    }
                ) {
                    Text("Отлично")
                }
            }
        )
    }

    // Диалог ошибки
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.resetOrderState()
            },
            icon = {
                Icon(
                    Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text("Ошибка") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog = false
                        viewModel.resetOrderState()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
