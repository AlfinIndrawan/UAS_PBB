package com.example.uas_pbb_alfin


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uas_pbb_alfin.ui.theme.UAS_PBB_AlfinTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class Transaction(
    val shortDescription: String,
    val nominalTransaction: String,
    val date: String
)

val dummyData = listOf(
    Transaction("Groceries", "50000", "2024-07-01"),
    Transaction("Rent", "1000000", "2024-07-02"),
    Transaction("Utilities", "150000", "2024-07-03"),
    Transaction("Internet", "60000", "2024-07-04"),
    Transaction("Dinner", "700000", "2024-07-05"),
    Transaction("Gym Membership", "400000", "2024-07-06"),
    Transaction("Movie", "320000", "2024-07-07"),
    Transaction("Books", "2000000", "2024-07-08"),
    Transaction("Clothes", "250000", "2024-07-09"),
    Transaction("Subscription", "150000", "2024-07-10")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UAS_PBB_AlfinTheme {
                val firstNominal = rememberSaveable { mutableStateOf("") }
                val lastNominal = rememberSaveable { mutableStateOf("") }
                val transactions = rememberSaveable { mutableStateOf(dummyData) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Greeting(
                            name = "Android",
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Add space between text and button
                        FilterAndTransactionList(
                            firstNominal = firstNominal.value,
                            onFirstNominalChange = { firstNominal.value = it },
                            lastNominal = lastNominal.value,
                            onLastNominalChange = { lastNominal.value = it },
                            onButtonClick = {
                                Log.d("MainActivity", "First Nominal: ${firstNominal.value}, Last Nominal: ${lastNominal.value}")
                                transactions.value = dummyData.filter {
                                    val nominal = it.nominalTransaction.toIntOrNull() ?: 0
                                    val first = firstNominal.value.toIntOrNull() ?: Int.MIN_VALUE
                                    val last = lastNominal.value.toIntOrNull() ?: Int.MAX_VALUE
                                    nominal in first..last
                                }
                            },
                            transactions = transactions.value
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
        modifier = modifier.padding(top = 16.dp),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun TransactionList(transactions: List<Transaction>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(transactions) { transaction ->
            TransactionItem(transaction)
            Divider()
        }
    }
}

@Composable
fun FilterAndTransactionList(
    firstNominal: String,
    onFirstNominalChange: (String) -> Unit,
    lastNominal: String,
    onLastNominalChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    transactions: List<Transaction>
) {
    val filteredTransactions = transactions.filter {
        val nominal = it.nominalTransaction.toIntOrNull() ?: 0
        val first = firstNominal.toIntOrNull() ?: Int.MIN_VALUE
        val last = lastNominal.toIntOrNull() ?: Int.MAX_VALUE
        nominal in first..last
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        FilterRow(
            firstNominal = firstNominal,
            onFirstNominalChange = onFirstNominalChange,
            lastNominal = lastNominal,
            onLastNominalChange = onLastNominalChange,
            onButtonClick = onButtonClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        TransactionList(transactions = filteredTransactions)
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = "Description: ${transaction.shortDescription}")
        Text(text = "Nominal: ${transaction.nominalTransaction}")
        Text(text = "Date: ${transaction.date}")
    }
}

@Composable
fun MyButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White // Background color
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.filter_button), // Replace with your image resource ID
            contentDescription = "Button Image",
            modifier = Modifier.size(40.dp) // Set the size of the image
        )
    }
}

@Composable
fun FilterRow(
    firstNominal: String,
    onFirstNominalChange: (String) -> Unit,
    lastNominal: String,
    onLastNominalChange: (String) -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        MyButton(onClick = onButtonClick)
        Spacer(modifier = Modifier.width(16.dp)) // Gap between button and first text field
        TextField(
            value = firstNominal,
            onValueChange = onFirstNominalChange,
            placeholder = { Text("Filter First Nominal") },
            modifier = Modifier.weight(1f) // Make the text field take available space
        )
        Spacer(modifier = Modifier.width(16.dp)) // Gap between text fields
        TextField(
            value = lastNominal,
            onValueChange = onLastNominalChange,
            placeholder = { Text("Filter Last Nominal") },
            modifier = Modifier.weight(1f) // Make the text field take available space
        )
    }
}

@Composable
fun MyButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { /* Do something when the button is clicked */ },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, // Background color
//            contentColor = Color.Black // Icon color
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.filter_button), // Replace with your image resource ID
            contentDescription = "Button Image" ,
            modifier = Modifier.size(40.dp) // Set the size of the image
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UAS_PBB_AlfinTheme {
        val firstNominal = rememberSaveable { mutableStateOf("") }
        val lastNominal = rememberSaveable { mutableStateOf("") }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Greeting("Android", modifier = Modifier.padding(top = 16.dp))
            Spacer(modifier = Modifier.height(16.dp)) // Add space between text and button
            FilterRow(
                firstNominal = firstNominal.value,
                onFirstNominalChange = { firstNominal.value = it },
                lastNominal = lastNominal.value,
                onLastNominalChange = { lastNominal.value = it },
                onButtonClick = {
                    Log.d("GreetingPreview", "First Nominal: ${firstNominal.value}, Last Nominal: ${lastNominal.value}")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterAndTransactionListPreview() {
    val firstNominal = rememberSaveable { mutableStateOf("") }
    val lastNominal = rememberSaveable { mutableStateOf("") }

    UAS_PBB_AlfinTheme {
        FilterAndTransactionList(
            firstNominal = firstNominal.value,
            onFirstNominalChange = { firstNominal.value = it },
            lastNominal = lastNominal.value,
            onLastNominalChange = { lastNominal.value = it },
            onButtonClick = {
                Log.d("FilterAndTransactionListPreview", "First Nominal: ${firstNominal.value}, Last Nominal: ${lastNominal.value}")
            },
            transactions = dummyData
        )
    }
}