
package de.workshops.bookshelf.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record BookSearchRequest (
        @NotBlank @Size(min = 3) String author,
        @Size(min = 10, max = 14) String isbn
) {
}
