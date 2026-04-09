package sebastian.blanchet.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import sebastian.blanchet.login.ui.theme.LoginTheme

class ContrasenaActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaContrasena(
                        auth,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaContrasena(auth: FirebaseAuth, modifier: Modifier = Modifier) {
    var correo by remember() { mutableStateOf(value = "") }
    val context = LocalContext.current

    fun esCorreo(): Boolean{
        return correo.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    Column(
        modifier = modifier.fillMaxSize().padding(all = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Recupera tu Contraseña", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text(text = "Correo Electroónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row() {
            Button(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }) {
                Text(text = "Cancelar")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if (
                    esCorreo()
                ) {
                    auth.sendPasswordResetEmail(correo)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(
                                    context,
                                    "Se ha mandado un enlace a tu correo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else{
                                Toast.makeText(context, "Firebase murio", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Correo erróneo o vacío", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = "Enviar enlace de recuperación")
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    LoginTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            PantallaContrasena(

                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}*/