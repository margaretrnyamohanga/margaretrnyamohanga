package genius.code.bamfamily.junitrestapiapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/book")
public class BookController {

    @Autowired
    BookRepository bookRepository;

    @GetMapping
    public List<Book> getAllBookRecords() {
        return bookRepository.findAll();

    }

    @GetMapping(value = "{bookId}")
    public Book getBookById(@PathVariable(value = "bookId") Long bookId) {
        return bookRepository.findById(bookId).get();
    }

    @PostMapping
    public Book CreateBookRecord(@RequestBody @Validated Book bookRecord){
        return bookRepository.save(bookRecord);
    }

    @PutMapping
    public Book updateRecord(@RequestBody @Validated Book bookRecord) throws  ClassNotFoundException {
        if(bookRecord == null || bookRecord.getBookId() == null) {
            throw new ClassNotFoundException("BookRecord or ID must not be null");

        }
        Optional<Book> optionalBook = bookRepository.findById(bookRecord.getBookId());
        if(optionalBook.isEmpty()) {
            throw new ClassNotFoundException("Book with ID: " + bookRecord.getBookId() + " does not exist");
        }
        Book existingBookRecord = optionalBook.get();
        existingBookRecord.setName(bookRecord.getName());
        existingBookRecord.setSummary(bookRecord.getSummary());
        existingBookRecord.setRating(bookRecord.getRating());

        return bookRepository.save(existingBookRecord);

    }

    // TODO: Write Delete endpoint

    @DeleteMapping(value = "{bookId}")
    public void deleteBookById(@PathVariable(value = "bookId") Long bookId) throws ClassNotFoundException {
        //we do a check repository first of if the book is present to delete or not present
        if(!bookRepository.findById(bookId).isPresent()) {
            throw new ClassNotFoundException("bookId" + bookId + " not present");

        }
        bookRepository.deleteById(bookId);
    }
}
