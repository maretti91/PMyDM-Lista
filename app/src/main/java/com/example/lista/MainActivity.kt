
package com.example.lista

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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


// CLASE DE DATOS: define la información de cada participante.
data class Participante(
    val id: Int, // Identificador único del participante.
    val nombre: String, // val impide reasignar la propiedad.
    val alias: String,
    val profesion: String
)


// ACTIVIDAD PRINCIPAL: inicia la aplicación Android.
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Inicializa la Activity.
        enableEdgeToEdge() // Permite dibujar detrás de las barras del sistema.

        setContent { // Define la interfaz con Jetpack Compose.
            ListaTheme { // Aplica el tema de la aplicación.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaTorneo(
                        modifier = Modifier.padding(innerPadding) // Respeta el espacio del Scaffold.
                    )
                }
            }
        }
    }
}


// PANTALLA PRINCIPAL: formulario, lista y gestión de participantes.
@Composable
fun PantallaTorneo(modifier: Modifier = Modifier) {

    // ESTADOS DEL FORMULARIO
    var nombre by rememberSaveable { mutableStateOf("") } // Conserva el texto y permite restaurarlo.
    var alias by rememberSaveable { mutableStateOf("") }
    var profesion by rememberSaveable { mutableStateOf("") }

    // LISTA OBSERVABLE
    val participantes = remember {
        mutableStateListOf<Participante>() // Compose detecta altas, bajas y cambios de elementos.
    }

    // CONTROL DE PARTICIPANTES
    var siguienteId by remember { mutableStateOf(1) } // ID del siguiente participante.
    var idEnEdicion by remember { mutableStateOf<Int?>(null) } // null: no estamos editando.
    var participanteAEliminar by remember { mutableStateOf<Participante?>(null) } // null: no hay confirmación pendiente.

    // VALIDACIÓN: todos los campos deben contener texto distinto de espacios.
    val formularioValido =
        nombre.isNotBlank() && alias.isNotBlank() && profesion.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize() // Ocupa el espacio disponible.
            .padding(16.dp), // Margen interior.
        verticalArrangement = Arrangement.spacedBy(8.dp) // Separación vertical entre elementos.
    ) {

        Text(
            text = "Inscripción al torneo",
            style = MaterialTheme.typography.headlineSmall // Estilo de título.
        )

        // CAMPO NOMBRE
        OutlinedTextField(
            value = nombre, // Texto almacenado en el estado.
            onValueChange = { nombre = it }, // it es el nuevo texto introducido.
            label = { Text("Nombre") }, // Etiqueta del campo.
            singleLine = true, // Impide introducir varias líneas.
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho.
        )

        // CAMPO ALIAS
        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text("Alias") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // CAMPO PROFESIÓN
        OutlinedTextField(
            value = profesion,
            onValueChange = { profesion = it },
            label = { Text("Profesión") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // BOTÓN: AÑADIR O GUARDAR CAMBIOS
        Button(
            onClick = {
                if (formularioValido) {

                    val idActual = idEnEdicion // Comprueba si estamos editando.

                    if (idActual == null) {

                        // AÑADIR: crea un participante nuevo.
                        val nuevo = Participante(
                            id = siguienteId,
                            nombre = nombre.trim(), // trim() elimina espacios al principio y final.
                            alias = alias.trim(),
                            profesion = profesion.trim()
                        )

                        participantes.add(nuevo) // Añade el objeto a la lista observable.
                        siguienteId++ // Incrementa el ID para el próximo participante.

                    } else {

                        // EDITAR: busca el participante por su ID.
                        val posicion = participantes.indexOfFirst {
                            it.id == idActual // Devuelve la posición o -1 si no existe.
                        }

                        if (posicion != -1) {

                            val participanteActual = participantes[posicion] // Obtiene el objeto existente.

                            participantes[posicion] = participanteActual.copy(
                                nombre = nombre.trim(),
                                alias = alias.trim(),
                                profesion = profesion.trim()
                            ) // copy() crea una copia modificando los datos indicados.
                        }
                    }

                    // LIMPIAR FORMULARIO DESPUÉS DE GUARDAR
                    nombre = ""
                    alias = ""
                    profesion = ""
                    idEnEdicion = null // Finaliza el modo edición.
                }
            },
            enabled = formularioValido, // Desactiva el botón si faltan datos.
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (idEnEdicion == null) {
                    "Añadir participante" // Modo creación.
                } else {
                    "Guardar cambios" // Modo edición.
                }
            )
        }

        // CANCELAR EDICIÓN: solo aparece si estamos editando.
        if (idEnEdicion != null) {
            Button(
                onClick = {
                    nombre = ""
                    alias = ""
                    profesion = ""
                    idEnEdicion = null // Vuelve al modo añadir.
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar Edición")
            }
        }

        // CONTADOR: size devuelve el número de elementos.
        Text("Participantes inscritos: ${participantes.size}")

        // MENSAJE CUANDO LA LISTA ESTÁ VACÍA
        if (participantes.isEmpty()) {
            Text("Todavía no hay participantes")
        }

        // LISTA VERTICAL: muestra los participantes con desplazamiento.
        LazyColumn(
            modifier = Modifier
                .weight(1f) // Ocupa la altura restante de la Column.
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp) // Espacio al final de la lista.
        ) {
            items(
                items = participantes, // Colección que se va a mostrar.
                key = { participante -> participante.id } // Identidad estable de cada elemento.
            ) { participante ->

                TarjetaParticipante(
                    participante = participante, // Envía los datos a la tarjeta.

                    onEditar = {
                        idEnEdicion = participante.id // Activa la edición.
                        nombre = participante.nombre // Carga sus datos en el formulario.
                        alias = participante.alias
                        profesion = participante.profesion
                    },

                    onEliminar = {
                        participanteAEliminar = participante // Solicita confirmar la eliminación.
                    }
                )
            }
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN DE ELIMINACIÓN
    val pendiente = participanteAEliminar

    if (pendiente != null) { // Solo muestra el diálogo si hay alguien pendiente de eliminar.

        AlertDialog(
            onDismissRequest = {
                participanteAEliminar = null // Cierra el diálogo sin eliminar.
            },

            title = {
                Text("Eliminar participante")
            },

            text = {
                Text("¿Estás seguro de que quieres eliminar a ${pendiente.nombre}?")
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        participantes.removeAll {
                            it.id == pendiente.id // Elimina al participante por ID.
                        }

                        // Si estábamos editando al eliminado, limpiamos el formulario.
                        if (idEnEdicion == pendiente.id) {
                            nombre = ""
                            alias = ""
                            profesion = ""
                            idEnEdicion = null
                        }

                        participanteAEliminar = null // Cierra el diálogo.
                    }
                ) {
                    Text("Eliminar")
                }
            }
        )
    }
}


// TARJETA: muestra los datos y botones de un participante.
@Composable
fun TarjetaParticipante(
    participante: Participante, // Objeto que contiene los datos.
    onEditar: () -> Unit, // Callback sin parámetros ni valor de retorno.
    onEliminar: () -> Unit
) {

    Card(modifier = Modifier.fillMaxWidth()) { // Tarjeta Material Design.

        Column(modifier = Modifier.padding(12.dp)) { // Organiza los datos en vertical.

            Text(
                text = "ID: ${participante.id} Nombre: ${participante.nombre}",
                style = MaterialTheme.typography.titleMedium // Destaca el nombre.
            )

            Text(text = "Alias: ${participante.alias}")
            Text(text = "Profesión: ${participante.profesion}")

            Row { // Organiza los botones horizontalmente.

                Button(onClick = onEditar) { // Ejecuta la acción recibida.
                    Text("Editar")
                }

                Spacer(modifier = Modifier.width(8.dp)) // Separación horizontal.

                Button(onClick = onEliminar) { // Solicita confirmación de eliminación.
                    Text("Eliminar")
                }
            }
        }
    }
}


// VISTA PREVIA: permite visualizar la pantalla sin ejecutar el emulador.
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PantallaTorneoPreview() {
    ListaTheme {
        PantallaTorneo()
    }
}


//