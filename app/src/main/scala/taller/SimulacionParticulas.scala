package taller

import common._
import org.scalameter._

/**
 * Taller 4 — simulación de partículas repartida entre varios núcleos.
 *
 * Complete cada función. Las firmas y los tipos no se modifican.
 *
 * Las abstracciones `parallel` y `task` vienen en el paquete `common`, que ya
 * está en el proyecto.
 */
class SimulacionParticulas {

  type Sistema = Array[Particula]

  // Punto 1: un paso de la simulación -----------------------------------------

  /** La posición nueva es la anterior más la velocidad por dt. */
  def mover(p: Particula, dt: Double): Particula =
    // Posición nueva = posición vieja + velocidad * dt. Lo demás no cambia.
    Particula(p.pos + p.vel * dt, p.vel, p.masa)

  /** Avanza todo el sistema un paso de tiempo dt. */
  def pasoSecuencial(s: Sistema, dt: Double): Sistema =
    // Es el segmento que va de 0 al final. Así la versión secuencial y la
    // paralela hacen el mismo trabajo de la misma forma.
    pasoSegmento(s, dt, 0, s.length)

  /** Avanza solo el tramo entre `desde` incluido y `hasta` excluido. */
  def pasoSegmento(s: Sistema, dt: Double, desde: Int, hasta: Int): Sistema =
    // slice toma el tramo [desde, hasta) y map mueve cada partícula
    s.slice(desde, hasta).map(p => mover(p, dt))

  /**
   * Parte el sistema por la mitad con `parallel` mientras el tramo tenga más
   * elementos que el umbral, y por debajo de esa cifra resuelve en secuencial.
   */
  def pasoParalelo(s: Sistema, dt: Double, umbral: Int): Sistema = {
    // Un umbral menor que 1 partiría un tramo de 1 elemento para siempre
    val limite = Math.max(umbral, 1)

    // Resuelve el tramo [desde, hasta)
    def resolver(desde: Int, hasta: Int): Sistema =
      if (hasta - desde <= limite) {
        // Tramo pequeño: se resuelve en secuencial
        pasoSegmento(s, dt, desde, hasta)
      } else {
        // Tramo grande: se parte por la mitad y las dos mitades van en paralelo
        val mitad = desde + (hasta - desde) / 2
        val (izquierda, derecha) = parallel(
          resolver(desde, mitad),
          resolver(mitad, hasta))
        // Se pegan en el mismo orden
        izquierda ++ derecha
      }

    resolver(0, s.length)
  }

  // Punto 2: magnitudes globales ----------------------------------------------

  /** La suma de las energías cinéticas, media masa por velocidad al cuadrado. */
  def energiaSecuencial(s: Sistema): Double = {
    // Energía de una sola partícula
    def energiaDe(p: Particula): Double =
      0.5 * p.masa * p.vel.norma * p.vel.norma

    // Si el sistema está vacío, la suma da 0.0
    s.map(p => energiaDe(p)).sum
  }

  /** La suma de las masas. */
  def masaTotal(s: Sistema): Double =
    s.map(p => p.masa).sum

  /**
   * La suma de las posiciones ponderadas por la masa, dividida entre la masa
   * total. Sin masa, el origen.
   */
  def centroDeMasaSecuencial(s: Sistema): Vector2 = {
    val origen = Vector2(0.0, 0.0)
    val masa = masaTotal(s)

    if (masa == 0.0) {
      origen
    } else {
      // Suma de las posiciones ponderadas por la masa
      val sumaPonderada = s
        .map(p => p.pos * p.masa)
        .foldLeft(origen)((acumulado, v) => acumulado + v)
      // Vector2 no tiene división, así que se multiplica por 1/masa
      sumaPonderada * (1.0 / masa)
    }
  }


  // Punto 3: las mismas magnitudes en paralelo --------------------------------

  /** La energía del sistema, repartida con el mismo umbral del punto 1. */
  def energiaParalela(s: Sistema, umbral: Int): Double = {
    val limite = Math.max(umbral, 1)

    def resolver(desde: Int, hasta: Int): Double =
      if (hasta - desde <= limite) {
        s.slice(desde, hasta)
          .map(p => 0.5 * p.masa * p.vel.norma * p.vel.norma)
          .sum
      } else {
        val mitad = desde + (hasta - desde) / 2
        val (izquierda, derecha) = parallel(
          resolver(desde, mitad),
          resolver(mitad, hasta)
        )
        izquierda + derecha
      }

    resolver(0, s.length)
  }

  /**
   * El centro de masa, repartido. Cada tramo devuelve la suma ponderada
   * de posiciones y su masa total; el centro se calcula una sola vez al final.
  */
  def centroDeMasaParalelo(s: Sistema, umbral: Int): Vector2 = {
    val limite = Math.max(umbral, 1)
    val origen = Vector2(0.0, 0.0)

    def resolver(desde: Int, hasta: Int): (Vector2, Double) =
      if (hasta - desde <= limite) {
        val tramo = s.slice(desde, hasta)

        val sumaPonderada = tramo
          .map(p => p.pos * p.masa)
          .foldLeft(origen)((acumulado, v) => acumulado + v)

        val masa = tramo.map(p => p.masa).sum

        (sumaPonderada, masa)
      } else {
        val mitad = desde + (hasta - desde) / 2

        val (izquierda, derecha) = parallel(
          resolver(desde, mitad),
          resolver(mitad, hasta)
        )

        (
          izquierda._1 + derecha._1,
          izquierda._2 + derecha._2
        )
      }

    val (sumaPonderada, masa) = resolver(0, s.length)

    if (masa == 0.0)
      origen
    else
      sumaPonderada * (1.0 / masa)
  }

  // Punto 4: trabajo y profundidad --------------------------------------------

  /** El número total de operaciones, una por partícula. */
  def trabajo(n: Int): Int = n

  /** Cuántas veces se parte a la mitad, más el tramo que queda sin partir. */
  def profundidad(n: Int, umbral: Int): Int = { 
    val limite = Math.max(umbral, 1)

  def resolver(tamano: Int): Int =
    if (tamano <= 0) 0
    else if (tamano <= limite) tamano
    else {
      val izquierda = tamano / 2
      val derecha = tamano - izquierda
      1 + Math.max(resolver(izquierda), resolver(derecha))
    }

  resolver(n)}

  /** La profundidad más el trabajo entre p procesadores. */
  def tiempoEstimado(n: Int, p: Int, umbral: Int): Double =
    profundidad(n, umbral) + trabajo(n).toDouble / p

  // Punto 5: medir la aceleración ---------------------------------------------

  /** Mediana de varias mediciones de ScalaMeter, en milisegundos. */
  def medir[A](repeticiones: Int)(cuerpo: => A): Double = {
    val muestras = Vector.fill(repeticiones) {
      (withWarmer(new Warmer.Default) measure { cuerpo }).value
    }
    muestras.sorted.apply(muestras.length / 2)
  }

  /** El cociente entre el tiempo secuencial y el paralelo. */
  def aceleracion(tSecuencial: Double, tParalelo: Double): Double =
    // Cuántas veces cabe el tiempo paralelo en el secuencial
    tSecuencial / tParalelo

  /**
   * El umbral que menos tarda de una lista de candidatos. Con la lista vacía,
   * el tamaño del sistema, que equivale a no repartir.
   */
  def umbralConveniente(s: Sistema, dt: Double, umbrales: List[Int]): Int =
    if (umbrales.isEmpty) {
      // Sin candidatos no se reparte: el umbral es el tamaño del sistema
      s.length
    } else {
      // Se mide cada candidato y se queda el de menor tiempo
      umbrales
        .map(u => (u, medir(3)(pasoParalelo(s, dt, u))))
        .minBy(par => par._2)
        ._1
    }
}
