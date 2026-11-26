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

}