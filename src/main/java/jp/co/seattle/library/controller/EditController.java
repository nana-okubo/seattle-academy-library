package jp.co.seattle.library.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class EditController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @RequestMapping(value = "/editBook", method = RequestMethod.POST) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    //details.jspの編集ボタンを押すとここに飛ぶ
    public String login(Locale locale, @RequestParam("bookId") Integer bookId, Model model) {

        //詳細画面を出したい

        BookDetailsInfo newIdInfo = booksService.getBookInfo(bookId);
        model.addAttribute("bookDetailsInfo", newIdInfo);

        return "edit";
    }

    //更新ボタンを押した時の話
    @Transactional
    @RequestMapping(value = "/updateBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String updateBook(Locale locale,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("publishDate") String publishDate,
            @RequestParam("isbn") String isbn,
            @RequestParam("description") String description,
            @RequestParam("thumbnail") MultipartFile file,
            @RequestParam("bookId") int bookId,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setPublishDate(publishDate);
        bookInfo.setIsbn(isbn);
        bookInfo.setDescription(description);
        bookInfo.setBookId(bookId);

        boolean isIsbnForCheck = isbn.matches("(^\\d{10,13}$)?");
        boolean errorCheck = false;

        if (!isIsbnForCheck) {
            model.addAttribute("error3", "ISBNの桁数または半角数字が正しくありません");
            errorCheck = true;

        }

        try {
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            df.setLenient(false); //そういうもん
            df.parse(publishDate);

        } catch (ParseException p) {
            model.addAttribute("error1", "出版日は半角数字のYYYYMMDDを形式で入力してください");
            errorCheck = true;
        }

        if (errorCheck) {
            return "edit";
        }
        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookDetailsInfo", bookInfo);
                return "edit";
            }
        }

        // 書籍情報を更新する　修正
        booksService.updateBook(bookInfo);

        model.addAttribute("resultMessage", "編集完了");

        booksService.getBookInfo(bookId);
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        // TODO 更新した書籍の詳細情報を表示するように実装
        //  詳細画面に遷移す


        return "details";
    }

}
