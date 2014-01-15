/**
 * SQL Dialect for SQL Anywhere 10 - for the Hibernate 3.2 distribution
 * Copyright (C) 2008 Glenn Paulley
 * Contact: http://iablog.sybase.com/paulley
 *
 * This Hibernate dialect should be considered BETA software.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 **/

package org.hibernate.dialect;

import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.QueryException;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.DecodeCaseFragment;
import org.hibernate.sql.JoinFragment;
import org.hibernate.sql.ANSIJoinFragment;
import org.hibernate.sql.CaseFragment;
import org.hibernate.sql.ForUpdateFragment;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.function.CastFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.NvlFunction;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.dialect.lock.SelectLockingStrategy;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.TableHiLoGenerator;
import org.hibernate.util.ReflectHelper;
import org.hibernate.exception.SQLExceptionConverter;
import org.hibernate.exception.SQLStateConverter;
import org.hibernate.exception.ViolatedConstraintNameExtracter;
import org.hibernate.exception.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.JDBCExceptionHelper;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.util.StringHelper;
import org.hibernate.type.Type;

/**
 * SQL Dialect for SQL Anywhere 10 - for the Hibernate 3.2 distribution
 * (Tested on SQL Anywhere 10.0.1)
 *
 * @author Glenn Paulley, Sybase iAnywhere
 */

public class SQLAnywhere10Dialect extends Dialect {

    public SQLAnywhere10Dialect() {
	super();

	registerCharacterTypeMappings();
	registerNumericTypeMappings();
	registerDateTimeTypeMappings();
	registerLargeObjectTypeMappings();
	
	registerReverseHibernateTypeMappings();
	registerFunctions();
	setDialectProperties();
	registerKeywords();
    }

    protected void registerCharacterTypeMappings() {
	registerColumnType( Types.CHAR, "char(1)" );
	registerColumnType( Types.VARCHAR, 32767, "varchar($l)" );
	registerColumnType( Types.VARCHAR, "long varchar" );
    }

    protected void registerNumericTypeMappings() {
	registerColumnType( Types.BIT, "bit" ); // BIT type is NOT NULL by default
	registerColumnType( Types.BIGINT, "bigint" );
	registerColumnType( Types.SMALLINT, "smallint" );
	registerColumnType( Types.TINYINT, "tinyint" );
	registerColumnType( Types.INTEGER, "integer" );
	registerColumnType( Types.FLOAT, "real" );
	registerColumnType( Types.DOUBLE, "double" );
	registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
	registerColumnType( Types.DECIMAL, "numeric($p,$s)" );
    }

    protected void registerDateTimeTypeMappings() {
	registerColumnType( Types.DATE, "date" );
	registerColumnType( Types.TIME, "time" );
	registerColumnType( Types.TIMESTAMP, "timestamp" );
    }
    
    protected void registerLargeObjectTypeMappings() {
	registerColumnType( Types.VARBINARY, 32767, "varbinary($l)" );
	registerColumnType( Types.VARBINARY, "long varbinary" );
	registerColumnType( Types.BLOB, "long varbinary" );
	registerColumnType( Types.CLOB, "long varchar" );
    }

    protected void registerReverseHibernateTypeMappings() {}
    
    protected void registerFunctions() {
	
	registerMathFunctions();
	registerXMLFunctions();
	registerAggregationFunctions();
	registerBitFunctions();
	registerDateFunctions();
	registerStringFunctions();
	registerSOAPFunctions();
	registerMiscellaneousFunctions();
    }

    protected void registerMathFunctions() {

	// mathematical functions

	registerFunction( "abs", new StandardSQLFunction("abs") );
	registerFunction( "acos", new StandardSQLFunction("acos", Hibernate.DOUBLE) );
	registerFunction( "asin", new StandardSQLFunction("asin", Hibernate.DOUBLE) );
	registerFunction( "atan", new StandardSQLFunction("atan", Hibernate.DOUBLE) );
	registerFunction( "atan2", new StandardSQLFunction("atan2", Hibernate.DOUBLE) );
	registerFunction( "ceiling", new StandardSQLFunction("ceiling", Hibernate.DOUBLE) );
	registerFunction( "cos", new StandardSQLFunction("cos", Hibernate.DOUBLE) );
	registerFunction( "cot", new StandardSQLFunction("cot", Hibernate.DOUBLE) );
	registerFunction( "degrees", new StandardSQLFunction("degrees", Hibernate.DOUBLE) );
	registerFunction( "exp", new StandardSQLFunction("exp", Hibernate.DOUBLE) );
	registerFunction( "floor", new StandardSQLFunction("floor", Hibernate.DOUBLE) );
	registerFunction( "log", new StandardSQLFunction("log", Hibernate.DOUBLE) );
	registerFunction( "log10", new StandardSQLFunction("log10", Hibernate.DOUBLE) );
	registerFunction( "mod", new StandardSQLFunction("mod") );
	registerFunction( "pi", new NoArgSQLFunction("pi", Hibernate.DOUBLE) );
	registerFunction( "power", new StandardSQLFunction("power", Hibernate.DOUBLE) );
	registerFunction( "radians", new StandardSQLFunction("radians", Hibernate.DOUBLE) );
	registerFunction( "rand", new StandardSQLFunction("rand", Hibernate.DOUBLE) );
	registerFunction( "remainder", new StandardSQLFunction("remainder") );
	registerFunction( "round", new StandardSQLFunction("round") );
	registerFunction( "sign", new StandardSQLFunction("sign", Hibernate.INTEGER) );
	registerFunction( "sin", new StandardSQLFunction("sin", Hibernate.DOUBLE) );
	registerFunction( "sqrt", new StandardSQLFunction("sqrt", Hibernate.DOUBLE) );
	registerFunction( "tan", new StandardSQLFunction("tan", Hibernate.DOUBLE) );
	registerFunction( "truncate", new StandardSQLFunction("truncate") );
    }

    protected void registerXMLFunctions() {
		    
	// XML scalar functions only

	registerFunction( "xmlconcat", new VarArgsSQLFunction( Hibernate.STRING, "xmlconcat(", ",", ")" ) );
	registerFunction( "xmlelement", new VarArgsSQLFunction( Hibernate.STRING, "xmlelement(", ",", ")" ) );
	registerFunction( "xmlgen", new VarArgsSQLFunction( Hibernate.STRING, "xmlgen(", ",", ")" ) );
	// missing: XMLForest().
    }
	
    protected void registerAggregationFunctions() {
		    
	// basic aggregate, linear regression OLAP, and window functions
	
	registerFunction( "bit_or", new StandardSQLFunction("bit_or") );
	registerFunction( "bit_and", new StandardSQLFunction("bit_and") );
	registerFunction( "bit_xor", new StandardSQLFunction("bit_xor") );
	registerFunction( "covar_pop", new StandardSQLFunction("covar_pop", Hibernate.DOUBLE) );
	registerFunction( "covar_samp", new StandardSQLFunction("covar_samp", Hibernate.DOUBLE) );
	registerFunction( "corr", new StandardSQLFunction("corr", Hibernate.DOUBLE) );
	registerFunction( "first_value", new VarArgsSQLFunction(Hibernate.DOUBLE, "first_value(", ",", ")" ));
	registerFunction( "grouping", new StandardSQLFunction("grouping", Hibernate.INTEGER) );
	registerFunction( "last_value", new VarArgsSQLFunction(Hibernate.DOUBLE, "last_value(", ",", ")" ));
	registerFunction( "list", new VarArgsSQLFunction("list(", ",", ")" ));
	registerFunction( "regr_avgx", new StandardSQLFunction("regr_avgx", Hibernate.DOUBLE) );
	registerFunction( "regr_avgy", new StandardSQLFunction("regr_avgy", Hibernate.DOUBLE) );
	registerFunction( "regr_count", new StandardSQLFunction("regr_count", Hibernate.DOUBLE) );
	registerFunction( "regr_intercept", new StandardSQLFunction("regr_intercept", Hibernate.DOUBLE) );
	registerFunction( "regr_r2", new StandardSQLFunction("regr_r2", Hibernate.DOUBLE) );
	registerFunction( "regr_slope", new StandardSQLFunction("regr_slope", Hibernate.DOUBLE) );
	registerFunction( "regr_sxx", new StandardSQLFunction("regr_sxx", Hibernate.DOUBLE) );
	registerFunction( "regr_sxy", new StandardSQLFunction("regr_sxy", Hibernate.DOUBLE) );
	registerFunction( "regr_syy", new StandardSQLFunction("regr_syy", Hibernate.DOUBLE) );
	registerFunction( "set_bits", new StandardSQLFunction("set_bits") );
	registerFunction( "stddev", new StandardSQLFunction("stddev", Hibernate.DOUBLE) );
	registerFunction( "stddev_pop", new StandardSQLFunction("stddev_pop", Hibernate.DOUBLE) );
	registerFunction( "stddev_samp", new StandardSQLFunction("stddev_samp", Hibernate.DOUBLE) );
	registerFunction( "variance", new StandardSQLFunction("variance", Hibernate.DOUBLE) );
	registerFunction( "var_pop", new StandardSQLFunction("var_pop", Hibernate.DOUBLE) );
	registerFunction( "var_samp", new StandardSQLFunction("var_samp", Hibernate.DOUBLE) );
	registerFunction( "xmlagg", new StandardSQLFunction("xmlagg") );
    }
    
    protected void registerBitFunctions() {
	
	registerFunction( "bit_length", new StandardSQLFunction("bit_length", Hibernate.INTEGER) );
	registerFunction( "bit_substr", new StandardSQLFunction("bit_substr") );
	registerFunction( "get_bit", new StandardSQLFunction("get_bit", Hibernate.BOOLEAN) );
	registerFunction( "set_bit", new VarArgsSQLFunction("set_bit(", ",", ")" ));
    }

    protected void registerDateFunctions() {

	registerFunction( "date", new StandardSQLFunction("date", Hibernate.DATE) );
	registerFunction( "dateadd", new StandardSQLFunction("dateadd", Hibernate.TIMESTAMP) );
	registerFunction( "datediff", new StandardSQLFunction("datediff", Hibernate.INTEGER) );
	registerFunction( "dateformat", new StandardSQLFunction("dateformat", Hibernate.STRING) );
	registerFunction( "datename", new StandardSQLFunction("datename", Hibernate.STRING) );
	registerFunction( "datepart", new StandardSQLFunction("datepart", Hibernate.INTEGER) );
	registerFunction( "datetime", new StandardSQLFunction("datetime", Hibernate.TIMESTAMP) );
	registerFunction( "day", new StandardSQLFunction("day", Hibernate.INTEGER) );
	registerFunction( "dayname", new StandardSQLFunction("dayname", Hibernate.STRING) );
	registerFunction( "days", new StandardSQLFunction("days") );
	registerFunction( "dow", new StandardSQLFunction("dow", Hibernate.INTEGER) );
	registerFunction( "getdate", new StandardSQLFunction("getdate", Hibernate.TIMESTAMP) );
	registerFunction( "hour", new StandardSQLFunction("hour", Hibernate.INTEGER) );
	registerFunction( "hours", new StandardSQLFunction("hours") );
	registerFunction( "minute", new StandardSQLFunction("minute", Hibernate.INTEGER) );
	registerFunction( "minutes", new StandardSQLFunction("minutes") );
	registerFunction( "month", new StandardSQLFunction("month", Hibernate.INTEGER) );
	registerFunction( "monthname", new StandardSQLFunction("monthname", Hibernate.STRING) );
	registerFunction( "months", new StandardSQLFunction("months") );
	registerFunction( "now", new NoArgSQLFunction("now", Hibernate.TIMESTAMP) );
	registerFunction( "quarter", new StandardSQLFunction("quarter", Hibernate.INTEGER) );
	registerFunction( "second", new StandardSQLFunction("second", Hibernate.INTEGER) );
	registerFunction( "seconds", new StandardSQLFunction("seconds") );
	registerFunction( "today", new NoArgSQLFunction("now", Hibernate.DATE) );
	registerFunction( "weeks", new StandardSQLFunction("weeks") );
	registerFunction( "year", new StandardSQLFunction("year", Hibernate.INTEGER) );
	registerFunction( "years", new StandardSQLFunction("years") );
	registerFunction( "ymd", new StandardSQLFunction("ymd", Hibernate.DATE) );
	
	// compatibility functions
	
	registerFunction( "current_timestamp", new NoArgSQLFunction("getdate", Hibernate.TIMESTAMP) );
	registerFunction( "current_time", new NoArgSQLFunction("getdate", Hibernate.TIME) );
	registerFunction( "current_date", new NoArgSQLFunction("getdate", Hibernate.DATE) );
    }
    
    protected void registerStringFunctions() {
	
	registerFunction( "ascii", new StandardSQLFunction("ascii", Hibernate.INTEGER) );
	registerFunction( "byte64_decode", new StandardSQLFunction("byte64_decode", Hibernate.CLOB) );
	registerFunction( "byte64_encode", new StandardSQLFunction("byte64_encode", Hibernate.CLOB) );
	registerFunction( "byte_length", new StandardSQLFunction("byte_length", Hibernate.INTEGER) );
	registerFunction( "byte_substr", new VarArgsSQLFunction( Hibernate.STRING, "byte_substr(",",",")" ) );
	registerFunction( "char", new StandardSQLFunction("char", Hibernate.CHARACTER) );
	registerFunction( "charindex", new StandardSQLFunction("charindex", Hibernate.INTEGER) );
	registerFunction( "char_length", new StandardSQLFunction("char_length", Hibernate.INTEGER) );
	registerFunction( "compare", new VarArgsSQLFunction( Hibernate.INTEGER, "compare(",",",")" ) );
	registerFunction( "compress", new VarArgsSQLFunction( Hibernate.BLOB, "compress(",",",")" ) );
	registerFunction( "concat", new VarArgsSQLFunction( Hibernate.STRING, "(","+",")" ) );
	registerFunction( "csconvert", new VarArgsSQLFunction( Hibernate.CLOB, "csconvert(",",",")" ) );
	registerFunction( "decompress", new VarArgsSQLFunction( Hibernate.BLOB, "decompress(",",",")" ) );
	registerFunction( "decrypt", new VarArgsSQLFunction( Hibernate.BLOB, "decrypt(",",",")" ) );
	registerFunction( "difference", new StandardSQLFunction("difference", Hibernate.INTEGER) );
	registerFunction( "encrypt", new VarArgsSQLFunction( Hibernate.BLOB, "encrypt(",",",")" ) );
	registerFunction( "hash", new VarArgsSQLFunction( Hibernate.STRING, "hash(",",",")" ) );
	registerFunction( "insertstr", new StandardSQLFunction("insertstr", Hibernate.STRING) );
	registerFunction( "lcase", new StandardSQLFunction("lcase", Hibernate.STRING) );
	registerFunction( "left", new StandardSQLFunction("left", Hibernate.STRING) );
	registerFunction( "length", new StandardSQLFunction("length", Hibernate.LONG) );
	registerFunction( "locate", new VarArgsSQLFunction( Hibernate.INTEGER, "locate(",",",")" ) );
	registerFunction( "lower", new StandardSQLFunction("lower", Hibernate.STRING) );
	registerFunction( "ltrim", new StandardSQLFunction("ltrim", Hibernate.STRING) );
	registerFunction( "patindex", new StandardSQLFunction("patindex", Hibernate.INTEGER) );
	registerFunction( "repeat", new StandardSQLFunction("repeat", Hibernate.STRING) );
	registerFunction( "replace", new StandardSQLFunction("replace", Hibernate.STRING) );
	registerFunction( "replicate", new StandardSQLFunction("replicate", Hibernate.STRING) );
	registerFunction( "reverse", new StandardSQLFunction("reverse", Hibernate.STRING) );
	registerFunction( "right", new StandardSQLFunction("right", Hibernate.STRING) );
	registerFunction( "rtrim", new StandardSQLFunction("rtrim", Hibernate.STRING) );
	registerFunction( "similar", new StandardSQLFunction("rtrim", Hibernate.INTEGER) );
	registerFunction( "sortkey", new VarArgsSQLFunction( Hibernate.BINARY, "sortkey(",",",")" ) );
	registerFunction( "soundex", new StandardSQLFunction("soundex", Hibernate.INTEGER) );
	registerFunction( "space", new StandardSQLFunction("space", Hibernate.STRING) );
	registerFunction( "str", new VarArgsSQLFunction( Hibernate.STRING, "str(",",",")" ) );
	registerFunction( "string", new VarArgsSQLFunction( Hibernate.STRING, "string(",",",")" ) );
	registerFunction( "strtouuid", new StandardSQLFunction("strtouuid") );
	registerFunction( "stuff", new StandardSQLFunction("stuff", Hibernate.STRING) );
	
	// In SQL Anywhere 10, substr() semantics depends on the ANSI_substring option
	
	registerFunction( "substr", new VarArgsSQLFunction( Hibernate.STRING, "substr(",",",")" ) );
	registerFunction( "substring", new VarArgsSQLFunction( Hibernate.STRING, "substr(",",",")" ) );
	registerFunction( "to_char", new VarArgsSQLFunction( Hibernate.STRING, "to_char(",",",")" ) );
	registerFunction( "to_nchar", new VarArgsSQLFunction( Hibernate.STRING, "to_nchar(",",",")" ) );
	
	registerFunction( "trim", new StandardSQLFunction( "trim", Hibernate.STRING) );
	registerFunction( "ucase", new StandardSQLFunction("ucase", Hibernate.STRING) );
	registerFunction( "unicode", new StandardSQLFunction("unicode", Hibernate.INTEGER) );
	registerFunction( "unistr", new StandardSQLFunction("unistr", Hibernate.STRING) );
	registerFunction( "upper", new StandardSQLFunction("upper", Hibernate.STRING) );
	registerFunction( "uuidtostr", new StandardSQLFunction("uuidtostr", Hibernate.STRING) );
	
    }
    
    protected void registerSOAPFunctions() {
	
	registerFunction( "html_decode", new StandardSQLFunction("html_decode", Hibernate.STRING) );
	registerFunction( "html_encode", new StandardSQLFunction("html_encode", Hibernate.STRING) );
	registerFunction( "http_decode", new StandardSQLFunction("http_decode", Hibernate.STRING) );
	registerFunction( "http_encode", new StandardSQLFunction("http_encode", Hibernate.STRING) );
	registerFunction( "http_header", new StandardSQLFunction("http_header", Hibernate.STRING) );
	registerFunction( "http_variable", new VarArgsSQLFunction( "http_variable(",",",")" ) );
	registerFunction( "next_http_header", new StandardSQLFunction("next_http_header", Hibernate.STRING) );
	registerFunction( "next_http_variable", new StandardSQLFunction("next_http_variable", Hibernate.STRING) );
	registerFunction( "next_soap_header", new VarArgsSQLFunction( "next_soap_header(",",",")" ) );
    }
    
    protected void registerMiscellaneousFunctions() {
	
	registerFunction( "argn", new VarArgsSQLFunction( "argn(",",",")" ) );
	registerFunction( "coalesce", new VarArgsSQLFunction( "coalesce(",",",")" ) );
	registerFunction( "conflict", new StandardSQLFunction("conflict", Hibernate.BOOLEAN) );
	registerFunction( "connection_property", new VarArgsSQLFunction( "connection_property(",",",")" ) );
	registerFunction( "connection_extended_property", new VarArgsSQLFunction( "connection_extended_property(",",",")" ) );
	registerFunction( "db_extended_property", new VarArgsSQLFunction( "db_extended_property(",",",")" ) );
	registerFunction( "db_property", new VarArgsSQLFunction( "db_property(",",",")" ) );
	registerFunction( "errormsg", new NoArgSQLFunction("errormsg", Hibernate.STRING) );
	registerFunction( "estimate", new VarArgsSQLFunction( "estimate(",",",")" ) );
	registerFunction( "estimate_source", new VarArgsSQLFunction( Hibernate.STRING, "estimate_source(",",",")" ) );
	registerFunction( "experience_estimate", new VarArgsSQLFunction( "experience_estimate(",",",")" ) );
	registerFunction( "explanation", new VarArgsSQLFunction( Hibernate.STRING, "explanation(",",",")" ) );
	registerFunction( "exprtype", new StandardSQLFunction("exprtype", Hibernate.STRING) );
	
	registerFunction( "get_identity", new VarArgsSQLFunction( "get_identity(",",",")" ) );
	registerFunction( "graphical_plan", new VarArgsSQLFunction( Hibernate.STRING, "graphical_plan(",",",")" ) );
	registerFunction( "greater", new StandardSQLFunction("greater") );
	registerFunction( "identity", new StandardSQLFunction("identity") );
	registerFunction( "ifnull", new VarArgsSQLFunction( "ifnull(",",",")" ) );
	registerFunction( "index_estimate", new VarArgsSQLFunction( "index_estimate(",",",")" ) );
	registerFunction( "isnull", new VarArgsSQLFunction( "isnull(",",",")" ) );
	registerFunction( "lesser", new StandardSQLFunction("lesser") );
	registerFunction( "newid", new NoArgSQLFunction("newid", Hibernate.STRING) );
	registerFunction( "nullif", new StandardSQLFunction("nullif") );
	registerFunction( "number", new NoArgSQLFunction("number", Hibernate.INTEGER) );
	registerFunction( "plan", new VarArgsSQLFunction( Hibernate.STRING, "plan(",",",")" ) );
	registerFunction( "property", new StandardSQLFunction( "property", Hibernate.STRING ) );
	registerFunction( "property_description", new StandardSQLFunction( "property_description", Hibernate.STRING ) );
	registerFunction( "property_name", new StandardSQLFunction( "property_name", Hibernate.STRING ) );
	registerFunction( "property_number", new StandardSQLFunction( "property_number", Hibernate.INTEGER ) );
	registerFunction( "rewrite", new VarArgsSQLFunction( Hibernate.STRING, "rewrite(",",",")" ) );
	registerFunction( "row_number", new NoArgSQLFunction("row_number", Hibernate.INTEGER) );
	registerFunction( "sqldialect", new StandardSQLFunction("sqldialect", Hibernate.STRING) );
	registerFunction( "sqlflagger", new StandardSQLFunction("sqlflagger", Hibernate.STRING) );
	registerFunction( "traceback", new NoArgSQLFunction("traceback", Hibernate.STRING) );
	registerFunction( "transactsql", new NoArgSQLFunction("transactsql", Hibernate.STRING) );
	registerFunction( "varexists", new StandardSQLFunction("varexists", Hibernate.INTEGER) );
	registerFunction( "watcomsql", new StandardSQLFunction("watcomsql", Hibernate.STRING) );
    }

    /**
     * SQL Anywhere supports the addBatch() and executeBatch() methods
     * for a preparedStatement, but only for INSERT SQL statements;
     * the JDBC driver will raise an exception if this is attempted
     * for UPDATE or DELETE statements, or other types of SQL
     * statements. If enabled, Hibernate will attempt to batch any
     * type of statement; consequently, we must disable batching
     * support in its entirety. This means that wide inserts cannot be
     * utilized by a Hibernate application unless the application
     * constructs its own wide insert through a JDBC call.
     **/

    protected void setDialectProperties() {
	
	getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, NO_BATCH );
    }
    
    protected void registerKeywords() {
	
	registerKeyword( "top" );
	registerKeyword( "first" );
	registerKeyword( "fetch" );
	registerKeyword( "start" );
	registerKeyword( "at" );
	registerKeyword( "with" );
    }
    
    // IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    public boolean supportsIdentityColumns() {
	return true;
    }
    
    public String getIdentitySelectString() {
	return "select @@identity";
    }
    
    public String getIdentityColumnString() {
	/** 
	 * Autoincrement starts with 1, implicitly
	 * identifiers are automatically unsigned 64 bits, scaled to the specified data type
	 *
	 **/
	return "not null default autoincrement"; 
    }
    
    public boolean supportsInsertSelectIdentity() {
	return true;
    }

    public String appendIdentitySelectToInsert(String insertSQL) {
	return insertSQL + "\nselect @@identity";
    }

    
    // GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    public String getSelectGUIDString() {
	return "select newid()";
    }
    
    // limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     *
     * SQL Anywhere Version 10 and up support both TOP N and START AT clauses
     *
     */
    
    public boolean supportsLimit() {
	return true;
    }
    
    public boolean supportsLimitOffset() {
	return true;
    }
    
    public boolean supportsVariableLimit() {
	return true;
    }
    
    public boolean bindLimitParametersInReverseOrder() {
	// SQL Anywhere syntax is SELECT TOP n START AT m
	return true;
    }
    
    public boolean bindLimitParametersFirst() {
	return true;
    }
    
    static int getAfterSelectInsertPoint(String sql) {
	int selectIndex = sql.toLowerCase().indexOf( "select" );
	final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
	return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
    }
    
    public String getLimitString( String sql, boolean hasOffset ) {
        /**
         * 
         * Produce a parametertized SQL query using positional parameters for 
	 * TOP and START AT (if specified). Positional parameters are expected if
	 * supportsVariableLimit() is true. 
         **/
	StringBuffer buf = new StringBuffer( sql.length() + 28 )
	    .append( sql );
	if( hasOffset ) {
	    buf.insert( getAfterSelectInsertPoint( sql ), 
			" top ? start at ? " );
	} else {
	    buf.insert( getAfterSelectInsertPoint( sql ), 
			" top ? " );
	}
	return buf.toString();
    }
    
    public String getLimitString( String querySelect, int offset, int limit ) 
	throws UnsupportedOperationException {
	/**
	 * Generate SELECT TOP n START AT m syntax using bound parameters
	 * Constraints: n > 0, m >= 0
	 **/
	if ( offset < 0 ) {
	    throw new UnsupportedOperationException( "negative FirstResult (SQL offset) is not supported" );
	}
	if ( limit <= 0 ) {
	    throw new UnsupportedOperationException( "negative or zero MaxResults (SQL limit) is not supported" );
	}
	return getLimitString(querySelect, offset > 0 );
    }
    
    // lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     *
     * SQL Anywhere 10 supports READ, WRITE, and INTENT row
     * locks. INTENT locks are sufficient to ensure that other
     * concurrent connections cannot modify a row (though other
     * connections can still read that row). SQL Anywhere also
     * supports 3 modes of snapshot isolation (multi-version
     * concurrency control (MVCC). <p/>
     *
     * SQL Anywhere's FOR UPDATE clause supports 
     *     FOR UPDATE BY [ LOCK | VALUES ]
     *     FOR UPDATE OF ( COLUMN LIST )
     *
     * though they cannot be specified at the same time. BY LOCK is
     * the syntax that acquires INTENT locks.  FOR UPDATE BY VALUES
     * forces the use of the KEYSET cursor, which returns a warning to
     * the application when a row in the cursor has been subsequently
     * modified by another connection, and an error if the row has
     * been deleted. <p/>
     *
     * SQL Anywhere does not support the FOR UPDATE NOWAIT syntax of
     * Oracle on a statement-by-statement basis.  However, the
     * identical functionality is provided by setting the connection
     * option BLOCKING to "OFF", or setting an appropriate timeout
     * period through the connection option BLOCKING_TIMEOUT.
     *
     **/

    public String getForUpdateString( LockMode lockMode ) {
	if( lockMode == LockMode.READ ) {
	    return getForReadOnlyString();
	} else if( lockMode == LockMode.UPGRADE ) {
	    return getForUpdateByLockString();
	} else if( lockMode == LockMode.UPGRADE_NOWAIT ) {
	    return getForUpdateNowaitString();
	} else if( lockMode == LockMode.FORCE ) {
	    return getForUpdateNowaitString();
	} else {
	    return "";
	}
    }

    public boolean forUpdateOfColumns() {
	return true;
    }

    public boolean supportsOuterJoinForUpdate() {
	return true;
    }

    public String getForReadOnlyString() {
	/**
	 * Enforce the condition that this query is read-only. This ensure that certain
	 * query rewrite optimizations, such as join elimination, can be used. 
	 *
	 **/

	return " for read only";
    }
	
    public String getForUpdateByLockString() {
	// This acquires intent locks on the rows. 
	return " for update by lock";
    }
	
    public String getForUpdateNoWaitString() {
	// This acquires intent locks on the rows. 
	return getForUpdateByLockString();
    }
	
    public String getForUpdateString(String aliases) {
	return " for update of " + aliases;
    }

    public boolean doesReadCommittedCauseWritersToBlockReaders() {
	return true; // here assume applications are not using snapshot isolation
    }

    public boolean doesRepeatableReadCauseReadersToBlockWriters() {
	return true; // here assume applications are not using snapshot isolation
    }

    public String appendLockHint(LockMode mode, String tableName) {
	if ( mode.greaterThan( LockMode.READ ) ) {
	    return tableName + " with( updlock )"; // Set an INTENT row lock
	} else {
	    return tableName;
	}
    }

    public String applyLocksToSql(String sql, Map aliasedLockModes, Map keyColumnNames) {
	/**
	 *
	 * Stolen from SybaseDialect.java. In SQL Anywhere, table hints
	 * using WITH do *NOT* cascade into base table(s) if the hint is
	 * applied to a view. FOR UPDATE BY LOCK in the SELECT clause
	 * is strongly preferred.
	 *
	 **/

	Iterator itr = aliasedLockModes.entrySet().iterator();
	StringBuffer buffer = new StringBuffer( sql );
	int correction = 0;
	while( itr.hasNext() ) {
	    final Map.Entry entry = ( Map.Entry ) itr.next();
	    final LockMode lockMode = ( LockMode ) entry.getValue();
	    if( lockMode.greaterThan( LockMode.READ ) ) {
		final String alias = ( String ) entry.getKey();
		int start = -1, end = -1;
		if( sql.endsWith( " " + alias ) ) {
		    start = ( sql.length() - alias.length() ) + correction;
		    end = start + alias.length();
		} else {
		    int position = sql.indexOf( " " + alias + " " );
		    if( position <= -1 ) {
			position = sql.indexOf( " " + alias + "," );
		    }
		    if( position > -1 ) {
			start = position + correction + 1;
			end = start + alias.length();
		    }
		}
		
		if( start > -1 ) {
		    final String lockHint = appendLockHint( lockMode, alias );
		    buffer.replace( start, end, lockHint );
		    correction += ( lockHint.length() - alias.length() );
		}
	    }
	}
	return buffer.toString();
    }

    // SQL Anywhere-specific query syntax

    /**
     *
     * Quoted identifiers are controlled through the QUOTED_IDENTIFIER connection option.
     *
     **/

    public boolean supportsCurrentTimestampSelection() {
	return true;
    }
    
    public String getCurrentTimestampSQLFunctionName() {
	return "getdate";
    }

    public boolean isCurrentTimestampSelectStringCallable() {
	return false;
    }

    public String getCurrentTimestampSelectString() {
	return "select getdate()";
    }

    public char closeQuote() {
	return '"';
    }

    public char openQuote() {
	return '"';
    }

    // Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



    public boolean supportsEmptyInList() {
	return false;
    }

    public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
	return false;
    }

    public boolean supportsExistsInSelect() {
	return false;
    }

    public boolean areStringComparisonsCaseInsensitive() {
	/** 
	 *
	 * By default, the SQL Anywhere dbinit utility creates a
	 * case-insensitive database for the CHAR collation.  This can
	 * be changed through the use of the -c command line switch on
	 * dbinit, and the setting may differ for the NCHAR collation
	 * for national character sets.  Whether or not a database
	 * supports case-sensitive comparisons can be determined via
	 * the * DB_Extended_property() function, for example
	 *
	 * SELECT DB_EXTENDED_PROPERTY( 'Collation', 'CaseSensitivity');
	 *
	 **/ 

	return( true ); // return the default for the CHAR collation
    }

    // DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public boolean dropConstraints() {
	// Unnecessary to drop all constraints before DROP of a table.
	return false;
    }

    public String getAddColumnString() {
	return "add ";
    }
	
    public String getNullColumnString() {
	return " null";
    }
	
    public boolean qualifyIndexName() {
	// Not necessary to qualify index names
	return false;
    }

    public String getDropForeignKeyString() {
	// ALTER TABLE DROP FOREIGN KEY <foo>
	return " drop foreign key ";
    }
    
    public boolean supportsCommentOn() {
	return false;
    }

    public String getTableComment( String comment ) {
	return "";
    }

    public String getColumnComment( String comment ) {
	return "";
    }

    /**
     * SQL Anywhere currently supports only "VALUES (DEFAULT)", not
     * the ANSI standard "DEFAULT VALUES". This latter syntax will be
     * supported in the SQL Anywhere 11.0.1 release.  For the moment,
     * "VALUES (DEFAULT)" works only for a single-column table.
     **/

    public String getNoColumnsInsertString() {
	return "values (default)";
    }
    
    // temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public boolean supportsTemporaryTables() {
	return true;
    }

    public String getCreateTemporaryTableString() {
	/** 
	 * In SQL Anywhere, the syntax
	 *
	 * DECLARE LOCAL TEMPORARY TABLE ...
	 *
	 * can also be used, which creates a temporary table with procedure scope, 
	 * which may be important for stored procedures. For Java clients, this is
	 * less important, though it is possible to create temporary tables within
	 * a Hibernate-built Java stored procedure.
	 **/
	return "create local temporary table ";
    }

    public String getCreateTemporaryTablePostfix() {
	// Assume that temporary table rows should be preserved across COMMITs.
	return " on commit preserve rows ";
    }

    public Boolean performTemporaryTableDDLInIsolation() {
	/**
	 * SQL Anywhere 10 does not perform a COMMIT upon creation of
	 * a temporary table.  However, it does perform an implicit
	 * COMMIT when creating an index over a temporary table, or
	 * upon ALTERing the definition of temporary table.
	 **/
	return null;
    }

    // callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public int registerResultSetOutParameter( CallableStatement statement, int col ) throws SQLException {
	return col; // SQL Anywhere just returns automatically
    }

    public ResultSet getResultSet( CallableStatement ps ) throws SQLException {
	boolean isResultSet = ps.execute();
	// This assumes you will want to ignore any update counts
	while( !isResultSet && ps.getUpdateCount() != -1 ) {
	    isResultSet = ps.getMoreResults();
	}
	// You may still have other ResultSets or update counts left to process here
	// but you can't do it now or the ResultSet you just got will be closed
	return ps.getResultSet();
    }

    // union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public boolean supportsUnionAll() {
	return true;
    }

}
