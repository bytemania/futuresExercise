package async

sealed trait Coffee
case object Expresso extends Coffee
case object Americano extends Coffee

case class MakeCoffee() {
  def prepareExpresso(): Coffee = {
    Thread.sleep(1000)
    Expresso
  }

  def prepareAmericano(): Coffee = {
    Thread.sleep(2000)
    Americano
  }

  def spill(): Coffee = throw new IllegalStateException("Spilled Coffee")
}

object Main extends App {

}
