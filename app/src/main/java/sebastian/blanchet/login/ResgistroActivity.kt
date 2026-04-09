package sebastian.blanchet.login

import android.content.Intent
import android.icu.util.Calendar
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import sebastian.blanchet.login.ui.theme.LoginTheme
import java.sql.Date
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class ResgistroActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRegistro(
                        auth,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        auth = Firebase.auth
    }
}

@Composable
fun PantallaRegistro(auth: FirebaseAuth, modifier: Modifier = Modifier) {

    var correo by remember() { mutableStateOf(value = "") }
    var usuario by remember() { mutableStateOf(value = "")}
    var contra by remember() {mutableStateOf(value = "")}
    var contraComp by remember() {mutableStateOf(value = "")}
    var fecha by remember() {mutableStateOf(value = "")}
    val context = LocalContext.current

    fun edad(fecha: String): Boolean{
        val partir = fecha.split("/")
        val dia = partir[0].toInt()
        val mes = partir[1].toInt() - 1
        val anio = partir[2].toInt()

        val actual = Calendar.getInstance()
        val nacimiento = Calendar.getInstance()
        nacimiento.set(anio, mes, dia)

        var edad = actual.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

        if (edad >= 18){
            return true
        } else{ return false}
    }

    fun vacio(): Boolean{
        if (correo.isEmpty()){return false }
        if (usuario.isEmpty()){return false }
        if (contra.isEmpty()){return false }
        if (contraComp.isEmpty()){return false }
        if (fecha.isEmpty()){return false }
        return true
    }

    fun esCorreo(): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
    }

    Column(
        modifier = modifier.fillMaxSize().padding(all = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registrarse!", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = {correo = it},
            label = { Text(text = "Correo Electroónico")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = usuario,
            onValueChange = {usuario = it},
            label = {Text(text = "Nombre de Usuario")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = contra,
            onValueChange = {contra = it},
            label = {Text(text = "Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = contraComp,
            onValueChange = {contraComp = it},
            label = {Text(text = "Reescriba su Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = fecha,
            onValueChange = {fecha = it},
            label = {Text(text = "Fecha de Nacimiento")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(){
            Button(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }){
                Text(text = "Iniciar Sesion")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                if (vacio()
                    && contra == contraComp
                    && edad(fecha)
                    && esCorreo()
                    ){
                    auth.createUserWithEmailAndPassword(correo, contra)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(context, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(context, "Firebase murio", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else{
                    Toast.makeText(context, "Campo erróneo o vacío", Toast.LENGTH_SHORT).show()
                }
            }){
                Text(text = "Registrarse")
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    LoginTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            PantallaRegistro(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}*/