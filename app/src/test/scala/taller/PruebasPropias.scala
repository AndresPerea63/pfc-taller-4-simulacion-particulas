package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import scala.util.Random

@RunWith(classOf[JUnitRunner])
class PruebasPropias extends AnyFunSuite {

  val sim = new SimulacionParticulas()
  import sim._

  val eps = 1e-9

  val vacio: Sistema = Array()

  val azar = new Random(7)

  val grande: Sistema = Array.fill(1000)(Particula(
    Vector2(azar.nextDouble() * 10, azar.nextDouble() * 10),
    Vector2(azar.nextDouble() * 2, azar.nextDouble() * 2),
    azar.nextDouble() * 5 + 0.1))

  def cerca(a: Double, b: Double): Boolean =
    Math.abs(a - b) < 1e-6

  def cerca(a: Vector2, b: Vector2): Boolean =
    Math.abs(a.x - b.x) < 1e-6 &&
    Math.abs(a.y - b.y) < 1e-6

  def sistemaDe(n: Int): Sistema = Array.tabulate(n)(i =>
    Particula(
      Vector2(i.toDouble, i.toDouble),
      Vector2(i.toDouble, -i.toDouble),
      i.toDouble + 1.0
    ))

// Punto 1 -------------------------------------------------------------------

  test("Punto 1: con dt negativo la partícula se mueve hacia atrás") {
    val q = Particula(Vector2(10, 10), Vector2(2, 3), 1.0)
    assert(mover(q, -1.0).pos == Vector2(8.0, 7.0))
  }

  test("Punto 1: mover no cambia la velocidad ni la masa") {
    val q = Particula(Vector2(1, 1), Vector2(5, -2), 7.5)
    val r = mover(q, 3.0)
    assert(r.vel == Vector2(5, -2) && r.masa == 7.5)
  }

  test("Punto 1: pasoSegmento da lo mismo que el trozo del paso secuencial") {
    val sistema = sistemaDe(10)
    val tramo = pasoSegmento(sistema, 1.0, 3, 6)
    val esperado = pasoSecuencial(sistema, 1.0).slice(3, 6)
    assert(tramo.length == 3 && tramo.toList == esperado.toList)
  }

  test("Punto 1: paralelo y secuencial dan exactamente lo mismo con varios umbrales") {
    val sistema = sistemaDe(101)
    val esperado = pasoSecuencial(sistema, 0.5).toList
    val umbrales = List(1, 2, 7, 10, 50, 101, 500)
    assert(umbrales.forall(u => pasoParalelo(sistema, 0.5, u).toList == esperado))
  }

  test("Punto 1: el paralelo no pierde ni repite partículas y conserva el orden") {
    val sistema = sistemaDe(37)
    val r = pasoParalelo(sistema, 2.0, 3)
    // la masa identifica a cada partícula
    assert(r.map(q => q.masa).toList == sistema.map(q => q.masa).toList)
  }

  test("Punto 1: el paralelo con umbral negativo da lo mismo que el secuencial") {
    val sistema = sistemaDe(8)
    assert(pasoParalelo(sistema, 1.0, -3).toList == pasoSecuencial(sistema, 1.0).toList)
  }

  // Punto 2 -------------------------------------------------------------------

  test("Punto 2: energía con velocidad (6,8) y masa 1 es 50") {
    // norma 10, media masa por 100
    val unica: Sistema = Array(Particula(Vector2(0, 0), Vector2(6, 8), 1.0))
    assert(cerca(energiaSecuencial(unica), 50.0))
  }

  test("Punto 2: la energía suma la de varias partículas") {
    val dos: Sistema = Array(
      Particula(Vector2(0, 0), Vector2(2, 0), 1.0), // 0.5 * 1 * 4 = 2
      Particula(Vector2(1, 1), Vector2(0, 4), 0.5)) // 0.5 * 0.5 * 16 = 4
    assert(cerca(energiaSecuencial(dos), 6.0))
  }

  test("Punto 2: masaTotal suma masas con decimales") {
    val dos: Sistema = Array(
      Particula(Vector2(0, 0), Vector2(0, 0), 1.5),
      Particula(Vector2(1, 0), Vector2(0, 0), 2.5))
    assert(cerca(masaTotal(dos), 4.0))
  }

  test("Punto 2: centro de masa con masas distintas") {
    // (1 * 1 + 4 * 3) / 4 = 3.25 en x y en y
    val dos: Sistema = Array(
      Particula(Vector2(1, 1), Vector2(0, 0), 1.0),
      Particula(Vector2(4, 4), Vector2(0, 0), 3.0))
    assert(cerca(centroDeMasaSecuencial(dos), Vector2(3.25, 3.25)))
  }

  test("Punto 2: el centro de masa de una sola partícula es su posición") {
    val unica: Sistema = Array(Particula(Vector2(-5, 7), Vector2(1, 1), 2.0))
    assert(cerca(centroDeMasaSecuencial(unica), Vector2(-5, 7)))
  }

  test("Punto 2: con todas las masas en cero el centro de masa es el origen") {
    val sinMasa: Sistema = Array(
      Particula(Vector2(5, 5), Vector2(0, 0), 0.0),
      Particula(Vector2(9, 1), Vector2(0, 0), 0.0))
    assert(centroDeMasaSecuencial(sinMasa) == Vector2(0, 0))
  }


  // Punto 3 -------------------------------------------------------------------

  test("Punto 3 propio: energía paralela con velocidades negativas") {
    val sistema: Sistema = Array(
      Particula(Vector2(1, 2), Vector2(-3, -4), 2.0),
      Particula(Vector2(0, 0), Vector2(-5, 0), 1.0))

    assert(cerca(energiaParalela(sistema, 1), energiaSecuencial(sistema)))
  }

  test("Punto 3 propio: energía paralela con umbral mayor al tamaño") {
    val sistema: Sistema = Array(
      Particula(Vector2(0, 0), Vector2(2, 3), 4.0),
      Particula(Vector2(1, 1), Vector2(4, 2), 3.0),
      Particula(Vector2(2, 2), Vector2(1, 7), 0.5))

    assert(cerca(energiaParalela(sistema, 100), energiaSecuencial(sistema)))
  }

  test("Punto 3 propio: energía paralela con umbral cero") {
    val sistema: Sistema = Array(
      Particula(Vector2(1, 1), Vector2(1, 2), 2.0),
      Particula(Vector2(3, 3), Vector2(3, 4), 1.0),
      Particula(Vector2(5, 5), Vector2(5, 12), 2.5),
      Particula(Vector2(7, 7), Vector2(8, 15), 0.5))

    assert(cerca(energiaParalela(sistema, 0), energiaSecuencial(sistema)))
  }

  test("Punto 3 propio: centro de masa con masas muy diferentes") {
    val sistema: Sistema = Array(
      Particula(Vector2(2, 8), Vector2(0, 0), 1.0),
      Particula(Vector2(10, -4), Vector2(0, 0), 9.0))

    assert(cerca(
      centroDeMasaParalelo(sistema, 1),
      centroDeMasaSecuencial(sistema)))
  }

  test("Punto 3 propio: centro de masa con posiciones negativas") {
    val sistema: Sistema = Array(
      Particula(Vector2(-8, -3), Vector2(1, 1), 2.0),
      Particula(Vector2(4, 6), Vector2(2, 2), 5.0),
      Particula(Vector2(-2, 10), Vector2(3, 3), 3.0))

    assert(cerca(
      centroDeMasaParalelo(sistema, 2),
      centroDeMasaSecuencial(sistema)))
  }

  test("Punto 3 propio: centro de masa con masas cero y positivas") {
    val sistema: Sistema = Array(
      Particula(Vector2(100, 100), Vector2(0, 0), 0.0),
      Particula(Vector2(3, 7), Vector2(1, 1), 2.0),
      Particula(Vector2(-50, -50), Vector2(0, 0), 0.0),
      Particula(Vector2(9, -1), Vector2(1, 1), 4.0))

    assert(cerca(
      centroDeMasaParalelo(sistema, 1),
      centroDeMasaSecuencial(sistema)))
  }

  test("Punto 3 propio: sistema vacío y umbral negativo") {
    assert(energiaParalela(vacio, -5) == 0.0)
    assert(centroDeMasaParalelo(vacio, -5) == Vector2(0, 0))
  }

  // Punto 4 -------------------------------------------------------------------

  test("Punto 4: trabajo con 37 partículas") {
    assert(trabajo(37) == 37)}

  test("Punto 4: profundidad con 100 partículas y umbral 10") {
    assert(profundidad(100, 10) == 11)
  }
  
  test("Punto 4: profundidad con 25 partículas y umbral 5") {
    assert(profundidad(25, 5) == 7)
  }
  
  test("Punto 4: profundidad con 15 partículas y umbral 4") {
    assert(profundidad(15, 4) == 6)
  }
  
  test("Punto 4: profundidad con 10 partículas y umbral 3") {
    assert(profundidad(10, 3) == 5)
  }
  
  test("Punto 4: tiempo estimado con 100 partículas, 5 procesadores y umbral 10") {
    assert(cerca(tiempoEstimado(100, 5, 10), 31.0))
  }
  
  test("Punto 4: tiempo estimado con 17 partículas, 4 procesadores y umbral 1") {
    assert(cerca(tiempoEstimado(17, 4, 1), 10.25))
  }

  // Punto 5 -------------------------------------------------------------------

  test("Punto 5 propio: aceleración con tiempos 90 y 30 da 3.0") {
    assert(cerca(aceleracion(90.0, 30.0), 3.0))
  }

  test("Punto 5 propio: aceleración menor que 1 cuando el paralelo tarda más") {
    assert(cerca(aceleracion(40.0, 80.0), 0.5))
  }

  test("Punto 5 propio: aceleración con tiempo secuencial cero da 0.0") {
    assert(cerca(aceleracion(0.0, 5.0), 0.0))
  }

  test("Punto 5 propio: umbralConveniente con un solo candidato lo devuelve") {
    assert(umbralConveniente(grande, 0.25, List(100)) == 100)
  }

  test("Punto 5 propio: umbralConveniente sin candidatos devuelve el tamaño") {
    assert(umbralConveniente(vacio, 0.5, Nil) == 0)
    assert(umbralConveniente(grande, 0.5, Nil) == 1000)
  }

  test("Punto 5 propio: umbralConveniente con candidatos no positivos") {
    val candidatos = List(-3, 0, 500)
    assert(candidatos.contains(umbralConveniente(grande, 0.5, candidatos)))
  }

}
