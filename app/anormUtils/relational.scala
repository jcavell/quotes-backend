package anormUtils

import anorm._

package object relational {

	/** Implicitly convert `SimpleSql[T]` to `RelationalSQL[T]` for almost seemless integration
	* @param sql 
	* @return A `RelationalSQL` wrapper of `SimpleSql`
	*/
	implicit def simple2Relational[T](sql: SimpleSql[T]): RelationalSQL[T] = RelationalSQL(sql)

	/** Implicitly convert `SqlQuery` to `RelationalSQL[Row]`
	* @param sql
	* @return A `RelationalSQL` wrapper of the `SqlQuery` as `SimpleSql[Row]`
	*/
	implicit def query2Relational(sql: SqlQuery): RelationalSQL[Row] = RelationalSQL(sql.asSimple[Row]())
}