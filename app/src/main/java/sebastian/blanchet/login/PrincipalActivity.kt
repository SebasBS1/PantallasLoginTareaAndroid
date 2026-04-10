package sebastian.blanchet.login

import android.app.Activity
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import sebastian.blanchet.login.ui.theme.LoginTheme

class PrincipalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //var nombre = intent.getStringExtra("correo") ?: "Anonimo"

        var uid = Firebase.auth.currentUser?.uid ?: ""

        var myRef = Firebase.database.getReference("usuarios").child(uid)
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaInicio(
                        myRef = myRef,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(myRef: DatabaseReference, modifier: Modifier = Modifier) {

    var nombre by remember { mutableStateOf("Cargando...") }
    var correo by remember { mutableStateOf("Cargando...") }
    var edad by remember { mutableStateOf("0/0/0000") }


    var nombreNew by remember { mutableStateOf("") }
    var edadNew by remember { mutableStateOf("") }

    val context = LocalContext.current

    fun anios(fecha: String): String{
        val partir = fecha.split("/")
        val dia = partir[0].toInt()
        val mes = partir[1].toInt() - 1
        val anio = partir[2].toInt()

        val actual = Calendar.getInstance()
        val nacimiento = Calendar.getInstance()
        nacimiento.set(anio, mes, dia)

        var edad = (actual.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)).toString()
        return edad
    }

    var nums = anios(edad)


    myRef.get().addOnSuccessListener { snapshot ->
        nombre = snapshot.child("name").value.toString()
        correo = snapshot.child("correo").value.toString()
        edad = snapshot.child("fecha").value.toString()
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "BIENVENIDO", fontSize = 40.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$nombre", fontSize = 32.sp)
        //Spacer(modifier = modifier.height(4.dp))
        Text(text = "$correo", fontSize = 32.sp)
        //Spacer(modifier = modifier.height(4.dp))
        Text(text = "$nums", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Button(onClick = {
            Firebase.auth.signOut()

            (context as? Activity)?.finish()
        }) {
            Text(text = "Cerrar Sesión")
        }

        OutlinedTextField(
            value = edadNew,
            onValueChange = {edadNew = it},
            label = {Text(text = "Fecha de Nacimiento")},
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = nombreNew,
            onValueChange = {nombreNew = it},
            label = {Text(text = "Usuario")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = modifier.height(16.dp))
        Button(onClick = {
            if(edadNew.isEmpty() || nombreNew.isEmpty()){
                Toast.makeText(context, "No puede haber campos vaciosD", Toast.LENGTH_SHORT).show()
            }else{
                var update = mapOf(
                    "name" to nombreNew,
                    "fecha" to edadNew
                )
                myRef.updateChildren(update)
                myRef.get().addOnSuccessListener { snapshot ->
                    nombre = snapshot.child("name").value.toString()
                    correo = snapshot.child("correo").value.toString()
                    edad = snapshot.child("fecha").value.toString()
                }
                Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
            }

        }) {
            Text(text = "Actualizar datos")
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoginTheme {
        Greeting("Android")
    }
}*/