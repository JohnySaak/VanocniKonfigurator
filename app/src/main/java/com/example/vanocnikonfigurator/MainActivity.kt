package com.example.vanocnikonfigurator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChristmasTreeConfigurator()
        }
    }
}

@Composable
fun ChristmasTreeConfigurator() {
    var treeScale by remember { mutableStateOf(1f) }
    var treeShade by remember { mutableStateOf(Color(0xFF2E8B57)) }
    var ornamentsSize by remember { mutableStateOf(10f) }
    var ornamentsColor by remember { mutableStateOf(Color.Red) }
    var lightsColor by remember { mutableStateOf(Color.Yellow) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Konfigurátor vánočního stromečku",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text("Velikost stromečku: ${(treeScale * 100).toInt()} %")
        Slider(value = treeScale, onValueChange = { treeScale = it }, valueRange = 2f..5.0f)

        Text("Velikost ozdob: ${ornamentsSize.toInt()} cm")
        Slider(value = ornamentsSize, onValueChange = { ornamentsSize = it }, valueRange = 5f..30f)

        Button(onClick = {
            ornamentsColor = Color(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat(),
                1f
            )
        }) {
            Text("Změnit barvu ozdob")
        }

        Button(onClick = {
            lightsColor = Color(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat(),
                1f
            )
        }) {
            Text("Změnit barvu světýlek")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ChristmasTreeView(
            treeScale,
            treeShade,
            ornamentsSize,
            ornamentsColor,
            lightsColor
        )
    }
}

/**
 * Vykreslí vánoční stromeček se čtyřmi vrstvami, ozdobami a světýlky.
 * - [treeScale] určuje škálování stromu (0.5 až 5).
 * - [treeShade] barva stromu.
 * - [ornamentsSize] poloměr ozdob.
 * - [ornamentsColor] barva ozdob.
 * - [lightsColor] barva světýlek (vykreslených jako menší kolečka).
 */
@Composable
@Preview(showBackground = true)
fun ChristmasTreeView(
    treeScale: Float = 1f,
    treeShade: Color = Color(0xFF2E8B57),
    ornamentsSize: Float = 10f,
    ornamentsColor: Color = Color.Red,
    lightsColor: Color = Color.Yellow
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        val width = size.width
        val height = size.height

        // Výpočet kmene
        val trunkHeight = height * 0.15f
        val trunkWidth = width * 0.15f
        val trunkColor = Color(0xFF8B4513)
        val treeBaseY = height * 0.85f

        // 1) KMEN
        drawRect(
            color = trunkColor,
            topLeft = Offset((width - trunkWidth) / 2, treeBaseY),
            size = Size(trunkWidth, trunkHeight)
        )

        // 2) ČTYŘI PATRA STROMU
        // Každé "patro" je trojúhelník definovaný vrcholem (apex), levým a pravým rohem.
        // Následující souřadnice si můžete dle libosti doladit (jsou jen ilustrativní).
        val layers = listOf(
            // Dolní patro (1)
            Triple(
                Offset(width * 0.5f, treeBaseY - 40 * treeScale), // Apex
                Offset(width * 0.25f, treeBaseY + 10 * treeScale), // Levý roh
                Offset(width * 0.75f, treeBaseY + 10 * treeScale)  // Pravý roh
            ),
            // 2. patro
            Triple(
                Offset(width * 0.5f, treeBaseY - 80 * treeScale),
                Offset(width * 0.28f, treeBaseY - 30 * treeScale),
                Offset(width * 0.72f, treeBaseY - 30 * treeScale)
            ),
            // 3. patro
            Triple(
                Offset(width * 0.5f, treeBaseY - 120 * treeScale),
                Offset(width * 0.31f, treeBaseY - 70 * treeScale),
                Offset(width * 0.69f, treeBaseY - 70 * treeScale)
            ),
            // Horní patro (4)
            Triple(
                Offset(width * 0.5f, treeBaseY - 160 * treeScale),
                Offset(width * 0.34f, treeBaseY - 110 * treeScale),
                Offset(width * 0.66f, treeBaseY - 110 * treeScale)
            )
        )

        // Vytvoříme Path z těchto 4 vrstev
        val treePath = Path().apply {
            layers.forEach { (apex, left, right) ->
                moveTo(apex.x, apex.y)
                lineTo(left.x, left.y)
                lineTo(right.x, right.y)
                close()
            }
        }

        // Vykreslení celého stromu
        drawPath(
            path = treePath,
            color = treeShade
        )

        // 3) ZVLÁŠTNÍ POZICE PRO OZDOBY I SVĚTÝLKA
        // Ornaments (ozdoby): dáme je třeba přímo na apex, left, right každé vrstvy
        val ornamentPositions = layers.flatMap { (apex, left, right) ->
            listOf(apex, left, right)
        }

        // Lights (světýlka): posuneme je oproti ozdobám, aby nebyly na sobě.
        // Např. apex trochu níž, rohy víc dovnitř apod.
        val lightsPositions = layers.flatMap { (apex, left, right) ->
            listOf(
                apex + Offset(0f, 15f * treeScale),
                left + Offset(10f * treeScale, -10f * treeScale),
                right + Offset(-10f * treeScale, -10f * treeScale)
            )
        }

        // 4) VYKRESLENÍ OZDOB
        ornamentPositions.forEach { position ->
            drawCircle(
                color = ornamentsColor,
                radius = ornamentsSize,
                center = position
            )
        }

        // 5) VYKRESLENÍ SVĚTÝLEK (o něco menší poloměr)
        lightsPositions.forEach { position ->
            drawCircle(
                color = lightsColor,
                radius = ornamentsSize / 2,
                center = position
            )
        }
    }
}
