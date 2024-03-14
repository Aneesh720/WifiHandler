package com.example.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WifiSettings()
                }
            }
        }
    }
}

class WifiHandler(private val context: Context) {

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    ///Not available Android10
    fun connectToWifi(ssid: String, password: String): Boolean {
        val specifier: NetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val request =
            NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build()


        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Connected successfully
                Log.d("WifiHandler", "Connected to $ssid")
            }

            override fun onUnavailable() {
                // Connection failed
                Log.e("WifiHandler", "Failed to connect to $ssid")
            }
        }

        connectivityManager.requestNetwork(request, networkCallback)

        return true
    }


}


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WifiSettings() {
    val context = LocalContext.current
    val wifiHandler = remember { WifiHandler(context) }
    val ssidState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = ssidState.value,
            onValueChange = { ssidState.value = it },
            label = { Text("SSID") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        TextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),

        )

        Button(
            onClick = {
                val ssid = ssidState.value
                val password = passwordState.value
                if (ssid.isNotEmpty() && password.isNotEmpty()) {
                    wifiHandler.connectToWifi(ssid, password)
                } else {
                    // Show error message or handle empty input cases
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Connect")
        }
    }
}