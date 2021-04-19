package jp.co.seattle.library.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import jp.co.seattle.library.dto.BookInfo;

@Configuration
public class BookInfoRowMapper implements RowMapper<BookInfo> {

    @Override
    public BookInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Query結果（ResultSet rs）を、オブジェクトに格納する実装
        BookInfo bookInfo = new BookInfo();
        // bookInfoの項目と、取得した結果(rs)のカラムをマッピングする
        bookInfo.setBookId(rs.getInt("ID"));
        bookInfo.setTitle(rs.getString("TITLE"));
        bookInfo.setThumbnail(rs.getString("THUMBNAIL_URL"));
        bookInfo.setPublisher(rs.getString("PUBLISHER"));
        bookInfo.setPublishDate(rs.getString("PUBLISH_DATE"));
        bookInfo.setAuthor(rs.getString("AUTHOR"));
        return bookInfo;
    }

}