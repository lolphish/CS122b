package fablix;

import java.sql.*; 
import java.util.*; 
  
public class AccessDB {  
	private Connection conn;
	private Statement stmt;
	private QueriesMaker queries;
	private PreparedStatement ps;
	private ResultSet rs;

	public AccessDB() {
		String DB_URL = "jdbc:mysql://localhost/moviedb";
		String username = "root";
		String password = "123456789";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, username, password);
			queries = new QueriesMaker(conn, ps);
		} catch(SQLException se){
		  //Handle errors for JDBC
		} catch(Exception e){
		  //Handle errors for Class.forName
		}	   
	}
	public boolean validate(String name,String pass){  
		try{  
			return queries.isCustomer(name, pass);
		}catch(Exception e){
			System.out.println(e);
			return false;
		}
	}

    public boolean validateCC(String fname, String lname, String ccid, String expiration){
        try{
            return queries.isCreditCard(fname, lname, ccid, expiration);
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

	public String getName(String email){
        try{
			rs = queries.selectQuery(String.format("select first_name from customers where email = '%s'", email)); 
			rs.next();
			return rs.getString("first_name");
		}catch(Exception e){
			System.out.println(e);
			return "";
		}
	}
    public ResultSet getPageMovies(int offset, boolean last, String order, int pageLimit, String genre) {
        try{
            if(last){
                return queries.getLast(queries.getPageMovie(genre, order), pageLimit);
            }    
            return queries.paginate(queries.getPageMovie(genre, order), offset, pageLimit);
        }catch(Exception e){
            System.out.println(e);
            return rs;
        }
    }
    public ResultSet searchForMovie(String toSearch, String genre, String order, int offset, boolean last, int pageLimit)  {
        try{
            if(last){
                return queries.getLast(queries.getMoviesActor(toSearch, genre, order), pageLimit);
            }
            return queries.paginate(queries.getMoviesActor(toSearch, genre, order), offset, pageLimit);
        }catch(Exception e) {
            return rs;
        }
    }
    public ResultSet searchMovies(String toSearch, int offset, boolean last, String order, int pageLimit) {
        try{
            if(last){
                return queries.getLast(queries.searchMovie(toSearch, order), pageLimit);
            }
            return queries.paginate(queries.searchMovie(toSearch, order), offset, pageLimit);
        }catch(Exception e){
            System.out.println(e);
            return rs;
        }
    }
    //Look for movie content
    public ResultSet findMovie(String id){
        try{
            rs = queries.selectQuery(String.format("select * from movies where id = '%s'", id));
            return rs;
        }catch(Exception e){
            System.out.println(e);
            return rs;
        }
    }

    //Find stars that are in the movie
    public ResultSet findStars(String movieID){
        try{
            rs = queries.selectQuery(String.format("select id as s_id, concat(s.first_name, ' ', s.last_name) as name from stars s, stars_in_movies sm where s.id = sm.star_id and sm.movie_id = '%s'", movieID));
            return rs;
        }catch(Exception e){
            System.out.println(e);
            return rs;
        }
    }
    
    //Find the genres of a movie
    public ResultSet findGenre(String movieID){
        try{
            rs = queries.selectQuery(String.format("select gname from genres g, genres_in_movies gm where g.id = gm.genre_id and gm.movie_id = '%s'", movieID));
            return rs;
        }catch(Exception e){
            System.out.println(e);
            return rs;
        }
    }
    
    //Look for contents of a star given ID
    public ResultSet getStar(String starID){
        try{
            rs = queries.selectQuery(String.format("select * from stars where id = '%s'", starID));
            return rs;
        }catch(Exception e){
            System.out.println(e);
            return rs;
        }
    }
 
    public ResultSet findMovies(String starID){
        try{
            rs = queries.selectQuery(String.format("select m.id as m_id, title from movies m, stars_in_movies sm where m.id = sm.movie_id and sm.star_id = '%s'", starID));
            return rs;
        }catch(Exception e){
            System.out.println(e);
            return rs;
        }
    }

    

    public ArrayList<String> getMovie(String id){
        ArrayList<String> movie = new ArrayList<String>();
        try{
            rs = findMovie(id);
            while(rs.next()){
                for(int i = 1; i <= 6; ++i){
                    movie.add(rs.getString(i));
                }
            }
        }catch(Exception e){
        }

        return movie;
    }

    public HashMap<String, String> getStars(String movieID){
        HashMap<String, String> stars = new HashMap<String, String>();
        try{
            rs = findStars(movieID);
            while(rs.next()){
                stars.put(rs.getString("s_id"), rs.getString("name"));
            }
        }catch(Exception e){
        }
        return stars;
    }

    public ArrayList<String> getGenre(String movieID){
        ArrayList<String> genre = new ArrayList<String>();
        try{
            rs = findGenre(movieID);
            while(rs.next()){
                genre.add(rs.getString(1));
            }
        }catch(Exception e){
        }
        return genre;
    }

    public ArrayList<String> getStarInfo(String id) {
        ArrayList<String> star = new ArrayList<String>();
        try{
            rs = getStar(id);
            while(rs.next()){
                for(int i = 1; i <= 5; ++i){
                    star.add(rs.getString(i));
                }
            }
        }catch(Exception e){
        }
        return star;
    }

    public HashMap<String, String> getMovies(String id){
        HashMap<String, String> movies = new HashMap<String, String>();
        try{
            rs = findMovies(id);
            while(rs.next()){
                movies.put(rs.getString("m_id"), rs.getString("title"));
            }
        }catch(Exception e){
        }
        return movies;
    }

    // Get user id from email cookie
    public String getCustomerId(String email)
    {
    	try {
			rs = queries.selectQuery(String.format("select * from customers where email = '%s'", email));
			return rs.getString(1);
    	} catch (Exception e) {
    		return null;
    	}
    }


}  