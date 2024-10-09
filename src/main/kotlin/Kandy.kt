import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.settings.LineType
import org.jetbrains.kotlinx.kandy.letsplot.x
import org.jetbrains.kotlinx.kandy.letsplot.y
import org.jetbrains.kotlinx.kandy.util.color.Color

// https://github.com/Kotlin/kandy
fun main() {
    val weatherData = dataFrameOf(
        "time" to listOf(0, 1, 2, 4, 5, 7, 8, 9),
        "temperature" to listOf(12.0, 14.2, 15.1, 15.9, 17.9, 15.6, 14.2, 24.3),
        "humidity" to listOf(0.5, 0.32, 0.11, 0.89, 0.68, 0.57, 0.56, 0.5)
    )

    val simplePlot = plot(weatherData) { // Begin plotting
        x(time) // Set x-axis with time data
        y(temperature) { // Set y-axis with temperature data
            // Define scale for temperature (y-axis)
            scale = continuous(0.0..25.5)
        }

        bars { // Add a bar layer
            fillColor(humidity) { // Customizing bar colors based on humidity
                // Setting the color range
                scale = continuous(range = Color.YELLOW..Color.RED)
            }
            borderLine.width = 0.0 // Define border line width
        }

        line {
            width = 3.0 // Set line width
            color = Color.hex("#6e5596") // Define line color
            type = LineType.DOTDASH // Specify the line type
        }

        layout { // Set plot layout
            title = "Simple plot with kandy-lets-plot" // Add title
            size = 700 to 450 // Plot dimension settings
        }
    }

    simplePlot.save("lets_plot_simple.png")
}