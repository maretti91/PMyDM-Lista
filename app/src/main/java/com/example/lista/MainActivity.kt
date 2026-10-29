package com.example.lista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lista.ui.theme.ListaTheme
data class Participante(  //Define clase para almacenar datos
    val nombre: String,  //var permite cambiar su valor, val no reasigna.
    val alias: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListaTheme {  //MaterialThem si falla
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->  //permite respetar los espacios reservados para las barras del sistema
                    PantallaTorneo(
                        modifier =
                            Modifier.padding(innerPadding)
                        //MUESTRA PANTALLA COMPLETA, NO SOLO TEXTO
                    )
                }
            }
        }
    }
}

//CREAR PANTALLA PRINCIPAL TORNEO DONDE ESTÁ GRETING
@Composable
fun PantallaTorneo(modifier: Modifier = Modifier) {

    var nombre by rememberSaveable { mutableStateOf("") }
    var alias by rememberSaveable { mutableStateOf("") }
    //BY NOS PERMITE ESCRIBIR EL NOMBRE DIRECTAMENTE, NO USAR nombre.value
    //rememberSaveable PERMITE CONSERVAR EL VALOR DURANTE LA RECOMPOSICION (como al girar el movil)
    //mUTABLEsTATEoF("") CREA UN ESTADO OBSRVABLE PARA Compose

    val participantes = remember {
        mutableStateListOf<Participante>()
        //mutable lista observable por Compose. Cuando añadimos o elminamos participantes, la interfaz
        //puede reflejar los cambios automáticamente

        //Remember hace que la lista se conserve durante la recomposicion
    }

    val formularioValido =
        nombre.isNotBlank() && alias.isNotBlank() //isNotBlank COMPRUEBA QUE EL CAMPO NO ES ESTÁ VACIO
   //SOLO INSCRIBIR PARTICIPANTES CON AMBOS CAMPOS TEXTO

    Column( // CONTENEDOR COLOCA ELEMENTOS VERTICALMENTE
        modifier = modifier
            .fillMaxSize() //PERMITE OCUPAR TODO EL ESPACIO
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)  //ESTABLECE UNA SEPARACIÓN VERTICAL ENTRE ELEMENTOS
    ) {

        Text(
            text = "Inscripción al torneo",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField( //CREAR UN CAMPO DONDE EL USUARIO PUEDA ESCRIBIR
            value = nombre,
            onValueChange = { nombre = it },//ACTUALIZA VARIABLE CADA VEZ QUE EL USUARIO ESCRIBE
            label = { Text("Nombre") },//MUESTRA EL IDENTIFICADOR DEL CAMPO
            singleLine = true,//EVITA QUE EL USUARIO ESCRIBA VARIAS LINEAS
            modifier = Modifier.fillMaxWidth()//HACE QUE OCUPE TODO EL ANCHO DISPONIBLE
        )

        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },//IT REPRESENTA AUTOMÁTICAMENTE EL NUEVO TEXTO INTRODUCIDO
            label = { Text("Alias") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(//Añadir participantes mediante un botón
            onClick = {
                if (formularioValido) {
                    val nuevo = Participante(
                        nombre = nombre.trim(), //TRIM ELIMINA ESPACIOS EN BLANCO
                        alias = alias.trim()
                    )

                    participantes.add(nuevo)

                    nombre = ""
                    alias = ""
                }
            },
            enabled = formularioValido,//BOTON DESACTIVADO SI FALTA ALGUN DATO
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir participante")
        }

        Text("Participantes inscritos: ${participantes.size}")//size dice cuantos participantes hay en la lista

        if (participantes.isEmpty()) { //comprueba si la lsita está vacía
            Text("Todavía no hay participantes")
        }

        LazyColumn( //CREAR LISTA VISUAL
            modifier = Modifier
                .weight(1f) //hace que la lista ocupe el espacio vertical restante dentro de la columna
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(participantes) { participante -> //RECORRE LA LISTA Y CREA LA INTERFAZ PARA CADA ELEMENTO
                TarjetaParticipante(participante) //mostrará sus datos

                Button(  //  permitirá quitarlos
                    onClick = {
                        participantes.remove(participante)
                    }
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable  //funcion recibe objeto Participante y dibuja tarjeta con su info
fun TarjetaParticipante(participante: Participante) {
    Card(modifier = Modifier.fillMaxWidth()) {  //porporciona contenedor visual con estilo Material Design
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = participante.nombre,
                style = MaterialTheme.typography.titleMedium //utiliza uno de los estilos tipográficos de Material Design para destacar el nombre.
            )

            Text(
                text = "Alias: ${participante.alias}"
            )
        }
    }
}


@Preview(
    showSystemUi = true, //vista previa puede incluir elementos del sistema Android, como la barra de estado.
    showBackground = true
)
@Composable
fun PantallaTorneoPreview() {
    ListaTheme {
        PantallaTorneo()
    }
}

//