package anormUtils.util

/** A class for extra `List` functions.
* @constructor Creates an `ExtendedList` from a `List`.
* @param list The list to extend.
*/
class ExtendedList[A](list: List[A]) {

    /**
    * Groups elements of a list that have consecutive keys that are equal.
    * This is useful alternative for `groupBy`, as it can preserve the order of the original list.
    * @param f The function that determines what forms a group.
    * @return A list of tuples with the first ordinate being a single group determined by `f`, 
    *         and the second ordinate a list of elements that fall in the group.
    */
    def groupConsecutive[K](f: (A) => K): List[(K, List[A])] = {
        this.list.foldRight(List[(K, List[A])]())((item: A, res: List[(K, List[A])]) =>
            res match {
                case Nil => List((f(item), List(item)))
                case (k, kLst) :: tail if k == f(item) => (k, item :: kLst) :: tail
                case _ => (f(item), List(item)) :: res
            })
    }
}

/** A companion object containing implicit conversions from `List` to `ExtendedList`. */
object ExtendedList {
    implicit def list2ExtendedList[A](list: List[A]) = new ExtendedList(list)
}