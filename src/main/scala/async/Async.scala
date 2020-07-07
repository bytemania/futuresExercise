package async

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

object Async extends AsyncInterface {

  /**
    * Transforms a successful asynchronous computation
    */
  override def transformSuccess[A,B](eventuallyX: Future[A])(f: A => B): Future[B] = eventuallyX.map(f)

  /**
    * Transforms a failed asynchronous computation
    */
  override def recoverFailure[A](eventuallyX: Future[A])(b: A): Future[A] = eventuallyX.recover(_ => b)

  /**
    * Perform two asynchronous computation, one after the other. `makeAsyncComputation2`
    * should start ''after'' the `Future` returned by `makeAsyncComputation1` has
    * completed.
    * In case the first asynchronous computation failed, the second one should not even
    * be started.
    * The returned `Future` value should contain the successful result of the first and
    * second asynchronous computations, paired together.
    */
  def sequenceComputations[A, B](
    makeAsyncComputation1: () => Future[A],
    makeAsyncComputation2: () => Future[B]
  ): Future[(A, B)] = for {
    a <- makeAsyncComputation1()
    b <- makeAsyncComputation2()
  } yield (a,b)

  /**
    * Concurrently perform two asynchronous computations and pair their successful
    * result together.
    * The two computations should be started independently of each other.
    * If one of them fails, this method should return the failure.
    */
  def concurrentComputations[A, B](
    makeAsyncComputation1: () => Future[A],
    makeAsyncComputation2: () => Future[B]
  ): Future[(A, B)] = makeAsyncComputation1() zip makeAsyncComputation2()

  /**
    * Attempt to perform an asynchronous computation.
    * In case of failure this method should try again to make
    * the asynchronous computation so that at most `maxAttempts`
    * are eventually performed.
    */
  def insist[A](makeAsyncComputation: () => Future[A], maxAttempts: Int): Future[A] = maxAttempts match {
    case 1 => makeAsyncComputation()
    case _ => makeAsyncComputation().recoverWith(_ => insist(makeAsyncComputation, maxAttempts - 1))
  }

  /**
    * Turns a callback-based API into a Future-based API
    * @return A `FutureBasedApi` that forwards calls to `computeIntAsync` to the `callbackBasedApi`
    *         and returns its result in a `Future` value
    *
    * Hint: Use a `Promise`
    */
  def futurize(callbackBasedApi: CallbackBasedApi): FutureBasedApi = () => {
   val p = Promise[Int]
    callbackBasedApi.computeIntAsync {
      case Success(n) => p.success(n)
      case Failure(exception) => p.failure(exception)
    }
    p.future
  }
}

/**
  * Dummy example of a callback-based API
  */
trait CallbackBasedApi {
  def computeIntAsync(continuation: Try[Int] => Unit): Unit
}

/**
  * API similar to [[CallbackBasedApi]], but based on `Future` instead
  */
trait FutureBasedApi {
  def computeIntAsync(): Future[Int]
}
