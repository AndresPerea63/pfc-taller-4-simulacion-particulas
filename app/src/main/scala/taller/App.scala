package taller

import scala.util.Random

/**
 * Punto 5: mide, sobre sistemas de al menos un millón de partículas, el
 * tiempo de `pasoSecuencial` y el de `pasoParalelo` con varios umbrales, y
 * reporta la aceleración medida junto a la que predice el punto 4.
 *
 * La salida son filas de tabla Markdown, listas para pegar en
 * `docs/paralelizacion.md`.
 */
object App {

  def main(args: Array[String]): Unit = {
    val sim = new SimulacionParticulas()
    import sim._

    val dt = 0.5
    val repeticiones = 3
    val nucleos = Runtime.getRuntime.availableProcessors
    val tamanos = List(1000000, 2000000, 4000000)
    val umbrales = List(1000, 10000, 100000)

    // Mismo sistema en cada corrida: la semilla es fija
    def sistemaDe(n: Int): Sistema = {
      val azar = new Random(42)
      Array.fill(n)(Particula(
        Vector2(azar.nextDouble() * 100, azar.nextDouble() * 100),
        Vector2(azar.nextDouble() * 2 - 1, azar.nextDouble() * 2 - 1),
        azar.nextDouble() * 5 + 0.1))
    }

    println(s"Núcleos disponibles: $nucleos")
    println(s"Java: ${System.getProperty("java.version")}")
    println()
    println("| n | Umbral | t_sec (ms) | t_par (ms) | Aceleración | Aceleración estimada |")
    println("|---:|---:|---:|---:|---:|---:|")

    tamanos.foreach { n =>
      val sistema = sistemaDe(n)
      val tSec = medir(repeticiones)(pasoSecuencial(sistema, dt))

      // El último umbral es el tamaño del sistema: es la fila de control
      (umbrales :+ n).foreach { u =>
        val tPar = medir(repeticiones)(pasoParalelo(sistema, dt, u))
        val medida = aceleracion(tSec, tPar)
        val estimada = trabajo(n).toDouble / tiempoEstimado(n, nucleos, u)
        println(f"| $n%d | $u%d | $tSec%.2f | $tPar%.2f | $medida%.2f | $estimada%.2f |")
      }

      val mejor = umbralConveniente(sistema, dt, umbrales)
      println(s"Umbral conveniente para n = $n entre $umbrales: $mejor")
    }
  }
}
