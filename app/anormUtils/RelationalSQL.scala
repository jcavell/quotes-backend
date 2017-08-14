package anormUtils

import anorm._
import java.sql.Connection

/** Holds a `SimpleSql[T]` statement created from `SQL(...)` in order to apply the underlying `RowParser`,
*   then use `RowFlattener`s to flatten the relational structure into the parent object.
*/
case class RelationalSQL[T](sql: SimpleSql[T]) {

    def asRelational[A, B, R](rp: RelationalResultParser[A, B, R])(implicit rf: RowFlattener[A, B], c: Connection): R = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

    def asRelational[A, B1, B2, R](rp: RelationalResultParser2[A, B1, B2, R])(implicit rf: RowFlattener2[A, B1, B2], c: Connection): R = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

    def asRelational[A, B1, B2, B3, R](rp: RelationalResultParser3[A, B1, B2, B3, R])(implicit rf: RowFlattener3[A, B1, B2, B3], c: Connection): R = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

    def asRelational[A, B1, B2, B3, B4, R](rp: RelationalResultParser4[A, B1, B2, B3, B4, R])(implicit rf: RowFlattener4[A, B1, B2, B3, B4], c: Connection): R = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

    def asRelational[A, B1, B2, B3, B4, B5, R](rp: RelationalResultParser5[A, B1, B2, B3, B4, B5, R])(implicit rf: RowFlattener5[A, B1, B2, B3, B4, B5], c: Connection): R = 
        rp.f(OneToMany.flatten(this.sql.as(rp.parser.parser *)))

}

/** Holds a `RelationalParser` and a function that describes what to do with the result `List`. */
case class RelationalResultParser[A, B, R](parser: RelationalParser[A, B], f: List[A] => R)

case class RelationalResultParser2[A, B1, B2, R](parser: RelationalParser2[A, B1, B2], f: List[A] => R)

case class RelationalResultParser3[A, B1, B2, B3, R](parser: RelationalParser3[A, B1, B2, B3], f: List[A] => R)

case class RelationalResultParser4[A, B1, B2, B3, B4, R](parser: RelationalParser4[A, B1, B2, B3, B4], f: List[A] => R)

case class RelationalResultParser5[A, B1, B2, B3, B4, B5, R](parser: RelationalParser5[A, B1, B2, B3, B4, B5], f: List[A] => R)