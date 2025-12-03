package com.team12.auction.dao;

import java.sql.Connection;

public abstract class BaseDao {
	protected final Connection conn;

    public BaseDao(Connection conn) {
       if (conn == null) {
            throw new IllegalArgumentException("Connection must not be null");
        }
        this.conn = conn;
    }
}
