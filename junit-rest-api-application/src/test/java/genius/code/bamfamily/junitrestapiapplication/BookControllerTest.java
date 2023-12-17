package genius.code.bamfamily.junitrestapiapplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class BookControllerTest {
    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookController bookController;

    Book RECORD_1 = new Book(1L, "Delicious meals", "How to cook healthy Kenya foods", 5);
    Book RECORD_2 = new Book(2l, "My journey as  Widow", "Prayers help through difficult and tough times of life", 5);
    Book RECORD_3 = new Book(3L, "The secret of cooking Nice and Delicious Pilau", "How to cook pilau in a very simple and easy way", 4);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void getAllReocrds_success() throws Exception {
        List<Book> records = new ArrayList<>(Arrays.asList(RECORD_1, RECORD_2, RECORD_3));// now we have the records as the intended output or value os the records created.

        Mockito.when(bookRepository.findAll()).thenReturn(records);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].name", is("The secret of cooking Nice and Delicious Pilau")))
                .andExpect(jsonPath("$[1].name", is("My journey as  Widow")));

    }

    @Test
    public void getBookById_success() throws Exception {
        Mockito.when(bookRepository.findById(RECORD_1.getBookId())).thenReturn(Optional.ofNullable(RECORD_1));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Delicious meals")));
    }

    @Test
    public void createRecord_success() throws Exception {
        Book record = Book.builder()
                .bookId(4L)
                .name("Introduction to records Management")
                .summary("Management of records is very essential")
                .rating(5)
                .build();

        Mockito.when(bookRepository.save(record)).thenReturn(record);
        String content = objectWriter.writeValueAsString(record);

        MockHttpServletRequestBuilder mockPostRequest = MockMvcRequestBuilders.post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockPostRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("Introduction to records Management")));
    }


    @Test
    public void updateRecord_success() throws Exception {
        Book updatedRecord = Book.builder()
                .bookId(1L)
                .name("updated Book Name")
                .summary("updated summary")
                .rating(1).build();

        Mockito.when(bookRepository.findById(RECORD_1.getBookId())).thenReturn(Optional.ofNullable(RECORD_1));
        Mockito.when(bookRepository.save(updatedRecord)).thenReturn(updatedRecord);

        String updatedContent = objectWriter.writeValueAsString(updatedRecord);

        MockHttpServletRequestBuilder mockUpdateRequest = MockMvcRequestBuilders
                .put("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(updatedContent);

        mockMvc.perform(mockUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("updated Book Name")));
    }

    @Test
    public void deleteBookById_success() throws Exception{
        Mockito.when(bookRepository.findById(RECORD_2.getBookId())).thenReturn(Optional.of(RECORD_2));
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/book/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}
