package anormUtils

import org.specs2.mutable._

object OneToManySpec extends Specification {

	"The OneToMany mapper" should {

		"flatten a simple one-to-many result set" in {
			val result: List[SimpleParent] = OneToMany.flatten(Mocks.simpleOneToMany)
			result must have size(4)
			result(0).children must have size(2)
			result(0).children.map(_.id) must contain(allOf(1,2))
			result(1).children must have size(3)
			result(1).children.map(_.id) must contain(allOf(3,4,5))
			result(2).children must have size(3)
			result(2).children.map(_.id) must contain(allOf(6,7,8))
			result(3).children must have size(1)
			result(3).children.map(_.id) must contain(9)
		}

		"flatten a multi-level one-to-many result set" in {
			val result: List[SimpleParent] = OneToMany.flatten(Mocks.doubleOneToMany)
			result must have size(2)
			result(0).children must have size(1)
			result(0).children(0).grandchildren must have size(2)
			result(0).children(0).grandchildren.map(_.id) must contain(allOf(1,2))
			result(1).children must have size(2)
			result(1).children(0).grandchildren must have size(3)
			result(1).children(0).grandchildren.map(_.id) must contain(allOf(3,4,5))
			result(1).children(1).grandchildren must have size(4)
			result(1).children(1).grandchildren.map(_.id) must contain(allOf(6,7,8,9))
		}

		"flatten a parent with two child lists" in {
			val result: List[Parent2] = OneToMany.flatten(Mocks.doubleChildren)
			result must have size(2)
			result(0).children must have size(2)
			result(0).children.map(_.id) must contain(1,2)
			result(0).children2 must have size(4)
			result(0).children2.map(_.id) must contain(11,12,13,14)
			result(1).children must have size(3)
			result(1).children.map(_.id) must contain(allOf(3,4,5))
			result(1).children2 must have size(5)
			result(1).children2.map(_.id) must contain(allOf(15,16,17,18,19))
		}

		"flatten a parent with five child lists" in {
			val result: List[Parent5] = OneToMany.flatten(Mocks.complex)
			result must have size(2)
			result(0).c1 must have size(4)
			result(0).c1.map(_.id) must contain(allOf(1,2,3,4))
			result(0).c2 must have size(1)
			result(0).c2.map(_.id) must contain(10)
			result(0).c3 must have size(3)
			result(0).c3.map(_.id) must contain(allOf(21,22,23))
			result(0).c4 must have size(8)
			result(0).c4.map(_.id) must contain(allOf(31,32,33,34,35,36,37,38))
			result(0).c5 must have size(4)
			result(0).c5.map(_.id) must contain(allOf(41,42,44,46))

			result(1).c1 must have size(1)
			result(1).c1.map(_.id) must contain(5)
			result(1).c2 must have size(1)
			result(1).c2.map(_.id) must contain(11)
			result(1).c3 must have size(1)
			result(1).c3.map(_.id) must contain(24)
			result(1).c4 must have size(1)
			result(1).c4.map(_.id) must contain(39)
			result(1).c5 must have size(3)
			result(1).c5.map(_.id) must contain(allOf(47,48,49))

		}


	}

}