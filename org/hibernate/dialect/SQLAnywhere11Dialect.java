package org.hibernate.dialect;

import org.hibernate.cfg.Environment;

/**
 * SQL Dialect for SQL Anywhere 11.0.1 and up - for the Hibernate 3.2 distribution
 * Copyright (C) 2008 Glenn Paulley
 * Contact: http://iablog.sybase.com/paulley
 * 
 * extending SQLAnywhere10Dialect - Sybase/iAnywhere SQL Anywhere 10 Dialect
 * @author Glenn Paulley
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

public class SQLAnywhere11Dialect extends SQLAnywhere10Dialect {

    /**
     * The SQL Anywhere 11.x JDBC driver supports the addBatch() and
     * executeBatch() methods for a preparedStatement for all update
     * DML statements (INSERT, UPDATE, DELETE). At this time, however,
     * the SQL Anywhere server supports ONLY wide INSERT SQL
     * statements. Wide UPDATE and DELETE statements are supported
     * from the client, but the server only executes a single UPDATE
     * or DELETE at a time.
     **/

    static final String DEFAULT_WIDE_BATCH_SIZE = "40";

    protected void setDialectProperties() {
	
	getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, 
					    DEFAULT_WIDE_BATCH_SIZE );
    }
    
    protected void registerKeywords() {
	
	registerKeyword( "top" );
	registerKeyword( "first" );
	registerKeyword( "fetch" );
	registerKeyword( "start" );
	registerKeyword( "at" );
	registerKeyword( "with" );
	registerKeyword( "contains" );
	registerKeyword( "regexp" );
	registerKeyword( "similar" );
    }

	
}
