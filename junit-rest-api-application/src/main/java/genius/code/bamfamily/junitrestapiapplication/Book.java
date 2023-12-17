package genius.code.bamfamily.junitrestapiapplication;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "book_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bookId;

    @NotNull
    private String name;

    @NotNull
    private String summary;

    private int rating;
}
