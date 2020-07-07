package async

import scala.concurrent.Future

/**
 * The interface used by the grading infrastructure. Do not change signatures
 * or your submission will fail with a NoSuchMethodError.
 */
trait AsyncInterface {
  def transformSuccess[A,B](eventuallyX: Future[A])(f: A => B): Future[B]
  def recoverFailure[A](eventuallyX: Future[A])(b: A): Future[A]
  def sequenceComputations[A, B](makeAsyncComputation1: () => Future[A], makeAsyncComputation2: () => Future[B]): Future[(A, B)]
  def concurrentComputations[A, B](makeAsyncComputation1: () => Future[A], makeAsyncComputation2: () => Future[B]): Future[(A, B)]
  def insist[A](makeAsyncComputation: () => Future[A], maxAttempts: Int): Future[A]
  def futurize(callbackBasedApi: CallbackBasedApi): FutureBasedApi
}
