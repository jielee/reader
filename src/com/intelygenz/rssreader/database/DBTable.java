package com.intelygenz.rssreader.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * 
 * @author Jie Lee
 *	Clase generica abstracta para hacer las operaciones de CRUD reusables 
 *	Ese proyecto tiene apenas la entidad news,pero tal vez en el futuro.
 * @param <T>
 */
public abstract class DBTable<T> {

	protected Context context;

	protected DBTable(Context context) {
		this.context = context;
	}

	public abstract void save(T object);
	
	public abstract List<T> getAll();

	public abstract T getFromCursor(Cursor cursor);

	protected void bind(SQLiteStatement statement, int index, Object value) {
		if (value == null) {
			statement.bindNull(index);
		} else if (value instanceof String) {
			statement.bindString(index, (String) value);
		} else if (value instanceof Long) {
			statement.bindLong(index, (Long) value);
		} else if (value instanceof Integer) {
			statement.bindLong(index, (Integer) value);
		} else if (value instanceof Double) {
			statement.bindDouble(index, (Double) value);
		} else if (value instanceof Boolean) {
			statement.bindLong(index, (Boolean) value ? 1 : 0);
		}
	}

	protected SQLiteStatement prepareStatement(String sql, Object... values) {
		SQLiteStatement statement = null;
		for (int i = 0; i < values.length; i++) {
			bind(statement, i + 1, values[i]);
		}

		return statement;
	}


	protected void insert(T object, String sql, Object... values) {
		try {
			SQLiteStatement statement = prepareStatement(sql, values);
			statement.executeInsert();
			statement.close();
		} catch (SQLiteConstraintException e) {
			Log.w(getClass().getSimpleName(), e.getMessage());

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected List<T> getList(String sql, String... selectionArgs) {
		List<T> list = new ArrayList<T>();
		try {
			Cursor cursor = null;
			while (cursor.moveToNext()) {
				T object = getFromCursor(cursor);
				list.add(object);
			}
			cursor.close();

		} catch (SQLException e) {
			list.clear();
		} catch (RuntimeException e) {
			list.clear();
		}
		return list;
	}


}
