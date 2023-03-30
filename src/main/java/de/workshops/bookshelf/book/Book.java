package de.workshops.bookshelf.book;

import lombok.Data;

@Data
public class Book {

    private String title;
    private String description;
    private String author;
    private String isbn;

    public Book(String s, String s1, String s3) {
        this.isbn = s;
        this.title = s1;
        this.author = s3;
    }
}
