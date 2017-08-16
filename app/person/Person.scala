package person

/**
  * Created by jcavell on 16/08/2017.
  */
case class Person(id: Option[Long] = None,
                  name: String,
                  email: String,
                  tel: String,
                  companyId: Option[Long])
