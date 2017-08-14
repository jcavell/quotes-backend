package anormUtils

/** A Function-like class to describe how to copy a list of child objects into
* 	their respective parents.
* @tparam A The type of the parent object in a one-to-many relation.
* @tparam B The type of the child object in a one-to-many relation.
* @param f A function that takes a parent and a list of it's children and returns the parent with the children nested inside.
*/
case class RowFlattener[A, B](f: (A, List[B]) => A) {

	/** Syntactic sugar for applying the `RowFlattener` function, without collision with implicit functions.
	* @param parent The parent object in a one-to-many relation.
	* @param children The children that should be copied into the parent object.
	* @return A parent containing the children in a nested list.
	*/
    def apply(parent: A, children: List[B]): A = f(parent, children)

}

/** Includes extra `apply` methods for each `RowFlattener` arity. */
object RowFlattener {

	def apply[A, B1, B2](f: (A, List[B1], List[B2]) => A): RowFlattener2[A, B1, B2] = RowFlattener2[A, B1, B2](f)

	def apply[A, B1, B2, B3](f: (A, List[B1], List[B2], List[B3]) => A): RowFlattener3[A, B1, B2, B3] = RowFlattener3[A, B1, B2, B3](f)

	def apply[A, B1, B2, B3, B4](f: (A, List[B1], List[B2], List[B3], List[B4]) => A): RowFlattener4[A, B1, B2, B3, B4] = RowFlattener4[A, B1, B2, B3, B4](f)

	def apply[A, B1, B2, B3, B4, B5](f: (A, List[B1], List[B2], List[B3], List[B4], List[B5]) => A): RowFlattener5[A, B1, B2, B3, B4, B5] = RowFlattener5[A, B1, B2, B3, B4, B5](f)

}

case class RowFlattener2[A, B1, B2](f: (A, List[B1], List[B2]) => A) {
    def apply(parent: A, c1: List[B1], c2: List[B2]): A = f(parent, c1, c2)
}

case class RowFlattener3[A, B1, B2, B3](f: (A, List[B1], List[B2], List[B3]) => A) {
    def apply(parent: A, c1: List[B1], c2: List[B2], c3: List[B3]): A = f(parent, c1, c2, c3)
}

case class RowFlattener4[A, B1, B2, B3, B4](f: (A, List[B1], List[B2], List[B3], List[B4]) => A) {
    def apply(parent: A, c1: List[B1], c2: List[B2], c3: List[B3], c4: List[B4]): A = f(parent, c1, c2, c3, c4)
}

case class RowFlattener5[A, B1, B2, B3, B4, B5](f: (A, List[B1], List[B2], List[B3], List[B4], List[B5]) => A) {
    def apply(parent: A, c1: List[B1], c2: List[B2], c3: List[B3], c4: List[B4], c5: List[B5]): A = f(parent, c1, c2, c3, c4, c5)
}