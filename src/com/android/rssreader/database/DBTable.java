package com.android.rssreader.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
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
			Cursor cursor = new Cursor() {
				
				@Override
				public void unregisterDataSetObserver(DataSetObserver observer) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void unregisterContentObserver(ContentObserver observer) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setNotificationUri(ContentResolver cr, Uri uri) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public Bundle respond(Bundle extras) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				@Deprecated
				public boolean requery() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void registerDataSetObserver(DataSetObserver observer) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void registerContentObserver(ContentObserver observer) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean moveToPrevious() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean moveToPosition(int position) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean moveToNext() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean moveToLast() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean moveToFirst() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean move(int offset) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isNull(int columnIndex) {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isLast() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isFirst() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isClosed() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isBeforeFirst() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean isAfterLast() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public boolean getWantsAllOnMoveCalls() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public int getType(int columnIndex) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public String getString(int columnIndex) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public short getShort(int columnIndex) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public int getPosition() {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public long getLong(int columnIndex) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public int getInt(int columnIndex) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public float getFloat(int columnIndex) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public Bundle getExtras() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public double getDouble(int columnIndex) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public String[] getColumnNames() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public String getColumnName(int columnIndex) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public int getColumnIndexOrThrow(String columnName)
						throws IllegalArgumentException {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public int getColumnIndex(String columnName) {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public int getColumnCount() {
					// TODO Auto-generated method stub
					return 0;
				}
				
				@Override
				public byte[] getBlob(int columnIndex) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public void deactivate() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void close() {
					// TODO Auto-generated method stub
					
				}
			};
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
