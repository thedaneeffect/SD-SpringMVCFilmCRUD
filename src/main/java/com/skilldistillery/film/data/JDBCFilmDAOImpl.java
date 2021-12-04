package com.skilldistillery.film.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skilldistillery.film.entities.Film;
import com.skilldistillery.film.entities.Rating;

@Service
public class JDBCFilmDAOImpl implements FilmDAO {
	
	private static final String URL = "jdbc:mysql://localhost:3306/sdvid?useSSL=false";
	private static final String user = "student";
	private static final String pass = "student";
	
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	//in progress need to return booleans if it was created or not
	@Override
	public boolean createFilm(Film film) throws SQLException {
		Connection conn;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false);
			String sql = "INSERT INTO film (title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, film.getTitle());
			stmt.setString(2, film.getDescription());
			stmt.setDate(3, film.getReleaseYear());
			
			//go back and figure these out
			stmt.setInt(4, film.getLanguage());
			
			stmt.setInt(5, film.getRentalDuration());
			stmt.setDouble(6, film.getRentalRate());
			stmt.setInt(7, film.getLength());
			stmt.setDouble(8, film.getReplacementCost());
			stmt.setString(9, film.getRating().toString());
			stmt.setString(10, film.getSpecialFeatures());
			
			
			
			int updateCount = stmt.executeUpdate();
			if (updateCount == 1) {
				ResultSet keys = stmt.getGeneratedKeys();
				if (keys.next()) {
					int newFilmId = keys.getInt(1);
					film.setId(newFilmId);
				}
			}
			stmt.close();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return film;
	}

	@Override
	public Film getFilmById(int filmId) throws SQLException {
		Film film = null;
		String sql = "SELECT film.id, film.title, film.description, film.release_year, language.name, film.rental_duration, film.rental_rate, film.length, film.replacement_cost, film.rating, film.special_features FROM film JOIN language ON film.language_id = language.id WHERE film.id = ?";
		try {
			Connection conn = DriverManager.getConnection(URL, user, pass);
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, filmId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {

				int filmID = rs.getInt("id");
				String title = rs.getString("title");
				String desc = rs.getString("description");
				Date releaseYear = rs.getDate("release_year");
				String lang = rs.getString("language.name");
				int rentDur = rs.getInt("rental_duration");
				double rate = rs.getDouble("rental_rate");
				Integer length = rs.getInt("length");
				double repCost = rs.getDouble("replacement_cost");
				Rating rating = Rating.valueOf(rs.getString("rating"));
				String features = rs.getString("special_features");
				film = new Film(filmID, title, desc, releaseYear, lang, rentDur, rate, length, repCost, rating,
						features);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return film;
	}

	@Override
	public List<Film> getFilmByKeyword(String keyword) throws SQLException {
		List<Film> films = new ArrayList();
		String sql = "SELECT id FROM film WHERE title LIKE ? OR description LIKE ?";
		Connection conn;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, "%" + keyword + "%");
			stmt.setString(2, "%" + keyword + "%");
			ResultSet filmResult = stmt.executeQuery();
			while (filmResult.next()) {
				films.add(getFilmById(filmResult.getInt(1)));
			}
			filmResult.close();
			stmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	@Override
	public boolean updateFilm(Film film) throws SQLException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false);
			String sql = "UPDATE film SET title=? WHERE id=?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, film.getTitle());
			stmt.setInt(2, film.getId());
			int updateCount = stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn != null) {
				try {conn.rollback();}
				catch(SQLException sqle2) {
					System.err.println("Attempting rollback");
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean deleteFilm(Film film) throws SQLException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, user, pass);
			conn.setAutoCommit(false);
			String sql = "DELETE FROM film_actor WHERE film_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, film.getId());
			int updateCount = stmt.executeUpdate();
			sql = "DELETE FROM film WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, film.getId());
			updateCount = stmt.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Attempting rollback");
				}
			}
			return false;
		}
		return true;
	}
	
	
	//grab the langauge ID to use to set the language in the create film method
	private int getLanguageId(String language) {
		int langId = 0;
		String sql = "SELECT id FROM language WHERE "
		return langId;
	}

}
