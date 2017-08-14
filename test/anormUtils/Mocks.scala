package anormUtils

case class GrandChild(id: Int, name: String)

case class Child(id: Int, name: String, grandchildren: List[GrandChild] = Nil)

object Child {
	implicit val rf = RowFlattener[Child, GrandChild]( (child, grandchildren) => child.copy(grandchildren = grandchildren))
}

case class SimpleParent(id: Int, children: List[Child])

object SimpleParent {
	implicit val rf = RowFlattener[SimpleParent, Child]( (parent, children) => parent.copy(children = children))

	implicit val rf2 = RowFlattener[SimpleParent, OneToMany[Child, GrandChild]](
		(parent, children) => parent.copy(children = OneToMany.flatten(children))
	)
}

case class Parent2(id: Int, children: List[Child] = Nil, children2: List[Child] = Nil)

object Parent2 {
	implicit val rf = RowFlattener[Parent2, Child, Child](
		(parent, children, children2) =>
			parent.copy(children = children, children2 = children2)
	)	
}

case class Parent5(id: Int, c1: List[Child] = Nil, c2: List[Child] = Nil, c3: List[Child] = Nil, c4: List[Child] = Nil, c5: List[Child] = Nil)

object Parent5 {
	implicit val rf = RowFlattener[Parent5, Child, Child, Child, Child, Child]( 
		(parent, c1, c2, c3, c4, c5) =>
			parent.copy(c1 = c1, c2 = c2, c3 = c3, c4 = c4, c5 = c5)
	)
}

object Mocks {

	val simpleOneToMany: List[OneToMany[SimpleParent, Child]] = List(
		OneToMany(SimpleParent(1, Nil), Some(Child(1, "child"))),
		OneToMany(SimpleParent(1, Nil), Some(Child(2, "child2"))),
		OneToMany(SimpleParent(2, Nil), Some(Child(3, "child3"))),
		OneToMany(SimpleParent(2, Nil), Some(Child(4, "child4"))),
		OneToMany(SimpleParent(2, Nil), Some(Child(5, "child5"))),
		OneToMany(SimpleParent(3, Nil), Some(Child(6, "child6"))),
		OneToMany(SimpleParent(3, Nil), Some(Child(7, "child7"))),
		OneToMany(SimpleParent(3, Nil), Some(Child(8, "child8"))),
		OneToMany(SimpleParent(4, Nil), Some(Child(9, "child9")))
	)

	val doubleOneToMany: List[OneToMany[SimpleParent, OneToMany[Child, GrandChild]]] = List(
		OneToMany(SimpleParent(1, Nil), Some(OneToMany(Child(1, "child"), Some(GrandChild(1, "gchild"))))),
		OneToMany(SimpleParent(1, Nil), Some(OneToMany(Child(1, "child"), Some(GrandChild(2, "gchild"))))),
		OneToMany(SimpleParent(2, Nil), Some(OneToMany(Child(2, "child"), Some(GrandChild(3, "gchild"))))),
		OneToMany(SimpleParent(2, Nil), Some(OneToMany(Child(2, "child"), Some(GrandChild(4, "gchild"))))),
		OneToMany(SimpleParent(2, Nil), Some(OneToMany(Child(2, "child"), Some(GrandChild(5, "gchild"))))),
		OneToMany(SimpleParent(2, Nil), Some(OneToMany(Child(3, "child"), Some(GrandChild(6, "gchild"))))),
		OneToMany(SimpleParent(2, Nil), Some(OneToMany(Child(3, "child"), Some(GrandChild(7, "gchild"))))),
		OneToMany(SimpleParent(2, Nil), Some(OneToMany(Child(3, "child"), Some(GrandChild(8, "gchild"))))),
		OneToMany(SimpleParent(2, Nil), Some(OneToMany(Child(3, "child"), Some(GrandChild(9, "gchild")))))
	)

	val doubleChildren: List[OneToMany2[Parent2, Child, Child]] = List(
		OneToMany2(Parent2(1), Some(Child(1, "child")), Some(Child(11, "child"))),
		OneToMany2(Parent2(1), Some(Child(1, "child")), Some(Child(12, "child"))),
		OneToMany2(Parent2(1), Some(Child(2, "child")), Some(Child(13, "child"))),
		OneToMany2(Parent2(1), Some(Child(2, "child")), Some(Child(14, "child"))),
		OneToMany2(Parent2(2), Some(Child(3, "child")), Some(Child(15, "child"))),
		OneToMany2(Parent2(2), Some(Child(3, "child")), Some(Child(16, "child"))),
		OneToMany2(Parent2(2), Some(Child(4, "child")), Some(Child(17, "child"))),
		OneToMany2(Parent2(2), Some(Child(4, "child")), Some(Child(18, "child"))),
		OneToMany2(Parent2(2), Some(Child(5, "child")), None),
		OneToMany2(Parent2(2), Some(Child(5, "child")), Some(Child(19, "child")))
	)

	val complex: List[OneToMany5[Parent5, Child, Child, Child, Child, Child]] = List(
		OneToMany5(Parent5(1), Some(Child(1, "child")), Some(Child(10, "child")), None,						Some(Child(31, "child")), Some(Child(41, "child"))),
		OneToMany5(Parent5(1), Some(Child(1, "child")), Some(Child(10, "child")), None,						Some(Child(32, "child")), Some(Child(41, "child"))),
		OneToMany5(Parent5(1), None,					Some(Child(10, "child")), None,						Some(Child(33, "child")), Some(Child(42, "child"))),
		OneToMany5(Parent5(1), None, 					Some(Child(10, "child")), None,						Some(Child(34, "child")), Some(Child(42, "child"))),
		OneToMany5(Parent5(1), Some(Child(2, "child")), Some(Child(10, "child")), None,						Some(Child(35, "child")), Some(Child(44, "child"))),
		OneToMany5(Parent5(1), Some(Child(2, "child")), Some(Child(10, "child")), None,						Some(Child(36, "child")), Some(Child(44, "child"))),
		OneToMany5(Parent5(1), Some(Child(3, "child")), Some(Child(10, "child")), Some(Child(21, "child")), Some(Child(37, "child")), Some(Child(46, "child"))),
		OneToMany5(Parent5(1), Some(Child(3, "child")), Some(Child(10, "child")), Some(Child(22, "child")), Some(Child(38, "child")), Some(Child(46, "child"))),
		OneToMany5(Parent5(1), Some(Child(4, "child")), Some(Child(10, "child")), Some(Child(23, "child")), Some(Child(38, "child")), Some(Child(46, "child"))),

		OneToMany5(Parent5(2), Some(Child(5, "child")), Some(Child(11, "child")), Some(Child(24, "child")), Some(Child(39, "child")), Some(Child(47, "child"))),
		OneToMany5(Parent5(2), Some(Child(5, "child")), Some(Child(11, "child")), Some(Child(24, "child")), Some(Child(39, "child")), Some(Child(47, "child"))),
		OneToMany5(Parent5(2), Some(Child(5, "child")), Some(Child(11, "child")), Some(Child(24, "child")), Some(Child(39, "child")), Some(Child(48, "child"))),
		OneToMany5(Parent5(2), Some(Child(5, "child")), Some(Child(11, "child")), Some(Child(24, "child")), Some(Child(39, "child")), Some(Child(49, "child")))
	)

}
	
